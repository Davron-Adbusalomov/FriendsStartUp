package com.example.demo.management.dto;

import java.util.List;

public class TeacherDTO {
    private Long id;

    private String name;

    private Long age;

    private String email;

    private Long phone_num;

    private String username;

    private String password;

    private List<GroupDTO> groupDTOS;

    private List<StudentDTO> studentDTOList;

    public TeacherDTO(Long id, String name, Long age, String email, Long phone_num, String username, String password, List<GroupDTO> groupDTOS, List<StudentDTO> studentDTOList) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone_num = phone_num;
        this.username = username;
        this.password = password;
        this.groupDTOS = groupDTOS;
        this.studentDTOList = studentDTOList;
    }

    public TeacherDTO(){

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(Long phone_num) {
        this.phone_num = phone_num;
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

    public List<GroupDTO> getGroupDTOS() {
        return groupDTOS;
    }

    public void setGroupDTOS(List<GroupDTO> groupDTOS) {
        this.groupDTOS = groupDTOS;
    }

    public List<StudentDTO> getStudentDTOList() {
        return studentDTOList;
    }

    public void setStudentDTOList(List<StudentDTO> studentDTOList) {
        this.studentDTOList = studentDTOList;
    }
}
