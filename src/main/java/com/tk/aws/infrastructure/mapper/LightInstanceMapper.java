package com.tk.aws.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tk.aws.domain.model.LightInstance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LightInstanceMapper extends BaseMapper<LightInstance> {
}
