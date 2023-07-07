package com.allthattour.web.user.service;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allthattour.web.user.model.User;
import com.allthattour.web.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String phone) {
        Optional<User> user = userRepository.findByEmail(phone);
        if (user.isPresent()) {
            if (user.get().getIsEnable()) {
                return UserDetailsImpl.build(user.get());
            }
            throw new UsernameNotFoundException("User is locked with username: " + phone);
        } else {
            throw new UsernameNotFoundException("User Not Found with username: " + phone);
        }
    }
}