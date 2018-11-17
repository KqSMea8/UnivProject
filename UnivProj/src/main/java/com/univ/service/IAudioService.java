package com.univ.service;

import com.common.entry.ResponseDto;
import com.univ.domain.AudioInfo;

import java.io.File;

/**
 * @Auther: EMMA
 * @Date: 2018/6/19
 * @Description:
 * @Since:
 */
public interface IAudioService {
    /**
     * 存储语音文件
     * */
    int addAudioInfo(AudioInfo audioInfo);
    AudioInfo getAudioInfo(String qrcodeId);
    ResponseDto saveAudioInfo(String qrcodeId, String openId, String mediaId);
    File getListen(String audioPath);
    String getListen2(String audioPath);
}
