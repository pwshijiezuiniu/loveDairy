package com.pw.lovedairy.dao;

import com.pw.lovedairy.entity.UserDairy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface DairyDao extends JpaRepository<UserDairy,Integer> {
    @Query("select u from UserDairy u where u.account = ?1 order by u.dairyId desc ")
    public List<UserDairy> findAllByAccount(String account);

    @Query("select u from UserDairy u where u.account <> ?1 order by u.dairyId desc ")
    public List<UserDairy> findAllByLoverAccount(String account);
    @Query(" select u.dairyId from UserDairy u where u.time=?1 and u.account=?2 ")
    public Integer findId(Date time,String account);


}
