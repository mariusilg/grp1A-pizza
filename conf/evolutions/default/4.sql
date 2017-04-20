# Users schema
 
# --- !Ups
CREATE TABLE Orders (
    id serial PRIMARY KEY,
    cust_id INT NOT NULL,
    state VARCHAR(20) NOT NULL,
    order_date DATE DEFAULT CURRENT_DATE,
    distance INT NOT NULL,
    duration INT NOT NULL,
    costs INT NOT NULL
);

# --- !Downs
DROP TABLE Orders;
