package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.response.ArquivoConsultaResponse;
import com.wise.sistema_gestao_consultas_backend.entity.ArquivoConsulta;
import com.wise.sistema_gestao_consultas_backend.entity.Consulta;
import com.wise.sistema_gestao_consultas_backend.entity.Usuario;
import com.wise.sistema_gestao_consultas_backend.enums.PerfilUsuario;
import com.wise.sistema_gestao_consultas_backend.repository.ArquivoConsultaRepository;
import com.wise.sistema_gestao_consultas_backend.repository.ConsultaRepository;
import com.wise.sistema_gestao_consultas_backend.repository.UsuarioRepository;
import com.wise.sistema_gestao_consultas_backend.security.AuthenticatedUserService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ArquivoConsultaService {

    private static final long TAMANHO_MAXIMO_BYTES = 10 * 1024 * 1024;
    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    private final ArquivoConsultaRepository arquivoConsultaRepository;
    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final NotificacaoService notificacaoService;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public ArquivoConsultaResponse anexar(Long consultaId, MultipartFile arquivo) {
        validarArquivo(arquivo);

        Consulta consulta = buscarConsultaComPermissao(consultaId);
        Usuario usuario = buscarUsuarioAutenticado();

        String nomeOriginal = limparNomeOriginal(arquivo.getOriginalFilename());
        String extensao = extrairExtensao(nomeOriginal);
        String nomeArmazenado = UUID.randomUUID() + extensao;
        Path diretorio = diretorioConsultas();
        Path destino = diretorio.resolve(nomeArmazenado).normalize();

        try {
            Files.createDirectories(diretorio);
            arquivo.transferTo(destino);
        } catch (IOException exception) {
            throw new IllegalStateException("Nao foi possivel salvar o arquivo");
        }

        ArquivoConsulta anexo = new ArquivoConsulta();
        anexo.setConsulta(consulta);
        anexo.setUsuario(usuario);
        anexo.setNomeOriginal(nomeOriginal);
        anexo.setNomeArmazenado(nomeArmazenado);
        anexo.setTipoConteudo(arquivo.getContentType());
        anexo.setTamanho(arquivo.getSize());
        anexo.setCaminhoArquivo(destino.toString());

        ArquivoConsulta salvo = arquivoConsultaRepository.save(anexo);
        notificacaoService.notificar(
                "Arquivo anexado",
                "Um arquivo foi anexado a consulta #" + consulta.getId(),
                "INFO",
                "ARQUIVO_CONSULTA",
                salvo.getId()
        );

        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<ArquivoConsultaResponse> listarPorConsulta(Long consultaId) {
        buscarConsultaComPermissao(consultaId);

        return arquivoConsultaRepository.findByConsultaIdOrderByDataUploadDesc(consultaId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ArquivoDownload carregarParaDownload(Long arquivoId) {
        ArquivoConsulta arquivo = buscarArquivoComPermissao(arquivoId);
        Path caminho = Paths.get(arquivo.getCaminhoArquivo()).normalize();

        try {
            Resource resource = new UrlResource(caminho.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException("Arquivo nao encontrado no armazenamento");
            }

            return new ArquivoDownload(
                    resource,
                    arquivo.getNomeOriginal(),
                    arquivo.getTipoConteudo(),
                    arquivo.getTamanho()
            );
        } catch (MalformedURLException exception) {
            throw new IllegalStateException("Arquivo invalido no armazenamento");
        }
    }

    @Transactional
    public void deletar(Long arquivoId) {
        ArquivoConsulta arquivo = buscarArquivoComPermissao(arquivoId);
        excluirArquivoFisico(arquivo);

        arquivoConsultaRepository.delete(arquivo);
        notificacaoService.notificar(
                "Arquivo removido",
                "O arquivo " + arquivo.getNomeOriginal() + " foi removido da consulta #" + arquivo.getConsulta().getId(),
                "ALERTA",
                "ARQUIVO_CONSULTA",
                arquivo.getId()
        );
    }

    @Transactional
    public void deletarArquivosDaConsultaSemNotificacao(Long consultaId) {
        List<ArquivoConsulta> arquivos = arquivoConsultaRepository.findByConsultaId(consultaId);
        arquivos.forEach(this::excluirArquivoFisico);
        arquivoConsultaRepository.deleteAll(arquivos);
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalStateException("Arquivo vazio");
        }

        if (arquivo.getSize() > TAMANHO_MAXIMO_BYTES) {
            throw new IllegalStateException("Arquivo excede o tamanho maximo de 10MB");
        }

        if (arquivo.getContentType() == null || !TIPOS_PERMITIDOS.contains(arquivo.getContentType())) {
            throw new IllegalStateException("Formato de arquivo nao permitido. Envie PDF, PNG ou JPG");
        }
    }

    private Consulta buscarConsultaComPermissao(Long consultaId) {
        PerfilUsuario perfil = authenticatedUserService.getCurrentPerfil();
        Long usuarioId = authenticatedUserService.getCurrentUserId();

        if (PerfilUsuario.ADMIN.equals(perfil)) {
            return consultaRepository.findByIdComRelacionamentos(consultaId)
                    .orElseThrow(() -> new IllegalArgumentException("Consulta nao encontrada"));
        }

        return consultaRepository.findByIdComRelacionamentosPorUsuario(consultaId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Consulta nao encontrada"));
    }

    private ArquivoConsulta buscarArquivoComPermissao(Long arquivoId) {
        ArquivoConsulta arquivo = arquivoConsultaRepository.findById(arquivoId)
                .orElseThrow(() -> new IllegalArgumentException("Arquivo nao encontrado"));

        buscarConsultaComPermissao(arquivo.getConsulta().getId());
        return arquivo;
    }

    private Usuario buscarUsuarioAutenticado() {
        return usuarioRepository.findById(authenticatedUserService.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));
    }

    private String limparNomeOriginal(String nomeOriginal) {
        String nome = StringUtils.cleanPath(nomeOriginal == null ? "arquivo" : nomeOriginal);
        if (nome.contains("..")) {
            throw new IllegalStateException("Nome de arquivo invalido");
        }
        return nome;
    }

    private String extrairExtensao(String nomeOriginal) {
        int indice = nomeOriginal.lastIndexOf(".");
        if (indice < 0) {
            return "";
        }
        return nomeOriginal.substring(indice);
    }

    private Path diretorioConsultas() {
        return Paths.get(uploadDir)
                .resolve("consultas")
                .toAbsolutePath()
                .normalize();
    }

    private void excluirArquivoFisico(ArquivoConsulta arquivo) {
        try {
            Files.deleteIfExists(Paths.get(arquivo.getCaminhoArquivo()));
        } catch (IOException exception) {
            throw new IllegalStateException("Nao foi possivel remover o arquivo do armazenamento");
        }
    }

    private ArquivoConsultaResponse toResponse(ArquivoConsulta arquivo) {
        return new ArquivoConsultaResponse(
                arquivo.getId(),
                arquivo.getConsulta().getId(),
                arquivo.getUsuario().getId(),
                arquivo.getUsuario().getNome(),
                arquivo.getNomeOriginal(),
                arquivo.getTipoConteudo(),
                arquivo.getTamanho(),
                arquivo.getDataUpload()
        );
    }

    public record ArquivoDownload(
            Resource resource,
            String nomeOriginal,
            String tipoConteudo,
            Long tamanho
    ) {
    }
}
