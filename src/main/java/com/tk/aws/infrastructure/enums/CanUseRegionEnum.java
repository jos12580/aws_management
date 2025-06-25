package com.tk.aws.infrastructure.enums;


import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 可用区枚举
 */
@Getter
public enum CanUseRegionEnum {


    /**
     * 可用区枚举
     */
    SYDNEY("ap-southeast-2", "悉尼","Sydney"),
    SINGAPORE("ap-southeast-1", "新加坡","Singapore"),
    TOKYO("ap-northeast-1", "东京","Tokyo"),
    SEOUL("ap-northeast-2", "首尔","Seoul"),
    MUMBAI("ap-south-1", "孟买","Mumbai"),
    STOCKHOLM("eu-north-1", "斯德哥摩尔","Stockholm"),
    OREGON("us-west-2", "俄勒冈州","Oregon"),
    OHIO("us-east-2", "俄亥俄州","Ohio"),
    VIRGINIA("us-east-1", "弗吉尼亚州","Virginia"),
    MONTREAL("ca-central-1", "蒙特利尔","Montreal"),
    FRANKFURT("eu-central-1", "法兰克福","Frankfurt"),
    PARIS("eu-west-3", "巴黎","Paris"),
    LONDON("eu-west-2", "伦敦","London"),
    IRELAND("eu-west-1", "爱尔兰","Ireland"),

    ;
    private final String name;
    private final String ChineseName;

    private final String en;

    CanUseRegionEnum(String name, String chineseName,String en) {
        this.name = name;
        this.ChineseName = chineseName;
        this.en=en;
    }


    public static List<CanUseRegionEnum> find(List<String> list) {
        if (ObjectUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return Arrays.stream(CanUseRegionEnum.values()).filter(i -> list.contains(i.name)).collect(Collectors.toList());
    }

    public static CanUseRegionEnum find(String region){
        return Arrays.stream(CanUseRegionEnum.values()).filter(i -> i.name.equals(region)).findFirst().orElse(null);
    }


}
