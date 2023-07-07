package com.allthattour.web.user.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.allthattour.web.user.model.Cart;
import com.allthattour.web.user.model.User;
import com.allthattour.web.user.payload.request.CartAddRequest;
import com.allthattour.web.user.repository.CartRepository;
import com.allthattour.web.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class HotelService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Transactional
    public Cart save(User user, CartAddRequest req) {
        return cartRepository.save(new Cart(user, req));
    }

    public List<Cart> getCarts(User user) {
        return cartRepository.findByUser(user);
    }

    @Transactional
    public void cartDelete(Long pid, Long cid) {
        User user = userRepository.findById(pid).orElseThrow();
        Cart cart = cartRepository.findById(cid).orElseThrow();
        cartRepository.delete(cart);
    }

    public List<Cart> getCarts(Long pid) {
        return cartRepository.findByUser(userRepository.findById(pid).orElseThrow());
    }
}
