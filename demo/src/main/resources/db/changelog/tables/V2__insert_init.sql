INSERT INTO center (id,created_at, created_by, status, updated_at, updated_by, name, location, contact_info)
VALUES (
           '550e8400-e29b-41d4-a716-446655440000',
           '2025-03-24 07:55:25.986510',
           1,
           'CREATED',
           '2025-03-24 07:55:25.986510',
           1,
           'My Center',
           'Tashkent',
           '99890 123 45 67'
       );

INSERT INTO users(
    id, created_at, created_by, status, updated_at, updated_by,
    attempts, confirmation_code, center_id, is_active, is_blocked,
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
        '550e8400-e29b-41d4-a716-446655440000',
           true,
           false,
           '$2a$10$Fgao4CtX91jkbLZlXKUGFOfnPFtfaS352yOqxAXJuoV8ZYrcT5f5q',
           '+998500043703',
           'Bingo Admin'
       )
ON CONFLICT (id) DO NOTHING;
-- correct sequence
SELECT setval('users_seq', COALESCE((SELECT MAX(id) FROM users), 0), true);


-- role
insert into role(name, privilege) VALUES
                                      ('ADMIN', 1),
                                      ('TEACHER', 2),
                                      ('STUDENT', 3),
                                      ('ROLE_USER', 4)
ON CONFLICT (name) DO NOTHING;

-- user-role
INSERT INTO user_roles(user_id, role_id) VALUES
    (1, 'ADMIN')
ON CONFLICT (user_id, role_id) DO NOTHING;

-- user permissions
INSERT INTO user_permission (
    id, created_at, created_by, status, updated_at, updated_by, name, user_id
)
VALUES
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
    (18, current_timestamp, 1, 'CREATED', NULL, 1, 'ASSIGN_TEACHER_TO_GROUP', 1),
    (19, current_timestamp, 1, 'CREATED', NULL, 1, 'GET_PERMISSIONS_BY_USER_ID', 1),
    (20, current_timestamp, 1, 'CREATED', NULL, 1, 'SAVE_USER_PERMISSIONS', 1),
    (21, current_timestamp, 1, 'CREATED', NULL, 1, 'GET_ADMINS_LIST', 1),
    (22, current_timestamp, 1, 'CREATED', NULL, 1, 'GET_ADMIN', 1),
    (23, current_timestamp, 1, 'CREATED', NULL, 1, 'DELETE_ADMIN', 1),
    (24, current_timestamp, 1, 'CREATED', NULL, 1, 'CREATE_ATTENDANCE', 1),
    (25, current_timestamp, 1, 'CREATED', NULL, 1, 'GET_ATTENDANCE_LIST', 1),
    (26, current_timestamp, 1, 'CREATED', NULL, 1, 'GET_ATTENDANCE_LIST', 1),
    (27, current_timestamp, 1, 'CREATED', NULL, 1, 'GET_ATTENDANCE', 1),
    (28, current_timestamp, 1, 'CREATED', NULL, 1, 'UPDATE_ATTENDANCE', 1),
    (29, current_timestamp, 1, 'CREATED', NULL, 1, 'DELETE_ATTENDANCE', 1),
    (30, current_timestamp, 1, 'CREATED', NULL, 1, 'RESTORE_ATTENDANCE', 1)
ON CONFLICT (id) DO NOTHING;
SELECT setval('user_permission_seq', COALESCE((SELECT MAX(id) FROM user_permission), 0), true);


--default permissions
INSERT INTO default_permission_entity (name, description) VALUES
                                                              ('CREATE', 'Create new resources'),
                                                              ('SIGN_UP', 'User sign-up or registration'),
                                                              ('GET_ADMINS_LIST', 'View list of all students'),
                                                              ('GET_ADMIN', 'View details of a single student'),
                                                              ('DELETE_ADMIN', 'Delete a student'),
                                                              ('UPDATE_ADMIN', 'Update student information'),
                                                              ('GET_STUDENTS_LIST', 'View list of all students'),
                                                              ('GET_STUDENT', 'View details of a single student'),
                                                              ('DELETE_STUDENT', 'Delete a student'),
                                                              ('UPDATE_STUDENT', 'Update student information'),
                                                              ('GET_TEACHERS_LIST', 'View list of all teachers'),
                                                              ('GET_TEACHER', 'View details of a single teacher'),
                                                              ('DELETE_TEACHER', 'Delete a teacher'),
                                                              ('UPDATE_TEACHER', 'Update teacher information'),
                                                              ('CREATE_GROUP', 'Create a new group'),
                                                              ('GET_GROUPS_LIST', 'View list of all groups'),
                                                              ('GET_GROUP', 'View details of a single group'),
                                                              ('DELETE_GROUP', 'Delete a group'),
                                                              ('UPDATE_GROUP', 'Update group information'),
                                                              ('ASSIGN_STUDENT_TO_GROUP', 'Assign student to a group'),
                                                              ('DEASSIGN_STUDENT_FROM_GROUP', 'Remove student from a group'),
                                                              ('ASSIGN_TEACHER_TO_GROUP', 'Assign teacher to a group'),
                                                              ('GET_PERMISSIONS_BY_USER_ID', 'Get assigned permissions for a specific user'),
                                                              ('SAVE_USER_PERMISSIONS', 'Save or update user permissions'),
                                                              ('CREATE_ATTENDANCE', 'Create attendance record'),
                                                              ('GET_ATTENDANCE_LIST', 'View list of attendance records'),
                                                              ('GET_ATTENDANCE', 'View details of a single attendance record'),
                                                              ('UPDATE_ATTENDANCE', 'Update attendance record'),
                                                              ('DELETE_ATTENDANCE', 'Delete attendance record'),
                                                              ('RESTORE_ATTENDANCE', 'Restore deleted attendance record'),
                                                              ('GET_QUESTIONS_LIST', 'View list of all questions'),
                                                              ('GET_QUESTION', 'View details of a single question'),
                                                              ('CREATE_QUESTION', 'Create a new question'),
                                                              ('UPDATE_QUESTION', 'Update question information'),
                                                              ('DELETE_QUESTION', 'Delete a question'),
                                                              ('CREATE_QUIZ', 'Create a new quiz'),
                                                              ('GET_QUIZZES_LIST', 'View list of all quizzes'),
                                                              ('GET_QUIZ', 'View details of a single quiz'),
                                                              ('UPDATE_QUIZ', 'Update quiz information'),
                                                              ('DELETE_QUIZ', 'Delete a quiz'),
                                                              ('CHECK_QUIZ', 'Check quiz answers and provide results')
