/*
Navicat MariaDB Data Transfer

Source Server         : localhost
Source Server Version : 100019
Source Host           : localhost:3306
Source Database       : essm

Target Server Type    : MariaDB
Target Server Version : 100019
File Encoding         : 65001

Date: 2016-03-28 21:48:48
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
  `type` varchar(36) DEFAULT NULL,
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
  `area_id` varchar(36) DEFAULT NULL,
  `deputy_manager_user_id` varchar(128) DEFAULT NULL
  PRIMARY KEY (`id`),
  KEY `FK_sixv5h7puaswyuptn1f4mnq5b` (`parent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_organ
-- ----------------------------
INSERT INTO `t_sys_organ` VALUES ('1', '2014-09-17 14:08:54', '1', '0', '2015-12-09 08:23:24', '1', '34', '', '', '', '1', 'XXX科技有限公司', '1', '', '01', '0', null, '', '', '0,', '');

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

-- ----------------------------
-- Table structure for t_sys_role_organ
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role_organ`;
CREATE TABLE `t_sys_role_organ` (
  `role_id` varchar(36) NOT NULL,
  `organ_id` varchar(36) NOT NULL,
  KEY `FK_5cpnrv79xvedd9dvowxbuf2mk` (`organ_id`) USING BTREE,
  KEY `FK_oorf8snvueb85oo97r33eixmu` (`role_id`) USING BTREE,
  CONSTRAINT `t_sys_role_organ_ibfk_1` FOREIGN KEY (`organ_id`) REFERENCES `t_sys_organ` (`id`),
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
  `photo` varchar(1024) DEFAULT NULL,
  `person_email` varchar(64) DEFAULT NULL,
  `qq` varchar(36) DEFAULT NULL,
  `weixin` varchar(64) DEFAULT NULL,
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
