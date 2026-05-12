CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    street_name VARCHAR(255) NOT NULL,
    street_number VARCHAR(50) NOT NULL,
    complement VARCHAR(255),
    zip_code VARCHAR(20) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL
);

CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    cpf VARCHAR(20) NOT NULL UNIQUE,
    phone VARCHAR(30),
    salary NUMERIC(15,2),
    address_id BIGINT,
    CONSTRAINT fk_customer_address
        FOREIGN KEY (address_id)
        REFERENCES addresses(id)
);