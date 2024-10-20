package com.demo.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "config.wechat")
public class WechatPayConfig {

    private String certPath;

    private String apiV3Key;

    private String merchantSerialNumber;

    private String merchantId;

    private String appId;

}