/*
Navicat MySQL Data Transfer

Source Server         : localmysql
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : trivia

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-12-18 22:08:07
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for player
-- ----------------------------
DROP TABLE IF EXISTS `player`;
CREATE TABLE `player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `level` int(11) DEFAULT '1',
  `win_count` int(11) DEFAULT '0',
  `lose_count` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of player
-- ----------------------------
INSERT INTO `player` VALUES ('1', '12', '12', '1', '0', '0');
INSERT INTO `player` VALUES ('2', '2', '232', '1', '0', '0');
INSERT INTO `player` VALUES ('3', '123', 'caomiao', '1', '0', '0');
INSERT INTO `player` VALUES ('4', '1', 'c', '1', '0', '0');
INSERT INTO `player` VALUES ('5', 'jjj', 'j', '1', '0', '0');
INSERT INTO `player` VALUES ('6', 'jjj', 'junkook', '1', '0', '0');

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `answers` varchar(255) DEFAULT NULL,
  `right_answer` varchar(255) DEFAULT NULL,
  `level` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES ('1', '1+1=', 'science', '2#12#22#以上都不对', '0', '1');
INSERT INTO `question` VALUES ('2', '水的化学式？', 'science', 'H2O#H2O2#H2#O2', '0', '1');
INSERT INTO `question` VALUES ('3', '偶像组合BTS的有几名成员？', 'pop', '5#6#7#8', '2', '1');
INSERT INTO `question` VALUES ('4', '偶像组合BIGBANG有几名成员？', 'pop', '5#6#7#8', '0', '1');
INSERT INTO `question` VALUES ('5', '偶像组合ARASHI有几名成员？', 'pop', '5#6#7#8', '0', '1');
INSERT INTO `question` VALUES ('6', '偶像组合SEVENTEEN有几名成员？', 'pop', '11#13#15#17', '1', '1');
INSERT INTO `question` VALUES ('7', '篮球的英文是？', 'sports', 'basketball#baseball#football#rugby', '0', '1');
INSERT INTO `question` VALUES ('8', '棒球的英文是？', 'sports', 'basketball#baseball#football#rugby', '1', '1');
INSERT INTO `question` VALUES ('9', '足球的英文是？', 'sports', 'basketball#baseball#football#rugby', '2', '1');
INSERT INTO `question` VALUES ('10', '橄榄球的英文是？', 'sports', 'basketball#baseball#football#rugby', '3', '1');
INSERT INTO `question` VALUES ('11', '我也不知道问什么反正第一个是对的？', 'rock', '对的#额#哈哈#呵呵', '0', '1');
