CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    balance NUMERIC(10, 2)
);

INSERT INTO accounts (balance) VALUES (50);