# Users schema
 
# --- !Ups
CREATE TABLE Sizes (
    id serial PRIMARY KEY,
    cat_id INT NOT NULL,
    name varchar(255) NOT NULL,
    size INT NOT NULL
);

INSERT INTO Sizes (
    cat_id,
    name,
    size
)   VALUES  (1,'Small', 10),
    (1,'Medium', 20),
    (1, 'Large', 30),
    (2, 'klein', 500),
    (2, 'normal', 1000),
    (2, 'groß', 1500),
    (3, 'normal', 100),
    (3, 'groß', 200);

# --- !Downs
DROP TABLE Items;