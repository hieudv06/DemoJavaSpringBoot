package vn.java.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.java.service.JwtService;

@Service
public class JwtServiceImpl implements JwtService {
    @Override
    public String generateToken(UserDetails user) {
        //todo
        return "access-token";
    }
}
