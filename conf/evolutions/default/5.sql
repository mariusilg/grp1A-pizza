# Users schema
 
# --- !Ups
CREATE TABLE order_items(
    id serial PRIMARY KEY,
    order_id INT NOT NULL,
    item_id Int NOT NULL,
    item_name varchar(255),
    quantity Int NOT NULL,
    size Int NOT NULL,
    unit varchar(255) NOT NULL,
    costs INT NOT NULL
);

# --- !Downs
DROP TABLE order_items;
