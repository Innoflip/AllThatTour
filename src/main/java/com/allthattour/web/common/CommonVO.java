package com.allthattour.web.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class CommonVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid; // 고유번호

    @CreationTimestamp
    @Column(nullable = false, name = "reg_dt", updatable = false)
    @ColumnDefault("current_timestamp")
    private LocalDateTime regDt;

    @UpdateTimestamp
    @Column(length = 20)
    private LocalDateTime updatedAt; // 수정 일자

    @Setter
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isEnable = true; // 사용 여부
}