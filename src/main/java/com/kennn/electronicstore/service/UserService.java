package com.kennn.electronicstore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kennn.electronicstore.domain.User;
import com.kennn.electronicstore.domain.dto.RegisterDTO;
import com.kennn.electronicstore.repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public User save(User user) {
        return this.userRepository.save(user);
    }

    public Optional<User> findById(long id) {
        return this.userRepository.findById(id);
    }

    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public void remove(User user) {
        this.userRepository.delete(user);
    }

    public User convertDTOToUser(RegisterDTO registerDTO) {
        User user = new User();

        user.setFullName(registerDTO.getFirstname() + " " + registerDTO.getLastname());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        user.setAddress(registerDTO.getAddress());

        this.userRepository.save(user);
        return user;
    }
}
