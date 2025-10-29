package com.seceorg.onlineexam.online_exam_system.controller;

import com.seceorg.onlineexam.online_exam_system.model.User;
import com.seceorg.onlineexam.online_exam_system.model.Role;
import com.seceorg.onlineexam.online_exam_system.service.UserService;
import com.seceorg.onlineexam.online_exam_system.service.AuthService;
import com.seceorg.onlineexam.online_exam_system.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @GetMapping("/users")
    public String manageUsers(Model model, HttpSession session) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "ADMIN")) {
            return "redirect:/auth/login";
        }
        
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("roles", roleRepository.findAll());
        
        return "admin/users";
    }
    
    @PostMapping("/users/{id}/toggle")
    public String toggleUserStatus(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "ADMIN")) {
            return "redirect:/auth/login";
        }
        
        User user = userService.getUserById(id).orElse(null);
        if (user != null) {
            user.setIsActive(!user.getIsActive());
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("success", "User status updated successfully!");
        }
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id, @RequestParam Long roleId, 
                                HttpSession session, RedirectAttributes redirectAttributes) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "ADMIN")) {
            return "redirect:/auth/login";
        }
        
        User user = userService.getUserById(id).orElse(null);
        Role role = roleRepository.findById(roleId).orElse(null);
        
        if (user != null && role != null) {
            user.setRole(role);
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("success", "User role updated successfully!");
        }
        
        return "redirect:/admin/users";
    }
}