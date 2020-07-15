
package com.yangk.baseproject.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class ParcelDTO implements java.io.Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 创建人id 
     */
    private String createId;

    /**
     * 创建时间 
     */
    private Date createTime;

    /**
     * 4px跟踪号 
     */
    private String fpxTrackingNo;

    /**
     * 业务类型 
     */
    private String businessType;

    /**
     * 仓库编码 
     */
    private String warehouseCode;

    /**
     * 包裹实重
     */
    private BigDecimal weight;

    /**
     * 包裹总价值
     */
    private BigDecimal totalPrice;

}