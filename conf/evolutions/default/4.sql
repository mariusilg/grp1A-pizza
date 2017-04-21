# Users schema
 
# --- !Ups
CREATE TABLE Orders (
    id serial PRIMARY KEY,
    cust_id INT NOT NULL,
    state VARCHAR(20) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    distance INT NOT NULL,
    duration INT NOT NULL,
    costs INT NOT NULL
);

# --- !Downs
DROP TABLE Orders;
