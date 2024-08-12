package com.matheus.SpringLibrary.controller;

import com.matheus.SpringLibrary.dto.response.dashboardDTO;
import com.matheus.SpringLibrary.model.User;
import com.matheus.SpringLibrary.repository.UserRepository;
import com.matheus.SpringLibrary.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class userController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenService tokenService;
    @GetMapping("/dashboard")
    public ResponseEntity<dashboardDTO> getDashboard(@RequestHeader("Authorization") String token) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(tokenService.getUserIdFromJWT(token.substring(7))));
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build(); // Retorna 404 se o usuário não for encontrado
        }

        User user = optionalUser.get();
        dashboardDTO dashboard = new dashboardDTO(user.getQntdDeLivros(), user.getTotalDownloads());
        return ResponseEntity.ok().body(dashboard); // Retorna o dashboardDTO como resposta
    }
}
