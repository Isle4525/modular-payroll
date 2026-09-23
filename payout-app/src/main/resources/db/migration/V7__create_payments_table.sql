CREATE TYPE payment_status AS ENUM ('PENDING', 'PROCESSING', 'PAID', 'FAILED');

CREATE TABLE payments (
                          id BIGSERIAL PRIMARY KEY,
                          task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
                          contract_id BIGINT NOT NULL REFERENCES contracts(id) ON DELETE CASCADE,
                          contractor_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          amount NUMERIC(12, 2) NOT NULL,
                          status payment_status NOT NULL DEFAULT 'PENDING',
                          provider_tx_id VARCHAR(255),
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                          paid_at TIMESTAMP WITH TIME ZONE
);