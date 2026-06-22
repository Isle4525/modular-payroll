CREATE TABLE contract_templates (
                                    id BIGSERIAL PRIMARY KEY,
                                    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                                    name VARCHAR(255) NOT NULL,
                                    body_template TEXT NOT NULL,
                                    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TYPE contract_status AS ENUM ('draft', 'sent', 'signed', 'active', 'closed', 'cancelled');

CREATE TABLE contracts (
                           id BIGSERIAL PRIMARY KEY,
                           company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                           template_id BIGINT REFERENCES contract_templates(id) ON DELETE SET NULL,
                           contractor_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                           contract_number VARCHAR(50) NOT NULL UNIQUE,
                           subject TEXT NOT NULL,
                           amount NUMERIC(12, 2) NOT NULL,
                           status contract_status NOT NULL DEFAULT 'draft',
                           signed_at TIMESTAMP WITH TIME ZONE,
                           file_url VARCHAR(500),
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);