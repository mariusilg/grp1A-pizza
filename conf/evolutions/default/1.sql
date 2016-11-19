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
);

CREATE TABLE Orders (
    order_id serial PRIMARY KEY,
    cust_id serial NOT NULL,
    item_id Int NOT NULL,
    quantity Int NOT NULL,
    start_date DATE DEFAULT SYSDATE,
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
    name
)   VALUES  (1,'Hawaii'),
    (1,'Diavolo'),
    (2,'Cola'),
    (3,'Apple Pie');

# --- !Downs
DROP TABLE Users;
DROP TABLE Categories;
DROP TABLE Items;
