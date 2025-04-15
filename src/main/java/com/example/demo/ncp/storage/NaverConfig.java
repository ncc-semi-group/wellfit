package com.example.demo.ncp.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ncp")
@Data
public class NaverConfig {

    @Bean
    public NcpObjectStorageService ncpObjectStorageService() {
        return new NcpObjectStorageService(this);
    }

    private String accessKey;
    private String secretKey;
    private String regionName;
    private String endPoint;
    private String bucketName;

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getEndPoint() {
        return endPoint;
    }
}
