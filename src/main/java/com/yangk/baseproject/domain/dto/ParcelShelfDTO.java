package com.yangk.baseproject.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Data
public class ParcelShelfDTO implements Serializable {
    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 4PX单号
     */
    @NotBlank(message = "{parcel.scan}")
    private String fpxTrackingNo;

    /**
     * 库位号
     */
    @NotBlank(message = "{warehouseNo.scan}")
    private String warehouseNumber;

    /**
     * 仓库编码
     */
    @NotBlank(message = "{warehouseCode.null}")
    private String warehouseCode;
}
