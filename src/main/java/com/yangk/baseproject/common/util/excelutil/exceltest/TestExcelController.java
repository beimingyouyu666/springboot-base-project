package com.yangk.baseproject.common.util.excelutil.exceltest;

import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yangk.baseproject.common.util.JedisClusterUtil;
import com.yangk.baseproject.common.util.excelutil.easyexcel.ExcelUtil;
import com.yangk.baseproject.common.util.excelutil.poi.CellMap;
import com.yangk.baseproject.common.util.excelutil.poi.ExcelExportUtil;
import com.yangk.baseproject.domain.response.ResponseMsg;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description 打包规则控制器
 * @Author yangkun
 * @Date 2020/6/17
 * @Version 1.0
 * @blame yangkun
 */
@RestController
@Slf4j
public class TestExcelController {


    @Autowired
    private JedisClusterUtil jedisClusterUtil;

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    @ResponseBody
    public void exportParcelList(HttpServletResponse response, HttpServletRequest request, ParcelQuery query) throws Exception {
        int pageNum = 0;
        ParcelQuery parcelQuery = new ParcelQuery();
        BeanUtils.copyProperties(query,parcelQuery);
        SXSSFWorkbook sxssfWorkbook = ExcelExportUtil.getSXSSFWorkbook();
        List<CellMap> cellMapList = buildCellMap();
        ExcelExportUtil.fillTitleRow(sxssfWorkbook, ExcelExportUtil.DEFAULT_SHEET_NAME, cellMapList);
        PageInfo<Object> pageData = new PageInfo<>(new ArrayList<>());

        while (true) {
            long start = System.currentTimeMillis();
            pageNum++;
            // 防止内存溢出的方法
            ExcelExportUtil.fillExcel_2007_SXSSF(sxssfWorkbook, ExcelExportUtil.DEFAULT_SHEET_NAME, cellMapList,
                    new ArrayList<>());
            if (pageData.isIsLastPage()) {
                break;
            }
            pageData = null;
            log.info("导出excel组装第"+pageNum+"页数据耗时"+(System.currentTimeMillis()-start)+" ms");
        }
        // 数据流中写入数据
        ExcelExportUtil.sendHttpResponse(response, sxssfWorkbook, "包裹数据");

    }

    @PostMapping("/upload")
    public ResponseMsg batchImportPackageRule(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Object> objects;
        long start = System.currentTimeMillis();
        try {
            objects = ExcelUtil.readExcel(file, new PackageRuleDTO(), 1);
            log.info("解析excel耗时: {} ms",System.currentTimeMillis()-start);
        } catch (ExcelAnalysisException e) {
            // 数据不能超过5000
            return ResponseMsg.buildFailMsg("导入数据不能超过5000条，请处理后重新导入");
        }
        if (CollectionUtils.isEmpty(objects)) {
            return ResponseMsg.buildFailMsg("导入数据不能为空");
        }

        return batchModifyPackageRule(objects,request,response);
    }

    /**
     * 组建表头（第一行数据）
     *
     * @return CellMap
     */
    private List<CellMap> buildCellMap() {
        List<CellMap> cellMapList = new ArrayList<>();
        cellMapList.add(new CellMap("参考号", "referenceNumber"));
        cellMapList.add(new CellMap("末端跟踪号", "endDeliveryNo"));
        cellMapList.add(new CellMap("状态", "parcelStatus"));
        cellMapList.add(new CellMap("所属仓库", "warehouseCode"));
        cellMapList.add(new CellMap("货位号", "goodsNumber"));
        cellMapList.add(new CellMap("客户代码", "customerCode"));
        cellMapList.add(new CellMap("寄件姓名", "senderName"));
        cellMapList.add(new CellMap("寄件电话", "senderPhoneNumber"));
        cellMapList.add(new CellMap("产品", "productCode"));
        cellMapList.add(new CellMap("渠道", "clearanceChannel"));
        cellMapList.add(new CellMap("站点重量", "preWeight"));
        cellMapList.add(new CellMap("仓库称重", "weight"));
        cellMapList.add(new CellMap("是否问题件", "whetherIssueParcel"));
        cellMapList.add(new CellMap("站点", "storeCode"));
        cellMapList.add(new CellMap("入库时间", "instockTime"));
        cellMapList.add(new CellMap("出库时间", "outstockTime"));
        cellMapList.add(new CellMap("出库总单", "batchNo"));
        cellMapList.add(new CellMap("收件人姓名", "recipientName"));
        cellMapList.add(new CellMap("收件人电话", "recipientPhoneNumber"));
        cellMapList.add(new CellMap("收件人地址", "recipientAddress"));
        cellMapList.add(new CellMap("税费", "tax"));
        cellMapList.add(new CellMap("入库分拣时间", "warehousingSortingTime"));
        cellMapList.add(new CellMap("打包规则", "packageRule"));
        return cellMapList;
    }

