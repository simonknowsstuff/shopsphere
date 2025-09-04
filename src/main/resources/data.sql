INSERT INTO users (first_name, last_name, email, password, role) VALUES
('Alice', 'Vendor', 'alice@vendor.com', 'password123', 'VENDOR'),
('Bob', 'Vendor', 'bob@vendor.com', 'password123', 'VENDOR'),
('Charlie', 'Customer', 'charlie@customer.com', 'password123', 'CUSTOMER');

INSERT INTO products (name, price, original_price, description, category, image, rating, review_count, in_stock, features, vendor_id) VALUES
('Laptop Pro', 1200, 1500, 'High-end laptop with 16GB RAM', 'Electronics', 'laptop.jpg', 4.5, 200, TRUE, '16GB RAM, 512GB SSD', 1),
('Wireless Headphones', 100, 150, 'Noise-cancelling headphones', 'Audio', 'headphones.jpg', 4.7, 500, TRUE, 'Bluetooth, 20h battery life', 1),
('Gaming Mouse', 50, 70, 'Ergonomic gaming mouse with RGB', 'Accessories', 'mouse.jpg', 4.6, 300, TRUE, 'Adjustable DPI, RGB lighting', 2),
                                                                                                                                          ('Mechanical Keyboard', 120, 160, 'Mechanical keyboard with blue switches', 'Accessories', 'keyboard.jpg', 4.8, 150, TRUE, 'RGB, hot-swappable keys', 2);

INSERT INTO cart (user_id, product_id, quantity)
VALUES (1, 1, 1),
       (1, 2, 2),
       (2, 2, 1);