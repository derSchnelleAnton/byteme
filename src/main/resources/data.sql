-- addresses
INSERT INTO address (id, postal_code, street, house_number, phone, created_at, updated_at)
VALUES
(1, '6020', 'Sillgasse', 5, '+43-650-1234567', NOW(), NOW()),
(2, '6020', 'Innrain', 25, '+43-650-2234567', NOW(), NOW()),
(3, '6020', 'Museumstrasse', 10, '+43-650-3234567', NOW(), NOW()),
(4, '6020', 'Technikerstrasse', 42, '+43-650-4234567', NOW(), NOW()),
(5, '6020', 'Kaiserjaegerstrasse', 7, '+43-650-5234567', NOW(), NOW());

-- users
INSERT INTO users (id, user_name, email, password, first_name, last_name, role, created_at, updated_at) VALUES
(1, 'adrian', 'adrian@mci.at', 'pass123', 'Adrian', 'Petre', 'manager', NOW(), NOW()),
(2, 'anton', 'anton@mci.at', 'pass123', 'Anton', 'Woerndle', 'client', NOW(), NOW()),
(3, 'marc', 'marc@mci.at', 'pass123', 'Marc', 'Boehme', 'client', NOW(), NOW()),
(4, 'patricia', 'patricia@mci.at', 'pass123', 'Patricia', 'Fueruter', 'client', NOW(), NOW()),
(5, 'tinsae', 'tinsae@mci.at', 'pass123', 'Tinsae', 'Ghilay', 'client', NOW(), NOW());


-- menu items
INSERT INTO menu_item (id, name, description, price, discount, is_available, created_at, updated_at)
VALUES
(1, 'Margherita Pizza', 'Tomato sauce, mozzarella, basil', 8.50, 0.00, true, NOW(), NOW()),
(2, 'Pepperoni Pizza', 'Spicy salami, tomato sauce, cheese', 9.50, 0.00, true, NOW(), NOW()),
(3, 'Veggie Pizza', 'Grilled vegetables, pesto, cheese', 9.00, 0.00, true, NOW(), NOW()),
(4, 'BBQ Chicken Pizza', 'Chicken, BBQ sauce, onion, cheese', 10.00, 0.00, true, NOW(), NOW()),
(5, 'Hawaiian Pizza', 'Ham, pineapple, tomato sauce, cheese', 9.50, 0.00, true, NOW(), NOW()),
(6, 'Pasta Carbonara', 'Cream, bacon, egg, parmesan', 8.00, 0.00, true, NOW(), NOW()),
(7, 'Lasagna', 'Beef ragu, pasta layers, bechamel', 9.00, 0.00, true, NOW(), NOW()),
(8, 'Caesar Salad', 'Lettuce, chicken, croutons, dressing', 7.00, 0.00, true, NOW(), NOW()),
(9, 'Tiramisu', 'Coffee-soaked sponge, mascarpone cream', 4.50, 0.00, true, NOW(), NOW()),
(10, 'Garlic Bread', 'Toasted bread, garlic butter', 3.50, 0.00, true, NOW(), NOW());

-- orders
INSERT INTO orders (id, admin_id, client_id, status , order_date, delivery_date)
VALUES
    (1, 1, 5 ,'NEW', NOW(), NOW()),
    (2, 1, 5 ,'NEW', NOW(), NOW()),
    (3, 1, 5 ,'NEW', NOW(), NOW()),
    (4, 1, 5 ,'NEW', NOW(), NOW()),
    (5, 1, 5 ,'NEW', NOW(), NOW());