package com.kennn.electronicstore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kennn.electronicstore.domain.Role;
import com.kennn.electronicstore.repository.RoleRepository;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAll() {
        return this.roleRepository.findAll();
    }

    public Optional<Role> findById(long id) {
        return this.roleRepository.findById(id);
    }

    public Role findByName(String name) {
        return this.roleRepository.findByName(name);
    }
}
