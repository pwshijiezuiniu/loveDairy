package com.pw.lovedairy.dao;

import com.pw.lovedairy.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author 张耀斌
 */
public interface UserDao extends JpaRepository<UserEntity,Integer> {

    @Query("select u from UserEntity u where u.account = ?1")
    public UserEntity findByAccount(String account);
}
