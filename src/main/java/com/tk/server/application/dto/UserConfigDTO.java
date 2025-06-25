package com.tk.server.application.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserConfigDTO implements Serializable {

    private Integer noGetNumConf=20;
    private Integer keepRunMinute=60;
    private Integer clickNum=14;
    private Boolean notBetweenDay=false;
    private Integer sleepHours=24;

    private Boolean sendGift=true;
    private Integer giftOneLv=100;
    private Integer  giftTwoLv=10;
    private Integer   giftThreeLv=1;
    private Integer  loginNum=80;

    /**
     * ip 休眠多久
     */
    private  double ipSleepHour = 2;
    /**
     * 连续0包几次后ip休眠
     */
    private  Integer ipSleepCount=2;

    /**
     * 劣质号抢包次数判断
     */
    private  Integer lowAccountCount=3;

    private String defaultIpRegion="eu-west-2";

    /**
     * 登录几天后抢包
     */
    private Integer regeditDay=7;

    private Boolean platformLogin=false;

    private Boolean running=true;

}
