package com.iss.fitness.service;

import com.iss.fitness.domain.user.Role;
import com.iss.fitness.domain.user.User;
import com.iss.fitness.repository.UserRepository;
import com.iss.fitness.repository.WorkoutRepository;
import com.iss.fitness.web.dto.auth.SignupRequest;
import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, WorkoutRepository workoutRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(SignupRequest request) {
        String username = request.username().trim();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setName(request.name().trim());
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public void deleteUser(UUID userId) {
        workoutRepository.deleteAllByOwnerId(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }
}
