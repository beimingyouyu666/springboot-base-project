package com.yangk.baseproject.common.elasticsearch.esentity;

import com.yangk.baseproject.common.elasticsearch.dto.PackageAddMqMessageDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @Description 用于操作es的实体
 *
 * shards (最多为5个分片) 代表索引分片，es可以把一个完整的索引分成多个分片，这样的好处是可以把一个大的索引拆分成多个，分布到不同的节点上。构成分布式搜索。分片的数量只能在索引创建前指定，并且索引创建后不能更改。
 *  replicas 代表索引副本，es可以设置多个索引的副本，副本的作用一是提高系统的容错性，当某个节点某个分片损坏或丢失时可以从副本中恢复。二是提高es的查询效率，es会自动对搜索请求进行负载均衡。
 * @Author yangkun
 * @Date 2020/7/28
 * @Version 1.0
 * @blame yangkun
 */
@Document(indexName = "cos", type = "package", shards = 5, replicas = 1, refreshInterval = "-1")
@Data
public class PackageEntity {

    @Id
    private String id;

    /**
     * 包裹信息
     */
    private PackageAddMqMessageDTO packageAddMqMessageDTO;

}
