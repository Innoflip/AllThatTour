package com.allthattour.web.user.model;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agoda_hotel_description")
@Setter
@DynamicInsert
@Getter
public class AgodaHotelDescription {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long hotelId;
        private String snippet;
        @Column(length = 1000)
        private String overview;
}