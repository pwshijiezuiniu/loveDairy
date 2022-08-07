package com.pw.lovedairy.dao;


import com.pw.lovedairy.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 张耀斌
 */
public interface RoleDao extends JpaRepository<RoleEntity,Integer> {
}
