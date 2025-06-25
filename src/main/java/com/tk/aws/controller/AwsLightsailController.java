package com.tk.aws.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tk.aws.controller.dto.LightInstanceListReqDTO;
import com.tk.aws.domain.model.AwsKeyConfig;
import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.service.AwsUserKeyService;
import com.tk.aws.service.LightService;
import com.tk.common.annotation.NoApiValidate;
import com.tk.common.annotation.RepeatSubmit;
import com.tk.common.annotation.UName;
import com.tk.common.config.AsyncConfig;
import com.tk.common.vo.R;
import com.tk.server.application.impl.UserApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/amazon/api/aws/")
@NoApiValidate
@Tag(name = "亚马逊服务器接口")
public class AwsLightsailController {

    @Resource
    private LightService lightService;


    @Resource
    private UserApplication application;

    @Resource
    private AwsUserKeyService awsUserKeyService;



    @Operation(summary = "aws创建成功回调")
    @NoApiValidate
    @RequestMapping("/ip/callback/{instanceName}")
    public void callback(@PathVariable("instanceName") String instanceName, HttpServletResponse response,
                         @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        log.info("callback instanceName==>{}", instanceName);
        StringBuilder sf = new StringBuilder();
        if (!ObjectUtils.isEmpty(file)) {
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                sf.append(line);
            }
        }
        lightService.callback(instanceName, sf.toString());
        try {
            // 设置响应信息
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            // 获取响应写入流
            PrintWriter writer = response.getWriter();
            // 写入响应内容
            writer.print("success");
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }
    }


    @Operation(summary = "获取aws账号列表")
    @GetMapping("/lightsailKey")
    public R<List<AwsKeyConfig>> lightsailKey() {
        List<AwsKeyConfig> list = lightService.lightsailKey();
        return R.ok(list);
    }


    @NoApiValidate
    @Operation(summary = "查看当前线程池队列")
    @GetMapping("/executor")
    public R<Map<Object, Object>> executorQueueCapacity() {
        Map<Object, Object> map = new HashMap<>();
        ThreadPoolTaskExecutor executor = AsyncConfig.getExecutor();
        if (Objects.nonNull(executor)) {
            ThreadPoolExecutor threadPoolExecutor = executor.getThreadPoolExecutor();
            BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
            map.put(executor.getThreadNamePrefix(), queue.size());
        }
        return R.ok(map);
    }


    @NoApiValidate
    @Operation(summary = "获取空闲ip集合")
    @GetMapping("/free")
    public R<List<LightInstance>> free(@RequestParam(required = false) Integer instanceType) {
        List<LightInstance> free = lightService.free(instanceType);
        return R.ok(free);
    }


    @NoApiValidate
    @Operation(summary = "获取ip集合")
    @GetMapping("/all")
    public R<List<String>> all(@RequestParam(required = false, defaultValue = "0") Integer instanceType) {
        return lightService.all(instanceType);
    }


    @NoApiValidate
    @Operation(summary = "随机获取ip")
    @GetMapping("/randomUse")
    public R<LightInstance> randomUse(@RequestParam(required = false, defaultValue = "0") Integer instanceType) {
        return lightService.randomUse(instanceType);
    }


    @Operation(summary = "获取区域ip数量")
    @GetMapping("/groupByRegion")
    public R<List<Map<String, Object>>> groupByRegion(@RequestParam(required = false, defaultValue = "0") Integer instanceType) {
        return lightService.groupByRegion(instanceType);
    }

    @Operation(summary = "空闲ip数量")
    @GetMapping("/freeNum")
    public R<Map<Object, Object>> freeNum(@RequestParam(required = false, defaultValue = "0") Integer instanceType) {
        return lightService.freeNum(instanceType);
    }


    @Operation(summary = "创建服务器")
    @PostMapping("/create")
    @RepeatSubmit(key = "#region", expireTime = 30)
    public R<Object> create(@RequestParam String region,
                    @RequestParam Integer num,
                    @RequestParam(required = false, defaultValue = "0") Integer instanceType,
                    @RequestParam(required = false, defaultValue = "0") Long awsConfigId) {
        return lightService.create(num, region, instanceType, awsConfigId);
    }

    @NoApiValidate
    @Operation(summary = "更换ip")
    @PostMapping("/changeIp")
    @RepeatSubmit(key = "#instanceName", expireTime = 30)
    public R<Object> changeIp(@RequestParam String instanceName) {
         lightService.changeIpByMq(instanceName);
         return R.ok();
    }


    @Operation(summary = "获取ip列表")
    @PostMapping("/list")
    public R<Object> ipInfoList(@RequestBody LightInstanceListReqDTO params) {
        List<AwsKeyConfig> list = awsUserKeyService.list();
        List<HashMap<Object, Object>> mapList = list.stream().map(i -> {
            HashMap<Object, Object> map = new HashMap<>();
            map.put("key", i.getId());
            map.put("value", i.getLoginName());
            return map;
        }).collect(Collectors.toList());
        IPage<LightInstance> page = lightService.lightInstanceList(params);
        Map<String, Object> map = BeanUtil.beanToMap(page);
        map.put("awsKeys", mapList);
        return R.ok(map);
    }

    @Operation(summary = "ip测试 sk5")
    @GetMapping("/ipTest")
    @NoApiValidate
    public R<Object> ipTest(@RequestParam String host) throws InterruptedException {
        Object str = lightService.ipTest(host);
        return R.ok(str);
    }


    @Operation(summary = "ip使用,从token获取")
    @PostMapping("/used")
    public R<LightInstance> used(@RequestParam("devicename") String devicename,
                                 @RequestParam(required = false, defaultValue = "0") Integer instanceType,
                                 @RequestParam(required = false) String region,
                                  @UName String username) {
        if (!StringUtils.hasText(devicename)) {
            R.fail("请输入设备标识");
        }
        LightInstance instance = lightService.used(devicename, instanceType, region, username);
        return R.ok(instance);
    }

    @Operation(summary = "ip使用")
    @NoApiValidate
    @PostMapping("/used/cache")
    public R<LightInstance> used(@RequestParam("devicename") String devicename,
                                 @RequestParam(required = false, defaultValue = "0") Integer instanceType,
                                 @RequestParam(required = false) String region,
                                 @RequestParam(value = "account", required = false, defaultValue = "") String account,
                                 @RequestParam(value = "password", required = false, defaultValue = "") String password) {
        if (!StringUtils.hasText(devicename)) {
            R.fail("请输入设备标识");
        }
        application.login(account, password);
        LightInstance instance = lightService.used(devicename, instanceType, region, account);
        return R.ok(instance);
    }

    @Operation(summary = "删除服务器")
    @DeleteMapping("/del")
    public R<String> del(@RequestParam String instanceName) {
        String str = lightService.del(instanceName);
        return R.ok(str);
    }

    @NoApiValidate
    @Operation(summary = "获取可用区")
    @GetMapping("/regions")
    public R<List<Map<Object, Object>>> regions() {
        List<Map<Object, Object>> list = lightService.regions();
        return R.ok(list);
    }


}
