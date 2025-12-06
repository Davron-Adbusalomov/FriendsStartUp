INSERT INTO center (id,created_at, created_by, status, updated_at, updated_by, name, location, contact_info)
VALUES (
           '550e8400-e29b-41d4-a716-446655440000',
           '2025-03-24 07:55:25.986510',
           1,
           'CREATED',
           '2025-03-24 07:55:25.986510',
           1,
           'Best Academy',
           'Namangan, Uzbekistan',
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
                                      ('SUPER_ADMIN', 0),
                                      ('DIRECTOR', 0),
                                      ('ADMIN', 1),
                                      ('TEACHER', 2),
                                      ('STUDENT', 3),
                                      ('ROLE_USER', 4)
ON CONFLICT (name) DO NOTHING;

-- user-role
INSERT INTO user_roles(user_id, role_id) VALUES
    (1, 'SUPER_ADMIN')
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


-- role permission
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
                                                              ('CHECK_QUIZ', 'Check quiz answers and provide results'),
                                                              ('FINALIZE_QUIZ', 'Finalize and submit the quiz for grading'),
                                                              ('RECORD_QUIZ_RESULT', 'Record the results of a completed quiz')
ON CONFLICT (name) DO NOTHING;


INSERT INTO role_default_permissions (role_id, default_permission_name) VALUES
-- ==========================================================
-- SUPER_ADMIN → Gets ALL permissions
-- ==========================================================
('SUPER_ADMIN', 'CREATE'),
('SUPER_ADMIN', 'SIGN_UP'),
('SUPER_ADMIN', 'GET_ADMINS_LIST'),
('SUPER_ADMIN', 'GET_ADMIN'),
('SUPER_ADMIN', 'DELETE_ADMIN'),
('SUPER_ADMIN', 'UPDATE_ADMIN'),
('SUPER_ADMIN', 'GET_STUDENTS_LIST'),
('SUPER_ADMIN', 'GET_STUDENT'),
('SUPER_ADMIN', 'DELETE_STUDENT'),
('SUPER_ADMIN', 'UPDATE_STUDENT'),
('SUPER_ADMIN', 'GET_TEACHERS_LIST'),
('SUPER_ADMIN', 'GET_TEACHER'),
('SUPER_ADMIN', 'DELETE_TEACHER'),
('SUPER_ADMIN', 'UPDATE_TEACHER'),
('SUPER_ADMIN', 'CREATE_GROUP'),
('SUPER_ADMIN', 'GET_GROUPS_LIST'),
('SUPER_ADMIN', 'GET_GROUP'),
('SUPER_ADMIN', 'DELETE_GROUP'),
('SUPER_ADMIN', 'UPDATE_GROUP'),
('SUPER_ADMIN', 'ASSIGN_STUDENT_TO_GROUP'),
('SUPER_ADMIN', 'DEASSIGN_STUDENT_FROM_GROUP'),
('SUPER_ADMIN', 'ASSIGN_TEACHER_TO_GROUP'),
('SUPER_ADMIN', 'GET_PERMISSIONS_BY_USER_ID'),
('SUPER_ADMIN', 'SAVE_USER_PERMISSIONS'),
('SUPER_ADMIN', 'CREATE_ATTENDANCE'),
('SUPER_ADMIN', 'GET_ATTENDANCE_LIST'),
('SUPER_ADMIN', 'GET_ATTENDANCE'),
('SUPER_ADMIN', 'UPDATE_ATTENDANCE'),
('SUPER_ADMIN', 'DELETE_ATTENDANCE'),
('SUPER_ADMIN', 'RESTORE_ATTENDANCE'),
('SUPER_ADMIN', 'GET_QUESTIONS_LIST'),
('SUPER_ADMIN', 'GET_QUESTION'),
('SUPER_ADMIN', 'CREATE_QUESTION'),
('SUPER_ADMIN', 'UPDATE_QUESTION'),
('SUPER_ADMIN', 'DELETE_QUESTION'),
('SUPER_ADMIN', 'CREATE_QUIZ'),
('SUPER_ADMIN', 'GET_QUIZZES_LIST'),
('SUPER_ADMIN', 'GET_QUIZ'),
('SUPER_ADMIN', 'UPDATE_QUIZ'),
('SUPER_ADMIN', 'DELETE_QUIZ'),
('SUPER_ADMIN', 'CHECK_QUIZ'),
('SUPER_ADMIN', 'FINALIZE_QUIZ'),
('SUPER_ADMIN', 'RECORD_QUIZ_RESULT'),
-- ==========================================================
-- DIRECTOR → View, manage, supervise, but fewer destructive permissions
-- ==========================================================
('DIRECTOR', 'CREATE'),
('DIRECTOR', 'SIGN_UP'),

('DIRECTOR', 'GET_ADMINS_LIST'),
('DIRECTOR', 'GET_ADMIN'),
('DIRECTOR', 'UPDATE_ADMIN'),

('DIRECTOR', 'GET_STUDENTS_LIST'),
('DIRECTOR', 'GET_STUDENT'),
('DIRECTOR', 'UPDATE_STUDENT'),

('DIRECTOR', 'GET_TEACHERS_LIST'),
('DIRECTOR', 'GET_TEACHER'),
('DIRECTOR', 'UPDATE_TEACHER'),

('DIRECTOR', 'GET_GROUPS_LIST'),
('DIRECTOR', 'GET_GROUP'),
('DIRECTOR', 'UPDATE_GROUP'),

('DIRECTOR', 'GET_ATTENDANCE_LIST'),
('DIRECTOR', 'GET_ATTENDANCE'),
('DIRECTOR', 'UPDATE_ATTENDANCE'),

('DIRECTOR', 'GET_QUESTIONS_LIST'),
('DIRECTOR', 'GET_QUESTION'),

('DIRECTOR', 'GET_QUIZZES_LIST'),
('DIRECTOR', 'GET_QUIZ'),

('DIRECTOR', 'CHECK_QUIZ'),
('DIRECTOR', 'RECORD_QUIZ_RESULT'),

('DIRECTOR', 'GET_PERMISSIONS_BY_USER_ID'),
-- ==========================================================
-- ADMIN → Full CRUD except quiz checking / advanced logic
-- (using same permission list structure as your previous ADMIN)
-- ==========================================================
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
('ADMIN', 'CREATE_ATTENDANCE'),
('ADMIN', 'GET_ATTENDANCE_LIST'),
('ADMIN', 'GET_ATTENDANCE'),
('ADMIN', 'UPDATE_ATTENDANCE'),
('ADMIN', 'DELETE_ATTENDANCE'),
('ADMIN', 'RESTORE_ATTENDANCE'),
('ADMIN', 'GET_QUESTIONS_LIST'),
('ADMIN', 'GET_QUESTION'),
('ADMIN', 'CREATE_QUESTION'),
('ADMIN', 'UPDATE_QUESTION'),
('ADMIN', 'DELETE_QUESTION'),
('ADMIN', 'CREATE_QUIZ'),
('ADMIN', 'GET_QUIZZES_LIST'),
('ADMIN', 'GET_QUIZ'),
('ADMIN', 'UPDATE_QUIZ'),
('ADMIN', 'DELETE_QUIZ'),
('ADMIN', 'FINALIZE_QUIZ'),
('ADMIN', 'RECORD_QUIZ_RESULT'),
-- ==========================================================
-- TEACHER → Create groups, assign students, manage attendance + quizzes
-- ==========================================================
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
('TEACHER', 'CREATE_ATTENDANCE'),
('TEACHER', 'GET_ATTENDANCE_LIST'),
('TEACHER', 'GET_ATTENDANCE'),
('TEACHER', 'UPDATE_ATTENDANCE'),
('TEACHER', 'GET_QUESTIONS_LIST'),
('TEACHER', 'GET_QUESTION'),
('TEACHER', 'CREATE_QUESTION'),
('TEACHER', 'UPDATE_QUESTION'),
('TEACHER', 'DELETE_QUESTION'),
('TEACHER', 'CHECK_QUIZ'),
('TEACHER', 'FINALIZE_QUIZ'),
('TEACHER', 'RECORD_QUIZ_RESULT'),
-- ==========================================================
-- STUDENT → Only view + participate in quizzes
-- ==========================================================
('STUDENT', 'GET_STUDENTS_LIST'),
('STUDENT', 'GET_STUDENT'),
('STUDENT', 'GET_TEACHERS_LIST'),
('STUDENT', 'GET_TEACHER'),
('STUDENT', 'GET_GROUPS_LIST'),
('STUDENT', 'GET_GROUP'),
('STUDENT', "GET_ATTENDANCE_LIST"),
('STUDENT', "GET_ATTENDANCE"),
('STUDENT', 'GET_ATTENDANCE'),
('STUDENT', 'GET_QUESTIONS_LIST'),
('STUDENT', 'GET_QUESTION'),
('STUDENT', 'GET_QUIZZES_LIST'),
('STUDENT', 'GET_QUIZ'),
('STUDENT', 'FINALIZE_QUIZ'),
-- ==========================================================
-- ROLE_USER → minimal authentication permissions
-- ==========================================================
('ROLE_USER', 'SIGN_UP'),
('ROLE_USER', 'GET_PERMISSIONS_BY_USER_ID')

ON CONFLICT (role_id, default_permission_name) DO NOTHING;
