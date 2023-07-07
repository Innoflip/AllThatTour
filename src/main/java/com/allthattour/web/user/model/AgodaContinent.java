package com.allthattour.web.user.model;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agoda_continent")
@Setter
@DynamicInsert
@Getter
public class AgodaContinent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer continentId, activeHotels;
    private String continentName, continentTranslated;
}