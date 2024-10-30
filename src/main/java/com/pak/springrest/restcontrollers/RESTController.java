package com.pak.springrest.restcontrollers;

import com.pak.springrest.models.Role;
import com.pak.springrest.models.User;
import com.pak.springrest.service.RoleService;
import com.pak.springrest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RESTController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        // Проверяем, указаны ли роли
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Roles must be specified");
        }

        // Получаем имена ролей и загружаем роли из базы данных
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        List<Role> roles = roleService.findRolesByNames(roleNames);
        user.setRoles(roles);

        // Проверяем, указан ли email, если нет — создаем временный
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            String tempEmail = "temp_" + UUID.randomUUID() + "@example.com";
            user.setEmail(tempEmail);
        }

        // Сохраняем пользователя
        userService.saveUser(user);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
            user.setId(id);
            return ResponseEntity.ok(userService.updateUser(user));
    }
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User with ID = " + id + " was deleted";
    }
}
