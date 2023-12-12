    package com.example.demo.management.dto;

    import com.example.demo.management.model.Grouping;

    import java.util.List;

    public class StudentDTO {

        private Long id;

        private String name;

        private Long age;

        private Long number;

        private String email;

        private String username;

        private String password;

        private String role;

        private Grouping grouping;

        private List<TeacherDTO> teacherDTOS;

        public StudentDTO(Long id, String name, Long age, Long number, String email, String username, String password, String role, Grouping grouping, List<TeacherDTO> teacherDTOS) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.number = number;
            this.email = email;
            this.username = username;
            this.password = password;
            this.role = role;
            this.grouping = grouping;
            this.teacherDTOS = teacherDTOS;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getAge() {
            return age;
        }

        public void setAge(Long age) {
            this.age = age;
        }

        public Long getNumber() {
            return number;
        }

        public void setNumber(Long number) {
            this.number = number;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Grouping getGroup() {
            return grouping;
        }

        public void setGroup(Grouping grouping) {
            this.grouping = grouping;
        }

        public List<TeacherDTO> getTeacherDTOS() {
            return teacherDTOS;
        }

        public void setTeacherDTOS(List<TeacherDTO> teacherDTOS) {
            this.teacherDTOS = teacherDTOS;
        }
    }