ON CONFLICT (name) DO NOTHING;

-- role permission
INSERT INTO role_default_permissions (role_id, default_permission_name) VALUES
                                                                            ('ADMIN', 'CREATE'),
                                                                            ('ADMIN', 'SIGN_UP'),
                                                                            ('ADMIN', 'GET_ADMINS_LIST'),
                                                                            ('ADMIN', 'GET_ADMIN'),
                                                                            ('ADMIN', 'DELETE_ADMIN'),
                                                                            ('ADMIN', 'UPDATE_ADMIN'),
                                                                            ('ADMIN', 'GET_STUDENTS_LIST'),
                                                                            ('ADMIN', 'GET_STUDENT'),
                                                                            ('ADMIN', 'DELETE_STUDENT'),
                                                                            ('ADMIN', 'UPDATE_STUDENT'),
                                                                            ('ADMIN', 'GET_TEACHERS_LIST'),
                                                                            ('ADMIN', 'GET_TEACHER'),
                                                                            ('ADMIN', 'DELETE_TEACHER'),
                                                                            ('ADMIN', 'UPDATE_TEACHER'),
                                                                            ('ADMIN', 'CREATE_GROUP'),
                                                                            ('ADMIN', 'GET_GROUPS_LIST'),
                                                                            ('ADMIN', 'GET_GROUP'),
                                                                            ('ADMIN', 'DELETE_GROUP'),
                                                                            ('ADMIN', 'UPDATE_GROUP'),
                                                                            ('ADMIN', 'ASSIGN_STUDENT_TO_GROUP'),
                                                                            ('ADMIN', 'DEASSIGN_STUDENT_FROM_GROUP'),
                                                                            ('ADMIN', 'ASSIGN_TEACHER_TO_GROUP'),
                                                                            ('ADMIN', 'GET_PERMISSIONS_BY_USER_ID'),
                                                                            ('ADMIN', 'SAVE_USER_PERMISSIONS'),
                                                                            ('TEACHER', 'CREATE'),
                                                                            ('TEACHER', 'GET_STUDENTS_LIST'),
                                                                            ('TEACHER', 'GET_STUDENT'),
                                                                            ('TEACHER', 'GET_TEACHERS_LIST'),
                                                                            ('TEACHER', 'GET_TEACHER'),
                                                                            ('TEACHER', 'UPDATE_TEACHER'),
                                                                            ('TEACHER', 'CREATE_GROUP'),
                                                                            ('TEACHER', 'GET_GROUPS_LIST'),
                                                                            ('TEACHER', 'GET_GROUP'),
                                                                            ('TEACHER', 'UPDATE_GROUP'),
                                                                            ('TEACHER', 'ASSIGN_STUDENT_TO_GROUP'),
                                                                            ('TEACHER', 'DEASSIGN_STUDENT_FROM_GROUP'),
                                                                            ('TEACHER', 'ASSIGN_TEACHER_TO_GROUP'),
                                                                            ('STUDENT', 'CREATE'),
                                                                            ('STUDENT', 'SIGN_UP'),
                                                                            ('STUDENT', 'GET_ADMINS_LIST'),
                                                                            ('STUDENT', 'GET_STUDENTS_LIST'),
                                                                            ('STUDENT', 'GET_STUDENT'),
                                                                            ('STUDENT', 'DELETE_STUDENT'),
                                                                            ('STUDENT', 'UPDATE_STUDENT'),
                                                                            ('STUDENT', 'GET_TEACHERS_LIST'),
                                                                            ('STUDENT', 'GET_TEACHER'),
                                                                            ('STUDENT', 'GET_GROUPS_LIST'),
                                                                            ('STUDENT', 'GET_GROUP'),
                                                                            ('STUDENT', 'GET_PERMISSIONS_BY_USER_ID'),
                                                                            ('ROLE_USER', 'SIGN_UP'),
                                                                            ('ROLE_USER', 'GET_PERMISSIONS_BY_USER_ID'),
                                                                            ('ADMIN', 'CREATE_ATTENDANCE'),
                                                                            ('ADMIN', 'GET_ATTENDANCE_LIST'),
                                                                            ('ADMIN', 'GET_ATTENDANCE'),
                                                                            ('ADMIN', 'UPDATE_ATTENDANCE'),
                                                                            ('ADMIN', 'DELETE_ATTENDANCE'),
                                                                            ('ROLE_USER', 'SAVE_USER_PERMISSIONS'),
                                                                            ('ADMIN', 'RESTORE_ATTENDANCE'),
                                                                            ('TEACHER', 'CREATE_ATTENDANCE'),
                                                                            ('TEACHER', 'GET_ATTENDANCE_LIST'),
                                                                            ('TEACHER', 'GET_ATTENDANCE'),
                                                                            ('TEACHER', 'UPDATE_ATTENDANCE')
ON CONFLICT (role_id, default_permission_name) DO NOTHING;
