package com.device.DeviceCommandModule.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Component
public class KeyDecoder {

    @Autowired
    private JwtDecoder jwtDecoder;

    public String getUsernamebyToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String newToken = token.replace("Bearer ","");
        Jwt decodedToken = this.jwtDecoder.decode(newToken);
        String subject = (String) decodedToken.getClaims().get("sub");
        String username = String.valueOf(subject.split(",")[1]);

        return username;
    }

    public UUID getIdbyToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String newToken = token.replace("Bearer ","");
        Jwt decodedToken = this.jwtDecoder.decode(newToken);
        String subject = (String) decodedToken.getClaims().get("sub");
        UUID id = UUID.fromString(subject.split(",")[0]);

        return id;
    }
}
