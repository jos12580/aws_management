package com.tk.aws.domain.service;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.SocksProxy;


public interface MyForestClient {


    /**
     * http://demo.ip-api.com/json/?lang=zh-CN
     * https://ipinfo.io/json
     * 使用 @SocksProxy 注解设置代 Socks 协议的理服务器
     * host 属性值来自全局变量 proxy.host
     * port 属性值来自全局变量 proxy.port
     * username 属性值来自方法的第一个参数 uname
     * password 属性值来自方法的第二个参数 pass
     */
    @Get( "http://demo.ip-api.com/json/?lang=zh-CN")
    @SocksProxy(host = "{0}", port = "{1}", username="{2}", password="{3}")
    String showIpInfo(String host,String port,String uname, String passowrd);
}
