-- ─────────────────────────────────────────────────────────────
-- ADDRESS SEED
-- ─────────────────────────────────────────────────────────────
INSERT INTO address (postal_code, street, house_number, phone, created_at, updated_at) VALUES
('6020','Sillgasse'        ,  5,'+43-650-1234567',NOW(),NOW()),
('6020','Innrain'          , 25,'+43-650-2234567',NOW(),NOW()),
('6020','Museumstrasse'    , 10,'+43-650-3234567',NOW(),NOW()),
('6020','Technikerstrasse' , 42,'+43-650-4234567',NOW(),NOW()),
('6020','Kaiserjaegerstrasse',7,'+43-650-5234567',NOW(),NOW());

-- ─────────────────────────────────────────────────────────────
-- USER SEED
-- ─────────────────────────────────────────────────────────────
INSERT INTO users (user_name,email,password,first_name,last_name,role,created_at,updated_at) VALUES
('admin'  ,'adri@mci.at'  ,'$2a$12$P12N6KrvhcDCASyQXg78Zu9iIJzpBnpAZ7NxX4fyLr44rK0xA5l4u','Adrian'  ,'Petre'   ,'ADMIN',NOW(),NOW()),
('adrian'  ,'adrian@mci.at'  ,'$2a$12$P12N6KrvhcDCASyQXg78Zu9iIJzpBnpAZ7NxX4fyLr44rK0xA5l4u','Adrian'  ,'Petre'   ,'manager',NOW(),NOW()),
('anton'   ,'anton@mci.at'   ,'pass123','Anton'   ,'Woerndle','client' ,NOW(),NOW()),
('marc'    ,'marc@mci.at'    ,'pass123','Marc'    ,'Boehme'  ,'client' ,NOW(),NOW()),
('patricia','patricia@mci.at','pass123','Patricia','Fueruter','client' ,NOW(),NOW()),
('tinsae'  ,'tinsae@mci.at'  ,'$2a$12$P12N6KrvhcDCASyQXg78Zu9iIJzpBnpAZ7NxX4fyLr44rK0xA5l4u','Tinsae'  ,'Ghilay'  ,'client' ,NOW(),NOW());

-- ─────────────────────────────────────────────────────────────
-- MENU‐ITEM SEED (10 items)
-- ─────────────────────────────────────────────────────────────
INSERT INTO menu_item (name,description,price,discount,is_available,created_at,updated_at) VALUES
('Margherita Pizza' ,'Tomato sauce, mozzarella, basil'          , 8.50,0.00,true,NOW(),NOW()),
('Pepperoni Pizza'  ,'Spicy salami, tomato sauce, cheese'       , 9.50,0.00,true,NOW(),NOW()),
('Veggie Pizza'     ,'Grilled vegetables, pesto, cheese'        , 9.00,0.00,true,NOW(),NOW()),
('BBQ Chicken Pizza','Chicken, BBQ sauce, onion, cheese'        ,10.00,0.00,true,NOW(),NOW()),
('Hawaiian Pizza'   ,'Ham, pineapple, tomato sauce, cheese'     , 9.50,0.00,true,NOW(),NOW()),
('Pasta Carbonara'  ,'Cream, bacon, egg, parmesan'              , 8.00,0.00,true,NOW(),NOW()),
('Lasagna'          ,'Beef ragu, pasta layers, bechamel'        , 9.00,0.00,true,NOW(),NOW()),
('Caesar Salad'     ,'Lettuce, chicken, croutons, dressing'     , 7.00,0.00,true,NOW(),NOW()),
('Tiramisu'         ,'Coffee-soaked sponge, mascarpone cream'   , 4.50,0.00,true,NOW(),NOW()),
('Garlic Bread'     ,'Toasted bread, garlic butter'             , 3.50,0.00,true,NOW(),NOW());

