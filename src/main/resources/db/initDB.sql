DROP TABLE IF EXISTS person cascade ;
DROP TABLE IF EXISTS operation;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS balance;
DROP TABLE IF EXISTS type_operation;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS source_info;
DROP SEQUENCE IF EXISTS global_seq;
CREATE SEQUENCE global_seq START WITH 100000;
CREATE TABLE users
(
    id            INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    chat_id       INTEGER UNIQUE,
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    username      VARCHAR(50),
    language_code VARCHAR(20),
    bot_state     VARCHAR(20)
);
CREATE TABLE person
(
    id INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    name VARCHAR (50) NOT NULL,
    user_id INTEGER REFERENCES users(id)
);
CREATE TABLE type_operation
(
    id INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    name VARCHAR UNIQUE
);
CREATE TABLE category
(
    id INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    name VARCHAR (100) UNIQUE NOT NULL
);
CREATE TABLE source_info
(
    id INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    type_source VARCHAR (100) UNIQUE NOT NULL

);
CREATE TABLE balance
(
    id INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    source_info_id INTEGER REFERENCES source_info(id),
    amount DOUBLE PRECISION
);
CREATE TABLE operation
(
    id INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    amount DOUBLE PRECISION,
    note VARCHAR (80),
    id_category INTEGER REFERENCES category(id),
    is_regular BOOLEAN,
    create_at date NOT NULL,
    raw_text VARCHAR (50) NOT NULL,
    type_operation_id INTEGER REFERENCES type_operation(id),
    person_id INTEGER REFERENCES person(id),
    source_id INTEGER REFERENCES source_info(id)
);
