package com.univ.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.univ.msg.QCloudConfigMsg;
import com.wechat.config.WechatConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;

/**
 * @author: EMMA
 * @date: 2018/7/1
 * @description:
 * @since:
 */
@Service("qCloudServer")
public class QCloudServer {
    private static final Logger log = LoggerFactory.getLogger(QCloudServer.class);
    @Resource
    private QCloudConfigMsg qCloudConfigMsg;
    /**
     * bean初始化之前调用的方法，等同于xml配置的init-method
     */
    public COSClient getClient() {
        COSClient cosClient = null;
        try{
            log.info( "++++++++++++++++init QCloudServer start++++++++++++++++++" );
            // 1 初始化用户身份信息(secretId, secretKey)
            COSCredentials cred = new BasicCOSCredentials(qCloudConfigMsg.getSecretId() ,qCloudConfigMsg.getSecretKey());
            // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
            ClientConfig clientConfig = new ClientConfig(new Region(qCloudConfigMsg.getRegion()));
            // 3 生成cos客户端
            cosClient = new COSClient(cred, clientConfig);
            log.info("init QCloudServer success");
        } catch (Exception e) {
            log.warn( "init QCloudServer error",e );
        }
        return cosClient;
    }
    public void upload(File audioFile){
        COSClient cosClient = null;
        try{
            log.info( "upload cloud file start " );
            cosClient = getClient();
            // 指定要上传到 COS 上对象键
            String key = qCloudConfigMsg.getBucketFolder()+audioFile.getName();
            PutObjectRequest putObjectRequest = new PutObjectRequest(qCloudConfigMsg.getBucket(), key, audioFile);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            log.info( "upload cloud file end" );
        } catch (Exception e) {
            log.warn( "upload error",e );
        } finally {
            try {
                if (cosClient != null) {
                    cosClient.shutdown();
                }
            } catch (Exception e) {
                log.warn( "cosClient error",e );
            }
        }
    }
    public void download(File audioFile){
        COSClient cosClient = null;
        try{
            log.info( "download cloud file start " );
            cosClient = getClient();
            // 指定要上传到 COS 上对象键
            String key = qCloudConfigMsg.getBucketFolder()+audioFile.getName();
            // 指定要下载到的本地路径
            // 指定要下载的文件所在的 bucket 和对象键
            GetObjectRequest getObjectRequest = new GetObjectRequest(qCloudConfigMsg.getBucket(), key);
            ObjectMetadata downObjectMeta = cosClient.getObject(getObjectRequest, audioFile);
             log.info( "download cloud file end" );
        } catch (Exception e) {
            log.warn( "download error",e );
        } finally {
            try {
                if (cosClient != null) {
                    cosClient.shutdown();
                }
            } catch (Exception e) {
                log.warn( "cosClient error",e );
            }
        }
    }

}
