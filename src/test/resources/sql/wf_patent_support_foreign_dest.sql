/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50623
Source Host           : localhost:3306
Source Database       : test2

Target Server Type    : MYSQL
Target Server Version : 50623
File Encoding         : 65001

Date: 2018-05-18 15:08:25
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for wf_patent_support_foreign_dest
-- ----------------------------
DROP TABLE IF EXISTS `wf_patent_support_foreign_dest`;
CREATE TABLE `wf_patent_support_foreign_dest` (
  `id` bigint(18) NOT NULL,
  `patentName` varchar(1024) DEFAULT NULL COMMENT '专利名称',
  `applyNo` varchar(64) DEFAULT NULL COMMENT '专利(申请)号',
  `authNo` varchar(64) DEFAULT NULL COMMENT '国际授权号',
  `applyDate` date DEFAULT NULL COMMENT '申请日',
  `authDate` date DEFAULT NULL COMMENT '授权日',
  `applyer` varchar(1024) DEFAULT NULL COMMENT '专利权利人',
  `type` varchar(100) DEFAULT NULL COMMENT '专利权属类别',
  `country` varchar(100) DEFAULT NULL COMMENT '国外发明专利授权国家',
  `attachment` varchar(1024) DEFAULT NULL COMMENT '国外授权发明专利证书',
  `oId` bigint(18) DEFAULT NULL COMMENT '资助表外键',
  `countryCode` varchar(18) DEFAULT NULL COMMENT '国际代码',
  `translation` varchar(1024) DEFAULT NULL COMMENT '中文翻译件',
  `statusType` int(1) DEFAULT NULL COMMENT '1表示已经申请资助或者申请中，0表示撤回了，可以重新申请',
  `money` int(8) DEFAULT NULL COMMENT '资助金额',
  PRIMARY KEY (`id`),
  KEY `pct_patent_support_fk` (`oId`),
  CONSTRAINT `pct_patent_support_fk` FOREIGN KEY (`oId`) REFERENCES `wf_support_foreign` (`Id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='国外资助中间表';
