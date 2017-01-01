# Users schema
 
# --- !Ups
CREATE TABLE Categories (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
);

INSERT INTO Categories (
    name
)   VALUES  ('Pizza'),
    ('Drinks'),
    ('Desserts');

# --- !Downs
DROP TABLE Categories;