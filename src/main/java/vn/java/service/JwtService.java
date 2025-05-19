package vn.java.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.java.model.User;
import vn.java.util.TokenType;

@Service
public interface JwtService {
    String generateToken(UserDetails user);
    String extractUsername(String token,TokenType tokenType);
    boolean isValid(String token, TokenType tokenType, UserDetails user);
    String generateRefreshToken(UserDetails user);
    String generateResetToken(UserDetails user);
}
