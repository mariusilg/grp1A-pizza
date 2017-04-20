# Users schema
 
# --- !Ups
CREATE TABLE Categories (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    unit varchar(255) NOT NULL,
    visibility BIT DEFAULT FALSE
);

INSERT INTO Categories (
    name,
    unit,
    visibility
)   VALUES  ('Pizza', 'cm', TRUE),
    ('Drinks', 'ml', TRUE),
    ('Desserts', 'g', TRUE);

# --- !Downs
DROP TABLE Categories;