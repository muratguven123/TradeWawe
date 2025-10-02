package com.murat.tradewave.security;
import com.murat.tradewave.Enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long jwtExpirationMillis;
    @Value("${jwt.clockSkewSeconds:60}")
    private long clockSkewSeconds;
    @Value("${jwt.secret.base64:false}") private boolean secretBase64;
    private Key getSigningKey() {
        if (secretKey == null || secretKey.isBlank())
            throw new IllegalStateException("jwt.secret is empty");

        byte[] keyBytes = secretBase64
                ? Base64.getDecoder().decode(secretKey)
                : secretKey.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32)
            throw new IllegalStateException("jwt.secret must be at least 32 bytes (after decoding if base64=true).");

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private JwtParser buildParser() {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(clockSkewSeconds);
    }

    public Claims extractAllClaims(String token) throws ExpiredJwtException, JwtException {
        return buildParser().parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }


    public String generateToken(String username) {
        return generateToken(username, List.of(Role.USER.name()), Collections.emptyMap(), jwtExpirationMillis);
    }

    public String generateToken(String username,
                                List<String> roles,
                                Map<String, Object> extraClaims,
                                long expirationMillis) {
        Map<String, Object> claims = new HashMap<>();
        if (extraClaims != null) claims.putAll(extraClaims);
        claims.put("roles", roles == null ? List.of(Role.USER.name()) : roles);

        long now = System.currentTimeMillis();
        Date iat = new Date(now);
        Date exp = new Date(now + (expirationMillis > 0 ? expirationMillis : jwtExpirationMillis));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        // jjwt 0.12.x için:
        // return Jwts.builder()
        //         .claims(claims)
        //         .subject(username)
        //         .issuedAt(iat)
        //         .expiration(exp)
        //         .signWith(getSigningKey(), Jwts.SIG.HS256)
        //         .compact();
    }


    public boolean isTokenValid(String token, String expectedUsername) throws ExpiredJwtException, JwtException {
        final String username = extractUsername(token); // burada Expired/Malformed fırlar
        if (!Objects.equals(username, expectedUsername)) return false;
        return !isTokenExpired(token);
    }


    public boolean isTokenValid(String token, org.springframework.security.core.userdetails.UserDetails ud)
            throws ExpiredJwtException, JwtException {
        return ud != null && isTokenValid(token, ud.getUsername());
    }

    public boolean isTokenExpired(String token) throws ExpiredJwtException, JwtException {
        final Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public List<String> extractRoles(String token) throws ExpiredJwtException, JwtException {
        Object rolesClaim = extractAllClaims(token).get("roles");
        if (rolesClaim == null) return List.of();

        if (rolesClaim instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        if (rolesClaim instanceof String s) {
            return Arrays.stream(s.split(","))
                    .map(String::trim)
                    .filter(x -> !x.isEmpty())
                    .toList();
        }
        return List.of(String.valueOf(rolesClaim));
    }
}