-- ─────────────────────────────────────────────────────────────
-- INITIAL 5 ORDERS (IDs 1-5) + LINKS
-- ─────────────────────────────────────────────────────────────
/*INSERT INTO orders (id,admin_id,client_id,status,order_date,delivery_date) VALUES
(1,1,5,'CANCELLED' ,NOW(),NOW()),
(2,1,5,'PENDING'   ,NOW(),NOW()),
(3,1,5,'CONFIRMED' ,NOW(),NOW()),
(4,1,5,'IN_PROGRESS',NOW(),NOW()),
(5,1,5,'DELIVERED' ,NOW(),NOW());

INSERT INTO order_menu (order_id,menu_item_id) VALUES
(1,1),(1,4),
(2,2),(2,3),
(3,5),(3,6),
(4,7),(4,8),
(5,9),(5,10);
*/
-- ─────────────────────────────────────────────────────────────
-- ADDITIONAL 45 ORDERS (IDs 6-50) + LINKS (2-5 items each)
-- ─────────────────────────────────────────────────────────────
INSERT INTO orders (id,admin_id,client_id,status,order_date,delivery_date) VALUES
( 6,1,2,'DELIVERED'  ,DATEADD('DAY',  -5,CURRENT_TIMESTAMP),DATEADD('DAY',  -5,CURRENT_TIMESTAMP)),
( 7,1,3,'CONFIRMED'  ,DATEADD('DAY', -12,CURRENT_TIMESTAMP),DATEADD('DAY', -12,CURRENT_TIMESTAMP)),
( 8,1,4,'IN_PROGRESS',DATEADD('DAY',  -2,CURRENT_TIMESTAMP),DATEADD('DAY',  -2,CURRENT_TIMESTAMP)),
( 9,1,5,'PENDING'    ,DATEADD('DAY', -30,CURRENT_TIMESTAMP),DATEADD('DAY', -30,CURRENT_TIMESTAMP)),
(10,1,2,'CANCELLED'  ,DATEADD('DAY', -18,CURRENT_TIMESTAMP),DATEADD('DAY', -18,CURRENT_TIMESTAMP)),
(11,1,3,'DELIVERED'  ,DATEADD('DAY', -42,CURRENT_TIMESTAMP),DATEADD('DAY', -42,CURRENT_TIMESTAMP)),
(12,1,4,'CONFIRMED'  ,DATEADD('DAY',  -7,CURRENT_TIMESTAMP),DATEADD('DAY',  -7,CURRENT_TIMESTAMP)),
(13,1,5,'IN_PROGRESS',DATEADD('DAY',  -1,CURRENT_TIMESTAMP),DATEADD('DAY',  -1,CURRENT_TIMESTAMP)),
(14,1,2,'PENDING'    ,DATEADD('DAY', -60,CURRENT_TIMESTAMP),DATEADD('DAY', -60,CURRENT_TIMESTAMP)),
(15,1,3,'DELIVERED'  ,DATEADD('DAY', -15,CURRENT_TIMESTAMP),DATEADD('DAY', -15,CURRENT_TIMESTAMP)),
(16,1,4,'CONFIRMED'  ,DATEADD('DAY', -80,CURRENT_TIMESTAMP),DATEADD('DAY', -80,CURRENT_TIMESTAMP)),
(17,1,5,'IN_PROGRESS',DATEADD('DAY', -25,CURRENT_TIMESTAMP),DATEADD('DAY', -25,CURRENT_TIMESTAMP)),
(18,1,2,'DELIVERED'  ,DATEADD('DAY',-100,CURRENT_TIMESTAMP),DATEADD('DAY',-100,CURRENT_TIMESTAMP)),
(19,1,3,'CONFIRMED'  ,DATEADD('DAY',  -3,CURRENT_TIMESTAMP),DATEADD('DAY',  -3,CURRENT_TIMESTAMP)),
(20,1,4,'PENDING'    ,DATEADD('DAY', -40,CURRENT_TIMESTAMP),DATEADD('DAY', -40,CURRENT_TIMESTAMP)),
(21,1,5,'CANCELLED'  ,DATEADD('DAY', -90,CURRENT_TIMESTAMP),DATEADD('DAY', -90,CURRENT_TIMESTAMP)),
(22,1,2,'DELIVERED'  ,DATEADD('DAY', -55,CURRENT_TIMESTAMP),DATEADD('DAY', -55,CURRENT_TIMESTAMP)),
(23,1,3,'IN_PROGRESS',DATEADD('DAY', -12,CURRENT_TIMESTAMP),DATEADD('DAY', -12,CURRENT_TIMESTAMP)),
(24,1,4,'CONFIRMED'  ,DATEADD('DAY',  -6,CURRENT_TIMESTAMP),DATEADD('DAY',  -6,CURRENT_TIMESTAMP)),
(25,1,5,'PENDING'    ,DATEADD('DAY', -70,CURRENT_TIMESTAMP),DATEADD('DAY', -70,CURRENT_TIMESTAMP)),
(26,1,2,'DELIVERED'  ,DATEADD('DAY',-110,CURRENT_TIMESTAMP),DATEADD('DAY',-110,CURRENT_TIMESTAMP)),
(27,1,3,'CONFIRMED'  ,DATEADD('DAY', -20,CURRENT_TIMESTAMP),DATEADD('DAY', -20,CURRENT_TIMESTAMP)),
(28,1,4,'IN_PROGRESS',DATEADD('DAY',  -4,CURRENT_TIMESTAMP),DATEADD('DAY',  -4,CURRENT_TIMESTAMP)),
(29,1,5,'DELIVERED'  ,DATEADD('DAY', -95,CURRENT_TIMESTAMP),DATEADD('DAY', -95,CURRENT_TIMESTAMP)),
(30,1,2,'CONFIRMED'  ,DATEADD('DAY', -45,CURRENT_TIMESTAMP),DATEADD('DAY', -45,CURRENT_TIMESTAMP)),
(31,1,3,'PENDING'    ,DATEADD('DAY',-130,CURRENT_TIMESTAMP),DATEADD('DAY',-130,CURRENT_TIMESTAMP)),
(32,1,4,'DELIVERED'  ,DATEADD('DAY', -10,CURRENT_TIMESTAMP),DATEADD('DAY', -10,CURRENT_TIMESTAMP)),
(33,1,5,'IN_PROGRESS',DATEADD('DAY',-140,CURRENT_TIMESTAMP),DATEADD('DAY',-140,CURRENT_TIMESTAMP)),
(34,1,2,'DELIVERED'  ,DATEADD('DAY',-160,CURRENT_TIMESTAMP),DATEADD('DAY',-160,CURRENT_TIMESTAMP)),
(35,1,3,'CONFIRMED'  ,DATEADD('DAY',-175,CURRENT_TIMESTAMP),DATEADD('DAY',-175,CURRENT_TIMESTAMP)),
(36,1,4,'PENDING'    ,DATEADD('DAY', -28,CURRENT_TIMESTAMP),DATEADD('DAY', -28,CURRENT_TIMESTAMP)),
(37,1,5,'DELIVERED'  ,DATEADD('DAY', -32,CURRENT_TIMESTAMP),DATEADD('DAY', -32,CURRENT_TIMESTAMP)),
(38,1,2,'CONFIRMED'  ,DATEADD('DAY', -11,CURRENT_TIMESTAMP),DATEADD('DAY', -11,CURRENT_TIMESTAMP)),
(39,1,3,'IN_PROGRESS',DATEADD('DAY', -21,CURRENT_TIMESTAMP),DATEADD('DAY', -21,CURRENT_TIMESTAMP)),
(40,1,4,'DELIVERED'  ,DATEADD('DAY', -59,CURRENT_TIMESTAMP),DATEADD('DAY', -59,CURRENT_TIMESTAMP)),
(41,1,5,'CONFIRMED'  ,DATEADD('DAY',  -8,CURRENT_TIMESTAMP),DATEADD('DAY',  -8,CURRENT_TIMESTAMP)),
(42,1,2,'PENDING'    ,DATEADD('DAY', -65,CURRENT_TIMESTAMP),DATEADD('DAY', -65,CURRENT_TIMESTAMP)),
(43,1,3,'DELIVERED'  ,DATEADD('DAY',-120,CURRENT_TIMESTAMP),DATEADD('DAY',-120,CURRENT_TIMESTAMP)),
(44,1,4,'CONFIRMED'  ,DATEADD('DAY', -14,CURRENT_TIMESTAMP),DATEADD('DAY', -14,CURRENT_TIMESTAMP)),
(45,1,5,'IN_PROGRESS',DATEADD('DAY', -47,CURRENT_TIMESTAMP),DATEADD('DAY', -47,CURRENT_TIMESTAMP)),
(46,1,2,'DELIVERED'  ,DATEADD('DAY', -72,CURRENT_TIMESTAMP),DATEADD('DAY', -72,CURRENT_TIMESTAMP)),
(47,1,3,'CONFIRMED'  ,DATEADD('DAY', -24,CURRENT_TIMESTAMP),DATEADD('DAY', -24,CURRENT_TIMESTAMP)),
(48,1,4,'PENDING'    ,DATEADD('DAY', -36,CURRENT_TIMESTAMP),DATEADD('DAY', -36,CURRENT_TIMESTAMP)),
(49,1,5,'DELIVERED'  ,DATEADD('DAY',-118,CURRENT_TIMESTAMP),DATEADD('DAY',-118,CURRENT_TIMESTAMP)),
(50,1,2,'CONFIRMED'  ,DATEADD('DAY',  -2,CURRENT_TIMESTAMP),DATEADD('DAY',  -2,CURRENT_TIMESTAMP));


