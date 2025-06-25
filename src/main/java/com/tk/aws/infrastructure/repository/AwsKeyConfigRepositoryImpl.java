package com.tk.aws.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tk.aws.domain.model.AwsKeyConfig;
import com.tk.aws.domain.repository.AwsKeyConfigRepository;
import com.tk.aws.infrastructure.mapper.AwsKeyConfigMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Repository
public class AwsKeyConfigRepositoryImpl extends ServiceImpl<AwsKeyConfigMapper, AwsKeyConfig> implements AwsKeyConfigRepository {


    @Override
    public AwsKeyConfig getByLoginName(String loginName) {
        LambdaQueryWrapper<AwsKeyConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(loginName), AwsKeyConfig::getLoginName, loginName)
                .last("limit 1");
        return this.getOne(wrapper);
    }
}
