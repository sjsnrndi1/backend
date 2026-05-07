package backend.service;

import backend.User;
import backend.dto.AuthResponse;
import backend.dto.LoginRequest;
import backend.dto.SignupRequest;
import backend.dto.UserResponse;
import backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public AuthResponse signup(SignupRequest request) {
        String name = requireText(request.getName(), "name");
        String email = normalizeEmail(requireText(request.getEmail(), "email"));
        String password = requireText(request.getPassword(), "password");

        if (password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));

        User savedUser = userRepository.save(user);
        String token = tokenService.createToken(savedUser);

        return new AuthResponse(token, UserResponse.from(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(requireText(request.getEmail(), "email"));
        String password = requireText(request.getPassword(), "password");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return new AuthResponse(tokenService.createToken(user), UserResponse.from(user));
    }

    public User findByToken(String authorizationHeader) {
        Long userId = tokenService.verifyAuthorizationHeader(authorizationHeader);

        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        return value.trim();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
