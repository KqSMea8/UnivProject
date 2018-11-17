package com.univ.service;

import com.univ.domain.AudioInfo;
import com.univ.domain.UserInfo;

/**
 * @Auther: EMMA
 * @Date: 2018/6/24
 * @Description:
 * @Since:
 */
public interface IUserService {
    /**
     * 存储语音文件
     * */
    int addUserInfo(UserInfo audioInfo);
    /**
     * 存储语音文件
     * */
    int addUserInfo(String openId);
    UserInfo getUserInfo(String openId);
}
