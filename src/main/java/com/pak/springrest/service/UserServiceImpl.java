package com.pak.springrest.service;

import com.pak.springrest.models.User;
import com.pak.springrest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        return userRepository.save(user); // Сохраняем обновленного пользователя
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