package vn.java.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.java.dto.request.ResetPasswordRequest;
import vn.java.dto.request.SignInRequest;
import vn.java.dto.response.TokenResponse;
import vn.java.exception.InvalidDataException;
import vn.java.model.RedisToken;
import vn.java.model.Token;
import vn.java.model.User;
import vn.java.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static vn.java.util.TokenType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final RedisTokenService redisTokenService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public TokenResponse accessToken(SignInRequest signInRequest) {

        log.info("------------- authenticate -----------");
        var user = userService.getByUsername(signInRequest.getUsername());
        if(!user.isEnabled()){
            throw new InvalidDataException("User not active");
        }
        List<String> roles =userService.getAllRolesByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();


        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword(),authorities));



        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        //save token va refresh token vao db
//        tokenService.save(Token.builder()
//                .username(user.getUsername())
//                .accessToken(accessToken)
//                .refreshToken(refreshToken).build());

        redisTokenService.save(RedisToken.builder()
                .id(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public TokenResponse refreshToken(HttpServletRequest request) {

        log.info("---------- refreshToken ----------");

        String refreshToken =request.getHeader("x-token");

        if(StringUtils.isBlank(refreshToken)){
            throw new InvalidDataException("Token must be not blank");
        }
        // extract user from token
        final  String userName =jwtService.extractUsername(refreshToken,REFRESH_TOKEN);
        User user = userService.getByUsername(userName);
        if(!jwtService.isValid(refreshToken,REFRESH_TOKEN,user)){
            throw new InvalidDataException("Token is invalid");
        }

        String accessToken =jwtService.generateToken(user);

        // save token to db
//         tokenService.save(Token.builder().username(user.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());
        redisTokenService.save(RedisToken.builder().id(user.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());


        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }




    //remove Token
    public String logout(HttpServletRequest request) {
        // validate
        String token =request.getHeader("x-token");
        if(StringUtils.isBlank(token)){
            throw new InvalidDataException("Token must be not blank");
        }
        //extract user from token
        final String username =jwtService.extractUsername(token,ACCESS_TOKEN);
//        Token tokenRepository =tokenService.getByUsername(username);
//        tokenService.delete(tokenRepository);
        //todo : vô hiệu hóa token
        redisTokenService.remove(username);

        return "Deleted Token ";
    }

    public String forgotPassword(String email) {
        // check email exist or not
        var user = userService.getByEmail(email);
        // User  is active or inactivated
        if(!user.isEnabled()){
            throw new InvalidDataException("User not active");
        }
        // Generate reset token
        String resetToken =jwtService.generateResetToken(user);
        redisTokenService.save(RedisToken.builder().id(user.getUsername()).resetToken(resetToken).build());


        // todo send email confirmLink
        String confirmLink = String.format("curl --location 'http://localhost:8080/auth/reset-password' \\\n" +
                "--header 'Content-Type: text/plain' \\\n" +
                "--data '%s'",resetToken);
        log.info("confirmLink={}",confirmLink);
        return "Sent";

    }

    public String resetPassword(String secretKey) {

        log.info("----- Rest Password ------");
        final  String userName =jwtService.extractUsername(secretKey,RESET_TOKEN);
        var user = userService.getByUsername(userName);
       redisTokenService.getById(user.getUsername());


        if(!jwtService.isValid(secretKey,RESET_TOKEN,user)){
            throw new InvalidDataException("Token is invalid");
        }
        return "Reset";
    }

    public String changePassword(ResetPasswordRequest passwordRequest) {

       User user = isValidUserByToken(passwordRequest.getSecretKey());
       if(!passwordRequest.getPassword().equals(passwordRequest.getConfirmPassword())){
           throw new InvalidDataException("Password not match");
       }
       user.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
       userService.save(user);
       //todo save resetToken
        return "Changed";
    }

    private User isValidUserByToken(String secretKey){

        final  String userName =jwtService.extractUsername(secretKey,RESET_TOKEN);
        var user = userService.getByUsername(userName);
        //user is active or inactivated
        if(!user.isEnabled()){
            throw new InvalidDataException("User not active");
        }
        if(!jwtService.isValid(secretKey,RESET_TOKEN,user)){
            throw new InvalidDataException("Not allow access with this token");
        }
        return user;

    }
}
