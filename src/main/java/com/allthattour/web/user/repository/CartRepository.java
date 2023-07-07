package com.allthattour.web.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.allthattour.web.user.model.Cart;
import com.allthattour.web.user.model.ERole;
import com.allthattour.web.user.model.Role;
import com.allthattour.web.user.model.User;

// @Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);

    boolean existsByUserAndHotelIdAndRoomIdAndStartAndEnd(User user, Long hotelId, Long roomId, String start,
            String end);
}