package com.pw.lovedairy.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 张耀斌
 * 用户表
 */
@Table(name = "tb_user")
@Entity
@Setter@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer userId;
    @Column
    String account;
    @Column
    String nickName;
    @Column
    String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tb_user_role",
        joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<RoleEntity> roles = new HashSet<>();
}
