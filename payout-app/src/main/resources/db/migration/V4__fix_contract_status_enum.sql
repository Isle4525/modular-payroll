ALTER TABLE contracts ALTER COLUMN status DROP DEFAULT;
ALTER TABLE contracts ALTER COLUMN status TYPE VARCHAR(20);
DROP TYPE contract_status;
CREATE TYPE contract_status AS ENUM ('DRAFT', 'SENT', 'SIGNED', 'ACTIVE', 'CLOSED', 'CANCELLED');
ALTER TABLE contracts ALTER COLUMN status TYPE contract_status USING status::contract_status;
ALTER TABLE contracts ALTER COLUMN status SET DEFAULT 'DRAFT';