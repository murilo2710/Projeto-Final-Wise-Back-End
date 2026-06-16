USE sistema_gestao_consultas;

INSERT IGNORE INTO usuarios (id, nome, cpf, email, senha, data_criacao, ultimo_login, perfil, ativo) VALUES
(1, 'Admin Odonto', '93541134780', 'admin@odonto.com', '$2a$10$a63OLvfq0ZKJyfpKQAct4.JquSSyoRG5O2qUno3XR4MfMPQliw10e', NOW(), NULL, 'ADMIN', TRUE),
(2, 'Dra Ana Souza', '52998224725', 'dentista@odonto.com', '$2a$10$a63OLvfq0ZKJyfpKQAct4.JquSSyoRG5O2qUno3XR4MfMPQliw10e', NOW(), NULL, 'DENTISTA', TRUE),
(3, 'Dr Bruno Lima', '16899535009', 'bruno.lima@odonto.com', '$2a$10$a63OLvfq0ZKJyfpKQAct4.JquSSyoRG5O2qUno3XR4MfMPQliw10e', NOW(), NULL, 'DENTISTA', TRUE),
(4, 'Dra Camila Rocha', '81209254085', 'camila.rocha@odonto.com', '$2a$10$a63OLvfq0ZKJyfpKQAct4.JquSSyoRG5O2qUno3XR4MfMPQliw10e', NOW(), NULL, 'DENTISTA', TRUE),
(5, 'Secretaria Clinica', '80492115923', 'secretaria@odonto.com', '$2a$10$a63OLvfq0ZKJyfpKQAct4.JquSSyoRG5O2qUno3XR4MfMPQliw10e', NOW(), NULL, 'ADMIN', TRUE);

INSERT IGNORE INTO pacientes (id, nome, email, cpf, data_criacao, telefone) VALUES
(1, 'Joao Pereira', 'joao.pereira@email.com', '11144477735', NOW(), '(11) 99999-1001'),
(2, 'Maria Oliveira', 'maria.oliveira@email.com', '12345678909', NOW(), '(11) 99999-1002'),
(3, 'Carla Mendes', 'carla.mendes@email.com', '98765432100', NOW(), '(11) 99999-1003'),
(4, 'Rafael Martins', 'rafael.martins@email.com', '69493352706', NOW(), '(11) 99999-1004'),
(5, 'Fernanda Costa', 'fernanda.costa@email.com', '91808801776', NOW(), '(11) 99999-1005'),
(6, 'Lucas Almeida', 'lucas.almeida@email.com', '88803244468', NOW(), '(11) 99999-1006'),
(7, 'Patricia Gomes', 'patricia.gomes@email.com', '69083010791', NOW(), '(11) 99999-1007'),
(8, 'Eduardo Nunes', 'eduardo.nunes@email.com', '29984446603', NOW(), '(11) 99999-1008');

INSERT IGNORE INTO dentistas (id, nome, cpf, email, cro, data_criacao, ativo) VALUES
(1, 'Dra Ana Souza', '52998224725', 'dentista@odonto.com', 'CRO-SP-12345', NOW(), TRUE),
(2, 'Dr Bruno Lima', '16899535009', 'bruno.lima@odonto.com', 'CRO-SP-54321', NOW(), TRUE),
(3, 'Dra Camila Rocha', '81209254085', 'camila.rocha@odonto.com', 'CRO-SP-67890', NOW(), TRUE),
(4, 'Dr Diego Ferreira', '59834558007', 'diego.ferreira@odonto.com', 'CRO-SP-98765', NOW(), FALSE);

INSERT IGNORE INTO especialidades (id, nome) VALUES
(1, 'Ortodontia'),
(2, 'Periodontia'),
(3, 'Implantodontia'),
(4, 'Clinica Geral'),
(5, 'Endodontia'),
(6, 'Odontopediatria'),
(7, 'Protese Dentaria'),
(8, 'Cirurgia Oral');

INSERT IGNORE INTO dentista_especialidade (id, id_dentista, id_especialidade) VALUES
(1, 1, 1),
(2, 1, 4),
(3, 2, 2),
(4, 2, 3),
(5, 2, 8),
(6, 3, 5),
(7, 3, 6),
(8, 3, 7),
(9, 4, 4);

INSERT IGNORE INTO consultas (id, id_paciente, id_dentista, id_usuario, descricao, motivo_cancelamento, data_inicio, data_fim, data_registro, status) VALUES
(1, 1, 1, 1, 'Avaliacao inicial ortodontica', NULL, '2026-07-10 09:00:00', '2026-07-10 10:00:00', NOW(), 'AGENDADA'),
(2, 2, 2, 5, 'Consulta de avaliacao periodontal', NULL, '2026-07-10 14:00:00', '2026-07-10 15:00:00', NOW(), 'AGENDADA'),
(3, 3, 3, 1, 'Tratamento de canal - primeira sessao', NULL, '2026-07-11 08:30:00', '2026-07-11 10:00:00', NOW(), 'AGENDADA'),
(4, 4, 2, 2, 'Planejamento de implante dentario', NULL, '2026-07-12 11:00:00', '2026-07-12 12:00:00', NOW(), 'AGENDADA'),
(5, 5, 1, 1, 'Retorno de manutencao ortodontica', NULL, '2026-06-10 09:00:00', '2026-06-10 09:45:00', NOW(), 'FINALIZADA'),
(6, 6, 3, 3, 'Consulta odontopediatrica preventiva', NULL, '2026-06-11 15:00:00', '2026-06-11 16:00:00', NOW(), 'FINALIZADA'),
(7, 7, 2, 5, 'Raspagem periodontal', NULL, '2026-06-12 13:30:00', '2026-06-12 14:30:00', NOW(), 'FINALIZADA'),
(8, 8, 3, 1, 'Prova de protese dentaria', 'Paciente solicitou remarcacao por conflito de horario', '2026-07-13 10:00:00', '2026-07-13 11:00:00', NOW(), 'CANCELADA'),
(9, 1, 2, 5, 'Cirurgia oral simples', 'Paciente apresentou indisponibilidade clinica', '2026-07-14 16:00:00', '2026-07-14 17:30:00', NOW(), 'CANCELADA');

