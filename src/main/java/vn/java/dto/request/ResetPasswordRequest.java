package vn.java.dto.request;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ResetPasswordRequest implements Serializable {
    private String secretKey;
    private String password;
    private String confirmPassword;
}
