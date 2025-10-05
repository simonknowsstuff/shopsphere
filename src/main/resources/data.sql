-- Users
INSERT INTO users (first_name, last_name, email, password, role) VALUES
('John', 'Doe', 'john@example.com', '$2a$10$hashedpassword1', 'CUSTOMER,VENDOR'),
('Jack', 'Doe', 'jackdoe@email.com', '$2a$10$cpERZJnIlPZRoAVnEXy.geRD9bLlBGE6yRyzSE16I479EMe726pOu', 'CUSTOMER,VENDOR,ADMIN'),
('Vendor', 'One', 'vendor1@example.com', '$2a$10$hashedpassword3', 'CUSTOMER,VENDOR');

-- Products
INSERT INTO products (name, price, original_price, description, category, image, rating, review_count, in_stock, features, vendor_id) VALUES
('Smartphone X10', 699.99, 899.99, 'Latest smartphone with high-end features', 'Electronics', 'image1.jpg', 4.5, 120, TRUE, '64GB,6GB RAM,OLED Display', 3),
('Laptop Pro 15', 1299.00, 1499.00, 'High-performance laptop for professionals', 'Electronics', 'image2.jpg', 4.7, 85, TRUE, 'Intel i7,16GB RAM,512GB SSD', 3),
('Coffee Maker 3000', 89.99, 129.99, 'Brew coffee in minutes', 'Home Appliances', 'image4.jpg', 4.0, 65, TRUE, 'Programmable,12-cup capacity', 1),
('Gaming Chair Elite', 249.99, 299.99, 'Ergonomic chair for gamers', 'Furniture', 'image5.jpg', 4.6, 50, TRUE, 'Adjustable height,Reclining', 2),
('Desk Lamp LED', 29.99, 39.99, 'Adjustable desk lamp with USB charging', 'Home Appliances', 'image6.jpg', 4.2, 34, TRUE, 'LED,USB Charging,Touch Control', 1),
('Smartwatch Series 5', 349.99, 399.99, 'Fitness tracking and notifications', 'Electronics', 'image7.jpg', 4.4, 78, TRUE, 'Heart rate monitor,Waterproof', 3),
('Yoga Mat Pro', 59.99, 79.99, 'High-quality non-slip yoga mat', 'Fitness', 'image8.jpg', 4.5, 42, TRUE, 'Non-slip,6mm thickness', 1),
('Blender Ultra', 129.99, 159.99, 'Powerful blender for smoothies', 'Home Appliances', 'image9.jpg', 4.3, 56, TRUE, '1200W,Multiple Speeds', 2),
('Electric Kettle 1.7L', 39.99, 59.99, 'Fast-boiling electric kettle', 'Home Appliances', 'image10.jpg', 4.1, 37, TRUE, 'Auto shut-off,1.7L capacity', 2);
-- Cart
INSERT INTO cart (user_id, product_id, quantity) VALUES
(1, 1, 1),
(1, 2, 2);

-- Orders
INSERT INTO orders (user_id, total_amount, status) VALUES
(1, 1899.98, 'PENDING');

-- Order Items
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 699.99),
(1, 2, 2, 1499.99);

-- Reviews
INSERT INTO reviews (user_id, product_id, rating, review) VALUES
(1, 1, 4.5, 'Great product!'),
(1, 2, 4.8, 'Great laptop!'),
(1, 3, 4.3, 'Great earbuds!');