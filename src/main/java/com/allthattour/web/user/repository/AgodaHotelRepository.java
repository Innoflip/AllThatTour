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
import com.allthattour.web.user.model.Role;
import com.allthattour.web.user.model.User;

// @Repository
public interface AgodaHotelRepository extends JpaRepository<AgodaHotel, Long> {
        @Query(value = "select agoda_hotel.* from agoda_hotel inner join agoda_city inner join agoda_country on agoda_hotel.city_id=agoda_city.city_id and agoda_hotel.country_id=agoda_country.country_id where (city_name like %:place% or country_name like %:place% or city_translated like %:place% or country_translated like %:place%) and star_rating in :rates and accommodation_type in :types", nativeQuery = true)
        List<AgodaHotel> findByConditions(@Param("place") String place, @Param("rates") double[] rates,
                        @Param("types") List<String> types);

        Optional<AgodaHotel> findByHotelId(Long hotelId);
}