package com.common.security;

import com.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @Auther: EMMA
 * @Date: 2018/6/24
 * @Description:
 * @Since:
 */
public class SignTool {
    public static final Logger log = LoggerFactory.getLogger(SignTool.class);
    private static final String LISTEN_PAGE = "/univ/app/listen.html";
    private static final String RECORE_PAGE = "/univ/app/record.html";
    /**
     * 获取签名
     * @param ticket
     * @return
     */
    public static Map<String, String> getSignature(String ticket, String url, String type) {
        Map<String, String> signMap = new HashMap<String, String>(  );
        String signature = null;
        //随机字符串
        String noncestr = createNoncestr();
        //时间戳
        String timestamp = createTimestamp();
        //获取url
        if("1".equals(type)) {
            //录音
            url += RECORE_PAGE;
        } else if ("2".equals(type)) {
            //播放
            url += LISTEN_PAGE;
        }

        //5、将参数排序并拼接字符串
        String str1 = "jsapi_ticket="+ticket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url;

        log.info(str1);

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(str1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
            signMap.put( Constants.SIGN_PARAM_NONCESTR, noncestr );
            signMap.put( Constants.SIGN_PARAM_TIMESTAMP, timestamp );
            signMap.put( Constants.SIGN_PARAM_URL, url );
            signMap.put( Constants.SIGN_PARAM_SIGNATURE, signature );
        }  catch (NoSuchAlgorithmException e) {
            log.warn( "Signature error NoSuchAlgorithmException",e );
        } catch (UnsupportedEncodingException e) {
            log.warn( "Signature error UnsupportedEncodingException",e );
        }
        return signMap;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
    /**
     * 生成
     * @return
     */
    private static String createNoncestr() {
        return UUID.randomUUID().toString();
    }

    private static String createTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
