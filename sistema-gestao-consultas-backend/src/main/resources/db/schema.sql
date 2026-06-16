CREATE DATABASE IF NOT EXISTS sistema_gestao_consultas
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sistema_gestao_consultas;

CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(120) NOT NULL,
  cpf VARCHAR(14) NOT NULL,
  email VARCHAR(120) NOT NULL,
  senha VARCHAR(255) NOT NULL,
  data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ultimo_login DATETIME NULL,
  perfil VARCHAR(20) NOT NULL,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuarios_cpf (cpf),
  UNIQUE KEY uk_usuarios_email (email)
);

CREATE TABLE IF NOT EXISTS pacientes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(120) NOT NULL,
  email VARCHAR(120) NOT NULL,
  cpf VARCHAR(14) NOT NULL,
  data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  telefone VARCHAR(20) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_pacientes_email (email),
  UNIQUE KEY uk_pacientes_cpf (cpf)
);

CREATE TABLE IF NOT EXISTS dentistas (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(120) NOT NULL,
  cpf VARCHAR(14) NOT NULL,
  email VARCHAR(120) NOT NULL,
  cro VARCHAR(20) NOT NULL,
  data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id),
  UNIQUE KEY uk_dentistas_cpf (cpf),
  UNIQUE KEY uk_dentistas_email (email),
  UNIQUE KEY uk_dentistas_cro (cro)
);

CREATE TABLE IF NOT EXISTS especialidades (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_especialidades_nome (nome)
);

CREATE TABLE IF NOT EXISTS dentista_especialidade (
  id BIGINT NOT NULL AUTO_INCREMENT,
  id_dentista BIGINT NOT NULL,
  id_especialidade BIGINT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_dentista_especialidade (id_dentista, id_especialidade),
  CONSTRAINT fk_de_dentista FOREIGN KEY (id_dentista) REFERENCES dentistas(id),
  CONSTRAINT fk_de_especialidade FOREIGN KEY (id_especialidade) REFERENCES especialidades(id)
);

CREATE TABLE IF NOT EXISTS consultas (
  id BIGINT NOT NULL AUTO_INCREMENT,
  id_paciente BIGINT NOT NULL,
  id_dentista BIGINT NOT NULL,
  id_usuario BIGINT NOT NULL,
  descricao VARCHAR(500) NOT NULL,
  motivo_cancelamento VARCHAR(500) NULL,
  data_inicio DATETIME NOT NULL,
  data_fim DATETIME NOT NULL,
  data_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(20) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_consultas_paciente (id_paciente),
  KEY idx_consultas_dentista (id_dentista),
  KEY idx_consultas_usuario (id_usuario),
  CONSTRAINT fk_consultas_paciente FOREIGN KEY (id_paciente) REFERENCES pacientes(id),
  CONSTRAINT fk_consultas_dentista FOREIGN KEY (id_dentista) REFERENCES dentistas(id),
  CONSTRAINT fk_consultas_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS materiais (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(120) NOT NULL,
  descricao VARCHAR(500) NULL,
  unidade_medida VARCHAR(30) NOT NULL,
  quantidade_atual DECIMAL(10,2) NOT NULL DEFAULT 0,
  quantidade_minima DECIMAL(10,2) NOT NULL DEFAULT 0,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS material_especialidade (
  id BIGINT NOT NULL AUTO_INCREMENT,
  id_material BIGINT NOT NULL,
  id_especialidade BIGINT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_material_especialidade (id_material, id_especialidade),
  CONSTRAINT fk_me_material FOREIGN KEY (id_material) REFERENCES materiais(id),
  CONSTRAINT fk_me_especialidade FOREIGN KEY (id_especialidade) REFERENCES especialidades(id)
);

CREATE TABLE IF NOT EXISTS movimentacoes_estoque (
  id BIGINT NOT NULL AUTO_INCREMENT,
  id_material BIGINT NOT NULL,
  id_usuario BIGINT NOT NULL,
  tipo VARCHAR(20) NOT NULL,
  quantidade DECIMAL(10,2) NOT NULL,
  estoque_anterior DECIMAL(10,2) NOT NULL,
  estoque_atual DECIMAL(10,2) NOT NULL,
  motivo VARCHAR(500) NOT NULL,
  data_movimentacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_movimentacoes_material (id_material),
  KEY idx_movimentacoes_usuario (id_usuario),
  CONSTRAINT fk_mov_material FOREIGN KEY (id_material) REFERENCES materiais(id),
  CONSTRAINT fk_mov_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS logs_atividade (
  id BIGINT NOT NULL AUTO_INCREMENT,
  usuario_id BIGINT NULL,
  usuario_nome VARCHAR(120) NULL,
  usuario_email VARCHAR(120) NULL,
  titulo VARCHAR(120) NOT NULL,
  mensagem VARCHAR(500) NOT NULL,
  tipo VARCHAR(30) NOT NULL,
  recurso VARCHAR(60) NOT NULL,
  recurso_id BIGINT NULL,
  data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS arquivos_consulta (
  id BIGINT NOT NULL AUTO_INCREMENT,
  id_consulta BIGINT NOT NULL,
  id_usuario BIGINT NOT NULL,
  nome_original VARCHAR(255) NOT NULL,
  nome_armazenado VARCHAR(255) NOT NULL,
  tipo_conteudo VARCHAR(120) NOT NULL,
  tamanho BIGINT NOT NULL,
  caminho_arquivo VARCHAR(500) NOT NULL,
  data_upload DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_arquivos_nome_armazenado (nome_armazenado),
  KEY idx_arquivos_consulta (id_consulta),
  KEY idx_arquivos_usuario (id_usuario),
  CONSTRAINT fk_arquivos_consulta FOREIGN KEY (id_consulta) REFERENCES consultas(id),
  CONSTRAINT fk_arquivos_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT,
  id_usuario BIGINT NOT NULL,
  token_hash VARCHAR(120) NOT NULL,
  data_expiracao DATETIME NOT NULL,
  data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  revogado BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id),
  UNIQUE KEY uk_refresh_tokens_hash (token_hash),
  KEY idx_refresh_tokens_usuario (id_usuario),
  CONSTRAINT fk_refresh_tokens_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);
