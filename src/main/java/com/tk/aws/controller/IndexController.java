package com.tk.aws.controller;

import com.tk.common.annotation.NoApiValidate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    /**
     * 根路径重定向到AWS管理系统
     */
    @GetMapping("/")
    @NoApiValidate
    public String index() {
        return "redirect:/aws/";
    }
} 