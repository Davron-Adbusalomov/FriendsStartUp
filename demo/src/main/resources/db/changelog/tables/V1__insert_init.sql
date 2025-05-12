INSERT INTO users(
    id, created_at, created_by, status, updated_at, updated_by,
    attempts, confirmation_code, is_active, is_blocked,
    password, username, full_name
)
VALUES (
           1,
           '2025-03-24 07:55:25.986510',
           1,
           'CREATED',
           '2025-03-24 07:55:25.986510',
           1,
           0,
           'ABC123',
           true,
           false,
           '$2a$10$Fgao4CtX91jkbLZlXKUGFOfnPFtfaS352yOqxAXJuoV8ZYrcT5f5q',
           'bingo',
           'Bingo Admin'
       );


insert into role(name, privilege) VALUES
                                      ('ADMIN', 1),
                                      ('TEACHER', 2),
                                      ('STUDENT', 3),
                                      ('ROLE_USER', 4);

INSERT INTO user_permission (
    id,
    created_at,
    created_by,
    status,
    updated_at,
    updated_by,
    name,
    user_id
) VALUES
      (1, current_timestamp, 1, 'CREATED', NULL, 1, 'CREATE', 1),
      (2, current_timestamp, 1, 'CREATED', NULL, 1, 'SIGN_UP', 1),
      (3, current_timestamp, 1, 'CREATED', NULL, 1, 'GET_STUDENTS_LIST', 1),
      (4, current_timestamp, 1, 'CREATED', NULL, 1, 'GET_STUDENT', 1),
      (5, current_timestamp, 1, 'CREATED', NULL, 1, 'DELETE_STUDENT', 1),
      (6, current_timestamp, 1, 'CREATED', NULL, 1, 'UPDATE_STUDENT', 1),
      (7, current_timestamp, 1, 'CREATED', NULL, 1, 'GET_TEACHERS_LIST', 1),
      (8, current_timestamp, 1, 'CREATED', NULL, 1, 'GET_TEACHER', 1),
      (9, current_timestamp, 1, 'CREATED', NULL, 1, 'DELETE_TEACHER', 1),
      (10, current_timestamp, 1, 'CREATED', NULL, 1, 'UPDATE_TEACHER', 1),
      (11, current_timestamp, 1, 'CREATED', NULL, 1, 'CREATE_GROUP', 1),
      (12, current_timestamp, 1, 'CREATED', NULL, 1, 'GET_GROUPS_LIST', 1),
      (13, current_timestamp, 1, 'CREATED', NULL, 1, 'GET_GROUP', 1),
      (14, current_timestamp, 1, 'CREATED', NULL, 1, 'DELETE_GROUP', 1),
      (15, current_timestamp, 1, 'CREATED', NULL, 1, 'UPDATE_GROUP', 1),
      (16, current_timestamp, 1, 'CREATED', NULL, 1, 'ASSIGN_STUDENT_TO_GROUP', 1),
      (17, current_timestamp, 1, 'CREATED', NULL, 1, 'DEASSIGN_STUDENT_FROM_GROUP', 1),
      (18, current_timestamp, 1, 'CREATED', NULL, 1, 'ASSIGN_TEACHER_TO_GROUP', 1);
