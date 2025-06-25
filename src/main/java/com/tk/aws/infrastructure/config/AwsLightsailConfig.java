package com.tk.aws.infrastructure.config;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class AwsLightsailConfig {

    private String accessKey;

    private String secretKey;

    public AwsLightsailConfig(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }



}
