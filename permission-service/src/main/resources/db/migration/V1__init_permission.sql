-- Minimal permissions table
CREATE TABLE IF NOT EXISTS permissions (
  id SERIAL PRIMARY KEY,
  subject VARCHAR(128) NOT NULL,
  resource VARCHAR(128) NOT NULL,
  action VARCHAR(64) NOT NULL,
  granted BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT now()
);
