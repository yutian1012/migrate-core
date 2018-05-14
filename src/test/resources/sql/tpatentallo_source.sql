/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50623
Source Host           : localhost:3306
Source Database       : test1

Target Server Type    : MYSQL
Target Server Version : 50623
File Encoding         : 65001

Date: 2018-05-14 16:49:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tpatentallo_source
-- ----------------------------
DROP TABLE IF EXISTS `tpatentallo_source`;
CREATE TABLE `tpatentallo_source` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `receiptNo` varchar(32) DEFAULT NULL COMMENT '根据时间来产生，后面加三位随即数\r\n            如：201504081446001',
  `patentName` varchar(256) DEFAULT NULL,
  `patentNo` varchar(128) DEFAULT NULL,
  `requestDate` date DEFAULT NULL,
  `requestCountry` varchar(16) DEFAULT NULL,
  `authDate` date DEFAULT NULL,
  `firstRequest` varchar(56) DEFAULT NULL,
  `otherRequest` varchar(256) DEFAULT NULL,
  `linkMan` varchar(32) DEFAULT NULL,
  `linkTel` varchar(32) DEFAULT NULL,
  `address` varchar(256) DEFAULT NULL,
  `mobile` varchar(32) DEFAULT NULL,
  `email` varchar(256) DEFAULT NULL,
  `zip` varchar(32) DEFAULT NULL,
  `pctDate` date DEFAULT NULL,
  `costType` varchar(16) DEFAULT NULL COMMENT '数据来自TConstant表',
  `requestType` varchar(16) DEFAULT NULL COMMENT '数据来自TConstant表',
  `bank` varchar(128) DEFAULT NULL,
  `bankName` varchar(128) DEFAULT NULL,
  `account` varchar(56) DEFAULT NULL,
  `status` smallint(6) DEFAULT '0' COMMENT '0:处理中 1:已处理',
  `result` varchar(512) DEFAULT NULL,
  `resultTime` datetime DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `creator` varchar(56) DEFAULT NULL,
  `resultor` varchar(56) DEFAULT NULL,
  `transferdate` date DEFAULT NULL,
  `transfermoney` decimal(9,2) DEFAULT NULL,
  `batch` varchar(32) DEFAULT NULL,
  `payee` varchar(32) DEFAULT NULL,
  `payeeaddr` varchar(128) DEFAULT NULL,
  `payeezip` varchar(16) DEFAULT NULL,
  `micro` char(2) DEFAULT '0',
  `organcode` varchar(32) DEFAULT NULL,
  `organname` varchar(128) DEFAULT NULL,
  `source` char(2) DEFAULT '1' COMMENT '1:PC,2:微信',
  `idcard` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_createTime` (`createTime`)
) ENGINE=InnoDB AUTO_INCREMENT=125586 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tpatentallo_source
-- ----------------------------
INSERT INTO `tpatentallo_source` VALUES ('9218', 'C100720110413002', '一种由多聚甲醛制备2,3-丁二酮的方法', '201010525698.0', '2010-11-01', 'ZG', null, 'xxxx', '', 'xxxx', 'xxxx', 'xxxxx', '', '', '', null, 'SQ1', 'ZCGX', 'xxxxx', 'xxxxx', 'xxxxx', '1', '', '2011-05-24 00:00:00', '2011-04-13 10:18:40', 'xxxx', 'xxx', null, '1000.00', 'xxxxx', 'xxxx', 'xxxx', '', '0', '', '', '1', '');
