package com.allthattour.web.user.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartAddRequest {
    private String searchId, blockId;
    private Long hotelId, roomId, price, rate;
    private Integer adult, child;
    private String start, end, hotelName, url;
}
