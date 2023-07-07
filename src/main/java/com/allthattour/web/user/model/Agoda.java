package com.allthattour.web.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agoda")
@Setter
@DynamicInsert
@Getter
public class Agoda {
    @Id
    private Long hotelId;
    private Integer chainId;
    private String chainName;
    private Integer brandId;
    private String brandName, hotelName, hotelFormerlyName, hotelTranslatedName, addressLine1, addressLine2,
            zipcode, city, state, country, countryisocode;
    private Integer starRating;
    private String longitude, latitude, url, checkin, checkout;
    private String numberRooms, numberFloors, yearOpened, yearRenovted;
    @Column(length = 300)
    private String photo1, photo2, photo3, photo4, photo5;
    @Column(length = 5000)
    private String overviews;
    private String ratesFrom;
    private Integer continentId;
    private String continentName;
    private Integer cityId, countryId;
    private Integer numberOfReviews;
    private Double ratingAverage;
    private String ratesCurrency;
    private Integer ratesFromExclusive;

    public String getP1() {
        return photo1.split("\\?")[0];
    }

    public String getP2() {
        return photo2.split("\\?")[0];
    }

    public String getP3() {
        return photo3.split("\\?")[0];
    }

    public String getP4() {
        return photo4.split("\\?")[0];
    }

    public String getP5() {
        return photo5.split("\\?")[0];
    }
}