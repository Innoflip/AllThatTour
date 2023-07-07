package com.allthattour.web.user.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.allthattour.web.common.CommonVO;
import com.allthattour.web.user.payload.request.CartAddRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart")
@Setter
@DynamicInsert
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Cart extends CommonVO {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
    private Long hotelId;
    private Long roomId;
    private String searchId, blockId;
    private Integer adult, child;
    private String url, start, end, hotelName;
    private Long price, rate;

    public Cart(User user, CartAddRequest req) {
        this.user = user;
        this.hotelId = req.getHotelId();
        this.roomId = req.getRoomId();
        this.adult = req.getAdult();
        this.child = req.getChild();
        this.start = req.getStart();
        this.end = req.getEnd();
        this.price = req.getPrice();
        this.url = req.getUrl();
        this.blockId = req.getBlockId();
        this.searchId = req.getSearchId();
        this.hotelName = req.getHotelName();
        this.rate = req.getRate();
    }
}