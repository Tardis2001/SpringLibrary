package com.matheus.SpringLibrary.config;

import com.matheus.SpringLibrary.model.User;
import com.matheus.SpringLibrary.service.TokenService;
import com.matheus.SpringLibrary.repository.UserRepository;

import com.matheus.SpringLibrary.utils.recoverToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;


@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private recoverToken recoverToken;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals("/auth/login") || request.getRequestURI().equals("/auth/register")){
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("teste");
        String token = recoverToken.recoverToken(request.getHeader("Authorization"));
        String id = tokenService.ValidateToken(token);

        if (id != null) {
            Optional<User> optionalUser = userRepository.findById(UUID.fromString(id));
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }


}
