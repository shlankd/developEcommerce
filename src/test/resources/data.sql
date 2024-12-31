-- This file allows us to load static data into the test database before tests are run.

INSERT INTO ecomm_role(id, role_name)
VALUES (11L, 'ADMIN'),
       (12L, 'USER');

SET @role_admin_id = (SELECT id FROM ecomm_role WHERE role_name = 'ADMIN');
SET @role_user_id = (SELECT id FROM ecomm_role WHERE role_name = 'USER');

-- Passwords are in the format: testPassword<UserNumber>$123. Unless specified otherwise.
-- Encrypted using the link: https://www.javainuse.com/onlineBcrypt
-- testAdmin password: testAdmin$123
INSERT INTO ecomm_user(email, first_name, last_name, password, username, verified_email)
VALUES ('testAdmin@junit.com', 'testAdmin-first_name', 'testAdmin-last_name',
        '$2a$10$m.wyt0pYvqhDHGdn0HjdSuBIzrEpZfdstiP22ksbyv0C8x7aKSIZK', 'testAdmin', true),
       ('testUser1@junit.com', 'testUser1-first_name', 'testUser1-last_name',
        '$2a$10$7JekFAmSAIBTvlVANnmDyejWpYGy8qyy1wcmrz/KcQaSd26nlPDRi', 'testUser1', true),
       ('testUser2@junit.com', 'testUser2-first_name', 'testUser2-last_name',
        '$2a$10$wi9I5NiiPEgP7FcJdF.MVutBLGIw.GYprbS/rFwlYqr2mgRzgHiOW', 'testUser2', false),
       ('testUser3@junit.com', 'testUser3-first_name', 'testUser3-last_name',
        '$2a$10$eQiYAZ3OiW5l0VM7CjOClOWCdinpB82jHTB3QtTAM/BHDbZ9e94UG', 'testUser3', true);

SET @testAdmin_id = (SELECT id FROM ecomm_user WHERE username = 'testAdmin');
SET @testUser1_id = (SELECT id FROM ecomm_user WHERE username = 'testUser1');
SET @testUser2_id = (SELECT id FROM ecomm_user WHERE username = 'testUser2');
SET @testUser3_id = (SELECT id FROM ecomm_user WHERE username = 'testUser3');

-- INSERT INTO user_role(role_id, user_id)
-- VALUES(@role_admin_id, @testAdmin_id),
--       (@role_user_id, @testAdmin_id),
--       (@role_user_id, @testUser1_id),
--       (@role_user_id, @testUser2_id),
--       (@role_user_id, @testUser3_id);

INSERT INTO user_role(user_id, role_id)
VALUES (@testAdmin_id, @role_admin_id),
       (@testAdmin_id, @role_user_id),
       (@testUser1_id, @role_user_id),
       (@testUser2_id, @role_user_id),
       (@testUser3_id, @role_user_id);

INSERT INTO address(address_line, city, country, user_id)
VALUES ('123 Tester Hill', 'London', 'England', @testUser1_id),
       ('312 Spring Boot Boulevard', 'Manchester', 'England', @testUser2_id),
       ('231 Duff Factory', 'Springfield', 'USA', @testUser3_id);

SET @address_id_of_testUser1 = (SELECT id FROM address WHERE user_id = @testUser1_id);
SET @address_id_of_testUser2 = (SELECT id FROM address WHERE user_id = @testUser2_id);
SET @address_id_of_testUser3 = (SELECT id FROM address WHERE user_id = @testUser3_id);

INSERT INTO product (product_description, product_name, price, product_quantity)
VALUES ('Description of product test #1.', 'Product Test #1', 10.56, 20), --
       ('Description of product test #2.', 'Product Test #2', 2.74, 30),
       ('Description of product test #3.', 'Product Test #3', 15.69, 40),
       ('Description of product test #4.', 'Product Test #4', 42.59, 0),
       ('Description of product test #5.', 'Product Test #5', 55.83, 38);

SET @testProduct1_id = (SELECT product_id FROM product WHERE product_name = 'Product Test #1');
SET @testProduct2_id = (SELECT product_id FROM product WHERE product_name = 'Product Test #2');
SET @testProduct3_id = (SELECT product_id FROM product WHERE product_name = 'Product Test #3');
SET @testProduct4_id = (SELECT product_id FROM product WHERE product_name = 'Product Test #4');
SET @testProduct5_id = (SELECT product_id FROM product WHERE product_name = 'Product Test #5');

INSERT INTO cart_item(cart_item_quantity, product_id, user_id)
VALUES (5, @testProduct2_id, @testUser1_id),
       (4, @testProduct3_id, @testUser1_id),
       (2, @testProduct2_id, @testUser3_id),
       (5, @testProduct1_id, @testUser3_id),
       (2, @testProduct4_id, @testUser3_id); --Out Of Stock


INSERT INTO ecomm_order (address_id, user_id)
VALUES (@address_id_of_testUser1, @testUser1_id), --order_id  = 1
       (@address_id_of_testUser1, @testUser1_id), --order_id = 2
       (@address_id_of_testUser1, @testUser1_id), --order_id = 3
       (@address_id_of_testUser3, @testUser3_id),  --order_id = 4
       (@address_id_of_testUser3, @testUser3_id);  --order_id = 5

SET @first_order_id_of_testUser1 = (SELECT MIN(id) FROM ecomm_order WHERE user_id = @testUser1_id AND address_id = @address_id_of_testUser1);
SET @second_order_id_of_testUser1 = @first_order_id_of_testUser1 + 1;
SET @third_order_id_of_testUser1 = @first_order_id_of_testUser1 + 2;
SET @first_order_id_of_testUser3 = @first_order_id_of_testUser1 + 3;
SET @second_order_id_of_testUser3 = @first_order_id_of_testUser1 + 4;

-- INSERT INTO order_item (quantity, order_id, product_id)
-- VALUES (5, 1, @testProduct1_id),
--        (5, 2, @testProduct1_id),
--        (5, 3, @testProduct2_id),
--        (5, 2, @testProduct2_id),
--        (5, 5, @testProduct2_id),
--        (5, 3, @testProduct3_id),
--        (5, 4, @testProduct4_id),
--        (5, 2, @testProduct4_id),
--        (5, 3, @testProduct5_id),
--        (5, 1, @testProduct5_id);

INSERT INTO order_item (quantity, order_id, product_id)
VALUES (5, @first_order_id_of_testUser1, @testProduct1_id),
       (5, @second_order_id_of_testUser1, @testProduct1_id),
       (5, @third_order_id_of_testUser1, @testProduct2_id),
       (5, @second_order_id_of_testUser1, @testProduct2_id),
       (5, @second_order_id_of_testUser3, @testProduct2_id),
       (5, @third_order_id_of_testUser1, @testProduct3_id),
       (5, @first_order_id_of_testUser3, @testProduct4_id),
       (5, @second_order_id_of_testUser1, @testProduct4_id),
       (5, @third_order_id_of_testUser1, @testProduct5_id),
       (5, @first_order_id_of_testUser1, @testProduct5_id);