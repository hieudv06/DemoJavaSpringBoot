package vn.java.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import vn.java.dto.request.SignInRequest;
import vn.java.dto.response.TokenResponse;
import vn.java.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public TokenResponse authenticate(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUserName(),signInRequest.getPassword()));


        var user = userRepository.findByUsername(signInRequest.getUserName()).orElseThrow(() -> new RuntimeException("Username or Password is incorrect"));

        String accessToken = jwtService.generateToken(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken("refreshToken")
                .userId(user.getId())
                .build();
    }

}
