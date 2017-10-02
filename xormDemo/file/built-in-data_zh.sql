-- ----------------------------
-- Table structure for `data_source`
-- ----------------------------
DROP TABLE IF EXISTS `data_source`;
CREATE TABLE `data_source` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户id',
  `created_time` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '数据源名称',
  `class` varchar(30) DEFAULT NULL,
  `url` varchar(50) DEFAULT NULL,
  `port` varchar(8) DEFAULT NULL,
  `database_name` varchar(50) DEFAULT NULL,
  `basic_auth` tinyint(4) DEFAULT NULL,
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `is_delete` int(2) DEFAULT NULL COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of data_source
-- ----------------------------
INSERT INTO `data_source` VALUES ('demo0000000000000000000000000001', '', '2017-07-11 14:49:58', '优云demo数据', 'uyun', '10.1.200.112:7752/show/innerdata/', '', 'uyun-demo', '0', '', '', '0');
INSERT INTO `data_source` VALUES ('f55300ef0e8b4bdbad82bea8e9d4b7bc', 'e10adc3949ba59abbe56e057f20f88dd', '2017-07-11 14:25:23', 'cly', 'API', '', '', '', '0', '', '', '0');
INSERT INTO `data_source` VALUES ('store000000000000000000000000001', '', '2017-07-11 14:49:42', '优云内置数据源', 'uyun', '10.1.200.112:7751/show/innerdata/', '', 'uyun-store', '0', '', '', '0');
