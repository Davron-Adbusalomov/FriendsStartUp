package com.example.demo.management.service;

import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.repository.GroupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {
    @Autowired
    GroupRepository groupRepository;

    public ResponseEntity<List<Grouping>> getGroups(){return ResponseEntity.status(HttpStatus.OK).body( groupRepository.findAll());}

    public ResponseEntity<?> getGroupById(Long groupID){
        Grouping grouping = groupRepository.findById(groupID)
                .orElseThrow(() -> new EntityNotFoundException("Not found group with id: "+groupID));
        return ResponseEntity.status(HttpStatus.OK).body(grouping);
    }

    public ResponseEntity<?> deleteGroup(Long groupId){
        Grouping grouping = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Not found group with id: "+groupId));
        groupRepository.delete(grouping);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted!");
    }

    public ResponseEntity<?> updateGroup(GroupDTO groupDTO, Long groupId){
        Optional<Grouping> group =groupRepository.findById(groupId);
        if(group.isEmpty()){
            throw new EntityNotFoundException("Not found group with id: "+groupId);
        }
        else{
            Grouping grouping1 = group.get();
            grouping1.setId(groupId);
            grouping1.setName(groupDTO.getName());
            grouping1.setSubject(groupDTO.getSubject());
            grouping1.setTeacher(groupDTO.getTeacherDTO());
            grouping1.setStudents(groupDTO.getStudentDTOList());
            grouping1.setTests(groupDTO.getTests());

            groupRepository.save(grouping1);
            return ResponseEntity.status(HttpStatus.OK).body(grouping1);
        }
    }
}
