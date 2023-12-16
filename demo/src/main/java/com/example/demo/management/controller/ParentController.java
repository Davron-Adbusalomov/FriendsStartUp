package com.example.demo.management.controller;

import com.example.demo.management.dto.ParentDTO;
import com.example.demo.management.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/parent")
public class ParentController {
    @Autowired
    private ParentService parentService;

    @GetMapping("/getAllParents")
    public ResponseEntity<?> getAll(){
        return parentService.getParents();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable Long id){
        return parentService.getParentByStudentId(id);
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteGroupById(@PathVariable Long id){
        return parentService.deleteParent(id);
    }

    @PutMapping("/updateParent/{id}")
    public ResponseEntity<?> updateGroup(@RequestBody ParentDTO parentDTO, @PathVariable Long id){
        return parentService.updateParent(parentDTO, id);
    }
}
