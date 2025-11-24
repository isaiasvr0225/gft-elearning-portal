INSERT INTO roles (id_role, name) VALUES (1, 'ADMIN');
INSERT INTO roles (id_role, name) VALUES (2, 'EMPLOYEE');

INSERT INTO document_types (id_document_type, name) VALUES (1, 'NIT');
INSERT INTO document_types (id_document_type, name) VALUES (2, 'CC');
INSERT INTO document_types (id_document_type, name) VALUES (3, 'CE');
INSERT INTO document_types (id_document_type, name) VALUES (4, 'PP');

INSERT INTO users (document_number, id_role, document_type_id, id_city, full_name, email, phone_number, address, password, account_no_expired, account_no_locked, credentials_no_expired, is_enabled, profile_image_link) VALUES (1007744055, 1, 2, null, 'Admin', 'admin@elearning.gft.com', 3116112594, null,'$2a$12$BQ0pUs7xG62yWoulLcdEh.V41jRnkryY87dOe.Qe4PVbGFTJ.8lBO', true, true, true, true, '');
INSERT INTO users (document_number, id_role, document_type_id, id_city, full_name, email, phone_number, address, password, account_no_expired, account_no_locked, credentials_no_expired, is_enabled, profile_image_link) VALUES (1010081565, 2, 2, null, 'User1', 'user1@elearning.gft.com', 3160102686, null,'$2a$12$BQ0pUs7xG62yWoulLcdEh.V41jRnkryY87dOe.Qe4PVbGFTJ.8lBO', true, true, true, true, '');
INSERT INTO users (document_number, id_role, document_type_id, id_city, full_name, email, phone_number, address, password, account_no_expired, account_no_locked, credentials_no_expired, is_enabled, profile_image_link) VALUES (1015453701, 2, 2, null, 'User2', 'user2@elearning.gft.com', 3232242599, null,'$2a$12$BQ0pUs7xG62yWoulLcdEh.V41jRnkryY87dOe.Qe4PVbGFTJ.8lBO', true, true, true, true, '');

INSERT INTO months (id_month, name) VALUES (1, 'Enero');
INSERT INTO months (id_month, name) VALUES (2, 'Febrero');
INSERT INTO months (id_month, name) VALUES (3, 'Marzo');
INSERT INTO months (id_month, name) VALUES (4, 'Abril');
INSERT INTO months (id_month, name) VALUES (5, 'Mayo');
INSERT INTO months (id_month, name) VALUES (6, 'Junio');
INSERT INTO months (id_month, name) VALUES (7, 'Julio');
INSERT INTO months (id_month, name) VALUES (8, 'Agosto');
INSERT INTO months (id_month, name) VALUES (9, 'Septiembre');
INSERT INTO months (id_month, name) VALUES (10, 'Octubre');
INSERT INTO months (id_month, name) VALUES (11, 'Noviembre');
INSERT INTO months (id_month, name) VALUES (12, 'Diciembre');

INSERT INTO cities (name) VALUES ('Ninguna');