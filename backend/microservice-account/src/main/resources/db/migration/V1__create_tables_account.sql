CREATE TABLE accounts (
    id            BIGSERIAL PRIMARY KEY,
    cpf_customer  VARCHAR(20)    NOT NULL UNIQUE,
    number        VARCHAR(10)    NOT NULL UNIQUE,
    creation_date DATE           NOT NULL DEFAULT CURRENT_DATE,
    balance       NUMERIC(15,2)  NOT NULL DEFAULT 0.00,
    account_limit NUMERIC(15,2)  NOT NULL DEFAULT 0.00,
    cpf_manager   VARCHAR(20)    NOT NULL
);

CREATE TABLE transaction_history (
    id           BIGSERIAL PRIMARY KEY,
    account_id   BIGINT         NOT NULL REFERENCES accounts(id),
    date_time    TIMESTAMP      NOT NULL DEFAULT NOW(),
    type         VARCHAR(30)    NOT NULL,
    cpf_origin   VARCHAR(20),
    cpf_dest     VARCHAR(20),
    amount       NUMERIC(15,2)  NOT NULL
);