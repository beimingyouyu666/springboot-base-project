CREATE TABLE `user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` varchar(32) NOT NULL DEFAULT '' COMMENT '用户id',
  `first_name` varchar(32) NOT NULL DEFAULT '' COMMENT '名/姓名',
  `last_name` varchar(32) NOT NULL DEFAULT '' COMMENT '姓',
  `phone` varchar(128) NOT NULL DEFAULT '' COMMENT '电话',
  `area_code` varchar(32) NOT NULL DEFAULT '' COMMENT '区号',
  `phone2` varchar(128) NOT NULL DEFAULT '' COMMENT '电话2',
  `company` varchar(64) NOT NULL DEFAULT '' COMMENT '公司名',
  `email` varchar(32) NOT NULL DEFAULT '' COMMENT '邮箱',
  `post_code` varchar(32) NOT NULL DEFAULT '' COMMENT '邮编',
  `country` varchar(32) NOT NULL DEFAULT '' COMMENT '国家',
  `province` varchar(64) NOT NULL DEFAULT '' COMMENT '省、直辖市、州',
  `city` varchar(64) NOT NULL DEFAULT '' COMMENT '市',
  `district` varchar(64) NOT NULL DEFAULT '' COMMENT '区、县',
  `street` varchar(256) NOT NULL DEFAULT '' COMMENT '乡镇、街道',
  `address` varchar(500) NOT NULL DEFAULT '' COMMENT '揽收地址',
  `house_number` varchar(32) NOT NULL DEFAULT '' COMMENT '门牌号',
  `certificate_type` varchar(32) NOT NULL DEFAULT '' COMMENT '证件类型',
  `certificate_no` varchar(64) NOT NULL DEFAULT '' COMMENT '证件号码',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户';


CREATE TABLE `person` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `score` int(11) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `name_score` (`name`(191),`score`),
  KEY `create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;