package com.allthattour.web.user.model;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agoda_hotel_address")
@Setter
@DynamicInsert
@Getter
public class AgodaHotelAddress {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long hotelId;
        private String addressType, addressLine1, addressLine2, postalCode, state, city, country;
}