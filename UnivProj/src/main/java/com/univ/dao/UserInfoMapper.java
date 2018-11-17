package com.univ.dao;

import com.univ.domain.UserInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoMapper {
    int insert(UserInfo record);

    int insertSelective(UserInfo record);
    UserInfo getUser(String openId);
}