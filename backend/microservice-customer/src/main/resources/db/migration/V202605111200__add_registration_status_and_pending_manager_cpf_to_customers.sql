ALTER TABLE customers
ADD COLUMN registration_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
ADD COLUMN pending_manager_cpf VARCHAR(255);
