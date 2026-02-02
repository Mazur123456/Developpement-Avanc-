-- Script d'initialisation de la base de données MasterAnnonce
-- Base de données: PostgreSQL

CREATE TABLE IF NOT EXISTS annonce (
    id SERIAL PRIMARY KEY,
    title VARCHAR(64),
    description VARCHAR(256),
    adress VARCHAR(64),
    mail VARCHAR(64),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
