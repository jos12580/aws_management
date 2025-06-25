package com.tk.aws.infrastructure.config;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lightsail.AmazonLightsail;
import com.amazonaws.services.lightsail.AmazonLightsailClient;
import com.amazonaws.services.lightsail.model.*;
import com.tk.common.constant.AwsInstanceConstant;
import com.tk.common.exception.GlobalException;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
public class AmazonClient {

    private AmazonLightsail server;

    private String regionName;


    public static AmazonClient build(AwsLightsailConfig lightsailConfig,String regionName) {
        AmazonClient client = new AmazonClient();
        AWSCredentials awsCredentials = new BasicAWSCredentials(lightsailConfig.getAccessKey(), lightsailConfig.getSecretKey());
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        client.server = AmazonLightsailClient.builder()
                .withRegion(Regions.fromName(regionName))
                .withCredentials(awsCredentialsProvider)
                .build();
        client.regionName = regionName;
        return client;
    }

    @SneakyThrows
    public void stopInstance(String instanceName) {
        long l = System.currentTimeMillis();
        while (true){
            try {
                if (DateUtil.offsetMinute(new Date(), -10).getTime() > l) {
                    throw new GlobalException("停止实例超时: " + instanceName);
                }
                this.server.stopInstance(new StopInstanceRequest().withInstanceName(instanceName).withForce(true));
                break;
            }catch (Exception e){
                Thread.sleep(5000);
                log.error(e.getMessage());
                if (e.getMessage().contains("The maximum API request")) {
                    Thread.sleep(10000);
                    continue;
                }
                throw e;
            }
        }

    }

    @SneakyThrows
    public void startInstance(String instanceName) {
        long l = System.currentTimeMillis();
        while (true){
            try {
                if (DateUtil.offsetMinute(new Date(), -10).getTime() > l) {
                    throw new GlobalException("启动实例超时: " + instanceName);
                }
                this.server.startInstance(new StartInstanceRequest().withInstanceName(instanceName));
                break;
            }catch (Exception e){
                Thread.sleep(5000);
                log.error(e.getMessage());
                if (e.getMessage().contains("The maximum API request")) {
                    Thread.sleep(10000);
                    continue;
                }
                throw e;
            }
        }

    }

    /**
     * 创建实例
     *
     * @param name       实例名称
     * @param regionName 可用区 ca-central-1
     * @param awsBaseUrl
     */
    @SneakyThrows
    public void createInstance(String name, String regionName, String awsBaseUrl) {
        long l = System.currentTimeMillis();
        String userData = String.format(AwsInstanceConstant.USER_DATA, awsBaseUrl,name);
        log.info("创建实例: {},{},{}", name, regionName, userData);
        while (true) {
            try {
                if (DateUtil.offsetMinute(new Date(), -10).getTime() > l) {
                    throw new GlobalException("创建实例超时: " + name);
                }
                CreateInstancesRequest request = new CreateInstancesRequest();
                request.withAvailabilityZone(this.getRegion(regionName))
                        .withBlueprintId(AwsInstanceConstant.BLUEPRINT_ID)
                        .withBundleId(AwsInstanceConstant.BUNDLE_ID)
                        .withUserData(userData)
                        .withInstanceNames(name);
                this.server.createInstances(request);
                break;
            } catch (Exception e) {
                Thread.sleep(5000);
                log.error(e.getMessage());
                if (e.getMessage().contains("The maximum API request")
                ||e.getMessage().contains("your requested bundle is not supported in your requested Availability Zone. Please retry your request with a different Availability Zone")
                ) {
                    Thread.sleep(10000);
                    continue;
                }
                throw e;
            }
        }
    }

