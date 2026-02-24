package com.akrdev.videostreamingtut.security.jwt;

import com.akrdev.videostreamingtut.entity.user.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationTime}")
    private long expirationTime;

    public String getJwtTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }

    public Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public Key  getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(UserDetailsImpl userDetails){
        String username =  userDetails.getUsername();
        String roles =  userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Date issued = new Date();
        Date expiration = new Date(issued.getTime() + expirationTime);
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(issued)
                .expiration(expiration)
                .signWith(getKey())
                .compact();
    }
}
