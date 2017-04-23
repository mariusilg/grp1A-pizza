# Users schema
 
# --- !Ups
CREATE TABLE Users (
    id serial PRIMARY KEY,
    gender BOOLEAN NOT NULL,
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
    gender,
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
)   VALUES (TRUE, 'Padrone', 'Padrone', 'Mustermann', 'Lothstraße. 64', '80335', 'München', '+4915112345123', 'n.engelbrecht1@hm.edu', 'adminPw', TRUE, 0, TRUE),
(TRUE, 'Emil', 'Emil', 'Mustermann', 'Lothstraße. 1', '80335', 'München', '+4915112345123', 'nils.engelbrecht@gmx.net','custPw', FALSE, 1, TRUE);

# --- !Downs
DROP TABLE Users;
