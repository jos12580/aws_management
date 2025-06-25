package com.tk.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TgUtil {


    private static final String TAG_URL = "https://api.telegram.org/bot8188789147:AAHXJq_VFTZ3T67olgTkDJugX5tj2sdAtaQ/sendMessage";

    public static void sendMsg(String text) {
        Msg msg = new Msg(text);
        HttpUtil.post(TAG_URL, BeanUtil.beanToMap(msg));
    }


    @Data
    static class Msg {
        public String chat_id = "-4528708363";
        public String text;

        public Msg(String text) {
            this.text = text;
        }
    }


}
