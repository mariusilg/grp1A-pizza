# Users schema
 
# --- !Ups
CREATE TABLE Users (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    admin_flag BIT DEFAULT FALSE
);

INSERT INTO Users (
    name,
    admin_flag
)   VALUES ('Padrone',1), ('Emil',0);

# --- !Downs
DROP TABLE Users;
