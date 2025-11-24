-- UNIQUE constraints
ALTER TABLE cities ADD CONSTRAINT uc_cities_name UNIQUE (name);
ALTER TABLE document_types ADD CONSTRAINT uc_document_types_name UNIQUE (name);
ALTER TABLE months ADD CONSTRAINT uc_months_name UNIQUE (name);
ALTER TABLE roles ADD CONSTRAINT uc_roles_name UNIQUE (name);
ALTER TABLE users ADD CONSTRAINT uc_users_email UNIQUE (email);
ALTER TABLE users ADD CONSTRAINT uc_users_phone_number UNIQUE (phone_number);

-- USERS
ALTER TABLE users ADD CONSTRAINT FK_USERS_ON_DOCUMENT_TYPE FOREIGN KEY (document_type_id) REFERENCES document_types (id_document_type);
ALTER TABLE users ADD CONSTRAINT FK_USERS_ON_ID_CITY FOREIGN KEY (id_city) REFERENCES cities (id_city);
ALTER TABLE users ADD CONSTRAINT FK_USERS_ON_ID_ROLE FOREIGN KEY (id_role) REFERENCES roles (id_role);

-- E-learning: constraints
-- Courses/modules relations
ALTER TABLE course_modules ADD CONSTRAINT fk_course_modules_on_course FOREIGN KEY (course_id) REFERENCES courses (id);

-- User courses relations and uniqueness
ALTER TABLE user_courses ADD CONSTRAINT uc_user_courses_user_course UNIQUE (user_document_number, course_id);
ALTER TABLE user_courses ADD CONSTRAINT fk_user_courses_on_user FOREIGN KEY (user_document_number) REFERENCES users (document_number);
ALTER TABLE user_courses ADD CONSTRAINT fk_user_courses_on_course FOREIGN KEY (course_id) REFERENCES courses (id);

-- Module completions relations and uniqueness
ALTER TABLE module_completions ADD CONSTRAINT uc_module_completions_user_module UNIQUE (user_document_number, module_id);
ALTER TABLE module_completions ADD CONSTRAINT fk_module_completions_on_user FOREIGN KEY (user_document_number) REFERENCES users (document_number);
ALTER TABLE module_completions ADD CONSTRAINT fk_module_completions_on_module FOREIGN KEY (module_id) REFERENCES course_modules (id);