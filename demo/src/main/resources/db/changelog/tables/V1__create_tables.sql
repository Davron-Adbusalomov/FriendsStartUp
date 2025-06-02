create sequence base_seq_gen
    increment by 1;

alter sequence base_seq_gen owner to postgres;

create sequence user_permission_seq;

alter sequence user_permission_seq owner to postgres;

create sequence users_seq;

alter sequence users_seq owner to postgres;

create table admin
(
    created_at timestamp(6) not null,
    created_by bigint,
    id         bigint       not null
        primary key,
    updated_at timestamp(6),
    updated_by bigint,
    full_name  varchar(255),
    image      varchar(255),
    password   varchar(255),
    status     varchar(255) not null
        constraint admin_status_check
            check ((status)::text = ANY
                   ((ARRAY ['CREATED'::character varying, 'UPDATED'::character varying, 'DELETED'::character varying])::text[])),
    username   varchar(255)
);

alter table admin
    owner to postgres;

create table default_permission_entity
(
    description varchar(255),
    name        varchar(255) not null
        primary key
        constraint default_permission_entity_name_check
            check ((name)::text = ANY
                   ((ARRAY ['CREATE'::character varying, 'SIGN_UP'::character varying, 'GET_ADMINS_LIST'::character varying, 'GET_ADMIN'::character varying, 'DELETE_ADMIN'::character varying, 'UPDATE_ADMIN'::character varying, 'GET_STUDENTS_LIST'::character varying, 'GET_STUDENT'::character varying, 'DELETE_STUDENT'::character varying, 'UPDATE_STUDENT'::character varying, 'GET_TEACHERS_LIST'::character varying, 'GET_TEACHER'::character varying, 'DELETE_TEACHER'::character varying, 'UPDATE_TEACHER'::character varying, 'CREATE_GROUP'::character varying, 'GET_GROUPS_LIST'::character varying, 'GET_GROUP'::character varying, 'DELETE_GROUP'::character varying, 'UPDATE_GROUP'::character varying, 'ASSIGN_STUDENT_TO_GROUP'::character varying, 'DEASSIGN_STUDENT_FROM_GROUP'::character varying, 'ASSIGN_TEACHER_TO_GROUP'::character varying, 'GET_PERMISSIONS_BY_USER_ID'::character varying, 'SAVE_USER_PERMISSIONS'::character varying])::text[]))
);

alter table default_permission_entity
    owner to postgres;

create table role
(
    privilege integer      not null,
    name      varchar(255) not null
        primary key
        constraint role_name_check
            check ((name)::text = ANY
                   ((ARRAY ['ADMIN'::character varying, 'TEACHER'::character varying, 'STUDENT'::character varying, 'ROLE_USER'::character varying])::text[]))
);

alter table role
    owner to postgres;

create table role_default_permissions
(
    default_permission_name varchar(255) not null
        constraint fkmttyw8hdemcjh0ajj9k19jg4k
            references default_permission_entity,
    role_id                 varchar(255) not null
        constraint fkidtqrvg7j3b3qjrasn2yq9c6u
            references role,
    primary key (default_permission_name, role_id)
);

alter table role_default_permissions
    owner to postgres;

create table student
(
    created_at     timestamp(6) not null,
    created_by     bigint,
    id             bigint       not null
        primary key,
    updated_at     timestamp(6),
    updated_by     bigint,
    full_name      varchar(255),
    parent_chat_id varchar(255),
    parent_contact varchar(255),
    phone_number   varchar(255),
    status         varchar(255) not null
        constraint student_status_check
            check ((status)::text = ANY
                   ((ARRAY ['CREATED'::character varying, 'UPDATED'::character varying, 'DELETED'::character varying])::text[]))
);

alter table student
    owner to postgres;

create table teacher
(
    created_at   timestamp(6) not null,
    created_by   bigint,
    id           bigint       not null
        primary key,
    updated_at   timestamp(6),
    updated_by   bigint,
    experience   varchar(255),
    full_name    varchar(255),
    image        varchar(255),
    phone_number varchar(255),
    status       varchar(255) not null
        constraint teacher_status_check
            check ((status)::text = ANY
                   ((ARRAY ['CREATED'::character varying, 'UPDATED'::character varying, 'DELETED'::character varying])::text[])),
    subject      varchar(255)
);

alter table teacher
    owner to postgres;

create table groups
(
    teacher_id bigint
        constraint fkh41v53xm83rq9vspgdjqjjsm2
            references teacher,
    id         uuid not null
        primary key,
    name       varchar(255),
    subject    varchar(255),
    time       varchar(255)
);

alter table groups
    owner to postgres;

create table group_student
(
    student_id bigint not null
        constraint fkhsowqx64bm8qxnje3a4avlbh5
            references student,
    group_id   uuid   not null
        constraint fkent8q7mj5tq6lup5v1e7alrqx
            references groups
);

alter table group_student
    owner to postgres;

