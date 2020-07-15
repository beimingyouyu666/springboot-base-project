package com.yangk.baseproject.domain.dos;

import lombok.Data;

import java.util.Date;

/**
 * @Description 用户信息
 * @Author yangkun
 * @Date 2020/7/6
 * @Version 1.0
 * @blame yangkun
 */
@Data
public class UserDO {

    /**
     * 自增id
     */
    private Integer id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 名/姓名
     */
    private String firstName;

    /**
     * 姓
     */
    private String lastName;

    /**
     * 电话
     */
    private String phone;

    /**
     * 区号
     */
    private String areaCode;

    /**
     * 电话2
     */
    private String phone2;

    /**
     * 公司名
     */
    private String company;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮编
     */
    private String postCode;

    /**
     * 国家
     */
    private String country;

    /**
     * 省、直辖市、州
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区、县
     */
    private String district;

    /**
     * 乡镇、街道
     */
    private String street;

    /**
     * 门牌号
     */
    private String houseNumber;

    /**
     * 证件类型
     */
    private String certificateType;

    /**
     * 证件号码
     */
    private String certificateNo;
}
