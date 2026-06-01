CREATE SEQUENCE IF NOT EXISTS user_permission_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE admin
(
    id           BIGINT                      NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    status       VARCHAR(255)                NOT NULL,
    created_by   BIGINT,
    updated_by   BIGINT,
    full_name    VARCHAR(255),
    image        VARCHAR(255),
    email        VARCHAR(255),
    phone_number VARCHAR(255),
    center_id    UUID                        NOT NULL,
    CONSTRAINT pk_admin PRIMARY KEY (id)
);

CREATE TABLE attendance
(
    id                UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    status            VARCHAR(255)                NOT NULL,
    created_by        BIGINT,
    updated_by        BIGINT,
    student_id        BIGINT                      NOT NULL,
    group_id          UUID                        NOT NULL,
    attendance_time   TIMESTAMP WITHOUT TIME ZONE,
    attendance_status VARCHAR(255),
    center_id         UUID                        NOT NULL,
    CONSTRAINT pk_attendance PRIMARY KEY (id)
);

CREATE TABLE center
(
    id           UUID                        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    status       VARCHAR(255)                NOT NULL,
    created_by   BIGINT,
    updated_by   BIGINT,
    name         VARCHAR(255),
    location     VARCHAR(255),
    contact_info VARCHAR(255),
    CONSTRAINT pk_center PRIMARY KEY (id)
);

CREATE TABLE default_permission_entity
(
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    CONSTRAINT pk_defaultpermissionentity PRIMARY KEY (name)
);

CREATE TABLE enrollment
(
    id                UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    status            VARCHAR(255)                NOT NULL,
    created_by        BIGINT,
    updated_by        BIGINT,
    student_id        BIGINT                      NOT NULL,
    group_id          UUID                        NOT NULL,
    enrollment_status SMALLINT,
    enrollment_date   TIMESTAMP WITHOUT TIME ZONE,
    center_id         UUID                        NOT NULL,
    CONSTRAINT pk_enrollment PRIMARY KEY (id)
);

CREATE TABLE group_student
(
    group_id   UUID   NOT NULL,
    student_id BIGINT NOT NULL
);

CREATE TABLE course
(
    id                 UUID                        NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    status             VARCHAR(255)                NOT NULL,
    created_by         BIGINT,
    updated_by         BIGINT,
    name               VARCHAR(255),
    description        VARCHAR(2000),
    image              VARCHAR(2000),
    duration_in_months INTEGER,
    subject_id         UUID,
    center_id          UUID                        NOT NULL,
    CONSTRAINT pk_course PRIMARY KEY (id)
);

CREATE TABLE groups
(
    id                 UUID                        NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    status             VARCHAR(255)                NOT NULL,
    created_by         BIGINT,
    updated_by         BIGINT,
    name               VARCHAR(255),
    time               VARCHAR(255),
    description        VARCHAR(2000),
    start_date         TIMESTAMP WITHOUT TIME ZONE,
    duration_in_months INTEGER,
    subject_id         UUID,
    teacher_id         BIGINT,
    center_id          UUID                        NOT NULL,
    image              VARCHAR(2000),
    course_id          UUID,
    CONSTRAINT pk_groups PRIMARY KEY (id)
);

CREATE TABLE lesson
(
    id             UUID                        NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    status         VARCHAR(255)                NOT NULL,
    created_by     BIGINT,
    updated_by     BIGINT,
    title          VARCHAR(255),
    description    VARCHAR(1000),
    video_url      VARCHAR(255),
    duration       INTEGER,
    order_index    INTEGER,
    course_id      UUID,
    inspector_name VARCHAR(255),
    inspector_info TEXT,
    center_id      UUID                        NOT NULL,
    CONSTRAINT pk_lesson PRIMARY KEY (id)
);

CREATE TABLE lesson_progress
(
    id           UUID                        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    status       VARCHAR(255)                NOT NULL,
    created_by   BIGINT,
    updated_by   BIGINT,
    lesson_id    UUID                        NOT NULL,
    student_id   BIGINT                      NOT NULL,
    center_id    UUID                        NOT NULL,
    completed    BOOLEAN,
    completed_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_lesson_progress PRIMARY KEY (id)
);

CREATE TABLE option
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    status      VARCHAR(255)                NOT NULL,
    created_by  BIGINT,
    updated_by  BIGINT,
    text        VARCHAR(255),
    question_id UUID,
    center_id   UUID                        NOT NULL,
    CONSTRAINT pk_option PRIMARY KEY (id)
);

