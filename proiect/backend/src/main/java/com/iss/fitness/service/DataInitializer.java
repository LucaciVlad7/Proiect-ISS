package com.iss.fitness.service;

import com.iss.fitness.domain.user.Role;
import com.iss.fitness.domain.user.User;
import com.iss.fitness.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExerciseCatalogService exerciseCatalogService;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.name}")
    private String adminName;

    public DataInitializer(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        ExerciseCatalogService exerciseCatalogService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.exerciseCatalogService = exerciseCatalogService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setName(adminName);
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        exerciseCatalogService.seedDefaultExercises();
    }
}