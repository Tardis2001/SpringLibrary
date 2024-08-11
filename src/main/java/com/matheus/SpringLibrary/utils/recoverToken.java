package com.matheus.SpringLibrary.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component("recoverToken")
public class recoverToken {
    public String recoverToken(String header) {
        String authHeader = header;
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7); // Remove "Bearer " prefix
    }
}
