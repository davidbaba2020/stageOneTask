package com.timtrix.service.auth;

import com.timtrix.config.security.jwt.JwtUtil;
import com.timtrix.dtos.UserDTO;
import com.timtrix.entities.User;
import com.timtrix.exceptions.EmailAlreadyExistsException;
import com.timtrix.exceptions.ResourceNotFoundException;
import com.timtrix.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Map<String, Object> createUser(UserDTO userDTO) {
        Optional<User> userFound = userRepository.findByEmail(userDTO.getEmail());
        if(userFound.isPresent()){
            throw new EmailAlreadyExistsException("User with email already exist");
        }
        User newUser = User.builder()
                        .firstName(userDTO.getFirstName())
                        .lastName(userDTO.getLastName())
                        .email(userDTO.getEmail())
                        .password(passwordEncoder.encode(userDTO.getPassword()))
                        .phone(userDTO.getPhone())
                        .build();
        User savedUser = userRepository.save(newUser);

        String accessToken = jwtUtil.generateToken(savedUser.getEmail());

        Map<String, Object> userData = Map.of(
                "userId", savedUser.getId(),
                "firstName", savedUser.getFirstName(),
                "lastName", savedUser.getLastName(),
                "email", savedUser.getEmail(),
                "phone", savedUser.getPhone()
        );

        return Map.of(
                "accessToken", accessToken,
                "user", userData
        );
    }
}
