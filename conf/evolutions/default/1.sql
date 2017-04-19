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
    admin_flag BIT DEFAULT FALSE,
    password varchar(255) NOT NULL,
    distance INT NOT NULL,
    active_flag BIT DEFAULT TRUE,
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
)   VALUES ('Padrone', 'Padrone', 'Mustermann', 'Lothstraße. 64', '80335', 'München', '+4915112345123', 'padrone@test.com', 'adminPw', 1, 0, 1),
('Emil', 'Emil', 'Mustermann', 'Lothstraße. 1', '80335', 'München', '+4915112345123', 'emil@test.com','custPw', 0, 1, 1);

# --- !Downs
DROP TABLE Users;
