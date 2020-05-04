package com.itodatamp.mpapigateway.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class PropertiesBean {

    @Value("${services.kafkaRestProxy.url}")
    private String kafkaRestProxyURL;

}
