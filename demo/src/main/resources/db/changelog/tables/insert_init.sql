truncate table users cascade;
INSERT INTO users(id, username, password, created_at, updated_at, is_active, is_blocked, attempts, status)
VALUES (1, 'bingo', '$2a$10$Fgao4CtX91jkbLZlXKUGFOfnPFtfaS352yOqxAXJuoV8ZYrcT5f5q', now(), now(), false, false, 0,
        'CREATED');

truncate table user_roles cascade;
insert into user_roles(role_id, user_id)
values ('ADMIN', 1),
       ('TEACHER', 2),
       ('STUDENT', 3),
       ('ROLE_USER', 4);

INSERT INTO user_permission(created_at, created_by, id, updated_at, updated_by, user_id, name, status) VALUES
                                                                                                           (now(), 1, 1, now(), 1, 1, 'SIGN_UP', 'CREATED'),
                                                                                                           (now(), 1, 2, now(), 1, 1, 'GET_STUDENTS_LIST', 'CREATED'),
                                                                                                           (now(), 1, 3, now(), 1, 1, 'GET_STUDENT', 'CREATED'),
                                                                                                           (now(), 1, 4, now(), 1, 1, 'DELETE_STUDENT', 'CREATED'),
                                                                                                           (now(), 1, 5, now(), 1, 1, 'UPDATE_STUDENT', 'CREATED'),
                                                                                                           (now(), 1, 6, now(), 1, 1, 'GET_TEACHERS_LIST', 'CREATED'),
                                                                                                           (now(), 1, 7, now(), 1, 1, 'GET_TEACHER', 'CREATED'),
                                                                                                           (now(), 1, 8, now(), 1, 1, 'UPDATE_TEACHER', 'CREATED'),
                                                                                                           (now(), 1, 9, now(), 1, 1, 'DELETE_TEACHER', 'CREATED'),
                                                                                                           (now(), 1, 10, now(), 1, 1, 'GET_GROUPS_LIST', 'CREATED'),
                                                                                                           (now(), 1, 11, now(), 1, 1, 'GET_GROUP', 'CREATED'),
                                                                                                           (now(), 1, 12, now(), 1, 1, 'DELETE_GROUP', 'CREATED'),
                                                                                                           (now(), 1, 13, now(), 1, 1, 'UPDATE_GROUP', 'CREATED'),
                                                                                                           (now(), 1, 14, now(), 1, 1, 'CREATE_GROUP', 'CREATED'),
                                                                                                           (now(), 1, 15, now(), 1, 1, 'ASSIGN_TEACHER_TO_GROUP', 'CREATED'),
                                                                                                           (now(), 1, 16, now(), 1, 1, 'DEASSIGN_STUDENT_TO_GROUP', 'CREATED'),
                                                                                                           (now(), 1, 17, now(), 1, 1, 'ASSIGN_STUDENT_TO_GROUP', 'CREATED');
