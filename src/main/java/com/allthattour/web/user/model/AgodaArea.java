package com.allthattour.web.user.model;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agoda_area")
@Setter
@DynamicInsert
@Getter
public class AgodaArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer areaId, cityId, activeHotels;
    private String areaName, areaTranslated, longitude, latitude;
}