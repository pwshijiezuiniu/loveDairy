package com.pw.lovedairy.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 张耀斌
 */
@Table(name = "tb_role")
@Entity
@Setter
@Getter
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer roleId;
    @Column
    String roleName;

    @ManyToMany(mappedBy = "roles")
    Set<UserEntity> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_role_right",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "right_id")
    )
    Set<RightEntity> rights = new HashSet<>();
}
