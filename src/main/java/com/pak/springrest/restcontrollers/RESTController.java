package com.pak.springrest.restcontrollers;

import com.pak.springrest.models.Role;
import com.pak.springrest.models.User;
import com.pak.springrest.service.RoleService;
import com.pak.springrest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RESTController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;


    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        // Получаем имена ролей
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName) // Предполагается, что getName() возвращает имя роли
                .collect(Collectors.toList());

        // Загружаем роли из базы данных
        List<Role> roles = roleService.findRolesByNames(roleNames);

        // Устанавливаем загруженные роли пользователю
        user.setRoles(roles);

        // Сохраняем пользователя
        userService.saveUser(user);

        return user;
    }
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        // Получаем существующего пользователя по ID
        User existingUser = userService.getUserById(id);

        // Проверяем, изменена ли почта
        if (!existingUser.getEmail().equals(user.getEmail())) {
            // Если почта изменена, проверяем уникальность
            if (userService.existsByEmail(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Конфликт: почта уже существует
            }
        }

        // Обновляем роли
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        List<Role> roles = roleService.findRolesByNames(roleNames);
        user.setRoles(roles);

        // Устанавливаем ID существующего пользователя для обновления
        user.setId(id);

        // Обновляем пользователя
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User with ID = " + id + " was deleted";
    }


}
