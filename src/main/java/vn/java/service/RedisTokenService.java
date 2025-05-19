package vn.java.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.java.exception.InvalidDataException;
import vn.java.exception.ResourceNotFoundException;
import vn.java.model.RedisToken;
import vn.java.repository.RedisTokenRepository;

@Service
@RequiredArgsConstructor
public class RedisTokenService {
    private final RedisTokenRepository redisTokenRepository;

    public String save(RedisToken token){

       RedisToken redisToken = redisTokenRepository.save(token);
       return redisToken.getId();
    }

    public void remove(String id){
        isExists(id);
        redisTokenRepository.deleteById(id);

    }

    public  boolean isExists(String id){
        if(!redisTokenRepository.existsById(id)){
            throw new InvalidDataException("Token not exists");
        }
        return true;
    }

    public RedisToken getById(String id){
        return redisTokenRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Redis Token not found! "));
    }
}
