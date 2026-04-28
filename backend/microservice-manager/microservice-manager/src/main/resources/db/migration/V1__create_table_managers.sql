CREATE TABLE managers (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    cpf        VARCHAR(20)  NOT NULL UNIQUE,
    phone      VARCHAR(30)
);

-- Dados pré-cadastrados conforme enunciado
INSERT INTO managers (name, email, cpf, phone) VALUES
    ('Geniéve',    'ger1@bantads.com.br', '98574307084', '(41) 98888-0001'),
    ('Godophredo', 'ger2@bantads.com.br', '64065268052', '(41) 98888-0002'),
    ('Gyândula',   'ger3@bantads.com.br', '23862179060', '(41) 98888-0003');