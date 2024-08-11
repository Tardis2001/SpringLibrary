package com.matheus.SpringLibrary.controller;

import com.matheus.SpringLibrary.dto.request.LoginRequestDTO;
import com.matheus.SpringLibrary.dto.request.RegisterRequestDTO;
import com.matheus.SpringLibrary.dto.response.ResponseAuthDTO;
import com.matheus.SpringLibrary.model.User;
import com.matheus.SpringLibrary.repository.UserRepository;
import com.matheus.SpringLibrary.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ResponseAuthDTO> login(@RequestBody LoginRequestDTO body) {
        Optional<User> userOptional = repository.findByEmail(body.email());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(body.password(), user.getPassword())) {
                String token = tokenService.generateToken(user);
                return ResponseEntity.ok(new ResponseAuthDTO(user.getName(), token));
            } else {
                return ResponseEntity.badRequest().body(new ResponseAuthDTO("Invalid password", null));
            }
        }
        return ResponseEntity.badRequest().body(new ResponseAuthDTO("User not found", null));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseAuthDTO> register(@RequestBody RegisterRequestDTO body) {
        Optional<User> userOptional = repository.findByEmail(body.email());
        if (userOptional.isEmpty()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setName(body.name());
            repository.save(newUser);

            String token = tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseAuthDTO(newUser.getName(), token));
        }
        return ResponseEntity.badRequest().body(new ResponseAuthDTO("User already exists", null));
    }
}