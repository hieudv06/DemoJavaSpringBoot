package vn.java.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.java.dto.request.SignInRequest;
import vn.java.dto.response.ResponseData;
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

    @PostMapping("/access")
    public ResponseEntity<TokenResponse> login(@RequestBody SignInRequest signInRequest){


        return new ResponseEntity<>( authenticationService.authenticate(signInRequest), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public String refresh(){
        return  "success";
    }

    @PostMapping("/logout")
    public String logout(){
        return  "success";
    }


}
