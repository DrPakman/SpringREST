package com.pak.springrest.service;

import com.pak.springrest.models.Role;
import com.pak.springrest.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    public List<Role> findRolesByNames(List<String> roleNames) {
        return roleRepository.findByRoleNameIn(roleNames);
    }
}
