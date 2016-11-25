# Users schema
 
# --- !Ups
CREATE TABLE Items (
    id serial PRIMARY KEY,
    cat_id serial NOT NULL,
    name varchar(255) NOT NULL,
    price Int NOT NULL
);

INSERT INTO Items (
    cat_id,
    name,
    price
)   VALUES  (1,'Hawaii', 21),
    (1,'Pizza Margerita', 20),
    (1,'Pizza Funghi', 22),
    (1,'Pizza Capriciosa', 50),
    (1,'Diavolo', 23),
    (2,'Cola', 150),
    (2,'Sprite', 150),
    (3,'Apple Pie', 300),
    (3,'Tiramisu', 370);

# --- !Downs
DROP TABLE Items;