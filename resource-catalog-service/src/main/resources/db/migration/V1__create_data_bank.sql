-- Create data_bank table to store arbitrary data entries for the system
CREATE TABLE IF NOT EXISTS data_bank (
    id BIGSERIAL PRIMARY KEY,
    entry_key TEXT NOT NULL UNIQUE,
    payload JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_data_bank_entry_key ON data_bank(entry_key);

-- trigger to update updated_at on row modification
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = now();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS set_timestamp ON data_bank;
CREATE TRIGGER set_timestamp
BEFORE UPDATE ON data_bank
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();
