CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       FIRST_NAME VARCHAR(255),
                       LAST_NAME VARCHAR(255),
                       EMAIL VARCHAR(255),
                       PASSWORD VARCHAR(255)
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
                          features VARCHAR(1000)
);