INSERT INTO accounts (cpf_customer, number, creation_date, balance, account_limit, cpf_manager) VALUES
    ('12912861012', '1291', '2000-01-01', 800.00,      5000.00,  '98574307084'),
    ('09506382000', '0950', '1990-10-10', -10000.00,   10000.00, '64065268052'),
    ('85733854057', '8573', '2012-12-12', -1000.00,    1500.00,  '23862179060'),
    ('58872160006', '5887', '2022-02-22', 150000.00,   0.00,     '98574307084'),
    ('76179646090', '7617', '2025-01-01', 1500.00,     0.00,     '64065268052');

-- Movimentações da Catharyna (conta 1291) conforme enunciado
INSERT INTO transaction_history (account_id, date_time, type, cpf_origin, cpf_dest, amount) VALUES
    ((SELECT id FROM accounts WHERE number = '1291'), '2020-01-01 10:00:00', 'DEPOSITO',        NULL,          NULL,          1000.00),
    ((SELECT id FROM accounts WHERE number = '1291'), '2020-01-01 11:00:00', 'DEPOSITO',        NULL,          NULL,          900.00),
    ((SELECT id FROM accounts WHERE number = '1291'), '2020-01-01 12:00:00', 'SAQUE',           NULL,          NULL,          550.00),
    ((SELECT id FROM accounts WHERE number = '1291'), '2020-01-01 13:00:00', 'SAQUE',           NULL,          NULL,          350.00),
    ((SELECT id FROM accounts WHERE number = '1291'), '2020-01-10 15:00:00', 'DEPOSITO',        NULL,          NULL,          2000.00),
    ((SELECT id FROM accounts WHERE number = '1291'), '2020-01-15 08:00:00', 'SAQUE',           NULL,          NULL,          500.00),
    ((SELECT id FROM accounts WHERE number = '1291'), '2020-01-20 12:00:00', 'TRANSFERENCIA',   '12912861012', '09506382000', 1700.00);

-- Movimentações do Cleuddônio (conta 0950) conforme enunciado
INSERT INTO transaction_history (account_id, date_time, type, cpf_origin, cpf_dest, amount) VALUES
    ((SELECT id FROM accounts WHERE number = '0950'), '2025-01-01 12:00:00', 'DEPOSITO',        NULL,          NULL,          1000.00),
    ((SELECT id FROM accounts WHERE number = '0950'), '2025-01-02 10:00:00', 'DEPOSITO',        NULL,          NULL,          5000.00),
    ((SELECT id FROM accounts WHERE number = '0950'), '2025-01-10 10:00:00', 'SAQUE',           NULL,          NULL,          200.00),
    ((SELECT id FROM accounts WHERE number = '0950'), '2025-02-05 10:00:00', 'DEPOSITO',        NULL,          NULL,          7000.00);

-- Movimentações da Catianna (conta 8573)
INSERT INTO transaction_history (account_id, date_time, type, cpf_origin, cpf_dest, amount) VALUES
    ((SELECT id FROM accounts WHERE number = '8573'), '2025-05-05 00:00:00', 'DEPOSITO',        NULL,          NULL,          1000.00),
    ((SELECT id FROM accounts WHERE number = '8573'), '2025-05-06 00:00:00', 'SAQUE',           NULL,          NULL,          2000.00);

-- Movimentações do Cutardo (conta 5887)
INSERT INTO transaction_history (account_id, date_time, type, cpf_origin, cpf_dest, amount) VALUES
    ((SELECT id FROM accounts WHERE number = '5887'), '2025-06-01 00:00:00', 'DEPOSITO',        NULL,          NULL,          150000.00);

-- Movimentações da Coândrya (conta 7617)
INSERT INTO transaction_history (account_id, date_time, type, cpf_origin, cpf_dest, amount) VALUES
    ((SELECT id FROM accounts WHERE number = '7617'), '2025-07-01 00:00:00', 'DEPOSITO',        NULL,          NULL,          1500.00);