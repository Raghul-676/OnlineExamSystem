package com.seceorg.onlineexam.online_exam_system.service;

import com.seceorg.onlineexam.online_exam_system.model.User;
import com.seceorg.onlineexam.online_exam_system.model.Role;
import com.seceorg.onlineexam.online_exam_system.repository.UserRepository;
import com.seceorg.onlineexam.online_exam_system.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    public User authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password) && user.getIsActive()) {
                return user;
            }
        }
        return null;
    }
    
    public User register(String username, String password, String email, String fullName, String roleName) {
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            return null;
        }
        
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) {
            return null;
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);
        
        return userRepository.save(user);
    }
    
    public void login(HttpSession session, User user) {
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole().getName());
    }
    
    public void logout(HttpSession session) {
        session.invalidate();
    }
    
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
    
    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }
    
    public boolean hasRole(HttpSession session, String roleName) {
        String userRole = (String) session.getAttribute("userRole");
        return roleName.equals(userRole);
    }
}