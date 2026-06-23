CREATE TYPE submission_status AS ENUM ('PENDING_REVIEW', 'APPROVED', 'REJECTED');

CREATE TABLE submissions (
                             id BIGSERIAL PRIMARY KEY,
                             task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
                             contractor_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                             content TEXT NOT NULL,
                             attachments JSONB DEFAULT '[]',
                             status submission_status NOT NULL DEFAULT 'PENDING_REVIEW',
                             submitted_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);