package com.allthattour.web.user.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.allthattour.web.user.model.Agoda;
import com.allthattour.web.user.model.AgodaHotel;
import com.allthattour.web.user.model.AgodaHotelDescription;
import com.allthattour.web.user.model.AgodaPicture;
import com.allthattour.web.user.model.Role;
import com.allthattour.web.user.model.User;

// @Repository
public interface AgodaHotelDescriptionRepository extends JpaRepository<AgodaHotelDescription, Long> {
        Optional<AgodaHotelDescription> findByHotelId(Long hotelId);
}