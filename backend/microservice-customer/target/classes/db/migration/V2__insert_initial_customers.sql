INSERT INTO addresses (
    street_name,
    street_number,
    complement,
    zip_code,
    city,
    state
) VALUES
    ('Rua das Araucárias', '120', 'Apto 301', '80010-000', 'Curitiba', 'PR'),
    ('Avenida Brasil', '450', 'Casa 2', '80220-100', 'Curitiba', 'PR'),
    ('Rua XV de Novembro', '980', NULL, '80020-310', 'Curitiba', 'PR'),
    ('Rua das Flores', '75', 'Fundos', '80410-120', 'Curitiba', 'PR'),
    ('Avenida Sete de Setembro', '1500', 'Bloco B', '80230-010', 'Curitiba', 'PR');

INSERT INTO customers (
    name,
    email,
    cpf,
    phone,
    salary,
    address_id
) VALUES
    (
        'Catharyna',
        'cli1@bantads.com.br',
        '12912861012',
        '(41) 99901-0001',
        10000.00,
        (SELECT id FROM addresses WHERE street_name = 'Rua das Araucárias' AND street_number = '120')
    ),
    (
        'Cleuddônio',
        'cli2@bantads.com.br',
        '09506382000',
        '(41) 99902-0002',
        20000.00,
        (SELECT id FROM addresses WHERE street_name = 'Avenida Brasil' AND street_number = '450')
    ),
    (
        'Catianna',
        'cli3@bantads.com.br',
        '85733854057',
        '(41) 99903-0003',
        3000.00,
        (SELECT id FROM addresses WHERE street_name = 'Rua XV de Novembro' AND street_number = '980')
    ),
    (
        'Cutardo',
        'cli4@bantads.com.br',
        '58872160006',
        '(41) 99904-0004',
        500.00,
        (SELECT id FROM addresses WHERE street_name = 'Rua das Flores' AND street_number = '75')
    ),
    (
        'Coândrya',
        'cli5@bantads.com.br',
        '76179646090',
        '(41) 99905-0005',
        1500.00,
        (SELECT id FROM addresses WHERE street_name = 'Avenida Sete de Setembro' AND street_number = '1500')
    );