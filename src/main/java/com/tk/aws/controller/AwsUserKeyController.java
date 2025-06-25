package com.tk.aws.controller;


import com.tk.aws.domain.model.AwsKeyConfig;
import com.tk.aws.service.AwsUserKeyService;
import com.tk.common.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/amazon/api/aws/key")
@Tag(name = "亚马逊账号接口")
public class AwsUserKeyController {


    @Resource
    private AwsUserKeyService awsUserKeyService;

    @Operation(summary = "添加aws账号")
    @PostMapping("/add")
    public R<Object> add(@RequestBody AwsKeyConfig reqDTO) {
        return R.ok(awsUserKeyService.add(reqDTO));
    }

    @Operation(summary = "更新aws账号")
    @PutMapping("/update")
    public R<Object> update(@RequestBody AwsKeyConfig reqDTO) {
        return R.ok(awsUserKeyService.update(reqDTO));
    }

    @Operation(summary = "根据ID获取aws账号")
    @GetMapping("/{id}")
    public R<AwsKeyConfig> getById(@PathVariable Long id) {
        return R.ok(awsUserKeyService.getById(id));
    }

    @Operation(summary = "删除aws账号")
    @PostMapping("/del/{id}")
    public R<Object>  del(@PathVariable Long id) {
        return R.ok(awsUserKeyService.del(id));
    }

    @Operation(summary = "获取aws账号列表")
    @PostMapping("/list")
    public R<Object>  list() {
        return R.ok(awsUserKeyService.list());
    }



}
