package com.wechat.config;

/**
 * Description: WechatConstants.<br>
 * Date: 2017/11/14 08:49<br>
 * Author: ysj
 */
public class WechatConstants
{
    /**authorization_code*/
    public static final String _AUTHORIZATION_CODE = "authorization_code";

    /**微信API - 获取code */
    public static final String _API_CONNECT_OAUTH2_ACCESS_TOKEN = "https://open.weixin.qq.com/connect/oauth2/authorize";
    /**微信API - 获取用户信息access_token  url*/
    public static final String _API_SNS_OAUTH2_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
    /**微信获取公众号 access_token API*/
    public static final String _API_GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token";
    /**微信用户基本信息 API*/
    public static final String _API_GET_USER_BASEINFO = "https://api.weixin.qq.com/sns/userinfo";
    /**微信API - 素材上传(POST)*/
    public static final String _API_UPLOAD_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=%s";
     /**微信API - 素材下载:不支持视频文件的下载(GET)*/
    public static final String _API_DOWNLOAD_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s";
     /**微信API - 获取ticket*/
    public static final String _API_GET_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";
    /**微信API参数 - appid*/
//        public static final String _API_APP_ID = "wx1a02ac5f370f3ee0"; //xcdh
//    public static final String _API_APP_ID = "wx39644ceaecde2831";
    public static final String _API_APP_ID = "wxf100671aab578cb7";


    /**微信API参数 - secret*/
//    public static final String _API_SECRET = "c2196218e3dcfce7269014bcdd2b09a9";
//    public static final String _API_SECRET = "478ee2bde8b76bf04809a06551def22b";
    public static final String _API_SECRET = "8e71777fb0a3e6e06fbf87a951b2b1cc";
    /**微信API参数 - scope：snsapi_base*/
    public static final String _API_SCOPE_TYPE_BASE = "snsapi_base";
    /**微信API参数 - scope：snsapi_userinfo*/
    public static final String _API_SCOPE_TYPE_USERINFO = "snsapi_userinfo";
    /**微信API参数 - grant_type*/
    public static final String _API_GRANT_TYPE_TOKEN = "client_credential";
    /**微信API 返回参数 - ticket*/
    public static final String API_RETURN_TICKET = "ticket";

    public static final String API_RETURN_ACCESS_TOKEN = "access_token";
    /**微信API 返回参数 - code*/
    public static final String API_RETURN_CODE = "access_token";
    /**微信API参数 - redirect_uri*/
    public static final String _API_REDIRECT_URL_UNIV = "/univ/user/checkFollow";
    /**微信API返回参数 - errcode*/
    public static final String API_RETURN_ERRCODE = "errcode";
    /**微信API返回参数 - subscribe*/
    public static final String API_RETURN_SUBSCRIBE = "subscribe";
    public static final String API_FOLLOW_URL = "https://mp.weixin.qq.com/mp/profile_ext?action=home&__biz=%s==#wechat_redirect";
}
