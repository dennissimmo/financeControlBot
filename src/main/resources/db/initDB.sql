DROP TABLE IF EXISTS person cascade ;
DROP TABLE IF EXISTS operation ;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS balance;
DROP TABLE IF EXISTS type_operation ;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS type_category;
DROP TABLE IF EXISTS source_info;
DROP SEQUENCE IF EXISTS global_seq cascade ;
CREATE SEQUENCE global_seq START WITH 0;
DROP SEQUENCE IF EXISTS hibernate_sequence cascade ;
CREATE SEQUENCE hibernate_sequence START 1;
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
CREATE TABLE users
(
    id            INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    chat_id       INTEGER UNIQUE NOT NULL,
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    username      VARCHAR(50),
    language_code VARCHAR(20),
    bot_state     INTEGER,
    balance_id INTEGER REFERENCES balance(id)
);


CREATE TABLE type_operation
(
    id INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    name VARCHAR UNIQUE
);
CREATE TABLE type_category
(
    id INTEGER PRIMARY KEY DEFAULT nextVal('global_seq'),
    name_type VARCHAR (100)
);
CREATE TABLE category
(
    id INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    name VARCHAR (100) UNIQUE NOT NULL,
    type_category_id INTEGER REFERENCES type_category(id),
    locale VARCHAR (100)
);

CREATE TABLE operation
(
    id INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    amount DOUBLE PRECISION,
    note VARCHAR (80),
    category_id INTEGER REFERENCES category(id),
    is_regular BOOLEAN,
    create_at timestamp,
    raw_text VARCHAR (50) NOT NULL,
    type_operation_id INTEGER REFERENCES type_operation(id),
    user_id INTEGER REFERENCES users(id),
    source_id INTEGER REFERENCES source_info(id),
    current_balance DOUBLE PRECISION
);
