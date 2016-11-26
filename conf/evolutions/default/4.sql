# Users schema
 
# --- !Ups
CREATE TABLE Orders (
    id serial PRIMARY KEY,
    cust_id INT NOT NULL,
    order_date DATETIME DEFAULT SYSDATE,
    costs INT NOT NULL
);

# --- !Downs
DROP TABLE Orders;
