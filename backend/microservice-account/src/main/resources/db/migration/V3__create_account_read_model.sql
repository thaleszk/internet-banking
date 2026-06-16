CREATE TABLE account_read_model (
    id            BIGSERIAL PRIMARY KEY,
    cpf_customer  VARCHAR(20)    NOT NULL UNIQUE,
    number        VARCHAR(10)    NOT NULL UNIQUE,
    creation_date DATE           NOT NULL,
    balance       NUMERIC(15,2)  NOT NULL,
    account_limit NUMERIC(15,2)  NOT NULL,
    cpf_manager   VARCHAR(20)    NOT NULL,
    updated_at    TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE transaction_read_model (
    id                    BIGSERIAL PRIMARY KEY,
    source_transaction_id BIGINT         NOT NULL UNIQUE,
    account_number        VARCHAR(10)    NOT NULL,
    date_time             TIMESTAMP      NOT NULL,
    type                  VARCHAR(30)    NOT NULL,
    cpf_origin            VARCHAR(20),
    cpf_dest              VARCHAR(20),
    amount                NUMERIC(15,2)  NOT NULL
);

INSERT INTO account_read_model (
    cpf_customer,
    number,
    creation_date,
    balance,
    account_limit,
    cpf_manager,
    updated_at
)
SELECT
    cpf_customer,
    number,
    creation_date,
    balance,
    account_limit,
    cpf_manager,
    NOW()
FROM accounts;

INSERT INTO transaction_read_model (
    source_transaction_id,
    account_number,
    date_time,
    type,
    cpf_origin,
    cpf_dest,
    amount
)
SELECT
    th.id,
    a.number,
    th.date_time,
    th.type,
    th.cpf_origin,
    th.cpf_dest,
    th.amount
FROM transaction_history th
JOIN accounts a ON a.id = th.account_id;
