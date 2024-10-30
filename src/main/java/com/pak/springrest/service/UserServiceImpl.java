package com.pak.springrest.service;

import com.pak.springrest.models.Role;
import com.pak.springrest.models.User;
import com.pak.springrest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    @Transactional
    @Override
    public User saveUser(User user) {
        // Хешируем пароль, если он указан
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(User user) {
        // Получаем существующего пользователя по ID
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // Проверяем, изменен ли email и его уникальность
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail()) &&
                userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email уже существует.");
        }

        // Устанавливаем новые значения для обновления
        existingUser.setUsername(user.getUsername());
        existingUser.setLastname(user.getLastname());
        existingUser.setAge(user.getAge());

        // Проверяем и обновляем пароль, если он указан и не совпадает с текущим
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }

        // Устанавливаем роли, если они указаны
        if (user.getRoles() != null) {
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
            List<Role> roles = roleService.findRolesByNames(roleNames);
            existingUser.setRoles(roles);
        }
        return userRepository.save(existingUser);
    }


    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}