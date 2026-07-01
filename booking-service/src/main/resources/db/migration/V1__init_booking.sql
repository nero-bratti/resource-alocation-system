-- Create minimal booking table used for dev
CREATE TABLE IF NOT EXISTS bookings (
  id SERIAL PRIMARY KEY,
  resource_id VARCHAR(128) NOT NULL,
  user_id VARCHAR(128) NOT NULL,
  start_ts TIMESTAMP WITHOUT TIME ZONE,
  end_ts TIMESTAMP WITHOUT TIME ZONE,
  created_at TIMESTAMP DEFAULT now()
);