create table question
(
    mark         integer not null,
    teacher_id   bigint
        constraint fkgnm83qijywvywwgsmi39x9wth
            references teacher,
    id           uuid    not null
        primary key,
    image        varchar(255),
    level        varchar(255),
    right_answer varchar(255),
    subject      varchar(255),
    title        varchar(255),
    type         varchar(255)
);

alter table question
    owner to postgres;

create table option
(
    id          uuid not null
        primary key,
    question_id uuid
        constraint fkgtlhwmagte7l2ssfsgw47x9ka
            references question,
    text        varchar(255)
);

alter table option
    owner to postgres;

create table quiz
(
    questions_num integer not null,
    duration      bigint,
    start_time    timestamp(6),
    teacher_id    bigint
        constraint fkc2jgoslusmwb86uhtt533a8g8
            references teacher,
    grouping_id   uuid
        constraint fkj4e8mc2e0y938fcagehn6tplv
            references groups,
    id            uuid    not null
        primary key
);

alter table quiz
    owner to postgres;

create table quiz_question
(
    question_id uuid not null
        constraint fk62empq7vfu15qv1kci624f1js
            references question,
    quiz_id     uuid not null
        constraint fkdtynvfjgh6e7fd8l0wk37nrpc
            references quiz
);

alter table quiz_question
    owner to postgres;

create table quiz_results
(
    mark       bigint,
    student_id bigint
        constraint fk88fmrcbl580wthvxr0g5c89a2
            references student,
    id         uuid not null
        primary key,
    quiz_id    uuid
        constraint fkp6r0q60kdd4tpuli9tknd79y1
            references quiz
);

alter table quiz_results
    owner to postgres;

create table user_permission
(
    created_at timestamp(6) not null,
    created_by bigint,
    id         bigint       not null
        primary key,
    updated_at timestamp(6),
    updated_by bigint,
    user_id    bigint       not null,
    name       varchar(255) not null
        constraint user_permission_name_check
            check ((name)::text = ANY
                   ((ARRAY ['CREATE'::character varying, 'SIGN_UP'::character varying, 'GET_ADMINS_LIST'::character varying, 'GET_ADMIN'::character varying, 'DELETE_ADMIN'::character varying, 'UPDATE_ADMIN'::character varying, 'GET_STUDENTS_LIST'::character varying, 'GET_STUDENT'::character varying, 'DELETE_STUDENT'::character varying, 'UPDATE_STUDENT'::character varying, 'GET_TEACHERS_LIST'::character varying, 'GET_TEACHER'::character varying, 'DELETE_TEACHER'::character varying, 'UPDATE_TEACHER'::character varying, 'CREATE_GROUP'::character varying, 'GET_GROUPS_LIST'::character varying, 'GET_GROUP'::character varying, 'DELETE_GROUP'::character varying, 'UPDATE_GROUP'::character varying, 'ASSIGN_STUDENT_TO_GROUP'::character varying, 'DEASSIGN_STUDENT_FROM_GROUP'::character varying, 'ASSIGN_TEACHER_TO_GROUP'::character varying, 'GET_PERMISSIONS_BY_USER_ID'::character varying, 'SAVE_USER_PERMISSIONS'::character varying])::text[])),
    status     varchar(255) not null
        constraint user_permission_status_check
            check ((status)::text = ANY
                   ((ARRAY ['CREATED'::character varying, 'UPDATED'::character varying, 'DELETED'::character varying])::text[]))
);

alter table user_permission
    owner to postgres;

create table users
(
    attempts          integer,
    is_active         boolean      not null,
    is_blocked        boolean      not null,
    created_at        timestamp(6) not null,
    created_by        bigint,
    id                bigint       not null
        primary key,
    updated_at        timestamp(6),
    updated_by        bigint,
    username          varchar(100) not null
        unique,
    confirmation_code varchar(255),
    full_name         varchar(255),
    password          varchar(255) not null,
    status            varchar(255) not null
        constraint users_status_check
            check ((status)::text = ANY
                   ((ARRAY ['CREATED'::character varying, 'UPDATED'::character varying, 'DELETED'::character varying])::text[]))
);

alter table users
    owner to postgres;

create table user_roles
(
    user_id bigint       not null
        constraint fkhfh9dx7w3ubf1co1vdev94g3f
            references users,
    role_id varchar(255) not null
        constraint fkrhfovtciq1l558cw6udg0h0d3
            references role,
    primary key (user_id, role_id)
);

alter table user_roles
    owner to postgres;

create table written_questions
(
    max_score      bigint,
    score          bigint,
    student_id     bigint
        constraint fk8x08cq4uys7h0dr9stiunwk67
            references student,
    id             uuid not null
        primary key,
    question_id    uuid,
    quiz_id        uuid,
    correct_answer varchar(255),
    question_title varchar(255),
    student_answer varchar(255)
);

alter table written_questions
    owner to postgres;

create table wrong_answers_analyze
(
    student_id   bigint
        constraint fk84hoybril8enxfiwm1f7o0jh4
            references student,
    id           uuid not null
        primary key,
    question_id  uuid,
    quiz_id      uuid,
    wrong_answer varchar(255)
);

alter table wrong_answers_analyze
    owner to postgres;

