package com.univ.msg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: EMMA
 * @date: 2018/6/27
 * @description:
 * @since:
 */
@Component("qCloudConfigMsg")
public class QCloudConfigMsg {
    @Value("#{configProperties['qcloud.appid']}")
    private String appid;
    @Value("#{configProperties['qcloud.secretId']}")
    private String secretId;
    @Value("#{configProperties['qcloud.secretKey']}")
    private String secretKey;
    @Value("#{configProperties['qcloud.bucket']}")
    private String bucket;
    @Value("#{configProperties['qcloud.region']}")
    private String region;
    @Value("#{configProperties['qcloud.bucketFolder']}")
    private String bucketFolder;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucketFolder() {
        return bucketFolder;
    }

    public void setBucketFolder(String bucketFolder) {
        this.bucketFolder = bucketFolder;
    }
}
