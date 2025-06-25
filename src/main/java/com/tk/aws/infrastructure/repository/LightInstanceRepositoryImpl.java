package com.tk.aws.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tk.aws.controller.dto.LightInstanceListReqDTO;
import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.domain.repository.LightInstanceRepository;
import com.tk.aws.infrastructure.enums.LightInstanceStaEnum;
import com.tk.aws.infrastructure.mapper.LightInstanceMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class LightInstanceRepositoryImpl extends ServiceImpl<LightInstanceMapper, LightInstance> implements LightInstanceRepository {
    @Override
    public LightInstance getByInstanceName(String instanceName) {
        LambdaQueryWrapper<LightInstance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LightInstance::getInstanceName, instanceName).last("limit 1");
        return this.getOne(wrapper);
    }

    @Override
    public LightInstance findByDeviceNameOrNull(String devicename, Integer instanceType, String region) {
        LambdaQueryWrapper<LightInstance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LightInstance::getNetCanUsed, true)
                .eq(LightInstance::getInstanceType, instanceType)
                .eq(LightInstance::getSta, LightInstanceStaEnum.RUNNING)
                .eq(StringUtils.hasText(region), LightInstance::getRegion, region)
                .and(i -> i.eq(StringUtils.hasText(devicename), LightInstance::getDevicename, devicename)
                        .or().isNull(LightInstance::getDevicename)
                        .or().eq(LightInstance::getDevicename,""))
                .orderByDesc(LightInstance::getDevicename)
                .orderByAsc(LightInstance::getLastUsedTime)
                .last("limit 1");
        return this.getOne(wrapper);
    }

    @Override
    public long countByUnameAndType(String uname, Integer instanceType) {
        LambdaQueryWrapper<LightInstance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LightInstance::getUsername,uname)
                .eq(Objects.nonNull(instanceType),LightInstance::getInstanceType,instanceType);
        return this.count(wrapper);
    }

    @Override
    public List<LightInstance> getByKeyConfigId(Long awsConfigId) {
        LambdaQueryWrapper<LightInstance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LightInstance::getAwsKeyConfigId, awsConfigId);
        return this.list(wrapper);
    }

    @Override
    public List<LightInstance> free(Integer instanceType) {
        LambdaQueryWrapper<LightInstance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Objects.nonNull(instanceType),LightInstance::getInstanceType,instanceType)
                .isNull(LightInstance::getDevicename);
        return list(wrapper);
    }

    @Override
    public List<LightInstance> findBySta(LightInstanceStaEnum staEnum) {
        LambdaQueryWrapper<LightInstance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LightInstance::getSta, staEnum);
        return this.list(wrapper);
    }


    @Override
    public LightInstance findByDeviceName(String devicename, Integer instanceType, String region) {
        LambdaQueryWrapper<LightInstance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LightInstance::getNetCanUsed, true)
                .eq(LightInstance::getInstanceType, instanceType)
                .eq(LightInstance::getSta, LightInstanceStaEnum.RUNNING)
                .eq(StringUtils.hasText(devicename), LightInstance::getDevicename, devicename)
                .eq(StringUtils.hasText(region), LightInstance::getRegion, region)
                .isNull(!StringUtils.hasText(devicename), LightInstance::getDevicename)
                .last("limit 1")
                .orderByAsc(LightInstance::getLastUsedTime);
        return this.getOne(wrapper);
    }

    @Override
    public void weekUpIpBeforeTime(long time, int instanceType) {
        LambdaUpdateWrapper<LightInstance> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(LightInstance::getSta, LightInstanceStaEnum.RUNNING)
                .eq(LightInstance::getInstanceType,instanceType)
                .le(LightInstance::getLastUsedTime, time)
                .set(LightInstance::getDevicename, null)
                .set(LightInstance::getUsername, null)
                .set(LightInstance::getNetCanUsed, true)
                .set(LightInstance::getUseCount,0);
        this.update(wrapper);
    }

    @Override
    public Page<LightInstance> search(LightInstanceListReqDTO params) {
        LambdaQueryWrapper<LightInstance> wrapper = Wrappers.lambdaQuery();
        wrapper.and(StringUtils.hasText(params.getSearch()), q ->
                q.like(LightInstance::getInstanceName, params.getSearch())
                        .or()
                        .like(LightInstance::getDevicename, params.getSearch())
                        .or()
                        .like(LightInstance::getIp, params.getSearch())
                        .or()
                        .like(LightInstance::getRegionDesc, params.getSearch()));
        wrapper.eq(LightInstance::getInstanceType, params.getInstanceType())
                .eq(Objects.nonNull(params.getAwsKeyConfigId()), LightInstance::getAwsKeyConfigId, params.getAwsKeyConfigId());
        Page<LightInstance> page = new Page<>(params.getCurrent(), params.getSize());
        page.addOrder(OrderItem.asc("last_used_time"));
        return this.page(page, wrapper);
    }

    @Override
    public long countFreeNum(Integer instanceType) {
        LambdaQueryWrapper<LightInstance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LightInstance::getNetCanUsed, true)
                .isNull(LightInstance::getDevicename)
                .eq(LightInstance::getInstanceType, instanceType)
                .eq(LightInstance::getSta, LightInstanceStaEnum.RUNNING);
        return this.count(wrapper);
    }

    @Override
    public List<Map<String, Object>> countByRegion(Integer instanceType) {
        QueryWrapper<LightInstance> query = Wrappers.query();
        query.select("region", "count(1) as count").lambda()
                .eq(LightInstance::getInstanceType, instanceType)
                .groupBy(LightInstance::getRegion);
        return this.getBaseMapper().selectMaps(query);
    }

    @Override
    public List<LightInstance> canUseList(Integer instanceType) {
        LambdaQueryWrapper<LightInstance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LightInstance::getNetCanUsed, true)
                .eq(LightInstance::getInstanceType, instanceType)
                .eq(LightInstance::getSta, LightInstanceStaEnum.RUNNING);
        return this.list(wrapper);
    }

    @Override
    public long count(Integer instanceType) {
        LambdaQueryWrapper<LightInstance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LightInstance::getInstanceType, instanceType);
        return this.count(wrapper);
    }




}
