package com.univ.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.util.FileUtil;
import com.common.util.HttpUtil;
import com.wechat.config.WechatConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.net.www.protocol.http.HttpURLConnection;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: WechatServ.<br>
 * date: 2018/06/24 18:53<br>
 * author: EMMA
 */
@Service("wechatServer")
public class WechatServer
{
    private static final Logger log = LoggerFactory.getLogger(WechatServer.class);
    private String accessToken;
    private String ticket;
    private long expiresIn;
    /**
     * 获取code的请求url拼接
     * */
    public String genGetOauth2AuthorizationUrl(String urlPrefix) throws UnsupportedEncodingException {
        return WechatConstants._API_CONNECT_OAUTH2_ACCESS_TOKEN + "?appid=" + WechatConstants._API_APP_ID + "&redirect_uri=" + URLEncoder.encode( urlPrefix + WechatConstants._API_REDIRECT_URL_UNIV, "UTF-8") + "&response_type=code&scope=" + WechatConstants._API_SCOPE_TYPE_USERINFO + "#wechat_redirect";
    }
    //    private WechatService(){
//
//    }
//    public static WechatService getInstance() {
//        if (wechatServ == null) {
//            synchronized(WechatService.class) {
//                wechatService = new WechatService();
//            }
//        }
//        return wechatServ;
//    }

    /**
     * bean初始化之前调用的方法，等同于xml配置的init-method
     */
    @PostConstruct
    public void init() {
        //初始化token
        String tokenResp = apiGetAccessToken();
        JSONObject token = (JSONObject) JSON.parse( tokenResp);
        if (token.containsKey( WechatConstants.API_RETURN_ACCESS_TOKEN )) {
            this.accessToken = (String) token.get( WechatConstants.API_RETURN_ACCESS_TOKEN );
            //初始化ticket
            String ticketResp = apiGetTicket();
            JSONObject ticketObj = (JSONObject) JSON.parse( ticketResp);
            if (ticketObj.containsKey( WechatConstants.API_RETURN_TICKET )) {
                this.ticket = (String) ticketObj.get( WechatConstants.API_RETURN_TICKET );
            }
        }
    }
    /**
     * 获取用户openId的请求url拼接
     * @param code
     * */
    public String apiGetOpenIdUrl(String code) {
        return String.format(WechatConstants._API_SNS_OAUTH2_ACCESS_TOKEN + "?appid=%s&secret=%s&code=%s&grant_type=%s", WechatConstants._API_APP_ID, WechatConstants._API_SECRET, code, WechatConstants._AUTHORIZATION_CODE);

    }
    /**
     * 获取OpenId接口
     * */
    public String apiGetOpenId(String code) {
        String result = null;
        try {
            String apiGetOpenIdUrlUrl = apiGetOpenIdUrl(code);
            log.info("openId Request: {}", apiGetOpenIdUrlUrl);
            result = HttpUtil.INSTANCE.doGet(apiGetOpenIdUrlUrl);
            log.info("openId Response: {}", result);
        } catch (Exception e) {
            log.warn( "apiWechatOpenId error", e );
        }
        return result;
    }
    /**
     * 获取access_token接口
     * */
    public String apiGetAccessToken() {

        String url = String.format( WechatConstants._API_GET_ACCESS_TOKEN + "?appid=%s&secret=%s&grant_type=%s", WechatConstants._API_APP_ID, WechatConstants._API_SECRET, WechatConstants._API_GRANT_TYPE_TOKEN);
        log.info("access_token Request: {}", url);
        String result = null;
        try {
            result = HttpUtil.INSTANCE.doGet(url);
            log.info("access_token Response: {}", result);
        } catch (IOException e) {
            log.warn( "apiWechatAccessToken error", e );
        }
        return result;
    }

