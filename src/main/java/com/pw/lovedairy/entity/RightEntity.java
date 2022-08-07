package com.pw.lovedairy.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 张耀斌
 */
@Table(name = "tb_right")
@Entity
@Setter
@Getter
public class RightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer rightId;
    @Column
    String rightName;
    @Column
    String rightCode;

    @ManyToMany(mappedBy = "rights")
    Set<RoleEntity> roles = new HashSet<>();
}
