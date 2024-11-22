package com.kennn.electronicstore.controller.admin;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.kennn.electronicstore.domain.Role;
import com.kennn.electronicstore.domain.User;
import com.kennn.electronicstore.domain.dto.UserDTO;
import com.kennn.electronicstore.service.RoleService;
import com.kennn.electronicstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/user")
public class UserController {
    private UserService userService;
    private RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping({ "", "/" })
    public String getManageUserPage(Model model) {
        List<User> users = this.userService.findAll();
        model.addAttribute("users", users);
        return "admin/page/user/index";
    }

    @GetMapping("/create")
    public String getCreateUserPage(Model model) {
        UserDTO newUser = new UserDTO();
        List<Role> roles = this.roleService.findAll();
        model.addAttribute("roles", roles);
        model.addAttribute("newUser", newUser);
        return "admin/page/user/create";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("newUser") UserDTO newUser, BindingResult result, Model model) {
        if (newUser.getAvatar().isEmpty()) {
            result.addError(new FieldError("newUser", "avatar", "Avatar không được để trống"));
        }
        if (result.hasErrors()) {
            List<Role> roles = this.roleService.findAll();
            model.addAttribute("roles", roles);
            return "admin/page/user/create";
        }

        User existedUser = this.userService.findByEmail(newUser.getEmail());

        if (existedUser != null) {
            result.rejectValue("email", "error.newUser", "This email is already registered.");
            List<Role> roles = this.roleService.findAll();
            model.addAttribute("roles", roles);
            return "admin/page/user/create";
        }
        MultipartFile avatarFile = newUser.getAvatar();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + avatarFile.getOriginalFilename();

        try {
            String uploadDir = "public/images/avatar/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = avatarFile.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        Optional<Role> optionalRole = this.roleService.findById(newUser.getRole().getId());
        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();
            User user = new User();
            user.setFullName(newUser.getFullName());
            user.setEmail(newUser.getEmail());
            user.setPassword(newUser.getPassword());
            user.setAddress(newUser.getAddress());
            user.setAvatar(storageFileName);
            user.setRole(role);
            user.setStatus(newUser.isStatus());

            this.userService.save(user);
        }

        return "redirect:/admin/user";
    }

    @GetMapping("/update/{id}")
    public String getUpdateUserPage(@PathVariable long id, Model model) {
        try {
            Optional<User> optionalUser = this.userService.findById(id);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                UserDTO updateUser = new UserDTO();
                updateUser.setFullName(user.getFullName());
                updateUser.setEmail(user.getEmail());
                updateUser.setRole(user.getRole());
                updateUser.setStatus(user.isStatus());
                updateUser.setAddress(user.getAddress());
                updateUser.setPassword(user.getPassword());
                model.addAttribute("user", user);
                model.addAttribute("updateUser", updateUser);
            }
            List<Role> roles = this.roleService.findAll();
            model.addAttribute("roles", roles);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "redirect:/admin/user";
        }
        return "admin/page/user/update";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam("id") long id, @Valid @ModelAttribute("updateUser") UserDTO userDTO,
            BindingResult result, Model model) {
        try {
            Optional<User> optionalUser = this.userService.findById(id);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (result.hasErrors()) {
                    List<Role> roles = this.roleService.findAll();
                    model.addAttribute("roles", roles);
                    return "admin/page/user/update";
                }

                User existedUser = this.userService.findByEmail(userDTO.getEmail());

                if (existedUser != null) {
                    result.rejectValue("email", "error.userDTO", "This email is already registered.");
                    List<Role> roles = this.roleService.findAll();
                    model.addAttribute("roles", roles);
                    return "admin/page/user/create";
                }
                if (!userDTO.getAvatar().isEmpty()) {
                    String uploadDir = "public/images/avatar/";
                    Path oldPath = Paths.get(uploadDir + user.getAvatar());
                    try {
                        Files.delete(oldPath);
                    } catch (Exception e) {
                        System.out.println("Exception: " + e.getMessage());
                    }
                    MultipartFile avatar = userDTO.getAvatar();
                    Date createdAt = new Date();
                    String storageFileName = createdAt.getTime() + "_" + avatar.getOriginalFilename();
                    try (InputStream inputStream = avatar.getInputStream()) {
                        Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                    user.setAvatar(storageFileName);
                }
                user.setAddress(userDTO.getAddress());
                user.setEmail(userDTO.getEmail());
                user.setFullName(userDTO.getFullName());
                user.setRole(this.roleService.findById(userDTO.getRole().getId()).get());
                user.setStatus(userDTO.isStatus());

                this.userService.save(user);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return "redirect:/admin/user";
    }

    @GetMapping("/delete/{id}")
    public String getDeleteUserPage(@PathVariable long id, Model model) {
        try {
            Optional<User> optionalUser = this.userService.findById(id);
            if (optionalUser.isPresent()) {
                User deleteUser = optionalUser.get();
                model.addAttribute("deleteUser", deleteUser);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "redirect:/admin/user";
        }
        return "admin/page/user/delete";
    }

    @PostMapping("/delete")
    public String deleteUser(@ModelAttribute("deleteUser") User user) {
        Optional<User> optionalUser = this.userService.findById(user.getId());
        if (optionalUser.isPresent()) {
            User deleteUser = optionalUser.get();
            String uploadDir = "public/images/avatar/";

            Path oldPath = Paths.get(uploadDir + deleteUser.getAvatar());
            try {
                Files.delete(oldPath);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            this.userService.remove(deleteUser);
        }

        return "redirect:/admin/user";
    }

    @GetMapping("/change-status/{id}")
    public String changeStatusUser(@PathVariable long id) {
        Optional<User> optionalUser = this.userService.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus(!user.isStatus());

            this.userService.save(user);
        }
        return "redirect:/admin/user";
    }

}
