package vn.java.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import vn.java.model.Role;
import vn.java.repository.RoleRepository;

import java.util.List;

@Service
public record RoleService(RoleRepository roleRepository) {
    @PostConstruct
    public List<Role> findAll(){

//        List<Role> roles =roleRepository.getAllByUserId(2l);
//        return roles;
        return null;
    };


}
