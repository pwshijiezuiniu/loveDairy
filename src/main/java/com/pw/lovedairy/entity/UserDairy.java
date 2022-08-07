package com.pw.lovedairy.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.xml.crypto.Data;
import java.util.Date;

@Table(name = "tb_dairy")
@Entity
@Setter
@Getter
public class UserDairy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer dairyId;
    @Column
    String account;
    @Column
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    Date time;
    @Column
    String title;
    @Column
    String text;
}