    /**
     * 获取可用区
     *
     * @param regionName ca-central-1a
     */
    @SneakyThrows
    private String getRegion(String regionName) {
        long l = System.currentTimeMillis();
        while (true){
            try{
                if (DateUtil.offsetMinute(new Date(), -10).getTime() > l) {
                    throw new GlobalException("获取可用区超时: " + regionName);
                }
                GetRegionsResult result = this.server.getRegions(new GetRegionsRequest().withIncludeAvailabilityZones(true));
                List<Region> list = result.getRegions().stream().filter(i -> i.getName().equalsIgnoreCase(regionName)).collect(Collectors.toList());
                List<AvailabilityZone> zoneList = list.get(RandomUtil.randomInt(0, list.size())).getAvailabilityZones();
                String zoneName=zoneList.get(RandomUtil.randomInt(0, zoneList.size())).getZoneName();
                log.info("可用区为: {},{}" ,regionName, zoneName);
                return zoneName;
            }catch (Exception e){
                Thread.sleep(5000);
                log.error(e.getMessage());
                if (e.getMessage().contains("The maximum API request")) {
                    Thread.sleep(10000);
                    continue;
                }
                throw e;
            }
        }

    }

    /**
     * 开放端口
     * 报错就是失败，没报错就成功
     */
    @SneakyThrows
    public void openNetwork(String instanceName) {
        long l = System.currentTimeMillis();
        while (true){
            try {
                if (DateUtil.offsetMinute(new Date(), -10).getTime() > l) {
                    throw new GlobalException("开放端口超时: " + instanceName);
                }
                OpenInstancePublicPortsRequest request = new OpenInstancePublicPortsRequest()
                        .withInstanceName(instanceName)
                        .withPortInfo(
                                new PortInfo().withFromPort(0).withToPort(65535).withProtocol(NetworkProtocol.All)
                        );
                this.server.openInstancePublicPorts(request);
                break;
            }catch (Exception e){
                Thread.sleep(5000);
                log.error(e.getMessage());
                if (e.getMessage().contains("The maximum API request")) {
                    Thread.sleep(10000);
                    continue;
                }
                throw e;
            }
        }

    }


    /**
     * 删除实例
     * 报错就是删除失败，没报错就删除成功
     */
    @SneakyThrows
    public void deleteInstance(String instanceName) {
        long l = System.currentTimeMillis();
        while (true){
            try {
                if (DateUtil.offsetMinute(new Date(), -10).getTime() > l) {
                    throw new GlobalException("删除实例超时: " + instanceName);
                }
                this.server.deleteInstance(new DeleteInstanceRequest().withInstanceName(instanceName));
                break;
            }catch (Exception e){
                Thread.sleep(5000);
                log.error(e.getMessage());
                if (e.getMessage().contains("The maximum API request")||e.getMessage().contains("You cannot delete an instance while it is in transition")) {
                    Thread.sleep(10000);
                    continue;
                }
                if(e.getMessage().contains("The Instance does not exist")){
                    log.error("实例不存在");
                    return;
                }

                throw e;
            }
        }

    }

    /**
     * 获取实例详情
     */
    @SneakyThrows
    public Instance detail(String instanceName) {
        long l = System.currentTimeMillis();
        while (true){
            try {
                if (DateUtil.offsetMinute(new Date(), -10).getTime() > l) {
                    throw new GlobalException("获取实例详情超时: " + instanceName);
                }
                GetInstanceResult result = this.server.getInstance(new GetInstanceRequest().withInstanceName(instanceName));
                return result.getInstance();
            }catch (Exception e){
                Thread.sleep(5000);
                log.error(e.getMessage());
                if (e.getMessage().contains("The maximum API request")) {
                    Thread.sleep(10000);
                    continue;
                }
                throw e;
            }
        }

    }

    @SneakyThrows
    public List<Region> getRegions() {
        long l = System.currentTimeMillis();
        while (true){
            try {
                if (DateUtil.offsetMinute(new Date(), -10).getTime() > l) {
                    throw new GlobalException("获取可用区超时: ");
                }
                GetRegionsResult result = this.server.getRegions(new GetRegionsRequest().withIncludeAvailabilityZones(true));
                return result.getRegions();
            }catch (Exception e){
                Thread.sleep(5000);
                log.error(e.getMessage());
                if (e.getMessage().contains("The maximum API request")) {
                    Thread.sleep(10000);
                    continue;
                }
                throw e;
            }
        }

    }

}
