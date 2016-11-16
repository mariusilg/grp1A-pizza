# Users schema
 
# --- !Ups
 
CREATE TABLE Users (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    admin_flag BIT
);
 
# --- !Downs
 
DROP TABLE User;
