package com.learnsyncai.agent.service;

import com.learnsyncai.agent.config.JwtService;
import com.learnsyncai.agent.dto.AuthRequest;
import com.learnsyncai.agent.dto.AuthResponse;
import com.learnsyncai.agent.dto.SignupRequest;
import com.learnsyncai.agent.exception.BadRequestException;
import com.learnsyncai.agent.model.Reminder;
import com.learnsyncai.agent.model.User;
import com.learnsyncai.agent.repository.ReminderRepository;
import com.learnsyncai.agent.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ReminderRepository reminderRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       ReminderRepository reminderRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.reminderRepository = reminderRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse signup(SignupRequest request) {
        String name = request.getName().trim();
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already registered");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);

        Reminder reminder = new Reminder();
        reminder.setUser(savedUser);
        reminder.setFrequencyPerWeek(3);
        reminderRepository.save(reminder);

        String token = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        savedUser.getEmail(), savedUser.getPassword(), java.util.List.of()
                )
        );
        return new AuthResponse(token, savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    public AuthResponse login(AuthRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        String token = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(), user.getPassword(), java.util.List.of()
                )
        );
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }
}
