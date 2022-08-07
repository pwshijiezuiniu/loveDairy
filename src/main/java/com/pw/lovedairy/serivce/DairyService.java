package com.pw.lovedairy.serivce;

import com.pw.lovedairy.common.ResultData;
import com.pw.lovedairy.dao.DairyDao;
import com.pw.lovedairy.entity.UserDairy;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DairyService {
    @Autowired
    DairyDao dairyDao;

    public Integer findId(UserDairy userDairy){
        return dairyDao.findId(userDairy.getTime(),userDairy.getAccount());
    }
    public List<UserDairy> findAllByAccount(String account){
        return dairyDao.findAllByAccount(account);
    }

    public List<UserDairy> findAllByLoverAccount(String account){
        return dairyDao.findAllByLoverAccount(account);
    }
    public void alter(Integer dairyId,String text){
        UserDairy userDairy = dairyDao.findById(dairyId).get();
        userDairy.setDairyId(dairyId);
        userDairy.setText(text);
        dairyDao.save(userDairy);
    }
    public void delete(Integer dairyId){
        dairyDao.deleteById(dairyId);
    }
    public void  save(UserDairy userDairy){
        dairyDao.save(userDairy);
    }
    public UserDairy findById(Integer dairyId){
        return dairyDao.findById(dairyId).get();
    }
}
