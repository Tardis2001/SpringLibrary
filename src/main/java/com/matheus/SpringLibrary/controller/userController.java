package com.matheus.SpringLibrary.controller;

import com.matheus.SpringLibrary.dto.response.dashboardDTO;
import com.matheus.SpringLibrary.model.User;
import com.matheus.SpringLibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class userController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<dashboardDTO> getDashboard(@RequestParam("userId") UUID userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build(); // Retorna 404 se o usuário não for encontrado
        }

        User user = optionalUser.get();
        dashboardDTO dashboard = new dashboardDTO(user.getQntdDeLivros(), user.getTotalDownloads(), user.getTotalUploads());
        return ResponseEntity.ok().body(dashboard); // Retorna o dashboardDTO como resposta
    }
}
