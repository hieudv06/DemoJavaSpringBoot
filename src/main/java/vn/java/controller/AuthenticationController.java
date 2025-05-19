package vn.java.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.java.dto.request.ResetPasswordRequest;
import vn.java.dto.request.SignInRequest;
import vn.java.dto.response.TokenResponse;
import vn.java.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@Validated
@Slf4j
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/access-token")
    public ResponseEntity<TokenResponse> login(@RequestBody SignInRequest signInRequest){
        return new ResponseEntity<>( authenticationService.authenticate(signInRequest), HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refresh(HttpServletRequest request){
        return new ResponseEntity<>( authenticationService.refreshToken(request), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        return new ResponseEntity<>( authenticationService.logout(request), HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email){
        return new ResponseEntity<>(authenticationService.forgotPassword(email),HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody String secretKey){
        return new ResponseEntity<>(authenticationService.resetPassword(secretKey),HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordDTO){
        return new ResponseEntity<>(authenticationService.changePassword(resetPasswordDTO),HttpStatus.OK);
    }


}
