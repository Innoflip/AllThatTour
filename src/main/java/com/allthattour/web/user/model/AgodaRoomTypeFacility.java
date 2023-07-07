package com.allthattour.web.user.model;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agoda_room_type_facility")
@Setter
@DynamicInsert
@Getter
public class AgodaRoomTypeFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hotelId, hotelRoomTypeId, propertyId;
    private String propertyName, translatedName;
}