package com.allthattour.web.user.model;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agoda_room_type")
@Setter
@DynamicInsert
@Getter
public class AgodaRoomType {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long hotelId, hotelRoomTypeId, maxOccupancyPerRoom, noOfRoom, sizeOfRoom, maxExtrabeds,
                        maxInfantInRoom,
                        hotelMasterRoomTypeId;
        private String standardCaption, standardCaptionTranslated, views, hotelRoomTypePicture, bedType, gender,
                        roomSizeInclTerrace;
        private Boolean sharedBathroom;
}