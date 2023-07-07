package com.allthattour.web.user.model;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agoda_hotel")
@Setter
@DynamicInsert
@Getter
public class AgodaHotel {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long hotelId, continentId, countryId, cityId, areaId;
        private Integer popularityScore, numberOfReviews, infantAge,
                        childrenAgeFrom, childrenAgeTo, minGuestAge;
        private String hotelName, hotelFormerlyName, translatedName, longitude, latitude, hotelUrl,
                        accommodationType, nationalityRestrictions;
        @Column(length = 5000)
        private String remark;
        private Double starRating, ratingAverage;
        private Boolean childrenStayFree, singleRoomProperty;
}