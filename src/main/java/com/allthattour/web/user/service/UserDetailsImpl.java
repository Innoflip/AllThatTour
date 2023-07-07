package com.allthattour.web.user.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.allthattour.web.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long pid;

    @JsonIgnore
    private String password;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String name;

    private Boolean isEnable = true;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String email, String password, String name,
            Collection<? extends GrantedAuthority> authorities, Boolean isEnable) {
        this.pid = id;
        this.password = password;
        this.email = email;
        this.name = name;
        this.authorities = authorities;
        this.isEnable = isEnable;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getPid(),
                user.getEmail(),
                user.getPassword(),
                user.getFirstName() + " " + user.getLastName(),
                authorities, user.getIsEnable());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getPid() {
        return pid;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnable;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(pid, user.pid);
    }

    public String getName() {
        return name;
    }

    public Boolean getIsEnable() {
        return isEnable;
    }
}