-- addresses
INSERT INTO address (postal_code, street, house_number, phone, created_at, updated_at)
VALUES
('6020', 'Sillgasse', 5, '+43-650-1234567', NOW(), NOW()),
('6020', 'Innrain', 25, '+43-650-2234567', NOW(), NOW()),
('6020', 'Museumstrasse', 10, '+43-650-3234567', NOW(), NOW()),
('6020', 'Technikerstrasse', 42, '+43-650-4234567', NOW(), NOW()),
('6020', 'Kaiserjaegerstrasse', 7, '+43-650-5234567', NOW(), NOW());

-- users
INSERT INTO users (user_name, email, password, first_name, last_name, role, created_at, updated_at)
VALUES
('adrian', 'adrian@mci.at', 'pass123', 'Adrian', 'Petre', 'manager', NOW(), NOW()),
('anton', 'anton@mci.at', 'pass123', 'Anton', 'Woerndle', 'client', NOW(), NOW()),
('marc', 'marc@mci.at', 'pass123', 'Marc', 'Boehme', 'client', NOW(), NOW()),
('patricia', 'patricia@mci.at', 'pass123', 'Patricia', 'Fueruter', 'client', NOW(), NOW()),
('tinsae', 'tinsae@mci.at', 'pass123', 'Tinsae', 'Ghilay', 'client', NOW(), NOW());

-- menu items
INSERT INTO menu_item (name, description, price, discount, is_available, created_at, updated_at)
VALUES

('Margherita Pizza', 'Tomato sauce, mozzarella, basil', 8.50, 0.00, true, NOW(), NOW()),
('Pepperoni Pizza', 'Spicy salami, tomato sauce, cheese', 9.50, 0.00, true, NOW(), NOW()),
('Veggie Pizza', 'Grilled vegetables, pesto, cheese', 9.00, 0.00, true, NOW(), NOW()),
('BBQ Chicken Pizza', 'Chicken, BBQ sauce, onion, cheese', 10.00, 0.00, true, NOW(), NOW()),
('Hawaiian Pizza', 'Ham, pineapple, tomato sauce, cheese', 9.50, 0.00, true, NOW(), NOW()),
('Pasta Carbonara', 'Cream, bacon, egg, parmesan', 8.00, 0.00, true, NOW(), NOW()),
('Lasagna', 'Beef ragu, pasta layers, bechamel', 9.00, 0.00, true, NOW(), NOW()),
('Caesar Salad', 'Lettuce, chicken, croutons, dressing', 7.00, 0.00, true, NOW(), NOW()),
('Tiramisu', 'Coffee-soaked sponge, mascarpone cream', 4.50, 0.00, true, NOW(), NOW()),
('Garlic Bread', 'Toasted bread, garlic butter', 3.50, 0.00, true, NOW(), NOW());



-- orders
INSERT INTO orders (id, admin_id, client_id, status , order_date, delivery_date)
VALUES
(1, 1, 5 ,'CANCELLED', NOW(), NOW()),
(2, 1, 5 ,'PENDING', NOW(), NOW()),
(3, 1, 5 ,'CONFIRMED', NOW(), NOW()),
(4, 1, 5 ,'IN_PROGRESS', NOW(), NOW()),
(5, 1, 5 ,'DELIVERED', NOW(), NOW());


-- Order-Menu junction table inserts
INSERT INTO order_menu (order_id, menu_item_id) VALUES
(1, 1),
(1, 2),
(2, 3),
(2, 4),
(3, 5),
(3, 6),
(3, 7),
(4, 1),
(4, 8),
(4, 9),
(5, 10);

