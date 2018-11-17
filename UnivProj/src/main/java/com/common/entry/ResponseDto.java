package com.common.entry;

/**
 * @Auther: EMMA
 * @Date: 2018/6/19
 * @Description: 通用返回类
 * @Since:
 */
public class ResponseDto {
    /**请求结果，fail，success*/
    private String result;
    /**请求结果编码*/
    private String code;
    /**请求返回实体*/
    private Object returnObj;
    /**请求失败信息*/
    private String msg;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Object getReturnObj() {
        return returnObj;
    }

    public void setReturnObj(Object returnObj) {
        this.returnObj = returnObj;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
