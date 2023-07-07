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

import com.allthattour.web.common.CommonVO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Setter
@DynamicInsert
@Getter
public class User extends CommonVO {

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Size(max = 10)
    private String snsType;

    @Size(max = 120)
    private String snsId;

    @NotBlank
    @Size(max = 120)
    private String password;

    private String firstName, lastName;

    private String phone;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_pid"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String email, String password, String firstName, String lastName, String phone) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }
}