package com.univ.msg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: EMMA
 * @date: 2018/6/27
 * @description:
 * @since:
 */
@Component("systemConfigMsg")
public class SystemConfigMsg {
    @Value("#{configProperties['system.filePath']}")
    private String filePath;
    @Value("#{configProperties['system.systemErr']}")
    private String systemErr;
    @Value("#{configProperties['system.userAuthErr']}")
    private String userAuthErr;
    @Value("#{configProperties['system.noQrcodeMsg']}")
    private String noQrcodeMsg;
    @Value("#{configProperties['system.getAudioFileErr']}")
    private String getAudioFileErr;
    @Value("#{configProperties['system.autioUploadErr']}")
    private String audoiUploadErr;
    @Value("#{configProperties['system.qrcodeIsUploaded']}")
    private String qrcodeIsUploaded;
    @Value("#{configProperties['system.wechatBiz']}")
    private String wechatBiz;
    @Value("#{configProperties['system.urlPrefix']}")
    private String urlPrefix;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSystemErr() {
        return systemErr;
    }

    public void setSystemErr(String systemErr) {
        this.systemErr = systemErr;
    }

    public String getUserAuthErr() {
        return userAuthErr;
    }

    public void setUserAuthErr(String userAuthErr) {
        this.userAuthErr = userAuthErr;
    }

    public String getNoQrcodeMsg() {
        return noQrcodeMsg;
    }

    public void setNoQrcodeMsg(String noQrcodeMsg) {
        this.noQrcodeMsg = noQrcodeMsg;
    }

    public String getGetAudioFileErr() {
        return getAudioFileErr;
    }

    public void setGetAudioFileErr(String getAudioFileErr) {
        this.getAudioFileErr = getAudioFileErr;
    }

    public String getAudoiUploadErr() {
        return audoiUploadErr;
    }

    public void setAudoiUploadErr(String audoiUploadErr) {
        this.audoiUploadErr = audoiUploadErr;
    }

    public String getQrcodeIsUploaded() {
        return qrcodeIsUploaded;
    }

    public void setQrcodeIsUploaded(String qrcodeIsUploaded) {
        this.qrcodeIsUploaded = qrcodeIsUploaded;
    }

    public String getWechatBiz() {
        return wechatBiz;
    }

    public void setWechatBiz(String wechatBiz) {
        this.wechatBiz = wechatBiz;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }
}