    private ResponseMsg batchModifyPackageRule(List<Object> objects, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<PackageRuleDTO> packageRuleDTOList = Collections.synchronizedList(new ArrayList<>(objects.size()));
//        parallelStreamHandle(objects, packageRuleDTOList);
        multiThreadHandle(objects, packageRuleDTOList);
        // 重新还原顺序
        packageRuleDTOList = packageRuleDTOList.stream().sorted(Comparator.comparing(PackageRuleDTO::getSortNo)).collect(Collectors.toList());
        if (packageRuleDTOList.stream().filter(rule -> StringUtils.isNotBlank(rule.getFailReason())).findFirst().isPresent()) {
            // 有校验失败的数据
            long objectSize = ObjectSizeCalculator.getObjectSize(packageRuleDTOList);
            log.info("集合大小:{} b",objectSize);
            String redisKey = UUID.randomUUID().toString();
            jedisClusterUtil.set("REDIS_WMS_BATCH_MODIFY_PACKAGE_RULE_PREFIX"+redisKey,JSON.toJSONString(packageRuleDTOList),300);
            request.getSession().setAttribute("BATCH_MODIFY_PACKAGE_RULE",redisKey);
            return ResponseMsg.buildFailMsg("导入失败");
        } else {
            // 异步针对数据重新计费
            List<List<PackageRuleDTO>> partition1 = Lists.partition(packageRuleDTOList, 50);
            for (List<PackageRuleDTO> packageRuleDTOS : partition1) {
//                batchModifyPackageRuleProvider.sendBatchModifyPackageRule(packageRuleDTOS);
            }
        }

        return ResponseMsg.buildSuccessMsg("导入成功");
    }

    /**
     * @Description:  并行流处理
     * @Author: yangkun
     * @Date: 2020/6/24
     * @Param:
     * @param objects
     * @param packageRuleDTOList
     * @return: void
     */
    private void parallelStreamHandle(List<Object> objects, List<PackageRuleDTO> packageRuleDTOList) {
        long start = System.currentTimeMillis();
        List<PackageRuleDTO> packageRuleDTOS = new ArrayList<>(objects.size());
        for (int i = 0; i < objects.size(); i++) {
            PackageRuleDTO rule = (PackageRuleDTO) objects.get(i);
            rule.setSortNo(i);
            packageRuleDTOS.add(rule);
        }
        packageRuleDTOS.parallelStream().forEach(rule->{
            if (StringUtils.isBlank(rule.getFpxTrackingNo())) {
                rule.setFailReason("4px单号为空");
                packageRuleDTOList.add(rule);
                return;
            }
            if (StringUtils.isBlank(rule.getPackageRuleCode())) {
                rule.setFailReason("打包规则编码为空");
                packageRuleDTOList.add(rule);
                return;
            }


            packageRuleDTOList.add(rule);
        });
        log.info("并行流处理耗时：{} ms",System.currentTimeMillis()-start);
    }

    /**
     * @Description:  线程池处理
     * @Author: yangkun
     * @Date: 2020/6/24
     * @Param:
     * @param objects
     * @param packageRuleDTOList
     * @return: void
     */
    private void multiThreadHandle(List<Object> objects, List<PackageRuleDTO> packageRuleDTOList) {
        long start = System.currentTimeMillis();
        // 设置序号
        List<PackageRuleDTO> packageRuleDTOS = new ArrayList<>(objects.size());
        for (int i = 0; i < objects.size(); i++) {
            PackageRuleDTO rule = (PackageRuleDTO) objects.get(i);
            rule.setSortNo(i);
            packageRuleDTOS.add(rule);
        }
        List<List<PackageRuleDTO>> partition = Lists.partition(packageRuleDTOS, 50);

        ExecutorService synExe  = new ThreadPoolExecutor(16, 16,
                0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(512), new ThreadFactoryBuilder().setNameFormat("GDS-WMS-BATCH-MODIFY-PACKAGERULE-POOL-%d").build());
        for (List<PackageRuleDTO> list : partition) {
            synExe.execute(()->{
                for (PackageRuleDTO rule : list) {
                    if (StringUtils.isBlank(rule.getFpxTrackingNo())) {
                        rule.setFailReason("4px单号为空");
                        packageRuleDTOList.add(rule);
                        continue;
                    }
                    if (StringUtils.isBlank(rule.getPackageRuleCode())) {
                        rule.setFailReason("打包规则编码为空");
                        packageRuleDTOList.add(rule);
                        continue;
                    }

                    if (StringUtils.isNotBlank(rule.getModifyReason()) && rule.getModifyReason().length() > 25) {
                        rule.setFailReason("修改原因，限制在25汉字以内");
                        packageRuleDTOList.add(rule);
                        continue;
                    }
                    packageRuleDTOList.add(rule);
                }
            });
        }
        synExe.shutdown();
        while (true) {
            if (synExe.isTerminated()){
                log.info("校验批量修改打包规则结束");
                break;
            }
        }
        log.info("线程池处理耗时：{} ms",System.currentTimeMillis()-start);
    }
}
