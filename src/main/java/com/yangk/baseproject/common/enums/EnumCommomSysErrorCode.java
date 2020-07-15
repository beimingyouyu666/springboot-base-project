package com.yangk.baseproject.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @Description 错误码枚举
 * @Author yangkun
 * @Date 2020/6/30
 * @Version 1.0
 * @blame yangkun
 */
@Getter
public enum EnumCommomSysErrorCode {
    /**
     * 未知异常
     */
    UNKNOW_ERROR("SERVER_INTERNAL_ERROR", "服务器内部错误"),
    /**
     * 校验失败
     */
    VALIDA_ERROR("VALIDA_ERROR", "校验失败"),
    /**
     * 缺少参数
     */
    FIELD_EMPTY_ERROR("FIELD_EMPTY_ERROR", "缺少参数"),
    /**
     * 包裹状态非法
     */
    PARCEL_STATUS_VALIDA_ERROR("PARCEL_STATUS_VALIDA_ERROR", "包裹状态非法"),
    /**
     * 不存在
     */
    DOES_NOT_EXIST("DOES_NOT_EXIST", "不存在"),
    /**
     * 服务繁忙请稍后再试
     */
    SERVICE_BUSY_ERROR("SERVICE_BUSY_ERROR", "服务繁忙请稍后再试"),
    /**
     * 超过可用数量
     */
    OVER_AVAILABLE_ERROR("OVER_AVAILABLE_ERROR", "提交包裹数超过套餐可用数量"),
    /**
     * 同步服务推送失败
     */
    SYSN_SYSTEM_SYN_FAIL("SYSN_SYSTEM_SYN_FAIL", "同步服务推送失败"),
    /**
     * 远程服务调用失败
     */
    FEIGN_ERROR("FEIGN_ERROR", "远程服务调用失败"),
    /**
     * 类型转换失败
     */
    PARSE_ERROR("PARSE_ERROR", "类型转换失败"),
    /**
     * 处理中
     */
    OPERATING_ERROR("OPERATING_ERROR", "处理中"),
    /**
     * 等待超时
     */
    WAIT_TIMEOUT_ERROR("WAIT_TIMEOUT_ERROR", "等待超时"),
    /**
     * MQ发送异常
     */
    MQ_ERROR("MQ_ERROR", "MQ发送异常"),
    /**
     * 关闭流异常
     */
    STREAM_CLOSE_ERROR("STREAM_CLOSE_ERROR", "关闭流异常"),
    /**
     * 尝试次数过多
     */
    RETRY_MAX_ERROR("RETRY_MAX_ERROR", "尝试次数过多"),
    /**
     * 请求URL不正确
     */
    REUQEST_URL_ERROR("REUQEST_URL_ERROR", "请求URL不正确"),
    /**
     * 获取用户信息失败
     */
    GET_USER_INFO_ERROR("GET_USER_INFO_ERROR", "获取用户信息失败"),
    /**
     * 重复提交
     */
    REPEAT_ERROR("REPEAT_ERROR", "重复提交"),
    /**
     * 加密失败
     */
    ENCRYPTION_ERROR("ENCRYPTION_ERROR", "加密失败"),
    /**
     * 解密失败
     */
    DECRYPTION_ERROR("DECRYPTION_ERROR", "解密失败"),
    /**
     * 秘钥错误
     */
    APP_KEY_ERROR("APP_KEY_ERROR", "秘钥错误"),
    /**
     * 预报错误
     */
    FORECAST_ERROR("FORECAST_ERROR", "预报错误"),
    /**
     * 消息转换失败
     */
    MSG_PARSE_ERROR("MSG_PARSE_ERROR", "消息转换失败"),
    /**
     * 远程服务返回失败
     */
    REMOTE_SERVICE_RETURN_FAILED("REMOTE_SERVICE_RETURN_FAILED", "远程服务返回失败"),
    /**
     * 业务校验失败
     */
    BUSINESS_VALIDA_FAILED("BUSINESS_VALIDA_FAILED", "业务校验失败"),
    /**
     * 不需要Code
     */
    BUSINESS_ERROR("BUSINESS_ERROR", "业务异常"),


    /**
     * 该excel版本不支持
     */
    EXCEL_VERSION_NOT_SUPPORT("EXCEL_VERSION_ERROR", "该excel版本不支持"),

    /**
     * 请求正在执行
     */
    REQUEST_IS_BEING_EXECUTED("REQUEST_IS_BEING_EXECUTED", "请求正在执行"),

    /**
     * 不支持该文件格式
     */
    FILE_TYPE_NOT_SUPPORT("FILE_TYPE_NOT_SUPPORT", "不支持该文件格式"),
    /**
     * 重复(幂等)请求
     */
    IDEMPOTENT_REQUEST("IDEMPOTENT_REQUEST", "重复(幂等)请求"),
    ;

    private String value;
    private String desc;

    EnumCommomSysErrorCode(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static List<EnumCommomSysErrorCode> getEnumErrorCodeList() {
        EnumCommomSysErrorCode[] values = EnumCommomSysErrorCode.values();
        return Arrays.asList(values);
    }
}
