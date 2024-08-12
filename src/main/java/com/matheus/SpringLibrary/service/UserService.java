package com.matheus.SpringLibrary.service;

import com.matheus.SpringLibrary.model.User;
import com.matheus.SpringLibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenService tokenProvider;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public Optional<User> getUserByToken(String token) {
        tokenProvider= new TokenService();
        return userRepository.findById(UUID.fromString( tokenProvider.getUserIdFromJWT(token)));
    }
    public void addUpload(String token){
        Optional<User> user = userRepository.findById(UUID.fromString(tokenProvider.getUserIdFromJWT(token)));
        user.get().setQntdDeLivros(user.get().getQntdDeLivros() + 1);
        userRepository.save(user.get());
//        user.get().setQtnd(user.get().getTotalUploads() + 1);
    }

    public void addDownload(String token) {
        Optional<User> user = userRepository.findById(UUID.fromString(tokenProvider.getUserIdFromJWT(token)));
        user.get().setTotalDownloads(user.get().getTotalDownloads() + 1);
        userRepository.save(user.get());

    }
}
