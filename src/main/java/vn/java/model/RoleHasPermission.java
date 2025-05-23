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
@Entity(name = "RoleHasPermission")
@Table(name="tbl_role_has_permission")
public class RoleHasPermission extends AbstractEntity<Integer>{

    @ManyToOne
    @JoinColumn(name ="role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name="permission_id" )
    private Permission permission;
}
