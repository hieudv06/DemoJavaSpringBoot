package vn.java.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.java.exception.InvalidDataException;
import vn.java.service.JwtService;
import vn.java.util.TokenType;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static vn.java.util.TokenType.*;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryHour}")
    private Long expiryHour;

    @Value("${jwt.expiryDay}")
    private Long expiryDay;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Value("${jwt.resetKey}")
    private String resetKey;


    @Override
    public String generateToken(UserDetails user) {
        return generateToken(new HashMap<>(), user);
    }

    @Override
    public String extractUsername(String token,TokenType tokenType) {
        return extractClaim(token,tokenType, Claims::getSubject);
    }

    @Override
    public boolean isValid(String token,TokenType tokenType, UserDetails userDetails) {
        final String username = extractUsername(token,tokenType);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token,tokenType);
    }

    private boolean isTokenExpired(String token,TokenType tokenType) {
        return extractExpiration(token,tokenType).before(new Date());
    }

    @Override
    public String generateRefreshToken(UserDetails user) {
        return generateRefreshToken(new HashMap<>(), user);
    }

    @Override
    public String generateResetToken(UserDetails user) {
        return generateResetToken(new HashMap<>(), user);
    }

    private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expiryHour)) // thoi gian hieu luc cua token
                .signWith(getKey(ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay)) // thoi gian hieu luc cua refreshtoken
                .signWith(getKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }
    private String generateResetToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay)) // thoi gian hieu luc cua refreshtoken
                .signWith(getKey(RESET_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenType tokenType) {
        switch (tokenType){
            case ACCESS_TOKEN -> {return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));}
            case REFRESH_TOKEN ->  {return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));}
            case RESET_TOKEN ->  {return Keys.hmacShaKeyFor(Decoders.BASE64.decode(resetKey));}
            default -> throw new InvalidDataException("Token type invalid");
        }
    }

    private <T> T extractClaim(String token,TokenType tokenType, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaim(token,tokenType);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaim(String token,TokenType tokenType) {
        switch (tokenType){
            case ACCESS_TOKEN -> {return Jwts.parserBuilder().setSigningKey(getKey(ACCESS_TOKEN)).build().parseClaimsJws(token).getBody();}
            case REFRESH_TOKEN -> {return Jwts.parserBuilder().setSigningKey(getKey(REFRESH_TOKEN)).build().parseClaimsJws(token).getBody();}
            case RESET_TOKEN -> {return Jwts.parserBuilder().setSigningKey(getKey(RESET_TOKEN)).build().parseClaimsJws(token).getBody();}
            default -> throw new InvalidDataException("Token type invalid");
        }
    }

    private Date extractExpiration(String token,TokenType tokenType) {
        return extractClaim(token,tokenType, Claims::getExpiration);
    }
}
