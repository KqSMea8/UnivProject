package com.univ.dao;

import com.univ.domain.AudioInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AudioInfo record);

    int insertSelective(AudioInfo record);

    AudioInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AudioInfo record);

    int updateByPrimaryKey(AudioInfo record);
    AudioInfo selectByQrcodeId(String qrcode);
}