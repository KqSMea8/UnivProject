package com.univ.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.Constants;
import com.common.entry.ResponseDto;
import com.common.entry.SystemConfigEnum;
import com.common.util.CommonUtil;
import com.common.util.FileUtil;
import com.univ.dao.AudioInfoMapper;
import com.univ.domain.AudioInfo;
import com.univ.msg.QCloudConfigMsg;
import com.univ.msg.SystemConfigMsg;
import com.univ.server.QCloudServer;
import com.univ.server.WechatServer;
import com.univ.service.IAudioService;
import com.univ.server.WechatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

/**
 * @Auther: EMMA
 * @Date: 2018/6/19
 * @Description:
 * @Since:
 */
@Service("audioService")
public class AudioService implements IAudioService{
    private static final Logger log = LoggerFactory.getLogger( AudioService.class );
    @Resource
    private WechatServer wechatServer;
    @Autowired
    private SystemConfigMsg systemConfigMsg;
    @Autowired
    private AudioInfoMapper audioInfoMapper;
    @Autowired
    private QCloudServer qCloudServer;
    @Resource
    private QCloudConfigMsg qCloudConfigMsg;

    @Override
    public ResponseDto saveAudioInfo(String qrcodeId, String openId, String mediaId) {
        ResponseDto responseDto = new ResponseDto();
        try {
            log.info( "start saveAudioInfo by param {}",qrcodeId,openId, mediaId );
            AudioInfo audioInfo = new AudioInfo(  );

            String targetPath = qrcodeId + Constants.FILE_SUFIIX_MP3;
            String sourcePath = qrcodeId + Constants.FILE_SUFIIX_AMR;
            audioInfo.setQrcodeId( qrcodeId );
            audioInfo.setAudioName( targetPath );
            audioInfo.setOpenId( openId );
            audioInfo.setCreateTm( new Date() );
            File audioFile = wechatServer.downloadMedia( FileUtil.getParentpath() + sourcePath, mediaId );
            File targetFile = FileUtil.changeCode( audioFile, FileUtil.getParentpath() + targetPath,FileUtil.getCmdpath() );
            //保存到腾讯云cos
            qCloudServer.upload( targetFile );

            //删除本地文件
            log.info( "delete local file {}", audioFile.delete() + ","+targetFile.delete() );
//            audioFile.delete();
//            targetFile.delete();
            log.info( "insert audio table " );
            audioInfoMapper.insert( audioInfo );
        } catch (Exception e) {
            CommonUtil.setResponse( responseDto, Constants.RESULT_FAIL, SystemConfigEnum.S0005.toString(), systemConfigMsg.getAudoiUploadErr() );
            log.warn( "saveAudioInfo error",e );
        }


        return responseDto;
    }

    @Override
    public AudioInfo getAudioInfo(String qrcodeId) {

        return audioInfoMapper.selectByQrcodeId(qrcodeId);
    }

    @Override
    public int addAudioInfo(AudioInfo audioInfo) {
        return audioInfoMapper.insert(audioInfo);
    }

    @Override
    public File getListen(String audioPath) {
        File audioFile = new File (audioPath);
        try {
            log.info( "start getListen by param {}",audioPath);
            AudioInfo audioInfo = new AudioInfo(  );
            qCloudServer.download( audioFile );
        } catch (Exception e) {
            log.warn( "getListen error",e );
        }
        return audioFile;
    }
    @Override
    public String getListen2(String audioPath) {
       String serverId = null;
        try {
            log.info( "start getListen2 by param {}",audioPath);
            AudioInfo audioInfo = new AudioInfo(  );
            File audioFile = new File (audioPath);
            qCloudServer.download( audioFile );
            JSONObject object = wechatServer.uploadMedia( audioFile );
            if(object.containsKey( "media_id" )) {
                log.info( "wechatServer upload success {}", object.get( "created_at" )  );
                serverId =object.getString( "object" );
            }
            //删除本地文件
            log.info( "delete local file {}", audioFile.delete()  );
        } catch (Exception e) {
            log.warn( "getListen2 error",e );
        }
        return serverId;
    }
}
