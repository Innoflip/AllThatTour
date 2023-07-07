package com.allthattour.web.user.model;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agoda_facility")
@Setter
@DynamicInsert
@Getter
public class AgodaFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer hotelId, propertyId;
    private String propertyGroupDescription, propertyName, propertyTranslatedName;
}