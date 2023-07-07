package com.allthattour.web.user.model;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agoda_country")
@Setter
@DynamicInsert
@Getter
public class AgodaCountry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer countryId, continentId, activeHotels;
    private String countryName, countryTranslated, countryIso, countryIso2, longitude, latitude;
}