    /**
     *
     * @param accessToken
     * @param openId
     * @return
     */
    public boolean checkUserFollow(String accessToken,String openId) {
        boolean isFlollwed = false;
        String url = String.format( WechatConstants._API_GET_USER_BASEINFO + "?access_token=%s&openid=%s&&lang=zh_CN", accessToken, openId);
        log.info("get user info Request: {}", url);
        String result = null;
        try {
            result = HttpUtil.INSTANCE.doGet(url);
            log.info("get user info Response: {}", result);
            JSONObject resObj = JSON.parseObject( result );
            if (!resObj.containsKey( WechatConstants.API_RETURN_ERRCODE ) ) {
                isFlollwed = StringUtils.isNotEmpty( resObj.getString( WechatConstants.API_RETURN_SUBSCRIBE )) && "1".equals( WechatConstants.API_RETURN_SUBSCRIBE );
            } else {
                log.warn( "get user info error, errcode:" + WechatConstants.API_RETURN_ERRCODE );
            }
        } catch (IOException e) {
            log.warn( "checkUserFollow error", e );
        }
        return isFlollwed;
    }
    /**
     * 获取ticket接口
     * */
    public String apiGetTicket() {

        String url = String.format( WechatConstants._API_GET_TICKET , this.accessToken);
        log.info("ticket Request: {}", url);
        String result = null;
        try {
            result = HttpUtil.INSTANCE.doGet(url);
            log.info("ticket Response: {}", result);
        } catch (IOException e) {
            log.warn( "apiWechatTicket error", e );
        }
        return result;
    }
    /**
     * 微信服务器素材上传
     * @param file  表单名称media
     */
    public JSONObject uploadMedia(File file) {
        if(!file.exists()){
            log.info("上传文件不存在,请检查!");
            return null;
        }
        JSONObject jsonObject = null;
        try {
            String url = getUploadUrl(  );
            Map<String, File> param = new HashMap<String, File>(  );
            param.put( "media",file );
            String result = HttpUtil.INSTANCE.doPost(url, param);
            jsonObject = JSON.parseObject( result );
        } catch (Exception e) {
            String error = String.format("上传媒体文件失败：%s", e);
            System.out.println(error);
        }
        return jsonObject;
    }

    /**
     * 多媒体下载接口
     *  不支持视频文件的下载
     * @param fileName  素材存储文件路径
     * @param mediaId   素材ID（对应上传后获取到的ID）
     * @return 素材文件
     */
    public File downloadMedia(String fileName, String mediaId) {
        String url = getDownloadUrl( mediaId);
        return httpRequestToFile(fileName, url);
    }
    private String getDownloadUrl(String mediaId) {
        return String.format(WechatConstants._API_DOWNLOAD_MEDIA, this.accessToken, mediaId);
    }
    private String getUploadUrl() {
        //type  type只支持四种类型素材(video/image/voice/thumb)
        return String.format(WechatConstants._API_UPLOAD_MEDIA, this.accessToken, "voice");
    }
    /**
     * 以http方式发送请求,并将请求响应内容输出到文件
     * @param path    请求路径
     * @return 返回响应的存储到文件
     */
    private File httpRequestToFile(String fileName,String path) {
        log.info( "down load Request:{}",path );
        if(fileName==null||path==null){
            return null;
        }

        File file = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");

            inputStream = conn.getInputStream();
            file = FileUtil.createFile( inputStream, fileName );
            log.info( "down load wechat file success" );
        } catch (Exception e) {
            log.warn("httpRequestToFile error", e);
        } finally {
            if(conn!=null){
                conn.disconnect();
            }
            /*
             * 必须关闭文件流
             * 否则JDK运行时，文件被占用其他进程无法访问
             */
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                log.warn("close stream error",e);
            }
        }
        return file;
    }

    public boolean checkSignature(String token, String timestamp, String nonce, String signature)
    {
        try
        {
            return this.genSignature(new String[]{token, timestamp, nonce}).equals(signature);
        }
        catch (Exception var5)
        {
            log.error("Checking signature failed, and the reason is :" + var5.getMessage());
            return false;
        }
    }

    public String genSignature(String... arr)
    {
        if (StringUtils.isAnyEmpty(arr))
        {
            throw new IllegalArgumentException("非法请求参数，有部分参数为空 : " + Arrays.toString(arr));
        }
        else
        {
            Arrays.sort(arr);
            StringBuilder sb = new StringBuilder();
            for (String a : arr)
            {
                sb.append(a);
            }
            return DigestUtils.sha1Hex(sb.toString());
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
