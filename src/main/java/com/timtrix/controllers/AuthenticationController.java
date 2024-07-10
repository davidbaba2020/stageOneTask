package com.timtrix.controllers;

import com.timtrix.config.security.jwt.JwtUtil;
import com.timtrix.config.security.jwt.UserDetailsServiceImpl;
import com.timtrix.dtos.AuthenticationDTO;
import com.timtrix.dtos.AuthenticationResponse;
import com.timtrix.dtos.UserDTO;
import com.timtrix.entities.User;
import com.timtrix.exceptions.InvalidCredentialsException;
import com.timtrix.repositories.UserRepository;
import com.timtrix.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public Map<String, Object> logIn(@RequestBody AuthenticationDTO authenticationDTO, HttpServletResponse response) throws BadCredentialsException, DisabledException, UsernameNotFoundException, IOException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationDTO.getEmail(), authenticationDTO.getPassword()));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Incorrect username or password!");
        } catch (DisabledException disabledException) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User is not activated");
            return null;
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationDTO.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        User user = userRepository.findByEmail(userDetails.getUsername()).get();
        log.info("The user details, {}", user);
        Map<String, Object> userData = Map.of(
                "userId", user.getId(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail(),
                "phone", user.getPhone()
        );
//        return new AuthenticationResponse(jwt);
        Map<String, Object> data = Map.of(
                "accessToken", jwt,
                "user", userData
        );

        return Map.of(
                "status", "success",
                "message", "Login successful",
                "data", data
        );

    }

    @PostMapping("/register")
    public ResponseEntity<?> signupUser(@Valid @RequestBody UserDTO userDTO) {

        Map<String, Object> createdUser = authService.createUser(userDTO);

        if (createdUser == null) {
            Map<String, Object> errorResponse = Map.of(
                    "status", "Bad request",
                    "message", "Registration unsuccessful",
                    "statusCode", 400
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> response = Map.of(
                "status", "success",
                "message", "Registration successful",
                "data", createdUser
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllUsers() {
        try {
            userRepository.deleteAll();
            return new ResponseEntity<>("Record deleted successfully",HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
