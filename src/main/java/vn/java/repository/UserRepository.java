package vn.java.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.java.model.Role;
import vn.java.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> , JpaSpecificationExecutor<User> {



    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query(value = "select r from Role r inner join  UserHasRole ur on r.id =ur.user.id where ur.user.id=:userId")
    List<User> getAlRolesByUserId(Long userId);


}
