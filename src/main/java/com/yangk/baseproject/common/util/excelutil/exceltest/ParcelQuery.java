
package com.yangk.baseproject.common.util.excelutil.exceltest;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 包裹 查询对象
 *
 * @date 2019-01-12
 */
@Data
public class ParcelQuery implements java.io.Serializable {

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
    private Date createTimeBegin;

    /**
     * 创建时间
     */
    private Date createTimeEnd;

    /**
     * 修改人ID
     */
    private String modifyId;

    /**
     * 修改时间
     */
    private Date modifyTimeBegin;

    /**
     * 修改时间
     */
    private Date modifyTimeEnd;

    /**
     * 4px跟踪号
     */
    private String fpxTrackingNo;

    /**
     * 客户自定义跟踪号
     */
    private String refNo;

    /**
     * 末端派送单号
     */
    private String endDeliveryNo;

    /**
     * 清关单号
     */
    private String clearanceNo;

    /**
     * 容器号
     */
    private String containerNo;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 清关渠道
     */
    private String clearanceChannel;

    /**
     * 起运国
     */
    private String departure;

    /**
     * 目的国
     */
    private String destinationCountry;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 包裹状态：未揽收 WAITING_PICKUP / 已交接（已揽收 和 仓库签收）PICKED_UP / 已发送 SENT / 已取消 CANCEL/ 配送完成 DELIVERY_COMPLETION
     */
    private String parcelStatus;

    /**
     * 库内包裹类型
     */
    private String instockParcelType;

    /**
     * 到仓方式（1:上门揽收；2:快递到仓；3:自送到仓；4:自送门店）
     */
    private String deliveryType;

    /**
     * 收货仓库/门店代码
     */
    private String storeCode;

    /**
     * 末端派送渠道
     */
    private String endDeliveryChannel;

    /**
     * 包裹被税
     */
    private Boolean parcelIstax;

    /**
     * 入库时间
     */
    private Date instockTimeBegin;

    /**
     * 入库时间
     */
    private Date instockTimeEnd;

    /**
     * 入库时间
     */
    private Date outstockTimeBegin;

    /**
     * 入库时间
     */
    private Date outstockTimeEnd;

    /**
     * 包裹实重
     */
    private Integer weight;

    /**
     * 收件人地址
     */
    private String address;
    /**
     * 收件人手机号
     */
    private String phone;
    /**
     * 收件人身份证
     */
    private String idCard;

    /**
     * 联系人类型
     */
    private String contactsType;

    /**
     * 调仓起始仓库
     */
    private String sourceWarehouseCode;

    /**
     * 包裹总价值
     */
    private BigDecimal totalPrice;

    /**
     * 包裹预报重
     */
    private Integer preWeight;

    /**
     * 加容器时间
     */
    private Date addContainerTimeBegin;

    /**
     * 加容器时间
     */
    private Date addContainerTimeEnd;


	/**
	 * 计费 1：计费成功；0：计费失败；null：未计费
	 */
	private Boolean billing;

    /**
     * 计费单号
     */
    private String billNo;

    /**
     * 是否已打印面单 1：已打单；0：未打单
     */
    private Boolean printed;

    /**
     * 分拣分区
     */
    private String sortingPartition;
    
    /** 批量查询 */
    private List<String> orderNoList;

    /**
     * 预报下发原始重量单位
     */
    private String systemWeightUnit;
}