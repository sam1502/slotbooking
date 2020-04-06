package com.postman.slotbooking.util;

import com.postman.slotbooking.models.PUsers;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    public static final long JWT_TOKEN_VALIDITY = 60 * 60;

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public <T> T extrackClaims(String token , Function<Claims,T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    public Date extractExipryDate(String token) {
        System.out.println("Expiry date : "+extrackClaims(token, Claims::getExpiration));
        return extrackClaims(token, Claims::getExpiration);
    }

    public String extractUserName(String token) {
        return extrackClaims(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractExipryDate(token).before(new Date());
    }

    public String generateToken(PUsers PUsers) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, PUsers.getUserName());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).
                setIssuedAt(new Date(System.currentTimeMillis())).
                setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY*10000)).
                signWith(SignatureAlgorithm.HS256, secret).compact();

    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
