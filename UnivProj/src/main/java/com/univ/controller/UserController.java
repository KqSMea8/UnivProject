package com.univ.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.Constants;
import com.common.entry.ResponseDto;
import com.common.entry.SystemConfigEnum;
import com.common.util.CommonUtil;
import com.common.util.SessionUtil;
import com.univ.msg.SystemConfigMsg;
import com.univ.service.IUserService;
import com.wechat.config.WechatConstants;
import com.univ.server.WechatServer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: EMMA
 * @date: 2018/6/24
 * @Description:
 * @Since:
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Resource
    private IUserService userService;
    @Resource
    private WechatServer wechatServer;
    @Autowired
    private SystemConfigMsg systemConfigMsg;
    /**
     * 获取openId
     * @return
     */
    @RequestMapping(value = "/getCode/{qrcodeId}",method = RequestMethod.GET)
    public void  getCode(@PathVariable String qrcodeId, HttpServletRequest request, HttpServletResponse response) {
        try {
            SessionUtil.setAttribute( Constants.REQUEST_QR_CODE, qrcodeId);
            String url =wechatServer.genGetOauth2AuthorizationUrl(systemConfigMsg.getUrlPrefix());
            response.sendRedirect(url);
        } catch (IOException e) {
            log.warn( "getCode error " , e);
        }
    }
    /**
     * 获取openId
     * @return
     */
    @RequestMapping(value = "/auth",method = RequestMethod.GET)
    @ResponseBody
    public Object userAuth(ModelMap modelMap, HttpServletRequest req, HttpServletResponse resp) {
        ResponseDto responseDto = new ResponseDto();
        String code =req.getParameter("code");
        try{
            log.info( "get request param code {} ",code );
            responseDto.setResult( Constants.RESULT_SUCCESS);
            String result = wechatServer.apiGetOpenId(code);
            JSONObject resObj = JSON.parseObject( result );
            if (! resObj.containsKey( WechatConstants.API_RETURN_ERRCODE )) {
                SessionUtil.setAttribute( Constants.API_OPEN_ID, resObj.getString( Constants.API_OPEN_ID ));
                SessionUtil.setAttribute( Constants.API_ACCESS_TOKEN, resObj.getString( Constants.API_ACCESS_TOKEN ));
                responseDto.setReturnObj( resObj.getString( Constants.API_OPEN_ID ) );
            }

        } catch (Exception e) {
            CommonUtil.setResponse(responseDto, Constants.RESULT_FAIL, SystemConfigEnum.S0001.toString(), systemConfigMsg.getSystemErr());
            log.error("getAudio error", e);
        }
        return JSON.toJSON(responseDto);
    }

    /**
     * 判断用户是否关注公众号
     * @return
     */
    @RequestMapping(value = "/checkFollow",method = RequestMethod.GET)
    public String userCheckFollow(ModelMap modelMap, HttpServletRequest req, HttpServletResponse resp) {
        ResponseDto responseDto = new ResponseDto();
        //获取code
        String code =req.getParameter("code");
        try{
            responseDto.setResult( Constants.RESULT_SUCCESS);
            //根据code获取openId
            String result = wechatServer.apiGetOpenId(code);
            JSONObject resObj = JSON.parseObject( result );
            if (! resObj.containsKey( WechatConstants.API_RETURN_ERRCODE )) {
                //获取access_token 和 openId
                String accessToken = resObj.getString( Constants.API_ACCESS_TOKEN );
                String openId = resObj.getString( Constants.API_OPEN_ID );
                SessionUtil.setAttribute( Constants.API_OPEN_ID, openId);
                SessionUtil.setAttribute( Constants.API_ACCESS_TOKEN, accessToken);
                responseDto.setReturnObj( resObj.getString( Constants.API_OPEN_ID ) );
                //判断用户是否关注公众号
                boolean isFollow = wechatServer.checkUserFollow(accessToken, openId );
//                if (isFollow) {
                    String qrcode = (String) SessionUtil.getAttribute( Constants.REQUEST_QR_CODE);
                    if(StringUtils.isNotEmpty(qrcode)) {
                        return "forward:/audio/getAudio/" + qrcode;
                    } else {
                        return "forward:/user/error/" + qrcode;
                    }
//                } else {
//                        return "redirect:" + String.format(WechatConstants.API_FOLLOW_URL, systemConfigMsg.getWechatBiz());
//                }
            } else {

            }
        } catch (Exception e) {
            responseDto.setResult(Constants.RESULT_FAIL);
            responseDto.setMsg("系统异常");
            log.error("getAudio error", e);
        }
        return JSON.toJSONString(responseDto);
    }
    /**
     * 获取openId
     * @return
     */
    @RequestMapping(value = "/error/{errorCode}",method = RequestMethod.GET)
    @ResponseBody
    public Object error(@PathVariable String errorCode) {
        ResponseDto responseDto = new ResponseDto();
        CommonUtil.setResponse(responseDto, Constants.RESULT_FAIL, SystemConfigEnum.S0001.toString(), systemConfigMsg.getSystemErr());
//        String code =req.getParameter("code");
//        try{
//            log.info( "get request param code {} ",code );
//            responseDto.setResult( Constants.RESULT_SUCCESS);
//            String result = wechatServer.apiGetOpenId(code);
//            JSONObject resObj = JSON.parseObject( result );
//            if (! resObj.containsKey( WechatConstants.API_RETURN_ERRCODE )) {
//                SessionUtil.setAttribute( Constants.API_OPEN_ID, resObj.getString( Constants.API_OPEN_ID ));
//                SessionUtil.setAttribute( Constants.API_ACCESS_TOKEN, resObj.getString( Constants.API_ACCESS_TOKEN ));
//                responseDto.setReturnObj( resObj.getString( Constants.API_OPEN_ID ) );
//            }
//
//        } catch (Exception e) {
//            responseDto.setResult(Constants.RESULT_FAIL);
//            responseDto.setMsg("系统异常");
//            log.error("getAudio error", e);
//        }
        return JSON.toJSON(responseDto);
    }
}