CREATE TABLE question
(
    id           UUID                        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    status       VARCHAR(255)                NOT NULL,
    created_by   BIGINT,
    updated_by   BIGINT,
    title        VARCHAR(255),
    level        VARCHAR(255),
    image        VARCHAR(255),
    type         VARCHAR(255),
    right_answer VARCHAR(255),
    mark         INTEGER                     NOT NULL,
    topic        VARCHAR(255),
    subject_id   UUID,
    teacher_id   BIGINT,
    center_id    UUID                        NOT NULL,
    CONSTRAINT pk_question PRIMARY KEY (id)
);

CREATE TABLE quiz
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    status        VARCHAR(255)                NOT NULL,
    created_by    BIGINT,
    updated_by    BIGINT,
    title         VARCHAR(255),
    questions_num INTEGER                     NOT NULL,
    duration      BIGINT,
    start_time    TIMESTAMP WITHOUT TIME ZONE,
    end_time      TIMESTAMP WITHOUT TIME ZONE,
    course_id     UUID,
    teacher_id    BIGINT                      NOT NULL,
    center_id     UUID                        NOT NULL,
    CONSTRAINT pk_quiz PRIMARY KEY (id)
);

CREATE TABLE quiz_question
(
    question_id UUID NOT NULL,
    quiz_id     UUID NOT NULL,
    CONSTRAINT pk_quiz_question PRIMARY KEY (question_id, quiz_id)
);

CREATE TABLE quiz_results
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    status     VARCHAR(255)                NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    mark       BIGINT,
    quiz_id    UUID                        NOT NULL,
    student_id BIGINT                      NOT NULL,
    center_id  UUID                        NOT NULL,
    CONSTRAINT pk_quizresults PRIMARY KEY (id)
);

CREATE TABLE role
(
    name      VARCHAR(255) NOT NULL,
    privilege INTEGER      NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (name)
);

CREATE TABLE role_default_permissions
(
    default_permission_name VARCHAR(255) NOT NULL,
    role_id                 VARCHAR(255) NOT NULL,
    CONSTRAINT pk_role_default_permissions PRIMARY KEY (default_permission_name, role_id)
);

CREATE TABLE statistics
(
    id              UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    status          VARCHAR(255)                NOT NULL,
    created_by      BIGINT,
    updated_by      BIGINT,
    type            VARCHAR(255),
    statistic_key   VARCHAR(255)                NOT NULL,
    statistic_value DOUBLE PRECISION,
    statistic_date  date,
    center_id       UUID                        NOT NULL,
    CONSTRAINT pk_statistics PRIMARY KEY (id)
);

CREATE TABLE student
(
    id             BIGINT                      NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    status         VARCHAR(255)                NOT NULL,
    created_by     BIGINT,
    updated_by     BIGINT,
    full_name      VARCHAR(255),
    phone_number   VARCHAR(255),
    email          VARCHAR(255),
    image          VARCHAR(255),
    parent_contact VARCHAR(255),
    parent_chat_id VARCHAR(255),
    center_id      UUID                        NOT NULL,
    CONSTRAINT pk_student PRIMARY KEY (id)
);

CREATE TABLE student_answer
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    status      VARCHAR(255)                NOT NULL,
    created_by  BIGINT,
    updated_by  BIGINT,
    student_id  BIGINT,
    quiz_id     UUID,
    question_id UUID,
    answer      VARCHAR(4000),
    correct     BOOLEAN,
    score       INTEGER,
    graded_by   BIGINT,
    graded_at   TIMESTAMP WITHOUT TIME ZONE,
    center_id   UUID,
    CONSTRAINT pk_studentanswer PRIMARY KEY (id)
);

CREATE TABLE subject
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    status      VARCHAR(255)                NOT NULL,
    created_by  BIGINT,
    updated_by  BIGINT,
    name        VARCHAR(255),
    code        VARCHAR(255),
    description VARCHAR(1000),
    center_id   UUID                        NOT NULL,
    CONSTRAINT pk_subject PRIMARY KEY (id)
);

CREATE TABLE teacher
(
    id           BIGINT                      NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    status       VARCHAR(255)                NOT NULL,
    created_by   BIGINT,
    updated_by   BIGINT,
    full_name    VARCHAR(255),
    experience   VARCHAR(255),
    image        VARCHAR(255),
    email        VARCHAR(255),
    phone_number VARCHAR(255),
    title        VARCHAR(255),
    subject_id   UUID,
    center_id    UUID                        NOT NULL,
    CONSTRAINT pk_teacher PRIMARY KEY (id)
);

