# Users schema
 
# --- !Ups
CREATE TABLE Users (
    id serial PRIMARY KEY,
    username varchar(255) NOT NULL UNIQUE,
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    street varchar(255) NOT NULL,
    zip varchar(255) NOT NULL,
    city varchar(255) NOT NULL,
    phone varchar(255) NOT NULL,
    email varchar(255) NOT NULL UNIQUE,
    admin_flag BOOLEAN DEFAULT FALSE,
    password varchar(255) NOT NULL,
    distance INT NOT NULL,
    active_flag BOOLEAN DEFAULT TRUE,
    token varchar(255)
);

INSERT INTO Users (
    username,
    first_name,
    last_name,
    street,
    zip,
    city,
    phone,
    email,
    password,
    admin_flag,
    distance,
    active_flag
)   VALUES ('padrone', 'Padrone', 'Mustermann', 'Lothstraße. 64', '80335', 'München', '+4915112345123', 'padrone@test.com', '123', TRUE, 0, TRUE),
('emil', 'Emil', 'Mustermann', 'Lothstraße. 1', '80335', 'München', '+4915112345123', 'emil@test.com','123', FALSE, 1, TRUE);

# --- !Downs
DROP TABLE Users;
