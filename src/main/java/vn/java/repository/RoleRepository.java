package vn.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.java.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {



}
