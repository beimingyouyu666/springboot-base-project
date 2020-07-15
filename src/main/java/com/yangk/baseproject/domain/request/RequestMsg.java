package com.yangk.baseproject.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 请求实体
 * @Author yangkun
 * @Date 2020/6/30
 * @Version 1.0
 * @blame yangkun
 */
@Data
public class RequestMsg implements Serializable {
    private static final long serialVersionUID = -654903607047802227L;

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 接口名
     */
    private String method;

    /**
     * appKey
     */
    private String appKey;

    /**
     * 客户请求参数(json格式字符串)
     */
    private String body;
}
