package vn.java.service;

import org.springframework.stereotype.Service;
import vn.java.exception.ResourceNotFoundException;
import vn.java.model.Token;
import vn.java.repository.TokenRepository;
import vn.java.util.TokenType;

import java.util.Optional;

@Service
public record TokenService(TokenRepository tokenRepository) {
    public int save(Token token){
       Optional<Token> tokenOptional = tokenRepository.findByUsername(token.getUsername());
       if(tokenOptional.isEmpty()){
           tokenRepository.save(token);
           return tokenOptional.get().getId();
       }else {
           Token currentToken = tokenOptional.get();
           currentToken.setAccessToken(token.getAccessToken());
           currentToken.setRefreshToken(token.getAccessToken());
           tokenRepository.save(currentToken);
           return currentToken.getId();
       }

    }
    public String delete(Token token){
        tokenRepository.delete(token);
        return "Deleted!";
    }
    public Token getByUsername(String username){
       return tokenRepository.findByUsername(username).orElseThrow(()-> new ResourceNotFoundException("Token not exist!"));
    }
}
