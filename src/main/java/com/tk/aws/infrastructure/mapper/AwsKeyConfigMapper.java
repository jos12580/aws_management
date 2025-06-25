package com.tk.aws.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tk.aws.domain.model.AwsKeyConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AwsKeyConfigMapper extends BaseMapper<AwsKeyConfig> {
}
