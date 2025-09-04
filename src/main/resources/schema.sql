CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       email VARCHAR(255) UNIQUE,
                       password VARCHAR(255),
                       role VARCHAR(50) NOT NULL -- "CUSTOMER" or "VENDOR"
);

CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255),
                          price DOUBLE,
                          original_price DOUBLE,
                          description VARCHAR(500),
                          category VARCHAR(255),
                          image VARCHAR(500),
                          rating DOUBLE,
                          review_count INT,
                          in_stock BOOLEAN,
                          features VARCHAR(1000),
                          vendor_id BIGINT, -- foreign key to users (vendors)
                          CONSTRAINT fk_vendor FOREIGN KEY (vendor_id) REFERENCES users(id)
);


CREATE TABLE cart (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       product_id BIGINT NOT NULL,
                       quantity INT DEFAULT 1,

                       CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id),
                       CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES products(id)
);
