package com.tk.common.util;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网络请求工具类
 */
public class HttpRequestUtil {


    /**
     * 提取url中的参数，返回参数map
     * @param url
     * @return
     */
    public static Map<String, Object> paramsToMap(String url) {
        Map<String, Object> paramsMap = new HashMap<>();

        // 获取URL的查询部分
        String query = url.substring(url.indexOf('?') + 1);

        // 按&分割查询字符串，得到每个key=value形式的参数
        String[] params = query.split("&");

        for (String param : params) {
            // 按=分割参数，得到key和value
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];

                // 如果map中已经存在该key，则将value添加到对应的list中
                if (paramsMap.containsKey(key)) {
                    Object existingValue = paramsMap.get(key);
                    if (existingValue instanceof List) {
                        ((List<String>) existingValue).add(value);
                    } else {
                        List<String> valuesList = new ArrayList<>();
                        valuesList.add((String) existingValue);
                        valuesList.add(value);
                        paramsMap.put(key, valuesList);
                    }
                } else {
                    // 如果map中不存在该key，则直接put
                    paramsMap.put(key, value);
                }
            }
        }

        return paramsMap;
    }

    /**
     * map请求数据转成query
     * @param params
     * @return
     */
    public static String mapToQueryString(Map<String, Object> params) {
        StringBuilder queryString = new StringBuilder();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (queryString.length() > 0) {
                    queryString.append("&");
                }
                queryString.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            // Handle the exception or rethrow as needed
            e.printStackTrace();
        }
        return queryString.toString();
    }




    /**
     * 自定义编码逻辑，保留 {} 和 []
     * {"from":"personal-center","uk":"C9p1MpNgm-TOnLN_vdZfRQ","tab":"dynamic"}
     * @param input
     * @return
     */
    public static String customEncode(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c == '{' || c == '}' || c == '[' || c == ']' || c == ':' || c == ',') {
                sb.append(c);
            } else {
                try {
                    sb.append(URLEncoder.encode(String.valueOf(c), StandardCharsets.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
