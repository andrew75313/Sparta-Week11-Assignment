package com.sparta.realestatefeed.entity;


import com.sparta.realestatefeed.dto.ProfileRequestDto;
import com.sparta.realestatefeed.dto.UserRegisterRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nick_name")
    private String nickName;
    private String info;

    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRoleEnum role;

    @ElementCollection
    @CollectionTable(name = "user_previous_passwords", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "previous_password")
    private List<String> previousPasswords = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likeList = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followerList = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followingList = new ArrayList<>();

    public User(UserRegisterRequestDto requestDto) {
        this.userName = requestDto.getUserName();
        this.password = requestDto.getPassword();
        this.nickName = requestDto.getNickName();
        this.info = requestDto.getInfo();
        this.email = requestDto.getEmail();
        this.role = requestDto.getRole();
    }

    public void updateProfile(ProfileRequestDto profileRequestDto) {
        this.nickName = profileRequestDto.getNickName();
        this.info = profileRequestDto.getInfo();
        this.email = profileRequestDto.getEmail();
    }
}
