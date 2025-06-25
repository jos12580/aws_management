package com.tk.aws.domain.model;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.amazonaws.regions.Regions;
import com.baomidou.mybatisplus.annotation.*;
import com.tk.aws.infrastructure.enums.CanUseRegionEnum;
import com.tk.aws.infrastructure.enums.LightInstanceStaEnum;
import com.tk.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.util.Date;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@TableName("light_instance")
@Data
public class LightInstance extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    private String instanceName;

    private String ip;

    /**
     * 状态
     */
    private LightInstanceStaEnum sta;

    private String region;

    private String regionDesc;
    private String error;

    private String info;


    /**
     * 最后一次拉取时间
     */
    private Long lastUsedTime;

    /**
     * 使用次数
     */
    private Integer useCount;

    /**
     * 上次使用手机标识
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String devicename;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    protected String username;

    /**
     * 是网络否可用
     */
    private Boolean netCanUsed;

    /**
     * 实例类型 0 抢包； 1 上号; 2 采集
     */
    private Integer instanceType;


    /**
     * 亚马逊账号配置Id
     */
    private Long awsKeyConfigId;

    private String awsKeyConfigName;



    public static LightInstance newInstance(String region, Integer instanceType, AwsKeyConfig awsKeyConfig) {
        Regions r = Regions.fromName(region);
        LightInstance it = new LightInstance();
        it.setRegion(region);
        it.setRegionDesc(r.getDescription());
        it.setInstanceName("aws" + instanceType + "_" + UUID.fastUUID().toString(true));
        it.setSta(LightInstanceStaEnum.INIT);
        it.lastUsedTime = 0L;
        it.setInstanceType(instanceType);
        it.setInfo("");
        it.setAwsKeyConfigId(awsKeyConfig.getId());
        it.setAwsKeyConfigName(awsKeyConfig.getLoginName());
        return it;
    }

    public String getDomainInfo() {
//        ec2-35-86-171-159.us-west-2.compute.amazonaws.com
        String format = "ec2-%s.%s.compute.amazonaws.com";
        if (!StringUtils.hasText(ip)) {
            return "";
        }
        return String.format(format, ip.replaceAll("\\.","-"), region);
    }

    public String getRegionChineseName() {
        CanUseRegionEnum regionEnum = CanUseRegionEnum.find(this.region);
        return regionEnum == null ? "未匹配" : regionEnum.getChineseName();
    }

    public String getCreateTimeStr() {
        return this.createTime == null ? "-" : DateUtil.formatDateTime(new Date(this.createTime));
    }

    public String getStaDesc() {
        return this.sta == null ? "" : this.sta.getDesc();
    }

    public Integer getStaCode() {
        return this.sta == null ? null : this.sta.getCode();
    }


    public String getVlessDetail() {
        if (StringUtils.hasText(this.info)) {
            int start =this.info.indexOf('@')+1;
            int end = this.info.indexOf(':', start);
            String replaceStr= this.info.substring(start, end);
            if(StringUtils.hasText(this.ip)){
                return this.info.replaceAll(replaceStr,this.ip);
            }
            return this.info;
        }
        return this.info;
    }




    public void use(String devicename) {
        this.devicename = devicename;
        this.lastUsedTime = System.currentTimeMillis();
        if (Objects.isNull(this.useCount)) {
            this.useCount = 0;
        }
        this.useCount++;
    }

    /**
     * 准备就绪
     */
    public void readyUse() {
        this.netCanUsed = true;
        this.devicename = null;
        this.username = null;
        this.useCount = 0;
        this.lastUsedTime = 0L;
        this.sta = LightInstanceStaEnum.RUNNING;
    }


    /**
     * 是否可以删除
     *
     * @return
     */
    public boolean canDel() {
        return LightInstanceStaEnum.RUNNING.equals(this.sta);
    }



}