INSERT IGNORE INTO materiais (id, nome, descricao, unidade_medida, quantidade_atual, quantidade_minima, ativo, data_criacao) VALUES
(1, 'Anestesico Odontologico', 'Tubetes de anestesico para procedimentos clinicos e cirurgicos', 'CAIXA', 25.00, 10.00, TRUE, NOW()),
(2, 'Braquete Metalico', 'Braquetes utilizados em tratamentos ortodonticos', 'PACOTE', 40.00, 15.00, TRUE, NOW()),
(3, 'Fio de Sutura', 'Fio utilizado em procedimentos periodontais e cirurgicos', 'CAIXA', 8.00, 5.00, TRUE, NOW()),
(4, 'Kit de Implante', 'Kit cirurgico utilizado em procedimentos de implantodontia', 'UNIDADE', 6.00, 3.00, TRUE, NOW()),
(5, 'Lima Endodontica', 'Instrumental para preparo de canais radiculares', 'CAIXA', 18.00, 8.00, TRUE, NOW()),
(6, 'Resina Composta', 'Material restaurador fotopolimerizavel', 'UNIDADE', 12.00, 6.00, TRUE, NOW()),
(7, 'Fluor Gel Infantil', 'Gel de fluor para aplicacao preventiva em odontopediatria', 'FRASCO', 4.00, 5.00, TRUE, NOW()),
(8, 'Moldeira para Protese', 'Moldeiras usadas em procedimentos de protese dentaria', 'UNIDADE', 0.00, 4.00, FALSE, NOW());

INSERT IGNORE INTO material_especialidade (id, id_material, id_especialidade) VALUES
(1, 1, 4),
(2, 1, 8),
(3, 2, 1),
(4, 3, 2),
(5, 3, 8),
(6, 4, 3),
(7, 4, 8),
(8, 5, 5),
(9, 6, 4),
(10, 7, 6),
(11, 8, 7);

INSERT IGNORE INTO movimentacoes_estoque (id, id_material, id_usuario, tipo, quantidade, estoque_anterior, estoque_atual, motivo, data_movimentacao) VALUES
(1, 1, 1, 'ENTRADA', 30.00, 0.00, 30.00, 'Compra inicial de anestesicos', '2026-06-01 08:30:00'),
(2, 1, 2, 'SAIDA', 5.00, 30.00, 25.00, 'Uso em procedimentos clinicos', '2026-06-10 10:20:00'),
(3, 2, 1, 'ENTRADA', 50.00, 0.00, 50.00, 'Compra de braquetes para ortodontia', '2026-06-01 09:00:00'),
(4, 2, 1, 'SAIDA', 10.00, 50.00, 40.00, 'Uso em manutencoes ortodonticas', '2026-06-10 11:00:00'),
(5, 3, 5, 'ENTRADA', 10.00, 0.00, 10.00, 'Reposicao de fios de sutura', '2026-06-02 13:00:00'),
(6, 3, 2, 'SAIDA', 2.00, 10.00, 8.00, 'Uso em procedimento periodontal', '2026-06-12 14:45:00'),
(7, 4, 1, 'ENTRADA', 6.00, 0.00, 6.00, 'Compra inicial de kits de implante', '2026-06-03 09:15:00'),
(8, 5, 3, 'ENTRADA', 20.00, 0.00, 20.00, 'Compra de instrumentais endodonticos', '2026-06-04 10:00:00'),
(9, 5, 3, 'SAIDA', 2.00, 20.00, 18.00, 'Uso em tratamento de canal', '2026-06-11 16:10:00'),
(10, 6, 5, 'ENTRADA', 15.00, 0.00, 15.00, 'Compra de resinas restauradoras', '2026-06-05 08:40:00'),
(11, 6, 1, 'SAIDA', 3.00, 15.00, 12.00, 'Uso em restauracoes clinicas', '2026-06-12 10:00:00'),
(12, 7, 3, 'AJUSTE', 4.00, 0.00, 4.00, 'Ajuste apos conferencia fisica do estoque', '2026-06-13 09:00:00');

INSERT IGNORE INTO logs_atividade (id, usuario_id, usuario_nome, usuario_email, titulo, mensagem, tipo, recurso, recurso_id, data_criacao) VALUES
(1, 1, 'Admin Odonto', 'admin@odonto.com', 'Sistema inicializado', 'Dados de exemplo carregados para demonstracao', 'INFO', 'SISTEMA', NULL, NOW()),
(2, 1, 'Admin Odonto', 'admin@odonto.com', 'Consulta cadastrada', 'Consulta de avaliacao inicial ortodontica registrada', 'CRIACAO', 'CONSULTA', 1, NOW()),
(3, 5, 'Secretaria Clinica', 'secretaria@odonto.com', 'Consulta cancelada', 'Consulta cancelada com motivo informado pelo paciente', 'ATUALIZACAO', 'CONSULTA', 8, NOW()),
(4, 1, 'Admin Odonto', 'admin@odonto.com', 'Material em baixo estoque', 'Fluor Gel Infantil esta abaixo da quantidade minima definida', 'ALERTA', 'MATERIAL', 7, NOW());
