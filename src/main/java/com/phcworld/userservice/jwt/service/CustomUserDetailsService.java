package com.phcworld.userservice.jwt.service;


import com.phcworld.userservice.exception.model.DeletedEntityException;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.infrastructure.UserEntity;
import com.phcworld.userservice.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(NotFoundException::new);
    }

    private UserDetails createUserDetails(UserEntity user) {
        if(Boolean.TRUE.equals(user.getIsDeleted())){
            throw new DeletedEntityException();
        }
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthority().toString());
        return new User(
                user.getUserId(),
                user.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
