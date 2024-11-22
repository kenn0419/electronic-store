package com.kennn.electronicstore.controller.client;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.kennn.electronicstore.domain.User;
import com.kennn.electronicstore.domain.dto.RegisterDTO;
import com.kennn.electronicstore.service.RoleService;
import com.kennn.electronicstore.service.UserService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private UserService userService;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        RegisterDTO user = new RegisterDTO();

        model.addAttribute("user", user);
        return "client/page/auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") RegisterDTO registerDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "client/page/auth/register";
        }

        User existedUser = this.userService.findByEmail(registerDTO.getEmail());

        if (existedUser != null) {
            result.rejectValue("email", "error.registerDTO", "This email is already registered.");
            return "client/page/auth/register";
        }

        User user = this.userService.convertDTOToUser(registerDTO);

        String hashPassword = this.passwordEncoder.encode(user.getPassword());

        user.setPassword(hashPassword);
        user.setRole(this.roleService.findByName("USER"));

        this.userService.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "client/page/auth/login";
    }

    @GetMapping("/access-deny")
    public String getDenyPage() {
        return "client/page/auth/deny";
    }

}
