package com.demo.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "config.alipay")
public class AliPayConfig {

    private String appId;

    private String privateKey;

    private String alipayPublicKey;

}