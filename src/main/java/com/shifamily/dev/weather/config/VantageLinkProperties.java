package com.shifamily.dev.weather.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "station.davis")
@Data
@EnableConfigurationProperties
public class VantageLinkProperties {
    private String mode;
    private String ip;
    private int port;

    private String device;
    private String speed;

}
