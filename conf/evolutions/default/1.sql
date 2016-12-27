# Users schema
 
# --- !Ups
CREATE TABLE Users (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    admin_flag BIT DEFAULT FALSE,
    password varchar(255) NOT NULL
);

INSERT INTO Users (
    name,
    password,
    admin_flag
)   VALUES ('Padrone', 'adminPw', 1), ('Emil', 'custPw', 0);

# --- !Downs
DROP TABLE Users;
