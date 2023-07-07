package com.allthattour.web.user.model;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agoda_city")
@Setter
@DynamicInsert
@Getter
public class AgodaCity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer cityId, countryId, activeHotels;
    private String cityName, cityTranslated, countryIso, countryIso2, longitude, latitude, noArea;
}