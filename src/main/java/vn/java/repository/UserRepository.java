package vn.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.java.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
