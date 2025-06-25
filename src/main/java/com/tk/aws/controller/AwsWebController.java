package com.tk.aws.controller;

import com.tk.aws.domain.model.AwsKeyConfig;
import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.service.AwsUserKeyService;
import com.tk.aws.service.LightService;
import com.tk.common.annotation.NoApiValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/aws")
@NoApiValidate
public class AwsWebController {

    @Resource
    private AwsUserKeyService awsUserKeyService;

    @Resource
    private LightService lightService;

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * 首页
     */
    @GetMapping("/")
    public String index() {
        return "aws/index";
    }

    /**
     * AWS密钥管理页面
     */
    @GetMapping("/keys")
    public String keysPage() {
        return "aws/keys";
    }

    /**
     * 添加AWS密钥页面
     */
    @GetMapping("/keys/add")
    public String addKeyPage() {
        return "aws/key-add";
    }

    /**
     * 编辑AWS密钥页面
     */
    @GetMapping("/keys/edit/{id}")
    public String editKeyPage(@PathVariable Long id, Model model) {
        AwsKeyConfig key = awsUserKeyService.getById(id);
        model.addAttribute("key", key);
        return "aws/key-edit";
    }

    /**
     * AWS实例管理页面
     */
    @GetMapping("/instances")
    public String instancesPage() {
        return "aws/instances";
    }

    /**
     * 添加AWS实例页面
     */
    @GetMapping("/instances/add")
    public String addInstancePage(Model model) {
        List<AwsKeyConfig> keys = awsUserKeyService.list();
        model.addAttribute("keys", keys);
        model.addAttribute("instanceTypes", new Object[][]{
            {0, "抢包"},
            {1, "上号"},
            {2, "采集"}
        });
        return "aws/instance-add";
    }

    /**
     * 编辑AWS实例页面
     */
    @NoApiValidate
    @GetMapping("/instances/edit/{id}")
    public String editInstancePage(@PathVariable Long id, Model model) {
        LightInstance instance = lightService.getById(id);
        List<AwsKeyConfig> keys = awsUserKeyService.list();
        model.addAttribute("instance", instance);
        model.addAttribute("keys", keys);
        model.addAttribute("instanceTypes", new Object[][]{
            {0, "抢包"},
            {1, "上号"},
            {2, "采集"}
        });
        return "aws/instance-edit";
    }
} 