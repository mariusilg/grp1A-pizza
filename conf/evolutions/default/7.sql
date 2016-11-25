# Users schema
 
# --- !Ups
CREATE TABLE Extras (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    price Int NOT NULL
);

INSERT INTO Extras (
    name,
    price
)   VALUES  ('Käse', 50),
    ('Knoblauch', 50),
    ('Cheesy Crust', 50),
    ('Salami', 50),
    ('Zwiebeln', 50),
    ('Champignons', 50),
    ('Ei', 50);

# --- !Downs
DROP TABLE Extras;