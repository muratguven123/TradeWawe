package com.murat.tradewave.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static javax.crypto.Cipher.SECRET_KEY;


@Service
public class JwtService {
    private final String secret_key="mysecretkey";
    private final Long EXPİRATİON_TİME=86400000L;

    public String generateJwtToken(String email) {
        Map<String, Object> claims = new HashMap<String, Object>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+EXPİRATİON_TİME))
                .signWith(SignatureAlgorithm.HS256,secret_key)
                .compact();
    }
    private String createToken(Map<String, Object> claims,String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+EXPİRATİON_TİME))
                .signWith(SignatureAlgorithm.HS256,secret_key)
                .compact();
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public boolean isTokenValid(String token,String userEmail){
        final String username=extractUsername(token);
        return (username.equals(userEmail)&&!isTokenExpired(token));
    }
    private boolean isTokenExpired(String token) {
        return extractEpiration(token).before(new Date());
    }
    private Date extractEpiration(String token){
        return extractClaim(token,Claims::getExpiration);

    }
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(String.valueOf(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}
