package com.allthattour.web.tool;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;

import com.allthattour.web.user.model.ERole;
import com.allthattour.web.user.service.UserDetailsImpl;

public class Utils {
    static public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    static public ERole getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return ERole.ROLE_VISITOR;
        }
        Set<String> roles = authentication.getAuthorities().stream()
                .map(r -> r.getAuthority()).collect(Collectors.toSet());
        String role = roles.iterator().next();
        return ERole.ROLE_USER.name().equals(role) ? ERole.ROLE_USER : ERole.ROLE_USER;
    }

    static public String getName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return "";
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getName();
    }

    static public Long getPid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return -1L;
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getPid();
    }

    static public boolean checkLockedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).isAccountNonLocked();
    }
}
