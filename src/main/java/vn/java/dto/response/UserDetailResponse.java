package vn.java.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import vn.java.util.Gender;
import vn.java.util.UserStatus;
import vn.java.util.UserType;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
public class UserDetailResponse implements Serializable {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Date dateOfBirth;

    private Gender gender;

    private String username;

    private String type;

    private UserStatus status;

}
