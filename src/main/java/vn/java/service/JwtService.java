package vn.java.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface JwtService {
    String generateToken(UserDetails user);
}