CREATE TABLE user_permission
(
    id         BIGINT                      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    status     VARCHAR(255)                NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    user_id    BIGINT                      NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_user_permission PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    role_id VARCHAR(255) NOT NULL,
    user_id BIGINT       NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

CREATE TABLE users
(
    id                BIGINT                      NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    status            VARCHAR(255)                NOT NULL,
    created_by        BIGINT,
    updated_by        BIGINT,
    username          VARCHAR(100)                NOT NULL,
    password          VARCHAR(255)                NOT NULL,
    full_name         VARCHAR(255),
    is_active         BOOLEAN                     NOT NULL,
    is_blocked        BOOLEAN                     NOT NULL,
    confirmation_code VARCHAR(255),
    fcm_token         VARCHAR(255),
    image         VARCHAR(255),
    attempts          INTEGER,
    center_id         UUID                        NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE written_questions
(
    id             UUID                        NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    status         VARCHAR(255)                NOT NULL,
    created_by     BIGINT,
    updated_by     BIGINT,
    question_id    UUID,
    quiz_id        UUID,
    student_answer VARCHAR(255),
    max_score      BIGINT,
    question_title VARCHAR(255),
    correct_answer VARCHAR(255),
    score          BIGINT,
    center_id      UUID                        NOT NULL,
    student_id     BIGINT                      NOT NULL,
    CONSTRAINT pk_writtenquestions PRIMARY KEY (id)
);

CREATE TABLE wrong_answers_analyze
(
    id           UUID                        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    status       VARCHAR(255)                NOT NULL,
    created_by   BIGINT,
    updated_by   BIGINT,
    question_id  UUID,
    quiz_id      UUID,
    wrong_answer VARCHAR(255),
    student_id   BIGINT                      NOT NULL,
    center_id    UUID                        NOT NULL,
    CONSTRAINT pk_wronganswersanalyze PRIMARY KEY (id)
);
CREATE TABLE lesson_comment
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    status     VARCHAR(255)                NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    content    VARCHAR(2000)               NOT NULL,
    lesson_id  UUID                        NOT NULL,
    user_id    BIGINT,
    center_id  UUID                        NOT NULL,
    CONSTRAINT pk_lesson_comment PRIMARY KEY (id)
);
CREATE TABLE attachment
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    status        VARCHAR(255)                NOT NULL,
    created_by    BIGINT,
    updated_by    BIGINT,
    original_name VARCHAR(255)                NOT NULL,
    stored_name   VARCHAR(255)                NOT NULL,
    title         VARCHAR(255)                NOT NULL,
    content_type  VARCHAR(255)                NOT NULL,
    size          BIGINT                      NOT NULL,
    type          VARCHAR(255),
    owner_type    VARCHAR(255),
    owner_id      UUID,
    uploaded_at   TIMESTAMP WITHOUT TIME ZONE,
    center_id     UUID                        NOT NULL,
    CONSTRAINT pk_attachment PRIMARY KEY (id)
);
CREATE TABLE badge
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    status      VARCHAR(255)                NOT NULL,
    created_by  BIGINT,
    updated_by  BIGINT,
    name        VARCHAR(255),
    description VARCHAR(1000),
    student_id  BIGINT,
    center_id   UUID                        NOT NULL,
    CONSTRAINT pk_badge PRIMARY KEY (id)
);
CREATE TABLE chat_room
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    status     VARCHAR(255)                NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    type       VARCHAR(255),
    title      VARCHAR(255),
    group_id   UUID,
    center_id  UUID,
    CONSTRAINT pk_chat_room PRIMARY KEY (id)
);
CREATE TABLE chat_room_member
(
    id                   UUID                        NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    status               VARCHAR(255)                NOT NULL,
    created_by           BIGINT,
    updated_by           BIGINT,
    room_id              UUID,
    user_id              BIGINT,
    role                 VARCHAR(255),
    muted                BOOLEAN,
    pinned               BOOLEAN,
    last_read_message_id UUID,
    center_id            UUID,
    CONSTRAINT pk_chat_room_member PRIMARY KEY (id)
);

ALTER TABLE chat_room_member
    ADD CONSTRAINT FK_CHAT_ROOM_MEMBER_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE badge
    ADD CONSTRAINT FK_BADGE_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE badge
    ADD CONSTRAINT FK_BADGE_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE attachment
    ADD CONSTRAINT FK_ATTACHMENT_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE admin
    ADD CONSTRAINT FK_ADMIN_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE attendance
    ADD CONSTRAINT FK_ATTENDANCE_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE attendance
    ADD CONSTRAINT FK_ATTENDANCE_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

ALTER TABLE attendance
    ADD CONSTRAINT FK_ATTENDANCE_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE enrollment
    ADD CONSTRAINT FK_ENROLLMENT_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE enrollment
    ADD CONSTRAINT FK_ENROLLMENT_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

ALTER TABLE enrollment
    ADD CONSTRAINT FK_ENROLLMENT_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE groups
    ADD CONSTRAINT FK_GROUPS_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE groups
    ADD CONSTRAINT FK_GROUPS_ON_TEACHER FOREIGN KEY (teacher_id) REFERENCES teacher (id);

