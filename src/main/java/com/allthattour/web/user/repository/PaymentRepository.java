package com.allthattour.web.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allthattour.web.user.model.Payment;
import com.allthattour.web.user.model.User;

// @Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUser(User user);
}