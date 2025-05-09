package com.example.demo.management.controller;

import com.example.demo.management.service.UserPermissionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user_permissions")
public class UserPermissionsController {

    private final UserPermissionsService userPermissionsService;

}
