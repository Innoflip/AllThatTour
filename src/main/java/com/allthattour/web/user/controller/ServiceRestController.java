package com.allthattour.web.user.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
import org.springframework.web.client.RestTemplate;

import com.allthattour.web.common.EmailUtil;
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
import com.allthattour.web.user.service.PaymentService;
import com.google.common.base.Predicate;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/service")
public class ServiceRestController {
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

    @Autowired
    PaymentService paymentService;

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

    @RequestMapping(value = "/cart.do", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
    public ResponseEntity<?> cart(Authentication authentication, @RequestBody CartAddRequest req) throws Exception {
        Long uid = Utils.getPid();
        User user = userRepository.findById(uid).orElseThrow();
        if (!cartRepository.existsByUserAndHotelIdAndRoomIdAndStartAndEnd(user, req.getHotelId(), req.getRoomId(),
                req.getStart(), req.getEnd())) {
            cartRepository.save(new Cart(user, req));
        }
        return ResponseEntity.ok().build();
    }

    @Transactional
    @Modifying
    @GetMapping("/delete-cart/{pid}")
    public ResponseEntity<?> deleteCart(Authentication authentication, @PathVariable("pid") Long pid) throws Exception {
        Long uid = Utils.getPid();
        User user = userRepository.findById(uid).orElseThrow();
        cartRepository.deleteById(pid);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @Modifying
    @GetMapping("/check-cart")
    public ResponseEntity<?> checkCart(Authentication authentication) throws Exception {
        Long uid = Utils.getPid();
        User user = userRepository.findById(uid).orElseThrow();
        List<Cart> carts = cartRepository.findByUser(user);
        boolean isOk = true;
        for (Cart cart : carts) {
            String url = "https://affiliateapiservices.agoda.com/api/v2/prebooking/precheck";

            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> map = new HashMap<>();
            map.put("waitTime", 60);
            Map<String, Object> precheckDetails = new HashMap<>();
            Map<String, Object> property = new HashMap<>();
            Map<String, Object> rooms = new HashMap<>();
            Map<String, Object> rate = new HashMap<>();
            rate.put("inclusive", cart.getRate());
            rooms.put("rate", rate);
            rooms.put("currency", "KRW");
            rooms.put("paymentModel", "Merchant");
            rooms.put("blockId", cart.getBlockId());
            rooms.put("count", 1);
            rooms.put("adults", cart.getAdult());
            rooms.put("children", cart.getChild());
            List<Object> roomList = new ArrayList<>();
            roomList.add(rooms);
            property.put("rooms", roomList);
            property.put("propertyId", cart.getHotelId());

            precheckDetails.put("searchId", Long.valueOf(cart.getSearchId()));
            precheckDetails.put("allowDuplication", false);
            precheckDetails.put("checkIn", cart.getStart());
            precheckDetails.put("checkOut", cart.getEnd());
            precheckDetails.put("language", "ko-kr");
            precheckDetails.put("currency", "KRW");
            precheckDetails.put("userCountry", "KR");
            precheckDetails.put("property", property);

            map.put("precheckDetails", precheckDetails);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "1913931:415DC3B2-3FD5-469D-9333-040D7C5C2603");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
            if (((Long) jsonObject.get("status")) == 200) {
            } else {
                isOk = false;
                cartRepository.delete(cart);
            }
        }
        if (isOk) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/email")
    public ResponseEntity<?> sendMail(Authentication authentication) {
        paymentService.sendMail(userRepository.findById(Utils.getPid()).orElseThrow().getEmail(), "TEST", "TEST");
        return ResponseEntity.ok().build();
    }
}
