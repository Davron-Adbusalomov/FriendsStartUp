package com.example.demo.management.service;

import com.example.demo.management.dto.ParentDTO;
import com.example.demo.management.model.Parent;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.ParentRepository;
import com.example.demo.management.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ParentService {
    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private StudentRepository studentRepository;

    public ResponseEntity<?> getParents(){return ResponseEntity.status(HttpStatus.OK).body(parentRepository.findAll());}

    public ResponseEntity<?> getParentByStudentId(Long id){
        Student student = studentRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("No student with this id: "+id));

        Parent parent = parentRepository.findById(student.getParent().getId())
                .orElseThrow(()->new EntityNotFoundException("No parent found with this student ID: "+id));

        return ResponseEntity.status(HttpStatus.OK).body(parent);
    }


    public ResponseEntity<?> updateParent(ParentDTO parentDTO, Long id){
        Parent parent = parentRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("No parent found with this id:"+id));

        parent.setName(parentDTO.getName());
        parent.setEmail(parentDTO.getEmail());
        parent.setPhone_number(parentDTO.getPhone_number());
        parent.setStudents(parentDTO.getStudentDTOS());

        parentRepository.save(parent);
        return ResponseEntity.status(HttpStatus.OK).body(parent);
    }

    public ResponseEntity deleteParent(Long id){
        Parent parent = parentRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("No parent found with this id:"+id));

        parentRepository.delete(parent);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted!");
    }

}
