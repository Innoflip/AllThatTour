package com.allthattour.web.user.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.transaction.Transactional;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.multipart.MultipartFile;

import com.allthattour.web.common.CommonVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment extends CommonVO {
    private String lastTransactionKey, paymentKey, requestedAt, approvedAt;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String orderName;

    private String receipt;

    private String bookingDids;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private User user;
}