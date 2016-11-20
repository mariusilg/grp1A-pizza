# Users schema
 
# --- !Ups
CREATE TABLE Users (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    admin_flag BIT DEFAULT FALSE
);

CREATE TABLE Categories (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
);

CREATE TABLE Items (
    id serial PRIMARY KEY,
    cat_id serial NOT NULL,
    name varchar(255) NOT NULL,
    price Int NOT NULL
);

CREATE TABLE Orders (
    id serial PRIMARY KEY,
    cust_id serial NOT NULL,
    item_id Int NOT NULL,
    quantity Int NOT NULL,
    size Int NOT NULL,
    start_date DATETIME DEFAULT SYSDATE,
    costs INT NOT NULL
);

INSERT INTO Users (
    name,
    admin_flag
)   VALUES ('Padrone',1), ('Customer',0);

INSERT INTO Categories (
    name
)   VALUES  ('Pizza'),
    ('Drinks'),
    ('Desserts');

INSERT INTO Items (
    cat_id,
    name,
    price
)   VALUES  (1,'Hawaii', 21),
    (1,'Pizza Margerita', 20),
    (1,'Pizza Funghi', 22),
    (1,'Diavolo', 23),
    (2,'Cola', 150),
    (2,'Sprite', 150),
    (3,'Apple Pie', 300),
    (3,'Tiramisu', 370);

# --- !Downs
DROP TABLE Users;
DROP TABLE Categories;
DROP TABLE Items;
DROP TABLE Orders;
