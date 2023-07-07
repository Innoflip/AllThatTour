package com.allthattour.web.user.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.allthattour.web.tool.Utils;
import com.allthattour.web.user.model.Cart;
import com.allthattour.web.user.model.ERole;
import com.allthattour.web.user.model.Role;
import com.allthattour.web.user.model.User;
import com.allthattour.web.user.payload.request.CartAddRequest;
import com.allthattour.web.user.payload.request.UserRegisterRequest;
import com.allthattour.web.user.repository.CartRepository;
import com.allthattour.web.user.repository.RoleRepository;
import com.allthattour.web.user.repository.UserRepository;
import com.google.common.base.Predicate;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserRestController {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    SessionRegistry sessionRegistry;

    @Autowired
    PasswordEncoder encoder;

    // @GetMapping("withdrawal")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<?> withdrawal(Authentication authentication,
    // @RequestParam Long pid) {
    // Long myPid = Utils.getPid();
    // if (pid == myPid) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인을 탈퇴시킬 수
    // 없습니다.");
    // }
    // blockUserService.blockUser(adminRepository.findById(pid).get().getPhone());
    // adminRepository.withdrawal(pid);
    // return ResponseEntity.ok().build();
    // }

    @RequestMapping(value = "/register.do", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
    public ResponseEntity<?> authenticateUser(@RequestBody UserRegisterRequest req) throws Exception {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }

        User user = new User(req.getEmail(), encoder.encode(req.getPasswd()),
                req.getFirstName(), req.getLastName(), req.getPhone());

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/mod-name.do", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
    public ResponseEntity<?> modName(@RequestBody UserRegisterRequest req) throws Exception {
        User user = userRepository.findById(Utils.getPid()).orElseThrow();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/mod-email.do", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
    public ResponseEntity<?> modEmail(@RequestBody UserRegisterRequest req) throws Exception {
        if (userRepository.existsByEmailAndPidNot(req.getEmail(), Utils.getPid())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        User user = userRepository.findById(Utils.getPid()).orElseThrow();
        user.setEmail(req.getEmail());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/mod-pw.do", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
    public ResponseEntity<?> modPw(@RequestBody UserRegisterRequest req) throws Exception {
        User user = userRepository.findById(Utils.getPid()).orElseThrow();
        user.setPassword(encoder.encode(req.getPasswd()));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/mod-phone.do", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
    public ResponseEntity<?> modPhone(@RequestBody UserRegisterRequest req) throws Exception {
        User user = userRepository.findById(Utils.getPid()).orElseThrow();
        user.setPhone(req.getPhone());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
