# Users schema
 
# --- !Ups
CREATE TABLE order_extras(
    order_item_id serial NOT NULL,
    extra_id Int NOT NULL,
    extra_name varchar(255),
    quantity Int NOT NULL,
    costs INT NOT NULL
);

# --- !Downs
DROP TABLE order_extras;
