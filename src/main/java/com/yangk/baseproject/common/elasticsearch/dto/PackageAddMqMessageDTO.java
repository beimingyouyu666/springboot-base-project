package com.yangk.baseproject.common.elasticsearch.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 测试es的实体dto
 * @Author yangkun
 * @Date 2020/7/28
 * @Version 1.0
 * @blame yangkun
 */
@Data
public class PackageAddMqMessageDTO implements Serializable {

    /**
     * 起始国
     */
    private String departureCountry;

    /**
     * 起始国name
     */
    private String departureCountryName;

    /**
     * 仓库code（仓库系统）
     */
    private String warhouseCode;

    /**
     * 创建时间
     */
    private Date createTime;

}
