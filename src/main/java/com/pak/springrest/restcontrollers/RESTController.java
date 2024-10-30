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
        // Получаем существующего пользователя по ID
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Если пользователь не найден
        }

        // Проверяем, изменен ли email и его уникальность
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail()) &&
                userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Устанавливаем новые значения для обновления
        existingUser.setUsername(user.getUsername());
        existingUser.setLastname(user.getLastname());
        existingUser.setAge(user.getAge());

// Проверяем и обновляем пароль, если он указан и не совпадает с текущим
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // Проверяем, отличается ли новый пароль от текущего пароля
            if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                // Если отличается, хешируем новый пароль
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            // Если пароли совпадают, ничего не делаем с паролем
        } else {
            // Если пароль не указан, оставляем его без изменений
            existingUser.setPassword(existingUser.getPassword());
        }


        // Устанавливаем роли, если они указаны
        if (user.getRoles() != null) {
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
            List<Role> roles = roleService.findRolesByNames(roleNames);
            existingUser.setRoles(roles);
        }

        // Сохраняем обновленного пользователя
        User updatedUser = userService.updateUser(existingUser);
        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User with ID = " + id + " was deleted";
    }
}
