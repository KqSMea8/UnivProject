package com.univ.service.impl;

import com.univ.dao.UserInfoMapper;
import com.univ.domain.UserInfo;
import com.univ.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Auther: EMMA
 * @Date: 2018/6/24
 * @Description:
 * @Since:
 */
@Service("userService")
public class UserService implements IUserService {
    private static final Logger log = LoggerFactory.getLogger( UserService.class );
    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public int addUserInfo(UserInfo userInfo) {
        return userInfoMapper.insert( userInfo );
    }

    @Override
    public int addUserInfo(String openId) {
        UserInfo userInfo = new UserInfo(  );
        userInfo.setOpenId( openId );
        userInfo.setCreateTm( new Date(  ) );
        return userInfoMapper.insert( userInfo );
    }

    @Override
    public UserInfo getUserInfo(String openId) {
        return userInfoMapper.getUser( openId );
    }
}
