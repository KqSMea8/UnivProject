package com.univ.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wechat.config.WechatConstants;
import com.univ.server.WechatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: EMMA
 * @Date: 2018/6/23
 * @Description: 定时任务类
 * @Since:
 */
@Component
public class ScheduledTask {
    private final Logger log = LoggerFactory.getLogger(ScheduledTask.class);
    @Resource
    private WechatServer wechatServer;
    /**
     * 定时任务，每天晚上1点删除数据表t_tempClob中的所有记录
     */
    @Scheduled(cron= "0 0/10 * * * ?")
    public void deleteAllTempClob(){
        log.info( "start to refresh token!");
        try {
            String response = wechatServer.apiGetAccessToken();
            JSONObject token = (JSONObject) JSON.parse( response);
            if (token.containsKey( WechatConstants.API_RETURN_ACCESS_TOKEN )) {
                wechatServer.setAccessToken( (String) token.get( WechatConstants.API_RETURN_ACCESS_TOKEN ) );
                String ticketResp = wechatServer.apiGetTicket();
                JSONObject ticketObj = (JSONObject) JSON.parse( ticketResp);
                if (ticketObj.containsKey( WechatConstants.API_RETURN_TICKET )) {
                    wechatServer.setTicket( (String) ticketObj.get( WechatConstants.API_RETURN_TICKET ) );
                }
            }
        } catch (Exception e) {
            log.warn( "refresh token error! ",e);
        }
    }
}
