# Users schema
 
# --- !Ups
CREATE TABLE Categories (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    visibility BIT DEFAULT FALSE
);

INSERT INTO Categories (
    name,
    visibility
)   VALUES  ('Pizza', 1),
    ('Drinks', 1),
    ('Desserts', 1);

# --- !Downs
DROP TABLE Categories;