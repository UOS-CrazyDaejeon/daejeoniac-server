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

    public enum Role {
        USER,
        ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;

    private Role role;

    private String password;

    private String memberName;

    @Column(unique = true)
    private String nickname;

    private LocalDateTime createdAt;

    private String phone;

    private Integer point;

    public void addPoint(Integer point) {
        if(this.point == null)
            this.point = 0;

        this.point += point;
    }

    public static Member create(
            String loginId,
            Role role,
            String password,
            String memberName,
            String nickname,
            String phone
    ) {
        return Member.builder()
                .loginId(loginId)
                .role(role)
                .password(password)
                .memberName(memberName)
                .nickname(nickname)
                .phone(phone)
                .point(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateProfile(String nickname, String phone) {
        this.nickname = nickname;
        this.phone = phone;
    }
}