ALTER TABLE groups
    ADD CONSTRAINT FK_GROUPS_ON_COURSE FOREIGN KEY (course_id) REFERENCES course (id);

ALTER TABLE course
    ADD CONSTRAINT FK_COURSE_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE course
    ADD CONSTRAINT FK_COURSE_ON_SUBJECT FOREIGN KEY (subject_id) REFERENCES subject (id);

ALTER TABLE lesson
    ADD CONSTRAINT FK_LESSON_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE lesson
    ADD CONSTRAINT FK_LESSON_ON_COURSE FOREIGN KEY (course_id) REFERENCES course (id);

ALTER TABLE lesson_progress
    ADD CONSTRAINT FK_LESSON_PROGRESS_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE lesson_progress
    ADD CONSTRAINT FK_LESSON_PROGRESS_ON_LESSON FOREIGN KEY (lesson_id) REFERENCES lesson (id);

ALTER TABLE lesson_progress
    ADD CONSTRAINT FK_LESSON_PROGRESS_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE option
    ADD CONSTRAINT FK_OPTION_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE option
    ADD CONSTRAINT FK_OPTION_ON_QUESTION FOREIGN KEY (question_id) REFERENCES question (id);

ALTER TABLE question
    ADD CONSTRAINT FK_QUESTION_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE question
    ADD CONSTRAINT FK_QUESTION_ON_SUBJECT FOREIGN KEY (subject_id) REFERENCES subject (id);

ALTER TABLE question
    ADD CONSTRAINT FK_QUESTION_ON_TEACHER FOREIGN KEY (teacher_id) REFERENCES teacher (id);

ALTER TABLE quiz_results
    ADD CONSTRAINT FK_QUIZRESULTS_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE quiz_results
    ADD CONSTRAINT FK_QUIZRESULTS_ON_QUIZ FOREIGN KEY (quiz_id) REFERENCES quiz (id);

ALTER TABLE quiz_results
    ADD CONSTRAINT FK_QUIZRESULTS_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE quiz
    ADD CONSTRAINT FK_QUIZ_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE quiz
    ADD CONSTRAINT FK_QUIZ_ON_COURSE FOREIGN KEY (course_id) REFERENCES course (id);

ALTER TABLE quiz
    ADD CONSTRAINT FK_QUIZ_ON_TEACHER FOREIGN KEY (teacher_id) REFERENCES teacher (id);

ALTER TABLE statistics
    ADD CONSTRAINT FK_STATISTICS_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE student_answer
    ADD CONSTRAINT FK_STUDENTANSWER_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE student
    ADD CONSTRAINT FK_STUDENT_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE subject
    ADD CONSTRAINT FK_SUBJECT_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE teacher
    ADD CONSTRAINT FK_TEACHER_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE teacher
    ADD CONSTRAINT FK_TEACHER_ON_SUBJECT FOREIGN KEY (subject_id) REFERENCES subject (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE written_questions
    ADD CONSTRAINT FK_WRITTENQUESTIONS_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE written_questions
    ADD CONSTRAINT FK_WRITTENQUESTIONS_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE wrong_answers_analyze
    ADD CONSTRAINT FK_WRONGANSWERSANALYZE_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE wrong_answers_analyze
    ADD CONSTRAINT FK_WRONGANSWERSANALYZE_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE group_student
    ADD CONSTRAINT fk_grostu_on_grouping FOREIGN KEY (group_id) REFERENCES groups (id);

ALTER TABLE group_student
    ADD CONSTRAINT fk_grostu_on_student FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE quiz_question
    ADD CONSTRAINT fk_quique_on_question FOREIGN KEY (question_id) REFERENCES question (id);

ALTER TABLE quiz_question
    ADD CONSTRAINT fk_quique_on_quiz FOREIGN KEY (quiz_id) REFERENCES quiz (id);

ALTER TABLE role_default_permissions
    ADD CONSTRAINT fk_roldefper_on_default_permission_entity FOREIGN KEY (default_permission_name) REFERENCES default_permission_entity (name);

ALTER TABLE role_default_permissions
    ADD CONSTRAINT fk_roldefper_on_role_entity FOREIGN KEY (role_id) REFERENCES role (name);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role_entity FOREIGN KEY (role_id) REFERENCES role (name);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user_entity FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE lesson_comment
    ADD CONSTRAINT FK_LESSON_COMMENT_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE lesson_comment
    ADD CONSTRAINT FK_LESSON_COMMENT_ON_LESSON FOREIGN KEY (lesson_id) REFERENCES lesson (id);

ALTER TABLE lesson_comment
    ADD CONSTRAINT FK_LESSON_COMMENT_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);