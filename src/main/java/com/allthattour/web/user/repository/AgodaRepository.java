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
import com.allthattour.web.user.model.Role;
import com.allthattour.web.user.model.User;

// @Repository
public interface AgodaRepository extends JpaRepository<Agoda, Long> {
        @Query(value = "select * from agoda where (city like %:place% or country like %:place%) and star_rating in :rates", nativeQuery = true)
        List<Agoda> findByConditions(@Param("place") String place, @Param("rates") double[] rates);

        Optional<Agoda> findByHotelId(Long hotelId);
}