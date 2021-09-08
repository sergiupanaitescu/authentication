-- pass testPass
INSERT INTO users (id, user_name, name, age, address, password) VALUES (1, 'testUser', 'Ion', '33', 'acasa', 'b62a565853f37fb1ec1efc287bfcebf9');
INSERT INTO USER_ROLES (id, role) VALUES (1, 'USER');
INSERT INTO USER_ROLES (id, role) VALUES (1, 'ADMIN');
INSERT INTO users (id, user_name, name, age, address, password) VALUES (2, 'testUserWeak', 'IonWeak', '33', 'acasa', 'b62a565853f37fb1ec1efc287bfcebf9');
INSERT INTO USER_ROLES (id, role) VALUES (2, 'USER');