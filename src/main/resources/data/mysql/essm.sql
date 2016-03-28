/*
Navicat MariaDB Data Transfer

Source Server         : localhost
Source Server Version : 100019
Source Host           : localhost:3306
Source Database       : essm

Target Server Type    : MariaDB
Target Server Version : 100019
File Encoding         : 65001

Date: 2016-03-28 21:44:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_disk_file
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_file`;
CREATE TABLE `t_disk_file` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(128) NOT NULL,
  `file_path` varchar(1024) DEFAULT NULL,
  `file_size` bigint(20) DEFAULT NULL,
  `file_suffix` varchar(36) DEFAULT NULL,
  `file_type` int(11) DEFAULT NULL,
  `keyword` varchar(128) DEFAULT NULL,
  `name` varchar(512) NOT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `share_user_id` varchar(36) DEFAULT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  `folder_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_451ihoi6yukkju3h47fo3ydyk` (`folder_id`) USING BTREE,
  CONSTRAINT `t_disk_file_ibfk_1` FOREIGN KEY (`folder_id`) REFERENCES `t_disk_folder` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_file
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_file_history
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_file_history`;
CREATE TABLE `t_disk_file_history` (
  `id` varchar(36) NOT NULL,
  `file_id` varchar(36) DEFAULT NULL,
  `operate_time` datetime DEFAULT NULL,
  `operate_type` int(11) DEFAULT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_file_history
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_file_notice
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_file_notice`;
CREATE TABLE `t_disk_file_notice` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `is_active` int(11) DEFAULT NULL,
  `is_read` int(11) DEFAULT NULL,
  `location` int(11) DEFAULT NULL,
  `operate_type` int(11) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  `file_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_kp1wtqqfy7puuvx0kiob6ycec` (`file_id`) USING BTREE,
  CONSTRAINT `t_disk_file_notice_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `t_disk_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_file_notice
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_file_share
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_file_share`;
CREATE TABLE `t_disk_file_share` (
  `id` varchar(36) NOT NULL,
  `share_time` datetime DEFAULT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  `file_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_41o1uggnll5ymn9d2cu6hinef` (`file_id`) USING BTREE,
  CONSTRAINT `t_disk_file_share_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `t_disk_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_file_share
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_file_shared_file
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_file_shared_file`;
CREATE TABLE `t_disk_file_shared_file` (
  `share_id` varchar(36) NOT NULL,
  `file_id` varchar(36) DEFAULT NULL,
  KEY `FK_i0ab35nh8w50gvqnp8ufl3gj0` (`share_id`) USING BTREE,
  CONSTRAINT `t_disk_file_shared_file_ibfk_1` FOREIGN KEY (`share_id`) REFERENCES `t_disk_file_share` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_file_shared_file
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_file_shared_user
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_file_shared_user`;
CREATE TABLE `t_disk_file_shared_user` (
  `share_id` varchar(36) NOT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  KEY `FK_sfi6lfuky31ke8uhg4n5c452d` (`share_id`) USING BTREE,
  CONSTRAINT `t_disk_file_shared_user_ibfk_1` FOREIGN KEY (`share_id`) REFERENCES `t_disk_file_share` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_file_shared_user
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_folder
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_folder`;
CREATE TABLE `t_disk_folder` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(64) DEFAULT NULL,
  `folder_authorize` int(11) DEFAULT NULL,
  `limit_size` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `order_no` int(11) DEFAULT NULL,
  `organ_id` varchar(36) DEFAULT NULL,
  `parent_id` varchar(36) DEFAULT NULL,
  `path` varchar(512) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `role_id` varchar(36) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_folder
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_notice_organ
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_notice_organ`;
CREATE TABLE `t_disk_notice_organ` (
  `notice_id` varchar(36) NOT NULL,
  `organ_id` varchar(36) DEFAULT NULL,
  KEY `FK_h8t33rbj4d3aeox1x4msqpekm` (`notice_id`) USING BTREE,
  CONSTRAINT `t_disk_notice_organ_ibfk_1` FOREIGN KEY (`notice_id`) REFERENCES `t_disk_file_notice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_notice_organ
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_notice_user
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_notice_user`;
CREATE TABLE `t_disk_notice_user` (
  `notice_id` varchar(36) NOT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  KEY `FK_6glcip936mw7se5h4wru0fu7y` (`notice_id`) USING BTREE,
  CONSTRAINT `t_disk_notice_user_ibfk_1` FOREIGN KEY (`notice_id`) REFERENCES `t_disk_file_notice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_notice_user
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_organ_storage
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_organ_storage`;
CREATE TABLE `t_disk_organ_storage` (
  `id` varchar(36) NOT NULL,
  `limit_size` int(11) DEFAULT NULL,
  `organ_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_organ_storage
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_user_storage
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_user_storage`;
CREATE TABLE `t_disk_user_storage` (
  `id` varchar(36) NOT NULL,
  `limit_size` int(11) DEFAULT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_user_storage
-- ----------------------------

-- ----------------------------
-- Table structure for t_notice
-- ----------------------------
DROP TABLE IF EXISTS `t_notice`;
CREATE TABLE `t_notice` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `content` text,
  `effect_time` datetime DEFAULT NULL,
  `invalid_time` datetime DEFAULT NULL,
  `end_top_day` int(11) DEFAULT NULL,
  `is_record_read` int(11) DEFAULT NULL,
  `is_top` int(11) DEFAULT NULL,
  `mode` int(11) DEFAULT NULL,
  `organ_id` varchar(36) DEFAULT NULL,
  `publish_time` datetime DEFAULT NULL,
  `title` varchar(512) DEFAULT NULL,
  `type` varchar(36) DEFAULT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  `receive_scope` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_notice
-- ----------------------------

-- ----------------------------
-- Table structure for t_notice_file
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_file`;
CREATE TABLE `t_notice_file` (
  `notice_id` varchar(36) NOT NULL,
  `file_id` varchar(36) DEFAULT NULL,
  KEY `FK_klwa06kkhdlcrcvnlruxdmpq9` (`notice_id`) USING BTREE,
  CONSTRAINT `t_notice_file_ibfk_1` FOREIGN KEY (`notice_id`) REFERENCES `t_notice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_notice_file
-- ----------------------------

-- ----------------------------
-- Table structure for t_notice_receive_info
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_receive_info`;
CREATE TABLE `t_notice_receive_info` (
  `id` varchar(36) NOT NULL,
  `notice_id` varchar(36) DEFAULT NULL,
  `is_read` int(11) DEFAULT NULL,
  `read_time` datetime DEFAULT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_notice_receive_info
-- ----------------------------

-- ----------------------------
-- Table structure for t_notice_send_info
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_send_info`;
CREATE TABLE `t_notice_send_info` (
  `id` varchar(36) NOT NULL,
  `notice_id` varchar(36) DEFAULT NULL,
  `receive_object_type` int(11) DEFAULT NULL,
  `receive_object_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_notice_send_info
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_config`;
CREATE TABLE `t_sys_config` (
  `id` varchar(36) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `value` varchar(8192) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_config
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dictionary`;
CREATE TABLE `t_sys_dictionary` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `order_no` int(11) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `group_id` varchar(36) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `dictionarytype_code` varchar(64) DEFAULT NULL,
  `parent_code` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_oya5576xpt36m50jajdtf15k8` (`code`) USING BTREE,
  KEY `FK_5sqpn4281nmn3kyxe9qh3clsq` (`dictionarytype_code`) USING BTREE,
  KEY `FK_h94gyv9yt8lsfmsl6o7nbxgq4` (`parent_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_dictionary
-- ----------------------------
INSERT INTO `t_sys_dictionary` VALUES ('1', '2014-08-28 08:47:16', 'admin', '0', null, null, '0', 'dic_sys', '系统管理', '1', '系统管理', null, null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('10', '2014-06-23 16:18:59', 'admin', '0', '2014-06-23 16:22:28', 'admin', '2', 'yes_no', '是否', '6', '是否', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('12', '2014-06-24 09:00:58', 'admin', '0', null, null, '0', 'color', '颜色值', '8', '', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('13', '2014-06-24 09:01:21', 'admin', '0', null, null, '0', 'theme', '主题方案', '9', '', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('14', '2014-06-24 09:01:32', 'admin', '0', null, null, '0', 'sys_area_type', '区域类型', '10', '', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('15', '2014-06-24 09:22:40', 'admin', '0', null, null, '0', 'sys_user_type', '用户类型', '11', '', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('16', '2014-06-24 09:25:28', 'admin', '0', '2015-10-08 13:57:02', '1', '1', 'prj_template_type', '代码模板', '12', '', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('17', '2014-06-24 09:25:49', 'admin', '0', null, null, '0', 'cms_theme', '站点主题', '13', '', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('18', '2014-06-24 09:30:02', 'admin', '0', null, null, '0', 'cms_show_modes', '展现方式', '14', '', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('19', '2014-06-24 09:30:12', 'admin', '0', null, null, '0', 'cms_posid', '推荐位', '15', '', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('2', '2014-08-28 08:47:47', 'admin', '0', '2015-10-09 14:51:16', '1', '1', 'notice', '内容管理', '2', '内容管理', '1', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('20', '2014-06-24 09:34:37', 'admin', '0', null, null, '0', 'cms_guestbook', '留言板分类', '16', '', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('21', '2014-06-24 09:34:47', 'admin', '0', null, null, '0', 'cms_del_flag', '内容状态', '17', '', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('22', '2015-08-14 09:30:12', 'admin', '0', '2015-08-14 09:30:12', '', '0', 'cms_deviceconfig', '显示设备', '19', '', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('3', '2014-08-29 17:42:18', 'admin', '0', null, null, '0', 'cms', '网站管理', '18', '', null, null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('8', '2014-06-18 14:41:51', 'admin', '0', '2014-06-24 09:30:41', 'admin', '1', 'cms_module', '栏目模型', '4', '', '3', null, null, null);
INSERT INTO `t_sys_dictionary` VALUES ('9', '2014-06-18 14:42:30', 'admin', '0', null, null, '0', 'show_hide', '隐藏', '5', '', '3', null, null, null);

-- ----------------------------
-- Table structure for t_sys_dictionary_item
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dictionary_item`;
CREATE TABLE `t_sys_dictionary_item` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `order_no` int(11) DEFAULT NULL,
  `remak` varchar(100) DEFAULT NULL,
  `value` varchar(100) DEFAULT NULL,
  `dictionary_id` varchar(36) DEFAULT NULL,
  `parent_id` varchar(36) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_dictionary_item
-- ----------------------------
INSERT INTO `t_sys_dictionary_item` VALUES ('10', '2014-06-24 09:18:19', 'admin', '0', '2014-06-24 09:18:47', 'admin', '1', 'cerulean', '天蓝主题', '8', '', 'cerulean', '13', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('11', '2014-06-24 09:19:22', 'admin', '0', null, null, '0', 'readable', '橙色主题', '9', '', 'readable', '13', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('12', '2014-06-24 09:19:43', 'admin', '0', null, null, '0', 'united', '红色主题', '10', '', 'united', '13', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('13', '2014-06-24 09:19:59', 'admin', '0', null, null, '0', 'flat', 'Flat主题', '11', '', 'flat', '13', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('16', '2014-06-24 09:23:08', 'admin', '0', '2014-06-24 09:23:41', 'admin', '1', 'sys_user_type1', '系统管理', '14', '', 'sys_user_type2', '15', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('17', '2014-06-24 09:23:32', 'admin', '0', null, null, '0', 'sys_user_type2', '部门经理', '15', '', '2', '15', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('18', '2014-06-24 09:24:03', 'admin', '0', null, null, '0', 'sys_user_type3', '普通用户', '16', '', '3', '15', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('19', '2014-06-24 09:24:40', 'admin', '0', null, null, '0', 'show_hide1', '显示', '17', '', '1', '9', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('20', '2014-06-24 09:25:00', 'admin', '0', null, null, '0', 'show_hide0', '隐藏', '18', '', '0', '9', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('21', '2014-06-24 09:26:34', 'admin', '0', null, null, '0', 'red', '红色', '19', '', 'red', '12', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('22', '2014-06-24 09:26:50', 'admin', '0', null, null, '0', 'green', '绿色', '20', '', 'green', '12', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('23', '2014-06-24 09:27:08', 'admin', '0', null, null, '0', 'blue', '蓝色', '21', '', 'blue', '12', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('24', '2014-06-24 09:28:36', 'admin', '0', null, null, '0', 'basic', '基础主题', '22', '', 'basic', '17', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('25', '2014-06-24 09:29:02', 'admin', '0', null, null, '0', 'cms_theme_blue', '蓝色主题', '23', '', 'blue', '17', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('26', '2014-06-24 09:29:45', 'admin', '0', null, null, '0', 'cms_theme_red', '站点红色主题', '24', '', 'red', '17', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('27', '2014-06-24 09:31:11', 'admin', '0', null, null, '0', 'cms_show_modes0', '默认展现方式', '25', '', '0', '18', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('28', '2014-06-24 09:31:35', 'admin', '0', null, null, '0', 'cms_show_modes1', '首栏目内容列表', '26', '', '1', '18', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('29', '2014-06-24 09:31:52', 'admin', '0', null, null, '0', 'cms_show_modes2', '栏目第一条内容', '27', '', '2', '18', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('30', '2014-06-24 09:32:21', 'admin', '0', null, null, '0', 'cms_posid1', '首页焦点图', '28', '', '1', '19', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('31', '2014-06-24 09:32:43', 'admin', '0', null, null, '0', 'cms_posid2', '栏目页文章推荐', '29', '', '2', '19', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('32', '2014-06-24 09:33:15', 'admin', '0', '2015-10-08 15:58:21', '1', '2', 'article', '文章模型', '30', '', 'article', '8', '', '');
INSERT INTO `t_sys_dictionary_item` VALUES ('33', '2014-06-24 09:33:33', 'admin', '0', null, null, '0', 'picture', '图片模型', '31', '', 'picture', '8', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('34', '2014-06-24 09:33:56', 'admin', '0', null, null, '0', 'download', '下载模型', '32', '', 'download', '8', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('35', '2014-06-24 09:34:09', 'admin', '0', null, null, '0', 'link', '链接模型', '33', '', 'link', '8', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('36', '2014-06-24 09:34:23', 'admin', '0', null, null, '0', 'special', '专题模型', '34', '', 'special', '8', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('37', '2014-06-24 09:35:07', 'admin', '0', '2014-06-24 09:43:08', 'admin', '1', 'cms_guestbook1', '咨询', '35', '', '1', '20', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('404e43317be441f0860d1e0d3187c100', '2015-08-10 16:19:08', '1', '0', '2015-08-10 16:20:19', '1', '2', 'cms_del_flag1', '已删除', '3', null, '1', '21', null, '');
INSERT INTO `t_sys_dictionary_item` VALUES ('43', '2014-06-24 09:42:30', 'admin', '0', null, null, '0', 'cms_guestbook2', '建议', '36', '', '2', '20', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('44', '2014-06-24 09:43:03', 'admin', '0', null, null, '0', 'cms_guestbook3', '投诉', '37', '', '3', '20', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('45', '2014-06-24 09:43:23', 'admin', '0', null, null, '0', 'cms_guestbook4', '其它', '38', '', '4', '20', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('48', '2014-06-24 09:44:22', 'admin', '0', '2015-08-10 16:20:16', '1', '1', 'cms_del_flag2', '审核', '2', '', '2', '21', null, '');
INSERT INTO `t_sys_dictionary_item` VALUES ('49', '2014-06-30 08:31:18', 'admin', '0', null, null, '0', 'yes_no1', '是', '42', '', '1', '10', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('50', '2014-06-30 08:31:33', 'admin', '0', null, null, '0', 'yes_no0', '否', '43', '', '0', '10', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('672fd5c422ec4ae68c6523b144175809', '2015-09-30 11:15:04', '1', '0', '2015-09-30 11:15:04', '1', '0', 'bug_02', '会议', '47', null, 'bug_02', '2', '', '');
INSERT INTO `t_sys_dictionary_item` VALUES ('67e904c40d104932a5af714b079e60d7', '2015-08-27 17:23:38', '1', '0', '2015-08-27 17:23:38', '1', '0', 'cms_posid3', '首页推荐', '46', null, '3', '19', null, '');
INSERT INTO `t_sys_dictionary_item` VALUES ('7', '2014-06-24 09:03:05', 'admin', '0', null, null, '0', 'yellow', '黄色', '5', '', 'yellow', '12', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('8', '2014-06-24 09:03:47', 'admin', '0', '2014-06-24 09:05:45', 'admin', '1', 'orange', '橙色', '6', '', 'orange', '12', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('9', '2014-06-24 09:18:02', 'admin', '0', null, null, '0', 'default', '默认主题', '7', '', 'default', '13', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('c4b513af682d43dfbf391a15eaa352bf', '2015-08-14 12:38:56', '1', '0', '2015-08-14 12:38:56', '1', '0', 'cms_deviceconfig1', 'PC端显示', '44', null, '1', '22', null, '');
INSERT INTO `t_sys_dictionary_item` VALUES ('cbf8e5fea47f4dd18e705df027cdbd3f', '2015-08-10 16:18:51', '1', '0', '2015-08-10 16:20:09', '1', '3', 'cms_del_flag0', '正常', '1', null, '0', '21', null, '');
INSERT INTO `t_sys_dictionary_item` VALUES ('cms_theme', '2014-08-28 08:48:08', 'admin', '0', '2014-08-29 08:30:50', 'admin', '2', 'bug01', '内部通知', '1', '', 'bug01', '2', null, null);
INSERT INTO `t_sys_dictionary_item` VALUES ('d7eecac8cda241ff8b85ea3816fe91e1', '2015-08-14 12:39:30', '1', '0', '2015-08-14 12:39:30', '1', '0', 'cms_deviceconfig2', '移动端显示', '45', null, '2', '22', null, '');

-- ----------------------------
-- Table structure for t_sys_log
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_log`;
CREATE TABLE `t_sys_log` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `action` varchar(255) DEFAULT NULL,
  `action_time` varchar(20) DEFAULT NULL,
  `browser_type` varchar(128) DEFAULT NULL,
  `device_type` varchar(128) DEFAULT NULL,
  `exception` longtext,
  `ip` varchar(64) DEFAULT NULL,
  `module` varchar(255) DEFAULT NULL,
  `oper_time` datetime DEFAULT NULL,
  `remark` text,
  `title` varchar(512) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_organ
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_organ`;
CREATE TABLE `t_sys_organ` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `fax` varchar(64) DEFAULT NULL,
  `manager_user_id` varchar(36) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `order_no` int(11) DEFAULT NULL,
  `phone` varchar(64) DEFAULT NULL,
  `sys_code` varchar(36) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `parent_id` varchar(36) DEFAULT NULL,
  `super_manager_user_id` varchar(36) DEFAULT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  `parent_ids` varchar(2000) DEFAULT NULL,
  `mobile` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_sixv5h7puaswyuptn1f4mnq5b` (`parent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_organ
-- ----------------------------
INSERT INTO `t_sys_organ` VALUES ('1', '2014-09-17 14:08:54', '1', '0', '2015-12-09 08:23:24', '1', '34', '', '', '', '1', '江西省锦峰软件科技有限公司', '1', '', '01', '0', null, '', '', '0,', '');

-- ----------------------------
-- Table structure for t_sys_post
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_post`;
CREATE TABLE `t_sys_post` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `remark` varchar(36) DEFAULT NULL,
  `organ_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_do2moeabchk0jr90i3d7qjkhe` (`organ_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_post
-- ----------------------------
INSERT INTO `t_sys_post` VALUES ('7577f76fe02b40d3b6295cfd57651d43', '2015-10-19 11:31:54', '1', '1', '2015-10-19 11:32:07', '1', '1', '', '12', '', 'c28497402a5848bb9283ce4f36ce01b8');
INSERT INTO `t_sys_post` VALUES ('b37b38c0d6df4a64946af57dd4d47aa7', '2015-10-13 14:55:37', '1', '0', '2015-10-13 15:16:33', '1', '3', '', '11', '', '1');
INSERT INTO `t_sys_post` VALUES ('f6bf3e44470d439fa2521eaaf583f688', '2015-07-15 11:14:48', '1', '0', '2015-10-19 14:18:49', '1', '1', '', '123', '', '1');

-- ----------------------------
-- Table structure for t_sys_post_organ
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_post_organ`;
CREATE TABLE `t_sys_post_organ` (
  `post_id` varchar(36) NOT NULL,
  `organ_id` varchar(36) DEFAULT NULL,
  KEY `FK_8og9yio9ub2otv0dm2a2ap2ee` (`post_id`) USING BTREE,
  CONSTRAINT `t_sys_post_organ_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `t_sys_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_post_organ
-- ----------------------------
INSERT INTO `t_sys_post_organ` VALUES ('b37b38c0d6df4a64946af57dd4d47aa7', '1');
INSERT INTO `t_sys_post_organ` VALUES ('7577f76fe02b40d3b6295cfd57651d43', 'c28497402a5848bb9283ce4f36ce01b8');
INSERT INTO `t_sys_post_organ` VALUES ('7577f76fe02b40d3b6295cfd57651d43', 'a06e91c7bd8b44fbae1f19b869f7e431');
INSERT INTO `t_sys_post_organ` VALUES ('f6bf3e44470d439fa2521eaaf583f688', 'c28497402a5848bb9283ce4f36ce01b8');
INSERT INTO `t_sys_post_organ` VALUES ('f6bf3e44470d439fa2521eaaf583f688', 'a06e91c7bd8b44fbae1f19b869f7e431');

-- ----------------------------
-- Table structure for t_sys_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_resource`;
CREATE TABLE `t_sys_resource` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(64) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `icon_cls` varchar(255) DEFAULT NULL,
  `mark_url` varchar(2000) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `order_no` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `parent_id` text,
  `parent_ids` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_resource
-- ----------------------------
INSERT INTO `t_sys_resource` VALUES ('1', null, null, '0', '2015-01-27 15:46:10', 'admin', '6', '', null, 'eu-icon-application', '', '系统管理', '9', '0', '', null, null);
INSERT INTO `t_sys_resource` VALUES ('155d9fa508a3497686a31b1c8c68a718', '2016-03-28 21:43:47', '1', '0', '2016-03-28 21:44:00', '1', '1', '', null, '', '', '属性配置', '30', '0', 'sys/config', '1', 'null1,');
INSERT INTO `t_sys_resource` VALUES ('1918c2e5f04547b180891cf2fe7488bb', '2015-08-04 10:01:26', 'ac9c62d1646942348eac686a7d41b0dc', '0', '2016-03-28 21:42:54', '1', '1', 'notice:publish', null, '', '', '发布', '1', '1', '', '78c495a1eab4439fb64b1ac5d568f4b0', 'null1,78c495a1eab4439fb64b1ac5d568f4b0,');
INSERT INTO `t_sys_resource` VALUES ('2', null, null, '0', '2015-10-14 11:01:16', '1', '11', '', null, 'eu-icon-link', '', '资源管理', '2', '0', 'sys/resource', '1', 'null1,');
INSERT INTO `t_sys_resource` VALUES ('21', '2013-12-08 17:26:38', 'admin', '0', '2014-09-17 12:31:54', 'admin', '5', '', null, 'eu-icon-server', '', '日志管理', '10', '0', 'sys/log', '1', null);
INSERT INTO `t_sys_resource` VALUES ('26', '2014-06-11 19:43:48', 'admin', '0', '2014-12-08 09:47:30', 'admin', '6', '', null, 'eu-icon-vcard', '', '岗位管理', '5', '0', 'sys/post', '1', null);
INSERT INTO `t_sys_resource` VALUES ('3', '2013-11-12 22:13:42', 'admin', '0', '2014-12-08 10:04:22', 'admin', '14', '', null, 'eu-icon-group', '', '角色管理', '3', '0', 'sys/role', '1', null);
INSERT INTO `t_sys_resource` VALUES ('4', '2013-11-12 22:14:10', 'admin', '0', '2014-09-17 12:30:30', 'admin', '6', '', null, 'eu-icon-gears', '', '机构管理', '4', '0', 'sys/organ', '1', null);
INSERT INTO `t_sys_resource` VALUES ('5', '2013-11-12 22:14:28', 'admin', '0', '2014-09-17 12:31:02', 'admin', '5', '', null, 'eu-icon-user', '', '用户管理', '6', '0', 'sys/user', '1', null);
INSERT INTO `t_sys_resource` VALUES ('78c495a1eab4439fb64b1ac5d568f4b0', '2016-03-28 21:42:46', '1', '0', '2016-03-28 21:43:55', '1', '2', '', null, '', '', '我的通知', '50', '0', 'notice', '1', 'null1,');
INSERT INTO `t_sys_resource` VALUES ('8', '2013-11-12 22:15:40', 'admin', '0', '2014-09-17 12:31:19', 'admin', '8', '', null, 'eu-icon-book', '', '数据字典', '8', '0', 'sys/dictionary', '1', null);

-- ----------------------------
-- Table structure for t_sys_role
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role`;
CREATE TABLE `t_sys_role` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `data_scope` varchar(1) DEFAULT NULL,
  `is_activity` varchar(1) DEFAULT NULL,
  `is_system` varchar(1) DEFAULT NULL,
  `organ_id` varchar(36) DEFAULT NULL,
  `role_type` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ljbh7lmeyig6pbauwq7mv4atk` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_role
-- ----------------------------
INSERT INTO `t_sys_role` VALUES ('178491aff86b486395ac825cad70cf63', '2015-12-08 17:07:24', '1', '0', '2015-12-08 17:13:02', '1', '4', 'tzgg', '通知公告人员', '', '8', '1', '1', null, null);
INSERT INTO `t_sys_role` VALUES ('1f370cb140db4365b9efe104edd712ad', '2015-12-09 10:30:06', '1', '0', '2015-12-09 11:23:04', '1', '2', '', '12', '', '8', '1', '1', null, null);
INSERT INTO `t_sys_role` VALUES ('3737c96066bf4dedbd6687df5aa41224', '2015-10-15 13:51:04', '1', '1', '2015-12-08 17:12:46', '1', '9', '', '123', '', '8', '1', '1', null, null);
INSERT INTO `t_sys_role` VALUES ('a47a816aa7e84dc7a8fcc92e93589bcc', '2015-10-13 11:40:26', '1', '0', '2015-12-08 17:08:14', '1', '24', 'base', '员工', '', '8', '1', '1', null, null);

-- ----------------------------
-- Table structure for t_sys_role_organ
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role_organ`;
CREATE TABLE `t_sys_role_organ` (
  `role_id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  KEY `FK_5cpnrv79xvedd9dvowxbuf2mk` (`user_id`) USING BTREE,
  KEY `FK_oorf8snvueb85oo97r33eixmu` (`role_id`) USING BTREE,
  CONSTRAINT `t_sys_role_organ_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_sys_organ` (`id`),
  CONSTRAINT `t_sys_role_organ_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `t_sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_role_organ
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role_resource`;
CREATE TABLE `t_sys_role_resource` (
  `role_id` varchar(36) NOT NULL,
  `resource_id` varchar(36) NOT NULL,
  KEY `FK_o4bwr9474osu6umabgmxbs3qk` (`resource_id`) USING BTREE,
  KEY `FK_ehsis8jjgkl0tp0xq3rnawvvp` (`role_id`) USING BTREE,
  CONSTRAINT `t_sys_role_resource_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `t_sys_role` (`id`),
  CONSTRAINT `t_sys_role_resource_ibfk_2` FOREIGN KEY (`resource_id`) REFERENCES `t_sys_resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_role_resource
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user`;
CREATE TABLE `t_sys_user` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `login_name` varchar(36) NOT NULL,
  `name` varchar(36) DEFAULT NULL,
  `order_no` int(11) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `personal_email` varchar(64) DEFAULT NULL,
  `photo` varchar(1024) DEFAULT NULL,
  `qq` varchar(36) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  `sex` int(11) DEFAULT NULL,
  `tel` varchar(36) DEFAULT NULL,
  `user_type` int(11) DEFAULT NULL,
  `mobile` varchar(36) DEFAULT NULL,
  `original_password` varchar(128) DEFAULT NULL,
  `default_organ_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user
-- ----------------------------
INSERT INTO `t_sys_user` VALUES ('1', null, null, '0', '2015-12-11 13:03:37', '1', '7', '', null, null, '', 'admin', '管理员', '1', 'c4ca4238a0b923820dcc509a6f75849b', '', '', '', '', null, '', '0', '', '7e0cd7be3e66d4a8', '1');

-- ----------------------------
-- Table structure for t_sys_user_organ
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_organ`;
CREATE TABLE `t_sys_user_organ` (
  `user_id` varchar(36) NOT NULL,
  `organ_id` varchar(36) NOT NULL,
  KEY `FK_g9ryaj7pdmtol9p8kqosbc2j2` (`organ_id`) USING BTREE,
  KEY `FK_l3dn8e1drj3p7i2b73pe40vu5` (`user_id`) USING BTREE,
  CONSTRAINT `t_sys_user_organ_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`id`),
  CONSTRAINT `t_sys_user_organ_ibfk_2` FOREIGN KEY (`organ_id`) REFERENCES `t_sys_organ` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user_organ
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_user_password
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_password`;
CREATE TABLE `t_sys_user_password` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `modify_time` datetime NOT NULL,
  `original_password` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user_password
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_post`;
CREATE TABLE `t_sys_user_post` (
  `user_id` varchar(36) NOT NULL,
  `post_id` varchar(36) NOT NULL,
  KEY `FK_6o9ao50lhoc3w2pveiy5ull8e` (`post_id`) USING BTREE,
  KEY `FK_ifxlp9gug6x73orq3cliviugy` (`user_id`) USING BTREE,
  CONSTRAINT `t_sys_user_post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`id`),
  CONSTRAINT `t_sys_user_post_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `t_sys_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user_post
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_user_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_resource`;
CREATE TABLE `t_sys_user_resource` (
  `user_id` varchar(36) NOT NULL,
  `resource_id` varchar(36) NOT NULL,
  KEY `FK_3w943h1pd1874m8cwu8l8h5rm` (`resource_id`) USING BTREE,
  KEY `FK_ft3xc4i3casdwlrlhsxr6mstu` (`user_id`) USING BTREE,
  CONSTRAINT `t_sys_user_resource_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`id`),
  CONSTRAINT `t_sys_user_resource_ibfk_2` FOREIGN KEY (`resource_id`) REFERENCES `t_sys_resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user_resource
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_role`;
CREATE TABLE `t_sys_user_role` (
  `user_id` varchar(36) NOT NULL,
  `role_id` varchar(36) NOT NULL,
  KEY `FK_fhpxr8fohqwyr0ee4fjkf1xd2` (`role_id`) USING BTREE,
  KEY `FK_rxiutb8ymuxoc91qa0able8vb` (`user_id`) USING BTREE,
  CONSTRAINT `t_sys_user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`id`),
  CONSTRAINT `t_sys_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `t_sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user_role
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_version_log
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_version_log`;
CREATE TABLE `t_sys_version_log` (
  `id` varchar(36) NOT NULL,
  `remark` varchar(4096) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version_code` varchar(36) DEFAULT NULL,
  `version_name` varchar(36) DEFAULT NULL,
  `file_id` varchar(36) DEFAULT NULL,
  `version_log_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_version_log
-- ----------------------------
