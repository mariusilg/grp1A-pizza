# Users schema
 
# --- !Ups
CREATE TABLE order_extras(
    id serial,
    order_item_id Int,
    extra_id Int NOT NULL,
    extra_name varchar(255),
    quantity Int NOT NULL,
    costs INT NOT NULL,
    FOREIGN KEY(order_item_id) references order_items(id) ON DELETE CASCADE
);
# --- !Downs
DROP TABLE order_extras;
