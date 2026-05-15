package com.iss.fitness.web;

import com.iss.fitness.domain.user.User;
import com.iss.fitness.service.UserService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "name", user.getName(),
            "role", user.getRole().name()
        ));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }
}
