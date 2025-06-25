package com.tk.aws.service.impl;

import com.tk.aws.domain.model.AwsKeyConfig;
import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.domain.repository.AwsKeyConfigRepository;
import com.tk.aws.service.AwsUserKeyService;
import com.tk.aws.service.LightService;
import com.tk.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class AwsUserKeyServiceImpl implements AwsUserKeyService {

    @Resource
    private AwsKeyConfigRepository awsKeyConfigRepository;

    @Resource
    private LightService lightService;

    @Override
    public AwsKeyConfig add(AwsKeyConfig reqDTO) {
        AwsKeyConfig awsKeyConfig= awsKeyConfigRepository.getByLoginName(reqDTO.getLoginName());
        if(Objects.nonNull(awsKeyConfig)){
            throw new GlobalException("账号已存在");
        }
        awsKeyConfigRepository.save(reqDTO);
        return reqDTO;
    }

    @Override
    public AwsKeyConfig update(AwsKeyConfig reqDTO) {
        AwsKeyConfig existingConfig = awsKeyConfigRepository.getById(reqDTO.getId());
        if (Objects.isNull(existingConfig)) {
            throw new GlobalException("账号不存在");
        }
        
        // 检查登录名称是否重复（排除自己）
        AwsKeyConfig duplicateConfig = awsKeyConfigRepository.getByLoginName(reqDTO.getLoginName());
        if (Objects.nonNull(duplicateConfig) && !duplicateConfig.getId().equals(reqDTO.getId())) {
            throw new GlobalException("登录名称已存在");
        }
        awsKeyConfigRepository.updateById(reqDTO);
        return reqDTO;
    }

    @Override
    public AwsKeyConfig getById(Long id) {
        return awsKeyConfigRepository.getById(id);
    }

    @Override
    public Long del(Long id) {
        List<LightInstance> list = lightService.getByKeyConfigId(id);
        if(!ObjectUtils.isEmpty(list)){
            throw new GlobalException("账号下有实例，无法删除");
        }
        awsKeyConfigRepository.removeById(id);
        return id;
    }

    @Override
    public List<AwsKeyConfig> list() {
        return awsKeyConfigRepository.list();
    }


}