INSERT INTO order_menu (order_id,menu_item_id) VALUES
( 6,1),( 6,4),( 6,7),( 6,8),
( 7,2),( 7,8),
( 8,3),( 8,6),( 8,9),( 8,10),
( 9,10),( 9,2),( 9,7),( 9,1),
(10,1),(10,3),(10,4),
(11,4),(11,6),(11,9),(11,2),
(12,8),
(13,3),(13,9),(13,10),(13,6),
(14,2),(14,4),(14,7),
(15,6),(15,1),(15,8),
(16,7),(16,9),(16,2),(16,3),(16,10),
(17,3),(17,8),(17,6),
(18,10),(18,2),(18,6),(18,1),
(19,1),(19,7),
(20,6),(20,7),(20,4),(20,2),
(21,8),(21,9),(21,3),
(22,4),(22,3),
(23,2),(23,10),(23,1),(23,6),
(24,8),(24,7),
(25,6),(25,9),(25,4),(25,2),
(26,3),(26,7),
(27,1),(27,2),(27,8),
(28,4),(28,6),(28,9),
(29,8),(29,10),(29,3),
(30,9),(30,3),(30,1),
(31,2),(31,4),(31,6),(31,7),
(32,1),(32,7),(32,9),
(33,8),(33,2),(33,6),
(34,3),(34,9),(34,10),
(35,4),(35,10),(35,1),(35,2),
(36,6),(36,2),
(37,7),(37,3),(37,9),(37,4),
(38,8),(38,1),
(39,1),(39,4),(39,6),(39,2),(39,9),
(40,2),(40,7),
(41,3),(41,8),(41,10),(41,6),
(42,4),(42,6),(42,9),
(43,9),(43,2),(43,7),
(44,1),(44,7),(44,2),(44,3),
(45,3),(45,8),(45,10),
(46,6),(46,8),(46,10),(46,4),
(47,2),(47,4),(47,7),
(48,7),(48,1),(48,9),
(49,8),(49,9),(49,3),
(50,10),(50,3),(50,6),(50,2),(50,1);
-- ─────────────────────────────────────────────────────────────
