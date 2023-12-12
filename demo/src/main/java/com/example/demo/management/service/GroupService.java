package com.example.demo.management.service;

import com.example.demo.management.dto.GroupDTO;
import com.example.demo.management.mapper.GroupMapper;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.repository.GroupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class GroupService {
    @Autowired
    GroupRepository groupRepository;

    public ArrayList<Grouping> getGroups(){return (ArrayList<Grouping>) groupRepository.findAll();}

    public Grouping getGroupById(Long groupID){
        Grouping grouping = groupRepository.findById(groupID)
                .orElseThrow(() -> new EntityNotFoundException("Not found group with id: "+groupID));
        return grouping;
    }

    public Grouping addGroup(GroupDTO groupDTO){
        return groupRepository.save(GroupMapper.INSTANCE.toModel(groupDTO));
    }

    public void deleteGroup(Long groupId){
        Grouping grouping = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Not found group with id: "+groupId));
        groupRepository.delete(grouping);
    }

    public Grouping updateGroup(GroupDTO groupDTO, Long groupId){
        Optional<Grouping> group =groupRepository.findById(groupId);
        if(group.isEmpty()){
            throw new EntityNotFoundException("Not found group with id: "+groupId);
        }
        else{
            Grouping grouping1 = group.get();
            return groupRepository.save(GroupMapper.toModel(groupDTO, grouping1));
        }
    }
}
