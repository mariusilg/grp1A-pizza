# Users schema
 
# --- !Ups
CREATE TABLE Items (
    id serial PRIMARY KEY,
    cat_id INT NOT NULL,
    name varchar(255) NOT NULL,
    price Int NOT NULL,
    extra_flag BOOLEAN DEFAULT FALSE,
    prep_duration INT DEFAULT 0,
    visibility BOOLEAN DEFAULT TRUE,
    FOREIGN KEY(cat_id) references Categories(id)
);

INSERT INTO Items (
    cat_id,
    name,
    extra_flag,
    price,
    prep_duration
)   VALUES  (1,'Hawaii', TRUE, 21, 10),
    (1,'Pizza Margerita', TRUE, 20, 10),
    (1,'Pizza Funghi', TRUE, 22, 10),
    (1,'Pizza Capriciosa', TRUE, 50, 10),
    (1,'Diavolo', TRUE, 23, 0),
    (2,'Cola', FALSE, 2, 0),
    (2,'Sprite', FALSE, 2, 0),
    (3,'Apple Pie', FALSE, 5, 0),
    (3,'Tiramisu', FALSE, 5, 0);

# --- !Downs
DROP TABLE Items;