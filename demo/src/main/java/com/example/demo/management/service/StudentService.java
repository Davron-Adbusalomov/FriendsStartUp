package com.example.demo.management.service;

import com.example.demo.management.dto.StudentDTO;
import com.example.demo.management.mapper.StudentMapper;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    StudentRepository studentRepository;

    public ArrayList<Student> getGroups(){return (ArrayList<Student>) studentRepository.findAll();}

    public Student getGroupById(Long studentID){
        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new EntityNotFoundException("Not found group with id: "+studentID));
        return student;
    }

    public Student addStudent(StudentDTO studentDTO){return studentRepository.save(StudentMapper.INSTANCE.toModel(studentDTO));}

    public void deleteStudent(Long studentID){
        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new EntityNotFoundException("Not found group with id: "+studentID));
        studentRepository.delete(student);
    }

    public Student updateStudent(StudentDTO studentDTO, Long studentID){
        Optional<Student> studentOptional = studentRepository.findById(studentID);
        if (studentOptional.isEmpty()){
            throw new EntityNotFoundException("Not found group with id: "+studentID);
        }
        else {
            Student student = studentOptional.get();
            return studentRepository.save(StudentMapper.INSTANCE.toModel(studentDTO));
        }
    }

}