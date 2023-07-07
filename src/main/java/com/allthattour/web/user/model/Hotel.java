package com.allthattour.web.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Hotel {
    private String name, address, introduce, photoUrl, rateStr, priceStr;
    private long hotelId, price, star, aminityCount, reviewCount;
    private double rate;
}
