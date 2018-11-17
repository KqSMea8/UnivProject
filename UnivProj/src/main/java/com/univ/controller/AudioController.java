package com.univ.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.Constants;
import com.common.entry.ResponseDto;
import com.common.entry.SystemConfigEnum;
import com.common.security.SignTool;
import com.common.util.CommonUtil;
import com.common.util.FileUtil;
import com.common.util.SessionUtil;
import com.univ.domain.AudioInfo;
import com.univ.msg.QCloudConfigMsg;
import com.univ.msg.SystemConfigMsg;
import com.univ.server.QCloudServer;
import com.univ.server.WechatServer;
import com.univ.service.IAudioService;
import com.univ.service.IUserService;
import com.wechat.config.WechatConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Lickey
 * @Date: 2018/6/19
 * @Description:
 * @Since:
 */
@Controller
@RequestMapping("/audio")
public class AudioController {
    private static final Logger log = LoggerFactory.getLogger( AudioController.class );

    @Resource
    private IAudioService audioService;
    @Resource
    private WechatServer wechatServer;
    @Resource
    private IUserService userService;
    @Resource
    private SystemConfigMsg systemConfigMsg;
    @Resource
    private QCloudServer qCloudServer;
    @Resource
    private QCloudConfigMsg qCloudConfigMsg;

    @RequestMapping(value = "/getAudio/{qrcodeId}", method = RequestMethod.GET)
    public String getAudio(@PathVariable String qrcodeId,HttpServletRequest request) {
        ResponseDto responseDto = new ResponseDto();
        Map<String, String> audioMap = new HashMap<String, String>();
        try {
            String openId = (String) SessionUtil.getAttribute( Constants.API_OPEN_ID );
            if(openId == null) {
                openId = "obSha06tnWlbAeQK_kXvSrz2a0t8";
            }
            log.info( openId );
            if(!StringUtils.isEmpty( openId )) {
                SessionUtil.setAttribute( Constants.REQUEST_QR_CODE_ID, qrcodeId);
                AudioInfo info = audioService.getAudioInfo( qrcodeId );
                responseDto.setResult( Constants.RESULT_SUCCESS );
                if (null != info && !StringUtils.isEmpty( info.getAudioName() )) {
                    //已经有语音，提供和腾讯云相关信息下载
                    if(userService.getUserInfo( openId ) == null) {
                        userService.addUserInfo( openId );
                    }
//                    return getListenUrl( info.getAudioName() );
                    return "redirect:/app/listen.html";
                } else {
                    //需要录音，传递 appId，timestamp，noncestr，signature

                    String url = systemConfigMsg.getUrlPrefix();
                    audioMap.putAll( SignTool.getSignature( wechatServer.getTicket() ,url ,"1") );
                    audioMap.put( Constants.API_APP_ID, WechatConstants._API_APP_ID );
                    responseDto.setReturnObj( audioMap );
                    responseDto.setCode( Constants.CODE_NO_FILE );
                    if(userService.getUserInfo( openId ) == null) {
                        userService.addUserInfo( openId );
                    }
//                    return getRecordUrl(audioMap);
                    return "redirect:/app/record.html";
                }
            }else {
                return "redirect:/user/getCode/" + qrcodeId;
            }
        } catch (Exception e) {
            CommonUtil.setResponse(responseDto, Constants.RESULT_FAIL, SystemConfigEnum.S0001.toString(), systemConfigMsg.getSystemErr());
            log.error( "getAudio error", e );
            return "redirect:/user/getCode/" + qrcodeId;
        }
    }

    @RequestMapping(value = "/downLoadAudio", method = RequestMethod.POST)
    @ResponseBody
    public Object downLoadAudio(@RequestParam String mediaId) {
        ResponseDto responseDto = new ResponseDto();
        try {
            log.info( "start downLoadAudio mediaId: {}", mediaId);
            String openId = (String) SessionUtil.getAttribute( Constants.API_OPEN_ID );
            String qrcodeId = (String) SessionUtil.getAttribute( Constants.REQUEST_QR_CODE_ID );
            if(!StringUtils.isEmpty( openId ) && !StringUtils.isEmpty( qrcodeId )) {
                audioService.saveAudioInfo( qrcodeId, openId, mediaId);
                responseDto.setResult( Constants.RESULT_SUCCESS );
                log.info( "downLoadAudio success {}", mediaId);
            } else {
                CommonUtil.setResponse(responseDto, Constants.RESULT_FAIL, SystemConfigEnum.S0005.toString(), systemConfigMsg.getAudoiUploadErr());
                log.info( "downLoadAudio fail {}", mediaId);
            }
        } catch (Exception e) {
            CommonUtil.setResponse(responseDto, Constants.RESULT_FAIL, SystemConfigEnum.S0001.toString(), systemConfigMsg.getSystemErr());
            log.error( "downLoadAudio error", e );
        }
        return JSON.toJSON( responseDto );
    }

