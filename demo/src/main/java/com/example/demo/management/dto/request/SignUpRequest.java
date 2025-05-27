package com.example.demo.management.dto.request;

//import io.swagger.v3.oas.annotations.media.Schema;
import com.example.demo.management.dto.RoleDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
//import uz.duol.ecopharm.module.employee.dto.EmployeeDto;
//import uz.duol.ecopharm.module.role.dto.RoleDto;

import java.util.List;
import java.util.Set;

@Data
@ToString
//@Schema(description = "Sign up details")
public class SignUpRequest {
    @NotNull(message = "username is required")
    private String username;
    private String fullName;
//    @NotNull(message = "Role is required")
    private Set<String> roles;
    private List<String> groups;
    private boolean isSendConfirmationCode = false;

//    @NotNull
//    private EmployeeDto employeeData;
}
