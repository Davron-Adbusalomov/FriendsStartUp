package com.example.demo.management.service;

import com.example.demo.management.authentication.enums.RolesEnum;
import com.example.demo.management.dto.AdminDTO;
import com.example.demo.management.mapper.AdminMapper;
import com.example.demo.management.model.Admin;
import com.example.demo.management.model.rbac.RoleEntity;
import com.example.demo.management.repository.AdminRepository;
import com.example.demo.management.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final UserRepository userRepository;

    public AdminService(AdminRepository adminRepository, AdminMapper adminMapper, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
        this.userRepository = userRepository;
    }

    public Page<AdminDTO> getAdmins(Pageable pageable) {
        Page<Admin> adminPage = adminRepository.findAll(pageable);

        List<AdminDTO> adminDTOs = adminPage.stream().map(admin -> {
            AdminDTO dto = adminMapper.toDTO(admin);
            dto.setRoles(getRoles(admin));
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(adminDTOs, pageable, adminPage.getTotalElements());
    }


    public ResponseEntity<Admin> getAdminById(Long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new EntityNotFoundException("Admin not found with id: " + adminId));
        AdminDTO adminDTO = adminMapper.toDTO(admin);
        adminDTO.setRoles(getRoles(admin));
        return ResponseEntity.ok(admin);
    }

    public AdminDTO addAdmin(AdminDTO adminDTO) throws Exception {
        if (adminRepository.findByUsername(adminDTO.getUsername()).isPresent()) {
            throw new Exception("Username already taken!");
        }

        Admin admin = adminMapper.toModel(adminDTO);
        admin = adminRepository.save(admin);
        AdminDTO savedDTO = adminMapper.toDTO(admin);
        savedDTO.setRoles(getRoles(admin));

        return savedDTO;
    }

    public ResponseEntity<Void> deleteById(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new EntityNotFoundException("Admin not found with id: " + id);
        }

        adminRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Admin> updateAdmin(AdminDTO adminDTO, Long id) throws Exception {
        Admin existingAdmin = adminRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Admin not found with id: " + adminDTO.getId()));

        Optional<Admin> usernameConflict = adminRepository.findByUsername(adminDTO.getUsername());
        if (usernameConflict.isPresent() && !usernameConflict.get().getId().equals(adminDTO.getId())) {
            throw new Exception("Username already taken!");
        }

        existingAdmin.setFullName(adminDTO.getFullName());
        existingAdmin.setUsername(adminDTO.getUsername());
//        existingAdmin.setPassword(adminDTO.getPassword());

        Admin updatedAdmin = adminRepository.save(existingAdmin);
        return ResponseEntity.ok(updatedAdmin);
    }

    private List<RolesEnum> getRoles(Admin admin) {
        if (admin == null || admin.getId() == null) return Collections.emptyList();

        Set<RoleEntity> roles = userRepository.findRolesByUserId(admin.getId());
        return roles == null ? Collections.emptyList() : roles.stream().map(RoleEntity::getName).filter(Objects::nonNull).collect(Collectors.toList());
    }

//    public StudentDTO registerStudent(StudentDTO studentDTO) throws Exception {
//        if (studentRepository.findByUsername(studentDTO.getUsername()).isPresent()){
//            throw new Exception("User already exists!");
//        }
//        Student student = studentRepository.save(StudentMapper.toModel(studentDTO));
//
//        Optional<Grouping> grouping = groupRepository.findByName(studentDTO.getGroupName());
//        if (grouping.isEmpty()){
//            throw new EntityNotFoundException("No group found with this name!");
//        }
//
//        Grouping group=grouping.get();
//        group.assignStudent(student);
//        groupRepository.save(group);
//
//        return StudentMapper.toDTO(student);
//    }
//
//    public TeacherDTO registerTeacher(TeacherDTO teacherDTO) throws Exception {
//        if (teacherRepository.findByUsername(teacherDTO.getUsername()).isPresent()){
//            throw new Exception("User already exists");
//        }
//        BufferedImage img = null;
//        if (teacherDTO.getImage().isEmpty()) {
//            String base64Image = teacherDTO.getImage().split(",")[1];
//            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
//            img = ImageIO.read(new ByteArrayInputStream(imageBytes));
//        }
//        Teacher teacher = new Teacher();
//        teacher.setName(teacherDTO.getName());
//        teacher.setSubject(teacherDTO.getSubject());
//        teacher.setExperience(teacherDTO.getExperience());
//        teacher.setPhone_num(teacherDTO.getPhone_num());
//        if (img!=null){
//        teacher.setImage(mediaService.uploadImageToAzureAndGetUrl(img, "teacher"+ UUID.randomUUID()));
//        }
//        teacherRepository.save(teacher);
//
//        Optional<Grouping> grouping = groupRepository.findByName(teacherDTO.getGroupName());
//        if (grouping.isEmpty()){
//            throw new EntityNotFoundException("No group found with this name!");
//        }
//
//        Grouping group=grouping.get();
//        group.assignTeacher(teacher);
//        groupRepository.save(group);
//
//        return TeacherMapper.toDTO(teacher);
//    }

//    public GroupDTO registerGroup(GroupDTO groupDTO) throws Exception {
//        if (groupRepository.findByName(groupDTO.getName()).isPresent()){
//            throw new Exception("Group already existed!");
//        }
//        Grouping group = groupRepository.save(groupMapper.toEntity(groupDTO));
//        return groupMapper.toDto(group);
//    }


//    public AdminLoginDTO loginAdmin(AdminDTO adminDTO){
//        try {
//            if (adminRepository.findByUsername(adminDTO.getUsername()).isEmpty() || !Objects.equals(adminRepository.findByUsername(adminDTO.getUsername()).get().getPassword(), adminDTO.getPassword())){
//                throw new EntityNotFoundException("There is no admin with this credentials!");
//            }
//            var admin = adminRepository.findByUsername(adminDTO.getUsername())
//                    .orElseThrow(() -> new RuntimeException("Admin not found"));
//
//            String token = jwtService.generateToken(admin);
//
//            AdminLoginDTO adminLoginDTO = new AdminLoginDTO();
//            adminLoginDTO.setUser(AdminMapper.INSTANCE.toDTO(admin));
//            adminLoginDTO.setToken(token);
//
//            return adminLoginDTO;
//        }
//        catch (Exception e) {
//            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
//        }
//    }
}
