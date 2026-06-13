package com.wise.sistema_gestao_consultas_backend.exception;

import com.wise.sistema_gestao_consultas_backend.dto.response.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<String> detalhes = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatarErroCampo)
                .toList();

        return construirResposta(
                HttpStatus.BAD_REQUEST,
                "Dados invalidos",
                "Existem campos invalidos na requisicao",
                request.getRequestURI(),
                detalhes
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<String> detalhes = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return construirResposta(
                HttpStatus.BAD_REQUEST,
                "Dados invalidos",
                "Existem parametros invalidos na requisicao",
                request.getRequestURI(),
                detalhes
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return construirResposta(
                HttpStatus.BAD_REQUEST,
                "Requisicao invalida",
                "Corpo da requisicao invalido ou mal formatado",
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        String detalhe = exception.getName() + ": valor invalido";

        return construirResposta(
                HttpStatus.BAD_REQUEST,
                "Parametro invalido",
                "Existem parametros invalidos na requisicao",
                request.getRequestURI(),
                List.of(detalhe)
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String mensagem = exception.getReason() == null || exception.getReason().isBlank()
                ? status.getReasonPhrase()
                : exception.getReason();

        return construirResposta(
                status,
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return construirResposta(
                HttpStatus.NOT_FOUND,
                "Recurso nao encontrado",
                exception.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroResponse> handleIllegalState(
            IllegalStateException exception,
            HttpServletRequest request
    ) {
        return construirResposta(
                HttpStatus.CONFLICT,
                "Conflito de regra de negocio",
                exception.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        return construirResposta(
                HttpStatus.FORBIDDEN,
                "Acesso negado",
                "Voce nao tem permissao para acessar este recurso",
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        return construirResposta(
                HttpStatus.CONFLICT,
                "Conflito de integridade",
                "Registro possui vinculos ou dados duplicados e nao pode ser processado",
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleException(
            Exception exception,
            HttpServletRequest request
    ) {
        return construirResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                "Erro inesperado no servidor",
                request.getRequestURI(),
                List.of()
        );
    }

    private String formatarErroCampo(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private ResponseEntity<ErroResponse> construirResposta(
            HttpStatus status,
            String erro,
            String mensagem,
            String path,
            List<String> detalhes
    ) {
        ErroResponse response = new ErroResponse(
                LocalDateTime.now(),
                status.value(),
                erro,
                mensagem,
                path,
                detalhes
        );

        return ResponseEntity.status(status).body(response);
    }
}
