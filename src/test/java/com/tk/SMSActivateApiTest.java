package com.tk;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.lightsail.model.GetInstancesRequest;
import com.amazonaws.services.lightsail.model.GetInstancesResult;
import com.amazonaws.services.lightsail.model.Instance;
import com.tk.aws.domain.model.AwsKeyConfig;
import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.domain.repository.AwsKeyConfigRepository;
import com.tk.aws.domain.repository.LightInstanceRepository;
import com.tk.aws.infrastructure.config.AmazonClient;
import com.tk.aws.infrastructure.config.AwsLightsailConfig;
import com.tk.aws.infrastructure.enums.CanUseRegionEnum;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class SMSActivateApiTest {

    @Resource
    private AwsKeyConfigRepository awsKeyConfigRepository;

    @Resource
    private LightInstanceRepository lightInstanceRepository;


    public static void main(String[] args) throws Exception {


    }


    public static void mains(String[] args) throws Exception {
        String url = "https://mbd.baidu.com/webpage?type=topic&action=search&format=json&content=&category=&resource_type=4&req_from=2&tab_id=&pn=1&rn=500";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        String body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONObject("tabs").getJSONArray("list");
        System.out.println(jsonArray);
        for (Object obj : jsonArray) {
            if (obj instanceof JSONObject json) {
                String tab_name = json.getString("name");
                String tab_id = json.getString("id");
//                System.out.println(tab_name);
                List<Map> list = lab(tab_id, tab_name);
            }
        }
    }

    public static  List<Map> lab( String tab_id,String tab_name) throws Exception {
        String url = String.format("https://mbd.baidu.com/webpage?type=topic&action=search&format=json&content=&category=&resource_type=4&req_from=3&tab_id=%s&pn=1&rn=150", tab_id);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        String body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONObject("all").getJSONArray("list");
        jsonArray.forEach(i->{
            if (i instanceof JSONObject json) {
                json.put("tab_name",tab_name);
            }
        });
        List<Map> list = jsonArray.toJavaList(Map.class);
        return list;
    }

    // 获取远程图片的文件大小
    public static long getFileSize(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(60000); // 设置连接超时
        connection.setReadTimeout(60000);    // 设置读取超时
        connection.connect();
        long fileSize = connection.getContentLengthLong();
        connection.disconnect();
        return fileSize;
    }

    /***
     * 删除aws无用实例
     */
//    @Test
    public void awsRemove() {

        List<AwsKeyConfig> list = awsKeyConfigRepository.list();
        HashMap<String, AwsKeyConfig> map = new HashMap<>();
        list.stream().forEach(i -> map.put(i.getAccessKey(), i));
        for (Map.Entry<String, AwsKeyConfig> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "---------------------------" + entry.getValue());
            List<LightInstance> collect = lightInstanceRepository.list();

            AwsLightsailConfig config = new AwsLightsailConfig(entry.getKey(), entry.getValue().getSecretKey());
            for (CanUseRegionEnum regionEnum : CanUseRegionEnum.values()) {
                if (regionEnum.getName().equals("eu-central-1")) {
                    continue;
                }
                AmazonClient client = AmazonClient.build(config, regionEnum.getName());
                GetInstancesRequest request = new GetInstancesRequest();
                String token = null;
                do {
                    request.setPageToken(token);
                    GetInstancesResult result = client.getServer().getInstances(request);
                    token = result.getNextPageToken();
                    for (Instance instance : result.getInstances()) {
                        Optional<LightInstance> optional = collect.stream().filter(i -> i.getInstanceName().equals(instance.getName())).findFirst();
                        if (!optional.isPresent()) {
                            client.deleteInstance(instance.getName());
                            System.out.println("deldeldeldel +" + instance.getName());
                        }
                    }
                    } while (StringUtils.hasText(token));
            }
        }

    }


}

