package com.allthattour.web.user.model;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agoda_hotel_info")
@Setter
@DynamicInsert
@Getter
public class AgodaHotelInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hotelId, propertyId;
    private String propertyName, propertyTranslatedName, propertyDetails;
}