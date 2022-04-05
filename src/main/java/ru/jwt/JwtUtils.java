package ru.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.service.UserDetailsImpl;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${security.jwt.secret")
    private String secret;

    @Value("${security.jwt.expiration")
    private int expiration;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean validateJwtToken(String jwt) {
        try {
            Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(jwt);
            return true;
        } catch (MalformedJwtException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public String getUserNameFromJwtToken(String jwt) {
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(jwt).getBody().getSubject();
    }
}
