package com.daejeongwang.uoscrazydaejeon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;

    private String password;

    private String memberName;

    @Column(unique = true)
    private String nickname;

    private LocalDateTime createdAt;

    private String phone;

    private Integer point;

    // 임시(역할이 애매해서 통일 필요)
    private Integer coupon;

    public void addPoint(Integer point) {
        if(this.point == null)
            this.point = 0;

        this.point += point;
    }
}
