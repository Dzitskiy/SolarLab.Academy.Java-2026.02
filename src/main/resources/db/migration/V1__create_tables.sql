CREATE TABLE client
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(255) DEFAULT 'name',
    email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE advertisement
(
    id               SERIAL PRIMARY KEY,
    name             VARCHAR(100) UNIQUE NOT NULL,
    category         VARCHAR             NOT NULL,
    subcategory      VARCHAR,
    cost             INTEGER             NOT NULL,
    description      VARCHAR(500),
    address          VARCHAR(500),
    create_date_time timestamp,
    client_id INT,
    CONSTRAINT fk_advertisements_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE ON UPDATE CASCADE
);
