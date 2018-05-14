/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50623
Source Host           : localhost:3306
Source Database       : test2

Target Server Type    : MYSQL
Target Server Version : 50623
File Encoding         : 65001

Date: 2018-05-14 16:49:43
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for z_patent_dest
-- ----------------------------
DROP TABLE IF EXISTS `z_patent_dest`;
CREATE TABLE `z_patent_dest` (
  `Id` bigint(16) NOT NULL COMMENT '主键',
  `owner` bigint(16) DEFAULT NULL COMMENT '所属企业/个人主键',
  `patentName` varchar(128) DEFAULT NULL COMMENT '发明名称',
  `appNumber` varchar(32) DEFAULT NULL COMMENT '申请号',
  `appDate` date DEFAULT NULL COMMENT '申请日',
  `publicNo` varchar(32) DEFAULT NULL COMMENT '公开号',
  `publicDate` date DEFAULT NULL COMMENT '公开日',
  `authNo` varchar(32) DEFAULT NULL COMMENT '授权号',
  `authDate` date DEFAULT NULL COMMENT '授权日',
  `patentType` varchar(16) DEFAULT NULL COMMENT '专利类型',
  `applyer` varchar(256) DEFAULT NULL COMMENT '申请权利人',
  `patentee` varchar(256) DEFAULT NULL COMMENT '专利权人',
  `address` varchar(1024) DEFAULT NULL COMMENT '地址',
  `abs` varchar(4000) DEFAULT NULL COMMENT '摘要 text',
  `mainIpc` varchar(64) DEFAULT NULL COMMENT '主分类号',
  `ipc` varchar(1024) DEFAULT NULL COMMENT '分类号',
  `inventors` varchar(1024) DEFAULT NULL COMMENT '发明设计人',
  `proCode` varchar(32) DEFAULT NULL COMMENT '国省代码',
  `agency` varchar(256) DEFAULT NULL COMMENT '代理机构',
  `agent` varchar(32) DEFAULT NULL COMMENT '代理人',
  `prioritys` varchar(1024) DEFAULT NULL COMMENT '优先权',
  `interApply` varchar(128) DEFAULT NULL COMMENT '国家申请',
  `interAuth` varchar(128) DEFAULT NULL COMMENT '国际公布',
  `interDate` date DEFAULT NULL COMMENT '进入国家日期',
  `division` varchar(1024) DEFAULT NULL COMMENT '分案申请号',
  `isFee` int(1) DEFAULT NULL COMMENT '当年缴费 0表示没有缴费，-1表示没授权，1表示已缴费',
  `isApply` int(1) DEFAULT '0' COMMENT '有否专利申请资助 1表示已申请资助，0表示没有申请资助',
  `isProvince` int(1) DEFAULT '0' COMMENT '有否省级专利资助 1表示已申请资助，0表示没有申请资助',
  `isCity` int(1) DEFAULT '0' COMMENT '有否市级专利资助 1表示已申请资助，0表示没有申请资助',
  `isDistrict` int(1) DEFAULT '0' COMMENT '有否区级专利资助 1表示已申请资助，0表示没有申请资助',
  `isAuto` int(1) DEFAULT NULL COMMENT '手动导入 1表示手动导入，0表示自动导入',
  `importDate` datetime NOT NULL COMMENT '导入时间',
  `batchNo` varchar(32) DEFAULT NULL COMMENT '导入批次',
  `patStatus` varchar(16) DEFAULT NULL COMMENT '专利状态',
  `patErr` varchar(255) DEFAULT NULL COMMENT '专利错误信息，针对自导入，即isAuto为1的记录',
  `ORGID` bigint(20) DEFAULT NULL COMMENT '组织ID',
  `ATTACHMENT` varchar(256) DEFAULT NULL COMMENT '专利证书附件',
  `isBigSupport` int(1) DEFAULT '0' COMMENT '有否专利大户资助 1表示已申请资助，0表示没有申请资助',
  `COUNTYEAR` int(2) DEFAULT NULL COMMENT '缴纳年费次数',
  `isOriginal` int(1) DEFAULT NULL COMMENT '是否原始数据  1表示原始数据，0表示不是原始数据',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `z_patent_appnumber_unique` (`appNumber`) USING BTREE,
  KEY `ownerIndex` (`owner`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='专利主表 通过专利接口导入的数据主表';

-- ----------------------------
-- Records of z_patent_dest
-- ----------------------------
INSERT INTO `z_patent_dest` VALUES ('10000000368520', null, '一种由多聚甲醛制备2,3-丁二酮的方法', 'CN201010525698.0', '2010-10-30', 'CN101973860A', '2011-02-16', 'CN101973860B', '2013-01-02', '发明专利', 'xxxxx', 'xxxxx;', '410081 湖南省长沙市岳麓区麓山路36号', '', 'C07C49/12(2006.01)I', '', '谭蓉;周全;银董红;陈益民;', '430104;岳麓区', '长沙市融智专利事务所 43114', '颜勇;', '', '', '', null, '0', null, '1', '0', '1', '0', '0', '2018-03-06 14:59:39', 'xxxxx', '无效', '', null, '', '0', null, null);