    /**
     * 录音页面url拼接
     *      签名信息
     * @param audioMap
     * @return
     */
    private String getRecordUrl(Map<String, String> audioMap) {
       StringBuilder url = new StringBuilder( "redirect:/app/record.html?" );
        url.append( Constants.API_APP_ID ).append( "=" ).append( audioMap.get( Constants.API_APP_ID ) );
        url.append( "&" ).append( Constants.SIGN_PARAM_NONCESTR ).append( "=" ).append( audioMap.get( Constants.SIGN_PARAM_NONCESTR ) );
        url.append( "&" ).append( Constants.SIGN_PARAM_TIMESTAMP ).append( "=" ).append( audioMap.get( Constants.SIGN_PARAM_TIMESTAMP ) );
        url.append( "&" ).append( Constants.SIGN_PARAM_SIGNATURE ).append( "=" ).append( audioMap.get( Constants.SIGN_PARAM_SIGNATURE ) );
       return url.toString();
    }

    /**
     * 收听页面url拼接
     *         腾讯云cos信息
     * @param qrcodeId
     * @return
     */
    private String getListenUrl(String qrcodeId) {
       StringBuilder url = new StringBuilder( "redirect:/app/listen.html?" );
        url.append( Constants.REQUEST_QR_CODE_ID ).append( "=" ).append( qrcodeId);
       return url.toString();
    }
    /***
     * 签名
     * @param type 1：录音；2：播放
     * @param request
     * @return
     */
    @RequestMapping(value = "/sign1/{type}", method = RequestMethod.GET)
    @ResponseBody
    public Object getSign1(@PathVariable String type, HttpServletRequest request) {
        Map<String, String> audioMap = new HashMap<String, String>();
        ResponseDto responseDto = new ResponseDto();
        try {
            String openId = (String) SessionUtil.getAttribute( Constants.API_OPEN_ID );
            log.info( openId );
            if(!StringUtils.isEmpty( openId )) {
                //需要录音，传递 appId，timestamp，noncestr，signature
                String url = systemConfigMsg.getUrlPrefix();
                audioMap.putAll( SignTool.getSignature( wechatServer.getTicket() ,url ,type) );
                audioMap.put( Constants.API_APP_ID, WechatConstants._API_APP_ID );
                audioMap.put( Constants.REQUEST_QR_CODE_ID, (String) SessionUtil.getAttribute( Constants.REQUEST_QR_CODE_ID ));
                responseDto.setReturnObj( audioMap );

                responseDto.setCode( Constants.CODE_NO_FILE );
                responseDto.setReturnObj( audioMap );
                responseDto.setCode( Constants.CODE_NO_FILE );
                responseDto.setReturnObj( audioMap );
                CommonUtil.setResponse(responseDto, Constants.RESULT_SUCCESS, null, null);
            }
        } catch (Exception e) {
            CommonUtil.setResponse(responseDto, Constants.RESULT_FAIL, SystemConfigEnum.S0001.toString(), systemConfigMsg.getSystemErr());
            log.error( "getAudio error", e );
        }
        return JSON.toJSON(audioMap);
    }
    @RequestMapping("/sign")
    @ResponseBody
    public Object getSign(HttpServletRequest request) {
        Map<String, String> audioMap = new HashMap<String, String>();
        ResponseDto responseDto = new ResponseDto();
        try {
            String openId = (String) SessionUtil.getAttribute( Constants.API_OPEN_ID );
            if(openId == null) {
                openId = "obSha01nViTrnHzCcCzy8t_tLKro";
            }
            log.info( openId );
            if(!StringUtils.isEmpty( openId )) {
                //需要录音，传递 appId，timestamp，noncestr，signature
                String url = systemConfigMsg.getUrlPrefix();
                audioMap.putAll( SignTool.getSignature( wechatServer.getTicket() ,url ,"1") );
                audioMap.put( Constants.API_APP_ID, WechatConstants._API_APP_ID );
            }
        } catch (Exception e) {
            CommonUtil.setResponse(responseDto, Constants.RESULT_FAIL, SystemConfigEnum.S0001.toString(), systemConfigMsg.getSystemErr());
            log.error( "getAudio error", e );
        }
        return JSON.toJSON(audioMap);
    }
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    @ResponseBody
    public Object upload() {
        ResponseDto responseDto = new ResponseDto();
        try {
            File file = new File( "E:\\01234.wma" );
            qCloudServer.upload( file );
            CommonUtil.setResponse(responseDto, Constants.RESULT_SUCCESS, null, null);
        } catch (Exception e) {
            CommonUtil.setResponse(responseDto, Constants.RESULT_FAIL, SystemConfigEnum.S0001.toString(), systemConfigMsg.getSystemErr());
            log.error( "getAudio error", e );
        }
        return JSON.toJSON(responseDto);
    }
    @RequestMapping(value = "/getQCloudInfo", method = RequestMethod.POST)
    @ResponseBody
    public Object getQCloudInfo() {
        ResponseDto responseDto = new ResponseDto();
        Map<String, String > map = new HashMap<String, String>(  );
        try {
            String qrcodeId =  (String )SessionUtil.getAttribute( Constants.REQUEST_QR_CODE_ID );
            AudioInfo info = audioService.getAudioInfo( qrcodeId );
            responseDto.setResult( Constants.RESULT_SUCCESS );
            if (null != info && !StringUtils.isEmpty( info.getAudioName() )) {
                map.put( Constants.API_APP_ID, qCloudConfigMsg.getAppid() );
                map.put( Constants.CLOUD_SECRET_ID, qCloudConfigMsg.getSecretId() );
                map.put( Constants.CLOUD_SECRET_KEY, qCloudConfigMsg.getSecretKey() );
                map.put( Constants.CLOUD_BUCKET, qCloudConfigMsg.getBucket() );
                map.put( Constants.CLOUD_REGION, qCloudConfigMsg.getRegion() );
                map.put( Constants.CLOUD_BUCKET_FOLDER, qCloudConfigMsg.getBucketFolder() );
                map.put(  Constants.CLOUD_KEY , info.getAudioName() );
                responseDto.setReturnObj( map );
            }
            CommonUtil.setResponse(responseDto, Constants.RESULT_SUCCESS, null, null);
        } catch (Exception e) {
            CommonUtil.setResponse(responseDto, Constants.RESULT_FAIL, SystemConfigEnum.S0001.toString(), systemConfigMsg.getSystemErr());
            log.error( "getAudio error", e );
        }
        return JSON.toJSON(responseDto);
    }

