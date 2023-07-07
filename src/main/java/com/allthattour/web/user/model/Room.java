package com.allthattour.web.user.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    private Long id;
    private String name, content, blockId;
    private List<String> tags;
    private Long price, rate;
}
