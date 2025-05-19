package vn.java.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import vn.java.util.Platform;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {
    @NotBlank(message="username must be not null")
    private String username;
    @NotBlank(message="password must be not null")
    private String password;
    @NotBlank(message="platform must be not null")
    private Platform platform;
    private  String deviceToken;

}
