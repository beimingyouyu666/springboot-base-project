package com.yangk.baseproject.common.util.excelutil.exceltest;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 包裹打包规则传输对象
 * @Author yangkun
 * @Date 2020/6/11
 * @Version 1.0
 * @blame yangkun
 */
@Data
public class PackageRuleDTO extends BaseRowModel implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 排序编号，并行流处理前设置编号，处理完后还原顺序
     */
    private Integer sortNo;

    /**
     * 4px跟踪号
     */
    @ExcelProperty(index = 0,value = "4px单号")
    private String fpxTrackingNo;

    /**
     * 打包规则（code）
     */
    @ExcelProperty(index = 1,value = "打包规则编码")
    private String packageRuleCode;
    /**
     * 打包规则（name）
     */
    private String packageRuleName;

    /**
     * 修改原因
     */
    @ExcelProperty(index = 2,value = "修改原因(非必填)")
    private String modifyReason;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 操作人id
     */
    private String operatorId;

    /**
     * 操作人名字
     */
    private String operatorName;


}
