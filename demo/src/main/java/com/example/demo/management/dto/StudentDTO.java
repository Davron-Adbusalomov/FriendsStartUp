    package com.example.demo.management.dto;

    import com.example.demo.management.model.Grouping;
    import com.example.demo.management.model.Teacher;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.util.List;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class StudentDTO {

        private Long id;

        private String name;

        private Long age;

        private Long number;

        private String email;

        private String username;

        private String password;

        private String role;

        private List<Grouping> grouping;

        private List<Teacher> teacherDTOS;

    }
