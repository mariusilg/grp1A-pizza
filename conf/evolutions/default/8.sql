# Users schema
 
# --- !Ups
CREATE TABLE Sizes (
    id serial PRIMARY KEY,
    cat_id INT NOT NULL,
    name varchar(255) NOT NULL,
    size INT NOT NULL,
    FOREIGN KEY(cat_id) references Categories(id)
);

INSERT INTO Sizes (
    cat_id,
    name,
    size
)   VALUES  (1,'Small', 10),
    (1,'Medium', 20),
    (1, 'Large', 30),
    (2, 'klein', 250),
    (2, 'normal', 500),
    (2, 'groß', 750),
    (3, 'klein', 50),
    (3, 'normal', 100);

# --- !Downs
DROP TABLE Items;