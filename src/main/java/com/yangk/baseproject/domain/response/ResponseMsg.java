package com.yangk.baseproject.domain.response;

import com.yangk.baseproject.common.exception.BusinessRuntimeException;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 响应公共实体
 * @Author yangkun
 * @Date 2020/6/29
 * @Version 1.0
 * @blame yangkun
 */
@Data
public class ResponseMsg<T> implements Serializable {


    /**
     * 响应标识-success
     */
    public static final String RESPONSE_FLAG_SUCCESS = "success";

    /**
     * 响应标识-failure
     */
    public static final String RESPONSE_FLAG_FAILURE = "fail";
    public static final String VALIDA_EMPTY = "VALIDA_ERROR";
    public static final String UNKNOW_ERROR = "UNKNOW_ERROR";
    public static final String SYS_INNER_ERROR = "System inner error";
    private static final long serialVersionUID = -5596930346535993316L;
    /**
     * 请求结果标识
     */
    private String flag;

    /**
     * 请求结果标识
     */
    private String returnCode;

    /**
     * 返回的信息
     */
    private String message;

    /**
     * 返回集合对象信息
     */
    private T obj;

    private ResponseMsg() {
    }

    private ResponseMsg(String flag, String returnCode, String message) {
        this.flag = flag;
        this.returnCode = returnCode;
        this.message = message;
    }

    private ResponseMsg(String flag, String returnCode, String message, T obj) {
        this.flag = flag;
        this.returnCode = returnCode;
        this.message = message;
        this.obj = obj;
    }

    public static ResponseMsg buildSuccessMsg() {
        return new ResponseMsg(RESPONSE_FLAG_SUCCESS, RESPONSE_FLAG_SUCCESS, RESPONSE_FLAG_SUCCESS);
    }

    /**
     * 接口调用成功,有返回内容,使用这个方法
     *
     * @param obj
     * @return
     */
    public static <T> ResponseMsg<T> buildSuccessMsg(T obj) {
        return new ResponseMsg<>(RESPONSE_FLAG_SUCCESS, RESPONSE_FLAG_SUCCESS, RESPONSE_FLAG_SUCCESS, obj);
    }

    public static ResponseMsg buildFailMsg() {
        return new ResponseMsg(RESPONSE_FLAG_FAILURE, RESPONSE_FLAG_FAILURE, RESPONSE_FLAG_FAILURE);
    }

    public static ResponseMsg buildFailMsg(String errorDesc) {
        return new ResponseMsg(RESPONSE_FLAG_FAILURE, RESPONSE_FLAG_FAILURE, errorDesc);
    }

    public static ResponseMsg buildFailMsg(BusinessRuntimeException ex) {
        return new ResponseMsg(RESPONSE_FLAG_FAILURE, String.valueOf(ex.getErrcode()), ex.getMessage(), null);
    }

    public static ResponseMsg buildFailMsg(String code, String msg) {
        return new ResponseMsg(RESPONSE_FLAG_FAILURE, code, msg);
    }

    public static ResponseMsg buildValidEmptyFailMsg() {
        return new ResponseMsg(RESPONSE_FLAG_FAILURE, VALIDA_EMPTY, VALIDA_EMPTY);
    }

    public static ResponseMsg buildValidEmptyFailMsg(String errorDesc) {
        return new ResponseMsg(RESPONSE_FLAG_FAILURE, VALIDA_EMPTY, errorDesc);
    }

    public static <T>ResponseMsg<T> buildValidFailMsg(String errorDesc, T obj) {
        return new ResponseMsg<>(RESPONSE_FLAG_FAILURE, VALIDA_EMPTY, errorDesc, obj);
    }

    public static ResponseMsg buildUnknownFailMsg() {
        return new ResponseMsg(RESPONSE_FLAG_FAILURE, UNKNOW_ERROR, SYS_INNER_ERROR);
    }

    /**
     * 判断返回表示是否为Success
     *
     * @return
     */
    public boolean isSuccess() {
        if (RESPONSE_FLAG_SUCCESS.equals(this.flag)) {
            return true;
        }
        return false;
    }
}
