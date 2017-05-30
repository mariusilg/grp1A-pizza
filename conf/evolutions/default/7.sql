# Users schema
 
# --- !Ups
CREATE TABLE Extras (
    id serial PRIMARY KEY,
    cat_id INT NOT NULL,
    name varchar(255) NOT NULL,
    price Int NOT NULL,
    FOREIGN KEY(cat_id) references Categories(id)
);

INSERT INTO Extras (
    cat_id,
    name,
    price
)   VALUES  (1, 'Käse', 50),
    (1, 'Soße', 50),
    (1, 'Knoblauch', 50),
    (1, 'Cheesy Crust', 50),
    (1, 'Salami', 50),
    (1, 'Zwiebeln', 50),
    (1, 'Champignons', 50),
    (1, 'Ei', 50);

# --- !Downs
DROP TABLE Extras;