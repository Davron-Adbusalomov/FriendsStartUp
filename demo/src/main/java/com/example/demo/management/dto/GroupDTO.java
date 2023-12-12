package com.example.demo.management.dto;


import java.util.List;

public class GroupDTO {

    private Long id;

    private String name;

    private String subject;

    private TeacherDTO teacherDTO;

    private List<StudentDTO> studentDTOList;

    public GroupDTO(Long id, String name, String subject, TeacherDTO teacherDTO, List<StudentDTO> studentDTOList) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.teacherDTO = teacherDTO;
        this.studentDTOList = studentDTOList;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public TeacherDTO getTeacherDTO() {
        return teacherDTO;
    }

    public void setTeacherDTO(TeacherDTO teacherDTO) {
        this.teacherDTO = teacherDTO;
    }

    public List<StudentDTO> getStudentDTOList() {
        return studentDTOList;
    }

    public void setStudentDTOList(List<StudentDTO> studentDTOList) {
        this.studentDTOList = studentDTOList;
    }
}
