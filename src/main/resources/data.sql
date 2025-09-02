INSERT INTO users (first_name, last_name, email, password)
VALUES ('John', 'Doe', 'john@example.com', 'password123'),
       ('Jane', 'Smith', 'jane@example.com', 'password456');

INSERT INTO products (name, price, original_price, description, category, image, rating, review_count, in_stock, features)
VALUES ('Laptop', 750.00, 1000.00, 'Gaming laptop', 'Electronics', 'laptop.png', 4.5, 120, TRUE, '16GB RAM, 512GB SSD'),
       ('Headphones', 50.00, 80.00, 'Wireless headphones', 'Electronics', 'headphones.png', 4.2, 85, TRUE, 'Noise cancelling');

INSERT INTO cart (user_id, product_id, quantity)
VALUES (1, 1, 1),  -- John has 1 Laptop
       (1, 2, 2),  -- John has 2 Headphones
       (2, 2, 1);  -- Jane has 1 Headphone
