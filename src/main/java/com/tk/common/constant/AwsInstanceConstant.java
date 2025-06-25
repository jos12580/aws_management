package com.tk.common.constant;

public class AwsInstanceConstant {

    /**
     * 操作系统id
     *      centos_7_2009_01
     *      ubuntu_22_04
     */
    public final static String BLUEPRINT_ID="ubuntu_22_04";


    /**
     * 实例规格id
     */
    public final static String BUNDLE_ID="nano_3_0";


    /**
     * 启动脚本 安装socks5  ip:16688:10010:10010  %s回调ip  %s 服务器名称
     * sudo su -c "bash <(curl -sL https://raw.githubusercontent.com/jos12580/vpnrepo/master/autosk5.sh)  %s %s"
     */
    public final static String USER_DATA="sudo su -c \"bash <(curl -sL https://raw.githubusercontent.com/jos12580/vpnrepo/master/autosk5.sh)  %s %s\"";

}
