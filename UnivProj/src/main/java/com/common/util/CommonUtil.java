package com.common.util;

import com.common.Constants;
import com.common.entry.ResponseDto;
import com.common.entry.SystemConfigEnum;

/**
 * @author: EMMA
 * @date: 2018/6/30
 * @description:
 * @since:
 */
public class CommonUtil {
    /**
     * 设置返回值
     * @param responseDto
     * @param result
     * @param code
     * @param msg
     */
    public static void setResponse(ResponseDto responseDto, String result, String code,String msg) {
        responseDto.setResult(result);
        responseDto.setMsg(msg);
        responseDto.setCode( code);
    }
}
