package com.matheus.SpringLibrary.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.matheus.SpringLibrary.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.management.RuntimeErrorException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret = "sB3dX3kO5v5X9AkB+5M/8D6nHd2C5/1lA5hZj4T0H2w=";
    public String generateToken(User user){
        try{
            Algorithm alg = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("SpringLibrary")
                    .withSubject(String.valueOf(user.getId()))
                    .withExpiresAt(this.generateExpirationDate())
                    .sign(alg);
        }catch(JWTCreationException e){
            throw new RuntimeErrorException(new Error("Auth"),"Erro ao autenticar!!");
        }
    }
    public String getUserIdFromJWT(String token) {
        try{
            System.out.println("SECRET : "+secret);
        Algorithm alg = Algorithm.HMAC256(secret);
        DecodedJWT jwt = JWT.require(alg).withIssuer("SpringLibrary")
                .build()
                .verify(token);
        return jwt.getSubject();
        }catch(JWTVerificationException e){
            throw new RuntimeErrorException(new Error("Erro ao recuperar ID!!"),"Erro ao recuperar ID!!");
        }
    }

    public String ValidateToken(String token){
        try{

            Algorithm alg = Algorithm.HMAC256(secret);
            return JWT.require(alg).withIssuer("SpringLibrary").build().verify(token).getSubject();
        }catch(JWTVerificationException e){
            throw new RuntimeErrorException(new Error("Erro ao validar o token!!"),"Erro ao validar o token!!");
        }
    }
    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
