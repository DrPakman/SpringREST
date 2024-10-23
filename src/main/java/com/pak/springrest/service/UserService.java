package com.pak.springrest.service;
import com.pak.springrest.models.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User saveUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
}
