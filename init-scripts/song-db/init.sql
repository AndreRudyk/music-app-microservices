CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE IF NOT EXISTS song_metadata (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    artist VARCHAR(255),
    album VARCHAR(255),
    duration VARCHAR(255),
    release_year VARCHAR(255)
);
