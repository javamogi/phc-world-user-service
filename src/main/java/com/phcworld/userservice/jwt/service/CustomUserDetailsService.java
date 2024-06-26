package com.phcworld.userservice.jwt.service;


import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.exception.model.DeletedEntityException;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.service.port.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

//    public CustomUserDetailsService(@Qualifier("jpaUserRepository") UserRepository userRepository) {
    public CustomUserDetailsService(@Qualifier("redisUserRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(NotFoundException::new);
    }

    private UserDetails createUserDetails(User user) {
        if(Boolean.TRUE.equals(user.isDeleted())){
            throw new DeletedEntityException();
        }
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthority().toString());
        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
