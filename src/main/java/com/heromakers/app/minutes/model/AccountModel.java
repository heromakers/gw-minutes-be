package com.heromakers.app.minutes.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

@Table(name = "tb_account")
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class AccountModel implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(name = "account_key")
    private String accountKey;

    @Column(name = "password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "join_type")
    private String joinType;

    @Column(name = "sns_key")
    private String snsKey;

    @Column(name = "human_name")
    private String humanName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "address")
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "email")
    private String email;
    
    @Column(name = "roles")
    @Builder.Default
    private String roles = "ROLE_USER";
    // ROLE_USER, ROLE_MANAGER, ROLE_ADMIN

    @Column(name = "level")
    private String level;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "fcm_token")
    private String fcmToken;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "use_fg")
    @Builder.Default
    private Boolean useFlag = true;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_at")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "login_at")
    private Instant loginAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "password_at")
    private Instant passwordAt;

    @Transient
    @Builder.Default
    private FileModel profileImage = new FileModel();

    @Transient
    @Builder.Default
    private String signStatus = "Success";

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
        if(roles == null || roles.isEmpty()) return null;

        String[] roleArray = roles.split(",");
        for(String role:roleArray) {
            auths.add(new SimpleGrantedAuthority(role));
        }
        return auths;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return accountKey;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return useFlag;
    }

}
