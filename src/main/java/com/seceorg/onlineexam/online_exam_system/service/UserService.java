package com.seceorg.onlineexam.online_exam_system.service;

import com.seceorg.onlineexam.online_exam_system.model.User;
import com.seceorg.onlineexam.online_exam_system.model.Role;
import com.seceorg.onlineexam.online_exam_system.repository.UserRepository;
import com.seceorg.onlineexam.online_exam_system.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }
    
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    @Transactional
    public void initializeRoles() {
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("System Administrator");
            roleRepository.save(adminRole);
        }
        if (roleRepository.findByName("TEACHER").isEmpty()) {
            Role teacherRole = new Role();
            teacherRole.setName("TEACHER");
            teacherRole.setDescription("Teacher/Instructor");
            roleRepository.save(teacherRole);
        }
        if (roleRepository.findByName("STUDENT").isEmpty()) {
            Role studentRole = new Role();
            studentRole.setName("STUDENT");
            studentRole.setDescription("Student");
            roleRepository.save(studentRole);
        }
    }
    
    @Transactional
    public void createDefaultAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
            if (adminRole != null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setEmail("admin@exam.com");
                admin.setFullName("System Administrator");
                admin.setRole(adminRole);
                userRepository.save(admin);
            }
        }
    }
}