    /**
     * 获取播放页面参数
     * @return
     */
    @RequestMapping(value = "/getListen", method = RequestMethod.GET)
    @ResponseBody
    public Object getListen() {
        ResponseDto responseDto = new ResponseDto();
        Map<String,Object> returnMap = new HashMap<String, Object>();
        try {
            String qrcodeId =  (String )SessionUtil.getAttribute( Constants.REQUEST_QR_CODE_ID );
    //            String qrcodeId =  "0120024";
            AudioInfo info = audioService.getAudioInfo( qrcodeId );
            if (null != info && !StringUtils.isEmpty( info.getAudioName() )) {
                File audioFile = audioService.getListen( FileUtil.getParentpath() + info.getAudioName() );
                String bsae64Str = FileUtil.encodeBase64File( audioFile );
                audioFile.delete();
                responseDto.setResult( Constants.RESULT_SUCCESS );
                returnMap.put(Constants.REQUEST_QR_CODE_ID,qrcodeId);
                returnMap.put("audio",bsae64Str);
                responseDto.setReturnObj( returnMap );
            }
        } catch (Exception e) {
            CommonUtil.setResponse(responseDto, Constants.RESULT_FAIL, SystemConfigEnum.S0001.toString(), systemConfigMsg.getSystemErr());
            log.error( "getAudio error", e );
        }
        return JSON.toJSON(responseDto);
    }
    @RequestMapping(value = "/getListen2", method = RequestMethod.POST)
    @ResponseBody
    public Object getListen2() {
        ResponseDto responseDto = new ResponseDto();
        try {
//            String qrcodeId =  (String )SessionUtil.getAttribute( Constants.REQUEST_QR_CODE_ID );
            String qrcodeId =  "0120024";
            AudioInfo info = audioService.getAudioInfo( qrcodeId );
            if (null != info && !StringUtils.isEmpty( info.getAudioName() )) {
                String serverId = audioService.getListen2( qCloudConfigMsg.getBucketFolder() + info.getAudioName() );
                responseDto.setResult( Constants.RESULT_SUCCESS );
                responseDto.setReturnObj( serverId );
            }
            CommonUtil.setResponse(responseDto, Constants.RESULT_SUCCESS, null, null);
        } catch (Exception e) {
            CommonUtil.setResponse(responseDto, Constants.RESULT_FAIL, SystemConfigEnum.S0001.toString(), systemConfigMsg.getSystemErr());
            log.error( "getAudio error", e );
        }
        return JSON.toJSON(responseDto);
    }
}
