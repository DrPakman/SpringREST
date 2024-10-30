package com.pak.springrest.controllers;

import com.pak.springrest.models.Role;
import com.pak.springrest.models.User;
import com.pak.springrest.service.RoleService;
import com.pak.springrest.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("")
    public String index(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("user", new User());
        model.addAttribute("currentUser", currentUser);
        return "users/index";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        return "users/index";
    }

}
