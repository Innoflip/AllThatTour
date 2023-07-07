package com.allthattour.web.user.controller;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.allthattour.web.user.service.PaymentService;
import com.allthattour.web.user.service.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/toss")
public class TossController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    PaymentService paymentService;

    @GetMapping("/success")
    public ResponseEntity<?> success(Authentication authentication, @RequestParam("p") Long productPid,
            @RequestParam("u") Long userPid,
            @RequestParam("paymentKey") String paymentKey, @RequestParam("amount") Long amount,
            @RequestParam("orderId") String orderId) {
        try {
            paymentService.createEntity(userPid, paymentKey, amount, orderId);

            return ResponseEntity.ok().body("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body("failed");
        }
    }

    @PostMapping("/cancel/{pid}")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER')")
    public ResponseEntity<?> write(Authentication authentication, @PathVariable Long pid) {
        Long userPid = ((UserDetailsImpl) authentication.getPrincipal()).getPid();
        paymentService.cancelPurchase(userPid, pid);

        // Agoda API 취소 호출

        return ResponseEntity.ok().build();
    }
}