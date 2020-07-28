package com.yangk.baseproject.common.elasticsearch.escontroller;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.alibaba.fastjson.JSON;
import com.yangk.baseproject.common.elasticsearch.dto.PackageAddMqMessageDTO;

import com.yangk.baseproject.common.elasticsearch.esentity.PackageEntity;
import com.yangk.baseproject.common.elasticsearch.esmapper.PackageEsMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 测试es的controller
 * @Author yangkun
 * @Date 2020/7/28
 * @Version 1.0
 * @blame yangkun
 */
@RestController
@RequestMapping("/es")
@Slf4j
public class EsController {

    @Autowired
    private PackageEsMapper packageEsMapper;

    @RequestMapping("/save")
    public void save(@RequestBody PackageAddMqMessageDTO packageAddMqMessageDTO) {
        PackageEntity packageEntity = new PackageEntity();
//        PackageAddMqMessageDTO packageAddMqMessageDTO = new PackageAddMqMessageDTO();
//        packageAddMqMessageDTO.setDepartureCountry("测试的啊");
//        packageAddMqMessageDTO.setDepartureCountryName("测试的数据");
//        packageAddMqMessageDTO.setWarhouseCode("测试的code");
//        packageAddMqMessageDTO.setCreateTime(new Date());

        packageEntity.setId("123456789");
        packageEntity.setPackageAddMqMessageDTO(packageAddMqMessageDTO);
        log.info("save es:{}", JSON.toJSONString(packageAddMqMessageDTO));
        packageEsMapper.save(packageEntity);
    }

    @RequestMapping("/get")
    public String get(String id) {
        log.info("get es info：{}",id);
        Optional<PackageEntity> optional = packageEsMapper.findById("id");
        if (optional.isPresent()) {
            PackageEntity entity = optional.get();
            return JSON.toJSONString(entity);
        }
        return null;
    }

    @RequestMapping("/update")
    public void update(String id) {
        Optional<PackageEntity> optional = packageEsMapper.findById(id);
        if (optional.isPresent()) {
            PackageEntity entity = optional.get();
            entity.getPackageAddMqMessageDTO().setCreateTime(new Date());
            entity.getPackageAddMqMessageDTO().setDepartureCountry("修改的数据哒");
            entity.getPackageAddMqMessageDTO().setWarhouseCode("修改的数据code");
            log.info("update es:{}", entity);
            packageEsMapper.save(entity);
        }
    }

    @RequestMapping("/getByKey")
    public String getByKey(String key) {
        log.info("get es info：{}", key);
        QueryBuilder queryBuilder = QueryBuilders.termQuery("","");
        Iterable<PackageEntity> search = packageEsMapper.search(queryBuilder);
        Iterator<PackageEntity> iterator = search.iterator();
        List<PackageEntity> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return JSON.toJSONString(list);
    }

}
