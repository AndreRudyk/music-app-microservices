CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE IF NOT EXISTS storage (
    id INT PRIMARY KEY,
    storage_type VARCHAR(55),
    bucket VARCHAR(55),
    path VARCHAR(255)
);

INSERT INTO storage (id, storage_type, bucket, path) VALUES
(1, 'STAGING', 'staging-bucket', 'http://localstack:4566/staging-bucket'),
(2, 'PERMANENT', 'permanent-bucket', 'http://localstack:4566/permanent-bucket')
ON CONFLICT (id) DO NOTHING;
