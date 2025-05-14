package vn.java.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "UserHasRole")
@Table(name="tbl_user_has_role")
public class UserHasRole extends AbstractEntity<Integer> {

    @ManyToOne
    @JoinColumn(name ="role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name="user_id" )
    private User user;
}
