CREATE TYPE task_status AS ENUM (
    'CREATED',
    'ACCEPTED',
    'IN_PROGRESS',
    'SUBMITTED',
    'REVIEW',
    'APPROVED',
    'REJECTED',
    'COMPLETED'
    );

CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,
                       company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                       created_by BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       assigned_to BIGINT REFERENCES users(id) ON DELETE SET NULL,
                       contract_id BIGINT UNIQUE REFERENCES contracts(id) ON DELETE SET NULL,
                       title VARCHAR(255) NOT NULL,
                       description TEXT NOT NULL,
                       budget NUMERIC(12, 2) NOT NULL,
                       deadline TIMESTAMP WITH TIME ZONE,
                       status task_status NOT NULL DEFAULT 'CREATED',
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);