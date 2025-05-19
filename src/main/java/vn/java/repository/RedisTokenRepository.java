package vn.java.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.java.model.RedisToken;

@Repository
public interface RedisTokenRepository extends CrudRepository<RedisToken,String> {
}
