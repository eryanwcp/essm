/*==============================================================*/
/* DBMS name:      ORACLE Version 10gR2                         */
/* Created on:     2018/9/7 10:01:25                            */
/*==============================================================*/


/*==============================================================*/
/* Table: T_DISK_FILE                                           */
/*==============================================================*/
create table T_DISK_FILE  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   NAME                 VARCHAR2(512)                   not null,
   CODE                 VARCHAR2(128)                   not null,
   FILE_PATH            VARCHAR2(1024)                 default NULL,
   FILE_SIZE            INTEGER                        default NULL,
   FILE_SUFFIX          VARCHAR2(36)                   default NULL,
   FILE_TYPE            VARCHAR2(36)                   default NULL,
   KEYWORD              VARCHAR2(128)                  default NULL,
   REMARK               VARCHAR2(255)                  default NULL,
   SHARE_USER_ID        VARCHAR2(36)                   default NULL,
   USER_ID              VARCHAR2(36)                   default NULL,
   FOLDER_ID            VARCHAR2(36)                   default NULL,
   constraint PK_T_DISK_FILE primary key (ID)
);

comment on table T_DISK_FILE is
'文件';

comment on column T_DISK_FILE.ID is
'主键ID';

comment on column T_DISK_FILE.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_DISK_FILE.VERSION is
'版本号';

comment on column T_DISK_FILE.CREATE_USER is
'创建者';

comment on column T_DISK_FILE.CREATE_TIME is
'创建时间';

comment on column T_DISK_FILE.UPDATE_USER is
'更新者';

comment on column T_DISK_FILE.UPDATE_TIME is
'更新时间';

comment on column T_DISK_FILE.NAME is
'文件名';

comment on column T_DISK_FILE.CODE is
'文件标识';

comment on column T_DISK_FILE.FILE_PATH is
'存储路径';

comment on column T_DISK_FILE.FILE_SIZE is
'文件大小';

comment on column T_DISK_FILE.FILE_SUFFIX is
'文件后缀';

comment on column T_DISK_FILE.FILE_TYPE is
'文件分类';

comment on column T_DISK_FILE.KEYWORD is
'关键字';

comment on column T_DISK_FILE.REMARK is
'备注';

comment on column T_DISK_FILE.SHARE_USER_ID is
'文件来源用户';

comment on column T_DISK_FILE.USER_ID is
'用户ID';

comment on column T_DISK_FILE.FOLDER_ID is
'文件夹ID';

/*==============================================================*/
/* Table: T_DISK_FOLDER                                         */
/*==============================================================*/
create table T_DISK_FOLDER  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   NAME                 VARCHAR2(255)                  default NULL,
   CODE                 VARCHAR2(64)                   default NULL,
   SORT                 INTEGER                        default NULL,
   FOLDER_AUTHORIZE     VARCHAR2(36)                   default NULL,
   TYPE                 VARCHAR2(36)                   default NULL,
   LIMIT_SIZE           INTEGER                        default NULL,
   PATH                 VARCHAR2(512)                  default NULL,
   USER_ID              VARCHAR2(36)                   default NULL,
   ORGAN_ID             VARCHAR2(36)                   default NULL,
   PARENT_ID            VARCHAR2(36)                   default NULL,
   PARENT_IDS           VARCHAR2(255),
   REMARK               VARCHAR2(255)                  default NULL,
   constraint PK_T_DISK_FOLDER primary key (ID)
);

comment on table T_DISK_FOLDER is
'文件夹';

comment on column T_DISK_FOLDER.ID is
'主键ID';

comment on column T_DISK_FOLDER.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_DISK_FOLDER.VERSION is
'版本号';

comment on column T_DISK_FOLDER.CREATE_USER is
'创建者';

comment on column T_DISK_FOLDER.CREATE_TIME is
'创建时间';

comment on column T_DISK_FOLDER.UPDATE_USER is
'更新者';

comment on column T_DISK_FOLDER.UPDATE_TIME is
'更新时间';

comment on column T_DISK_FOLDER.NAME is
'名称';

comment on column T_DISK_FOLDER.CODE is
'文件夹标识 授权类型为System时使用';

comment on column T_DISK_FOLDER.SORT is
'排序';

comment on column T_DISK_FOLDER.FOLDER_AUTHORIZE is
'授权 我的云盘：0 系统云盘：1';

comment on column T_DISK_FOLDER.TYPE is
'文件夹类型 正常：0 隐藏：1 ';

comment on column T_DISK_FOLDER.LIMIT_SIZE is
'大小限制 单位：M 无限制：0';

comment on column T_DISK_FOLDER.PATH is
'存储路径';

comment on column T_DISK_FOLDER.USER_ID is
'用户ID';

comment on column T_DISK_FOLDER.ORGAN_ID is
'所属部门';

comment on column T_DISK_FOLDER.PARENT_ID is
'父级ID';

comment on column T_DISK_FOLDER.PARENT_IDS is
'父级IDS';

comment on column T_DISK_FOLDER.REMARK is
'备注';

/*==============================================================*/
/* Table: T_NOTICE                                              */
/*==============================================================*/
create table T_NOTICE  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   TITLE                VARCHAR2(512),
   TYPE                 VARCHAR2(64),
   CONTENT              CLOB,
   USER_ID              VARCHAR2(36),
   ORGAN_ID             VARCHAR2(36),
   IS_TOP               CHAR(1),
   END_TOP_DAY          INTEGER,
   BIZ_MODE             VARCHAR2(36),
   PUBLISH_TIME         DATE,
   EFFECT_TIME          DATE,
   INVALID_TIME         DATE,
   IS_RECORD_READ       CHAR(1),
   RECEIVE_SCOPE        VARCHAR2(36),
   APP_ID               VARCHAR2(36),
   constraint PK_T_NOTICE primary key (ID)
);

comment on table T_NOTICE is
'通知';

comment on column T_NOTICE.ID is
'主键ID UUID';

comment on column T_NOTICE.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_NOTICE.VERSION is
'版本号';

comment on column T_NOTICE.CREATE_USER is
'创建者';

comment on column T_NOTICE.CREATE_TIME is
'创建时间';

comment on column T_NOTICE.UPDATE_USER is
'更新者';

comment on column T_NOTICE.UPDATE_TIME is
'更新时间';

comment on column T_NOTICE.TITLE is
'标题';

comment on column T_NOTICE.TYPE is
'类型 来源于数据字典 NOTICE_TYPE';

comment on column T_NOTICE.CONTENT is
'内容';

comment on column T_NOTICE.USER_ID is
'发布人';

comment on column T_NOTICE.ORGAN_ID is
'发布部门';

comment on column T_NOTICE.IS_TOP is
'是否置顶 是/否 1/0';

comment on column T_NOTICE.END_TOP_DAY is
'结束置顶天数';

comment on column T_NOTICE.BIZ_MODE is
'发布状态 未发布/已发布/已失效 0/1/2';

comment on column T_NOTICE.PUBLISH_TIME is
'发布时间';

comment on column T_NOTICE.EFFECT_TIME is
'生效时间';

comment on column T_NOTICE.INVALID_TIME is
'失效时间';

comment on column T_NOTICE.IS_RECORD_READ is
'是否记录查看情况 是/否 1/0';

comment on column T_NOTICE.RECEIVE_SCOPE is
'接收范围 所在单位及以下/所在单位/所在部门及以下/所在部门/所有/自定义 2/3/4/5/10/0';

comment on column T_NOTICE.APP_ID is
'APP标识';

/*==============================================================*/
/* Table: T_NOTICE_FILE                                         */
/*==============================================================*/
create table T_NOTICE_FILE  (
   NOTICE_ID            VARCHAR2(36),
   FILE_ID              VARCHAR2(36)
);

comment on table T_NOTICE_FILE is
'通知附件';

comment on column T_NOTICE_FILE.NOTICE_ID is
'通知ID';

comment on column T_NOTICE_FILE.FILE_ID is
'附件ID';

/*==============================================================*/
/* Table: T_NOTICE_MESSAGE                                      */
/*==============================================================*/
create table T_NOTICE_MESSAGE  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   TITLE                VARCHAR2(512)                  default NULL,
   CONTENT              CLOB,
   CATEGORY             VARCHAR2(36)                   default NULL,
   URL                  VARCHAR2(512)                  default NULL,
   IMAGE                VARCHAR2(512)                  default NULL,
   SENDER               VARCHAR2(36)                   default NULL,
   SEND_TIME            DATE,
   ORGAN_ID             VARCHAR2(36)                   default NULL,
   BIZ_MODE             VARCHAR2(36)                   default NULL,
   TIP_MESSAGE          VARCHAR2(36)                   default NULL,
   APP_ID               VARCHAR2(64),
   constraint PK_T_NOTICE_MESSAGE primary key (ID)
);

comment on table T_NOTICE_MESSAGE is
'消息';

comment on column T_NOTICE_MESSAGE.ID is
'主键ID UUID';

comment on column T_NOTICE_MESSAGE.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_NOTICE_MESSAGE.VERSION is
'版本号';

comment on column T_NOTICE_MESSAGE.CREATE_USER is
'创建者';

comment on column T_NOTICE_MESSAGE.CREATE_TIME is
'创建时间';

comment on column T_NOTICE_MESSAGE.UPDATE_USER is
'更新者';

comment on column T_NOTICE_MESSAGE.UPDATE_TIME is
'更新时间';

comment on column T_NOTICE_MESSAGE.TITLE is
'标题';

comment on column T_NOTICE_MESSAGE.CONTENT is
'内容';

comment on column T_NOTICE_MESSAGE.CATEGORY is
'消息类型 ';

comment on column T_NOTICE_MESSAGE.URL is
'链接地址';

comment on column T_NOTICE_MESSAGE.IMAGE is
'图片';

comment on column T_NOTICE_MESSAGE.SENDER is
'发送者';

comment on column T_NOTICE_MESSAGE.SEND_TIME is
'发送时间';

comment on column T_NOTICE_MESSAGE.ORGAN_ID is
'发布部门';

comment on column T_NOTICE_MESSAGE.BIZ_MODE is
'状态（业务） 草稿:draft 正在发布:Publishing 已发布:Published';

comment on column T_NOTICE_MESSAGE.TIP_MESSAGE is
'提醒 通过微信发送或者短信发送 果个之间以“,”分割 微信：weixin 短信 sms';

comment on column T_NOTICE_MESSAGE.APP_ID is
'应用标识';

/*==============================================================*/
/* Index: "Index_8"                                             */
/*==============================================================*/
create index "Index_8" on T_NOTICE_MESSAGE (
   SENDER ASC,
   APP_ID ASC
);

/*==============================================================*/
/* Table: T_NOTICE_MESSAGE_RECEIVE                              */
/*==============================================================*/
create table T_NOTICE_MESSAGE_RECEIVE  (
   ID                   VARCHAR2(36)                    not null,
   MESSAGE_ID           VARCHAR2(36)                   default NULL,
   USER_ID              VARCHAR2(36)                   default NULL,
   IS_READ              CHAR(1)                        default NULL,
   READ_TIME            DATE,
   constraint PK_T_NOTICE_MESSAGE_RECEIVE primary key (ID)
);

comment on table T_NOTICE_MESSAGE_RECEIVE is
'消息接收表';

comment on column T_NOTICE_MESSAGE_RECEIVE.ID is
'主键ID UUID';

comment on column T_NOTICE_MESSAGE_RECEIVE.MESSAGE_ID is
'消息ID';

comment on column T_NOTICE_MESSAGE_RECEIVE.USER_ID is
'用户ID';

comment on column T_NOTICE_MESSAGE_RECEIVE.IS_READ is
'是否读 是/否 1/0';

comment on column T_NOTICE_MESSAGE_RECEIVE.READ_TIME is
'读取时间';

/*==============================================================*/
/* Index: "Index_9"                                             */
/*==============================================================*/
create index "Index_9" on T_NOTICE_MESSAGE_RECEIVE (
   USER_ID ASC,
   IS_READ ASC
);

/*==============================================================*/
/* Table: T_NOTICE_MESSAGE_SENDER                               */
/*==============================================================*/
create table T_NOTICE_MESSAGE_SENDER  (
   ID                   VARCHAR2(36)                    not null,
   OBJECT_TYPE          VARCHAR2(12)                   default NULL,
   OBJECT_ID            VARCHAR2(36)                   default NULL,
   SEND_TIME            DATE                           default NULL,
   MESSAGE_ID           VARCHAR2(36)                   default NULL,
   constraint PK_T_NOTICE_MESSAGE_SENDER primary key (ID)
);

comment on table T_NOTICE_MESSAGE_SENDER is
'消息发送表';

comment on column T_NOTICE_MESSAGE_SENDER.ID is
'主键ID UUID';

comment on column T_NOTICE_MESSAGE_SENDER.OBJECT_TYPE is
'发送类型 部门/单位：organ 用户：user';

comment on column T_NOTICE_MESSAGE_SENDER.OBJECT_ID is
'发送对象ID';

comment on column T_NOTICE_MESSAGE_SENDER.SEND_TIME is
'发送时间';

comment on column T_NOTICE_MESSAGE_SENDER.MESSAGE_ID is
'消息ID';

/*==============================================================*/
/* Table: T_NOTICE_RECEIVE_INFO                                 */
/*==============================================================*/
create table T_NOTICE_RECEIVE_INFO  (
   ID                   VARCHAR2(36)                    not null,
   NOTICE_ID            VARCHAR2(36),
   USER_ID              VARCHAR2(36),
   IS_READ              CHAR(1),
   READ_TIME            DATE,
   IS_SEND              CHAR(1),
   constraint PK_T_NOTICE_RECEIVE_INFO primary key (ID)
);

comment on table T_NOTICE_RECEIVE_INFO is
'通知接收信息';

comment on column T_NOTICE_RECEIVE_INFO.ID is
'主键ID UUID';

comment on column T_NOTICE_RECEIVE_INFO.NOTICE_ID is
'通知ID';

comment on column T_NOTICE_RECEIVE_INFO.USER_ID is
'用户ID';

comment on column T_NOTICE_RECEIVE_INFO.IS_READ is
'是否读 是/否 1/0';

comment on column T_NOTICE_RECEIVE_INFO.READ_TIME is
'阅读时间';

comment on column T_NOTICE_RECEIVE_INFO.IS_SEND is
'是否发送成功 是/否 1/0';

/*==============================================================*/
/* Index: "Index_10"                                            */
/*==============================================================*/
create index "Index_10" on T_NOTICE_RECEIVE_INFO (
   USER_ID ASC,
   IS_READ ASC
);

/*==============================================================*/
/* Table: T_NOTICE_SEND_INFO                                    */
/*==============================================================*/
create table T_NOTICE_SEND_INFO  (
   ID                   VARCHAR2(36)                    not null,
   NOTICE_ID            VARCHAR2(36),
   RECEIVE_OBJECT_TYPE  INTEGER,
   RECEIVE_OBJECT_ID    VARCHAR2(36),
   constraint PK_T_NOTICE_SEND_INFO primary key (ID)
);

comment on table T_NOTICE_SEND_INFO is
'通知发送信息';

comment on column T_NOTICE_SEND_INFO.ID is
'主键ID UUID';

comment on column T_NOTICE_SEND_INFO.NOTICE_ID is
'通知ID';

comment on column T_NOTICE_SEND_INFO.RECEIVE_OBJECT_TYPE is
'接收对象类型 用户/部门 0/2';

comment on column T_NOTICE_SEND_INFO.RECEIVE_OBJECT_ID is
'接收对象ID';

/*==============================================================*/
/* Table: T_SYS_AREA                                            */
/*==============================================================*/
create table T_SYS_AREA  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   NAME                 VARCHAR2(255),
   SHORT_NAME           VARCHAR2(255),
   CODE                 VARCHAR2(36),
   TYPE                 VARCHAR2(36),
   SORT                 INTEGER,
   PARENT_ID            VARCHAR2(36),
   PARENT_IDS           VARCHAR2(1024),
   REMARK               VARCHAR2(255),
   constraint PK_T_SYS_AREA primary key (ID),
   constraint AK_KEY_2_T_SYS_AR unique (NAME)
);

comment on table T_SYS_AREA is
'区域表';

comment on column T_SYS_AREA.ID is
'主键ID';

comment on column T_SYS_AREA.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_AREA.VERSION is
'版本号';

comment on column T_SYS_AREA.CREATE_USER is
'创建者';

comment on column T_SYS_AREA.CREATE_TIME is
'创建时间';

comment on column T_SYS_AREA.UPDATE_USER is
'更新者';

comment on column T_SYS_AREA.UPDATE_TIME is
'更新时间';

comment on column T_SYS_AREA.NAME is
'名称';

comment on column T_SYS_AREA.SHORT_NAME is
'简称';

comment on column T_SYS_AREA.CODE is
'编码';

comment on column T_SYS_AREA.TYPE is
'类型 区域类型（1：国家；2：省份、直辖市；3：地市；4：区县；5：乡镇；6：村庄；9：其它）';

comment on column T_SYS_AREA.SORT is
'排序';

comment on column T_SYS_AREA.PARENT_ID is
'父级ID';

comment on column T_SYS_AREA.PARENT_IDS is
'上级ID集合';

comment on column T_SYS_AREA.REMARK is
'备注';

/*==============================================================*/
/* Table: T_SYS_CONFIG                                          */
/*==============================================================*/
create table T_SYS_CONFIG  (
   ID                   VARCHAR2(36)                    not null,
   CODE                 VARCHAR2(128)                   not null,
   VALUE                VARCHAR2(512),
   REMARK               VARCHAR2(255),
   constraint PK_T_SYS_CONFIG primary key (ID)
);

comment on table T_SYS_CONFIG is
'配置参数';

comment on column T_SYS_CONFIG.ID is
'主键ID UUID';

comment on column T_SYS_CONFIG.CODE is
'标识';

comment on column T_SYS_CONFIG.VALUE is
'属性值';

comment on column T_SYS_CONFIG.REMARK is
'备注';

/*==============================================================*/
/* Table: T_SYS_DICTIONARY                                      */
/*==============================================================*/
create table T_SYS_DICTIONARY  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   NAME                 VARCHAR2(255)                   not null,
   CODE                 VARCHAR2(64)                    not null,
   ORDER_NO             INTEGER,
   GROUP_ID             VARCHAR2(36),
   REMARK               VARCHAR2(255),
   constraint PK_T_SYS_DICTIONARY primary key (ID)
);

comment on table T_SYS_DICTIONARY is
'字典表';

comment on column T_SYS_DICTIONARY.ID is
'主键ID UUID';

comment on column T_SYS_DICTIONARY.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_DICTIONARY.VERSION is
'版本号';

comment on column T_SYS_DICTIONARY.CREATE_USER is
'创建者';

comment on column T_SYS_DICTIONARY.CREATE_TIME is
'创建时间';

comment on column T_SYS_DICTIONARY.UPDATE_USER is
'更新者';

comment on column T_SYS_DICTIONARY.UPDATE_TIME is
'更新时间';

comment on column T_SYS_DICTIONARY.NAME is
'名称';

comment on column T_SYS_DICTIONARY.CODE is
'编码';

comment on column T_SYS_DICTIONARY.ORDER_NO is
'排序';

comment on column T_SYS_DICTIONARY.GROUP_ID is
'分组ID';

comment on column T_SYS_DICTIONARY.REMARK is
'备注';

/*==============================================================*/
/* Table: T_SYS_DICTIONARY_ITEM                                 */
/*==============================================================*/
create table T_SYS_DICTIONARY_ITEM  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   DICTIONARY_ID        VARCHAR2(36),
   NAME                 VARCHAR2(255)                   not null,
   CODE                 VARCHAR2(64),
   VALUE                VARCHAR2(255),
   PARENT_ID            VARCHAR2(36),
   ORDER_NO             INTEGER,
   REMARK               VARCHAR2(255),
   constraint PK_T_SYS_DICTIONARY_ITEM primary key (ID)
);

comment on table T_SYS_DICTIONARY_ITEM is
'字典项表';

comment on column T_SYS_DICTIONARY_ITEM.ID is
'主键ID UUID';

comment on column T_SYS_DICTIONARY_ITEM.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_DICTIONARY_ITEM.VERSION is
'版本号';

comment on column T_SYS_DICTIONARY_ITEM.CREATE_USER is
'创建者';

comment on column T_SYS_DICTIONARY_ITEM.CREATE_TIME is
'创建时间';

comment on column T_SYS_DICTIONARY_ITEM.UPDATE_USER is
'更新者';

comment on column T_SYS_DICTIONARY_ITEM.UPDATE_TIME is
'更新时间';

comment on column T_SYS_DICTIONARY_ITEM.DICTIONARY_ID is
'字典ID';

comment on column T_SYS_DICTIONARY_ITEM.NAME is
'名称';

comment on column T_SYS_DICTIONARY_ITEM.CODE is
'编码';

comment on column T_SYS_DICTIONARY_ITEM.VALUE is
'参数值';

comment on column T_SYS_DICTIONARY_ITEM.PARENT_ID is
'父级ID';

comment on column T_SYS_DICTIONARY_ITEM.ORDER_NO is
'排序';

comment on column T_SYS_DICTIONARY_ITEM.REMARK is
'备注';

/*==============================================================*/
/* Table: T_SYS_LOG                                             */
/*==============================================================*/
create table T_SYS_LOG  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   TYPE                 CHAR(1),
   TITLE                VARCHAR2(512),
   USER_ID              VARCHAR2(36),
   IP                   VARCHAR2(64),
   OPER_TIME            DATE,
   MODULE               VARCHAR2(255),
   ACTION               VARCHAR2(255),
   BROWSER_TYPE         VARCHAR2(128),
   DEVICE_TYPE          VARCHAR2(128),
   USER_AGENT           VARCHAR2(255),
   ACTION_TIME          VARCHAR2(20),
   REMARK               VARCHAR2(2000),
   EXCEPTION            VARCHAR2(4000),
   LONGITUDE            NUMBER(10,6),
   LATITUDE             NUMBER(10,6),
   ACCURACY             NUMBER(12,2),
   USER_TYPE            CHAR(1),
   constraint PK_T_SYS_LOG primary key (ID)
);

comment on table T_SYS_LOG is
'审计日志';

comment on column T_SYS_LOG.ID is
'主键ID UUID';

comment on column T_SYS_LOG.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_LOG.VERSION is
'版本号';

comment on column T_SYS_LOG.CREATE_USER is
'创建者';

comment on column T_SYS_LOG.CREATE_TIME is
'创建时间';

comment on column T_SYS_LOG.UPDATE_USER is
'更新者';

comment on column T_SYS_LOG.UPDATE_TIME is
'更新时间';

comment on column T_SYS_LOG.TYPE is
'类型 安全:0 操作:1 访问:2 异常:3 API调用:A';

comment on column T_SYS_LOG.TITLE is
'标题';

comment on column T_SYS_LOG.USER_ID is
'登录名';

comment on column T_SYS_LOG.IP is
'IP';

comment on column T_SYS_LOG.OPER_TIME is
'操作开始时间';

comment on column T_SYS_LOG.MODULE is
'模块';

comment on column T_SYS_LOG.ACTION is
'操作方法';

comment on column T_SYS_LOG.BROWSER_TYPE is
'客户端（浏览器类型）';

comment on column T_SYS_LOG.DEVICE_TYPE is
'设备类型';

comment on column T_SYS_LOG.USER_AGENT is
'客户端信息';

comment on column T_SYS_LOG.ACTION_TIME is
'操作耗时ms';

comment on column T_SYS_LOG.REMARK is
'备注';

comment on column T_SYS_LOG.EXCEPTION is
'异常信息';

comment on column T_SYS_LOG.LONGITUDE is
'经度';

comment on column T_SYS_LOG.LATITUDE is
'经度';

comment on column T_SYS_LOG.ACCURACY is
'精度';

comment on column T_SYS_LOG.USER_TYPE is
'用户类型 用户：U；零售户：R；消费者：C；其它：O';

/*==============================================================*/
/* Table: T_SYS_LOG_HISTORY                                     */
/*==============================================================*/
create table T_SYS_LOG_HISTORY  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   TYPE                 CHAR(1),
   TITLE                VARCHAR2(512),
   USER_ID              VARCHAR2(36),
   IP                   VARCHAR2(64),
   OPER_TIME            DATE,
   MODULE               VARCHAR2(255),
   ACTION               VARCHAR2(255),
   BROWSER_TYPE         VARCHAR2(128),
   DEVICE_TYPE          VARCHAR2(128),
   USER_AGENT           VARCHAR2(255),
   ACTION_TIME          VARCHAR2(20),
   REMARK               VARCHAR2(2000),
   EXCEPTION            VARCHAR2(4000),
   LONGITUDE            NUMBER(10,6),
   LATITUDE             NUMBER(10,6),
   ACCURACY             NUMBER(12,2),
   USER_TYPE            CHAR(1),
   constraint PK_T_SYS_LOG_HISTORY primary key (ID)
);

comment on table T_SYS_LOG_HISTORY is
'审计日志历史';

comment on column T_SYS_LOG_HISTORY.ID is
'主键ID UUID';

comment on column T_SYS_LOG_HISTORY.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_LOG_HISTORY.VERSION is
'版本号';

comment on column T_SYS_LOG_HISTORY.CREATE_USER is
'创建者';

comment on column T_SYS_LOG_HISTORY.CREATE_TIME is
'创建时间';

comment on column T_SYS_LOG_HISTORY.UPDATE_USER is
'更新者';

comment on column T_SYS_LOG_HISTORY.UPDATE_TIME is
'更新时间';

comment on column T_SYS_LOG_HISTORY.TYPE is
'类型 安全:0 操作:1 访问:2 异常:3 API调用:A';

comment on column T_SYS_LOG_HISTORY.TITLE is
'标题';

comment on column T_SYS_LOG_HISTORY.USER_ID is
'登录名';

comment on column T_SYS_LOG_HISTORY.IP is
'IP';

comment on column T_SYS_LOG_HISTORY.OPER_TIME is
'操作开始时间';

comment on column T_SYS_LOG_HISTORY.MODULE is
'模块';

comment on column T_SYS_LOG_HISTORY.ACTION is
'操作方法';

comment on column T_SYS_LOG_HISTORY.BROWSER_TYPE is
'客户端（浏览器类型）';

comment on column T_SYS_LOG_HISTORY.DEVICE_TYPE is
'设备类型';

comment on column T_SYS_LOG_HISTORY.USER_AGENT is
'客户端信息';

comment on column T_SYS_LOG_HISTORY.ACTION_TIME is
'操作耗时ms';

comment on column T_SYS_LOG_HISTORY.REMARK is
'备注';

comment on column T_SYS_LOG_HISTORY.EXCEPTION is
'异常信息';

comment on column T_SYS_LOG_HISTORY.LONGITUDE is
'经度';

comment on column T_SYS_LOG_HISTORY.LATITUDE is
'经度';

comment on column T_SYS_LOG_HISTORY.ACCURACY is
'精度';

comment on column T_SYS_LOG_HISTORY.USER_TYPE is
'用户类型 用户：U；零售户：R；消费者：C；其它：O';

/*==============================================================*/
/* Table: T_SYS_ORGAN                                           */
/*==============================================================*/
create table T_SYS_ORGAN  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   NAME                 VARCHAR2(255)                   not null,
   SHORT_NAME           VARCHAR2(255),
   TYPE                 VARCHAR2(36),
   CODE                 VARCHAR2(36),
   SYS_CODE             VARCHAR2(36),
   SORT                 INTEGER,
   ADDRESS              VARCHAR2(255),
   MOBILE               VARCHAR2(64),
   PHONE                VARCHAR2(64),
   FAX                  VARCHAR2(64),
   MANAGER_USER_ID      VARCHAR2(36),
   DEPUTY_MANAGER_USER_ID VARCHAR2(128),
   SUPER_MANAGER_USER_ID VARCHAR2(36),
   PARENT_ID            VARCHAR2(36),
   PARENT_IDS           VARCHAR2(1024),
   AREA_ID              VARCHAR2(36),
   REMARK               VARCHAR2(255),
   constraint PK_T_SYS_ORGAN primary key (ID)
);

comment on table T_SYS_ORGAN is
'机构表';

comment on column T_SYS_ORGAN.ID is
'主键ID';

comment on column T_SYS_ORGAN.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_ORGAN.VERSION is
'版本号';

comment on column T_SYS_ORGAN.CREATE_USER is
'创建者';

comment on column T_SYS_ORGAN.CREATE_TIME is
'创建时间';

comment on column T_SYS_ORGAN.UPDATE_USER is
'更新者';

comment on column T_SYS_ORGAN.UPDATE_TIME is
'更新时间';

comment on column T_SYS_ORGAN.NAME is
'名称';

comment on column T_SYS_ORGAN.SHORT_NAME is
'简称';

comment on column T_SYS_ORGAN.TYPE is
'机构类型 机构(法人单位):0  部门:1 小组：2';

comment on column T_SYS_ORGAN.CODE is
'机构编码';

comment on column T_SYS_ORGAN.SYS_CODE is
'机构系统编码';

comment on column T_SYS_ORGAN.SORT is
'排序';

comment on column T_SYS_ORGAN.ADDRESS is
'地址';

comment on column T_SYS_ORGAN.MOBILE is
'电话号码';

comment on column T_SYS_ORGAN.PHONE is
'电话号码';

comment on column T_SYS_ORGAN.FAX is
'传真';

comment on column T_SYS_ORGAN.MANAGER_USER_ID is
'机构负责人ID';

comment on column T_SYS_ORGAN.DEPUTY_MANAGER_USER_ID is
'副主管';

comment on column T_SYS_ORGAN.SUPER_MANAGER_USER_ID is
'分管领导';

comment on column T_SYS_ORGAN.PARENT_ID is
'父级ID';

comment on column T_SYS_ORGAN.PARENT_IDS is
'上级ID集合';

comment on column T_SYS_ORGAN.AREA_ID is
'区域ID';

comment on column T_SYS_ORGAN.REMARK is
'备注';

/*==============================================================*/
/* Index: "Index_11"                                            */
/*==============================================================*/
create index "Index_11" on T_SYS_ORGAN (
   CODE ASC,
   SYS_CODE ASC
);

/*==============================================================*/
/* Table: T_SYS_ORGAN_EXTEND                                    */
/*==============================================================*/
create table T_SYS_ORGAN_EXTEND  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   NAME                 VARCHAR2(255)                   not null,
   SHORT_NAME           VARCHAR2(255),
   TYPE                 VARCHAR2(36),
   CODE                 VARCHAR2(64),
   SYS_CODE             VARCHAR2(64),
   SORT                 INTEGER,
   ADDRESS              VARCHAR2(255),
   MOBILE               VARCHAR2(64),
   PHONE                VARCHAR2(64),
   FAX                  VARCHAR2(64),
   MANAGER_USER_ID      VARCHAR2(36),
   DEPUTY_MANAGER_USER_ID VARCHAR2(128),
   SUPER_MANAGER_USER_ID VARCHAR2(36),
   PARENT_ID            VARCHAR2(36),
   PARENT_IDS           VARCHAR2(1024),
   AREA_ID              VARCHAR2(36),
   REMARK               VARCHAR2(255),
   TREE_LEVEL           INTEGER,
   COMPANY_ID           VARCHAR2(36),
   COMPANY_CODE         VARCHAR2(64),
   HOME_COMPANY_ID      VARCHAR2(36),
   HOME_COMPANY_CODE    VARCHAR2(64),
   constraint PK_T_SYS_ORGAN_EXTEND primary key (ID)
);

comment on table T_SYS_ORGAN_EXTEND is
'机构扩展表';

comment on column T_SYS_ORGAN_EXTEND.ID is
'主键ID';

comment on column T_SYS_ORGAN_EXTEND.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_ORGAN_EXTEND.VERSION is
'版本号';

comment on column T_SYS_ORGAN_EXTEND.CREATE_USER is
'创建者';

comment on column T_SYS_ORGAN_EXTEND.CREATE_TIME is
'创建时间';

comment on column T_SYS_ORGAN_EXTEND.UPDATE_USER is
'更新者';

comment on column T_SYS_ORGAN_EXTEND.UPDATE_TIME is
'更新时间';

comment on column T_SYS_ORGAN_EXTEND.NAME is
'名称';

comment on column T_SYS_ORGAN_EXTEND.SHORT_NAME is
'简称';

comment on column T_SYS_ORGAN_EXTEND.TYPE is
'机构类型 机构(法人单位):0  部门:1 小组：2';

comment on column T_SYS_ORGAN_EXTEND.CODE is
'机构编码';

comment on column T_SYS_ORGAN_EXTEND.SYS_CODE is
'机构系统编码';

comment on column T_SYS_ORGAN_EXTEND.SORT is
'排序';

comment on column T_SYS_ORGAN_EXTEND.ADDRESS is
'地址';

comment on column T_SYS_ORGAN_EXTEND.MOBILE is
'电话号码';

comment on column T_SYS_ORGAN_EXTEND.PHONE is
'电话号码';

comment on column T_SYS_ORGAN_EXTEND.FAX is
'传真';

comment on column T_SYS_ORGAN_EXTEND.MANAGER_USER_ID is
'机构负责人ID';

comment on column T_SYS_ORGAN_EXTEND.DEPUTY_MANAGER_USER_ID is
'副主管';

comment on column T_SYS_ORGAN_EXTEND.SUPER_MANAGER_USER_ID is
'分管领导';

comment on column T_SYS_ORGAN_EXTEND.PARENT_ID is
'父级ID';

comment on column T_SYS_ORGAN_EXTEND.PARENT_IDS is
'上级ID集合';

comment on column T_SYS_ORGAN_EXTEND.AREA_ID is
'区域ID';

comment on column T_SYS_ORGAN_EXTEND.REMARK is
'备注';

comment on column T_SYS_ORGAN_EXTEND.TREE_LEVEL is
'层级';

comment on column T_SYS_ORGAN_EXTEND.COMPANY_ID is
'单位ID';

comment on column T_SYS_ORGAN_EXTEND.COMPANY_CODE is
'单位编码';

comment on column T_SYS_ORGAN_EXTEND.HOME_COMPANY_ID is
'上级单位ID';

comment on column T_SYS_ORGAN_EXTEND.HOME_COMPANY_CODE is
'上级单位编码';

/*==============================================================*/
/* Index: "Index_17"                                            */
/*==============================================================*/
create index "Index_17" on T_SYS_ORGAN_EXTEND (
   CODE ASC,
   SYS_CODE ASC
);

/*==============================================================*/
/* Table: T_SYS_POST                                            */
/*==============================================================*/
create table T_SYS_POST  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   NAME                 VARCHAR2(100)                   not null,
   CODE                 VARCHAR2(36),
   SORT                 INTEGER,
   REMARK               VARCHAR2(255),
   ORGAN_ID             VARCHAR2(36),
   constraint PK_T_SYS_POST primary key (ID)
);

comment on table T_SYS_POST is
'岗位表';

comment on column T_SYS_POST.ID is
'主键ID';

comment on column T_SYS_POST.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_POST.VERSION is
'版本号';

comment on column T_SYS_POST.CREATE_USER is
'创建者';

comment on column T_SYS_POST.CREATE_TIME is
'创建时间';

comment on column T_SYS_POST.UPDATE_USER is
'更新者';

comment on column T_SYS_POST.UPDATE_TIME is
'更新时间';

comment on column T_SYS_POST.NAME is
'岗位名称';

comment on column T_SYS_POST.CODE is
'编码';

comment on column T_SYS_POST.SORT is
'排序';

comment on column T_SYS_POST.REMARK is
'备注';

comment on column T_SYS_POST.ORGAN_ID is
'机构ID';

/*==============================================================*/
/* Table: T_SYS_POST_ORGAN                                      */
/*==============================================================*/
create table T_SYS_POST_ORGAN  (
   ORGAN_ID             VARCHAR2(36),
   POST_ID              VARCHAR2(36)
);

comment on table T_SYS_POST_ORGAN is
'岗位附属机构表';

comment on column T_SYS_POST_ORGAN.ORGAN_ID is
'机构ID';

comment on column T_SYS_POST_ORGAN.POST_ID is
'岗位ID';

/*==============================================================*/
/* Table: T_SYS_RESOURCE                                        */
/*==============================================================*/
create table T_SYS_RESOURCE  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   NAME                 VARCHAR2(255)                   not null,
   CODE                 VARCHAR2(64),
   SORT                 INTEGER,
   URL                  VARCHAR2(512),
   ICON_CLS             VARCHAR2(128),
   ICON                 VARCHAR2(255),
   MARK_URL             VARCHAR2(1024),
   TYPE                 VARCHAR2(36),
   PARENT_ID            VARCHAR2(36),
   PARENT_IDS           VARCHAR2(1024),
   constraint PK_T_SYS_RESOURCE primary key (ID)
);

comment on table T_SYS_RESOURCE is
'资源表';

comment on column T_SYS_RESOURCE.ID is
'主键ID UUID';

comment on column T_SYS_RESOURCE.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_RESOURCE.VERSION is
'版本号';

comment on column T_SYS_RESOURCE.CREATE_USER is
'创建者';

comment on column T_SYS_RESOURCE.CREATE_TIME is
'创建时间';

comment on column T_SYS_RESOURCE.UPDATE_USER is
'更新者';

comment on column T_SYS_RESOURCE.UPDATE_TIME is
'更新时间';

comment on column T_SYS_RESOURCE.NAME is
'资源名称';

comment on column T_SYS_RESOURCE.CODE is
'资源编码';

comment on column T_SYS_RESOURCE.SORT is
'排序';

comment on column T_SYS_RESOURCE.URL is
'资源url地址';

comment on column T_SYS_RESOURCE.ICON_CLS is
'图标';

comment on column T_SYS_RESOURCE.ICON is
'图标地址';

comment on column T_SYS_RESOURCE.MARK_URL is
'标记URL';

comment on column T_SYS_RESOURCE.TYPE is
'资源类型 应用：10 菜单：0 功能：1 栏目：21';

comment on column T_SYS_RESOURCE.PARENT_ID is
'父级ID';

comment on column T_SYS_RESOURCE.PARENT_IDS is
'父级ID集合';

/*==============================================================*/
/* Index: "Index_12"                                            */
/*==============================================================*/
create index "Index_12" on T_SYS_RESOURCE (
   CODE ASC
);

/*==============================================================*/
/* Table: T_SYS_ROLE                                            */
/*==============================================================*/
create table T_SYS_ROLE  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   NAME                 VARCHAR2(255)                   not null,
   CODE                 VARCHAR2(36),
   ORGAN_ID             VARCHAR2(36),
   IS_SYSTEM            CHAR(1),
   IS_ACTIVITY          CHAR(1),
   ROLE_TYPE            VARCHAR2(36),
   DATA_SCOPE           CHAR(1),
   REMARK               VARCHAR2(255),
   constraint PK_T_SYS_ROLE primary key (ID),
   constraint AK_KEY_2_T_SYS_RO unique (NAME)
);

comment on table T_SYS_ROLE is
'角色表';

comment on column T_SYS_ROLE.ID is
'主键ID';

comment on column T_SYS_ROLE.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_ROLE.VERSION is
'版本号';

comment on column T_SYS_ROLE.CREATE_USER is
'创建者';

comment on column T_SYS_ROLE.CREATE_TIME is
'创建时间';

comment on column T_SYS_ROLE.UPDATE_USER is
'更新者';

comment on column T_SYS_ROLE.UPDATE_TIME is
'更新时间';

comment on column T_SYS_ROLE.NAME is
'名称';

comment on column T_SYS_ROLE.CODE is
'编码';

comment on column T_SYS_ROLE.ORGAN_ID is
'所属机构ID';

comment on column T_SYS_ROLE.IS_SYSTEM is
'系统角色：（1/0 是/否）';

comment on column T_SYS_ROLE.IS_ACTIVITY is
'是否有效 （1/0 是 否）';

comment on column T_SYS_ROLE.ROLE_TYPE is
'权限类型 任务分配:assignment 管理角色:security-role 普通角色:user';

comment on column T_SYS_ROLE.DATA_SCOPE is
'数据范围 所有数据:1 所在公司及以下数据:2 所在公司数据:3 所在部门及以下数据:4 所在部门数据:5 仅本人数据:8 按明细设置:9';

comment on column T_SYS_ROLE.REMARK is
'备注';

/*==============================================================*/
/* Table: T_SYS_ROLE_ORGAN                                      */
/*==============================================================*/
create table T_SYS_ROLE_ORGAN  (
   ORGAN_ID             VARCHAR2(36),
   ROLE_ID              VARCHAR2(36)
);

comment on table T_SYS_ROLE_ORGAN is
'角色授权机构表';

comment on column T_SYS_ROLE_ORGAN.ORGAN_ID is
'机构ID';

comment on column T_SYS_ROLE_ORGAN.ROLE_ID is
'角色ID';

/*==============================================================*/
/* Table: T_SYS_ROLE_RESOURCE                                   */
/*==============================================================*/
create table T_SYS_ROLE_RESOURCE  (
   RESOURCE_ID          VARCHAR2(36),
   ROLE_ID              VARCHAR2(36)
);

comment on table T_SYS_ROLE_RESOURCE is
'角色资源表';

comment on column T_SYS_ROLE_RESOURCE.RESOURCE_ID is
'资源ID';

comment on column T_SYS_ROLE_RESOURCE.ROLE_ID is
'角色ID';

/*==============================================================*/
/* Table: T_SYS_SERIAL_NUMBER                                   */
/*==============================================================*/
create table T_SYS_SERIAL_NUMBER  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   MODULE_NAME          VARCHAR2(255),
   MODULE_CODE          VARCHAR2(64),
   CONFIG_TEMPLATE      VARCHAR2(128),
   MAX_SERIAL           VARCHAR2(128),
   IS_AUTO_INCREMENT    CHAR(1),
   PRE_MAX_NUM          VARCHAR2(36),
   REMARK               VARCHAR2(255),
   YEAR_CLEAR           CHAR(1),
   constraint PK_T_SYS_SERIAL_NUMBER primary key (ID)
);

comment on table T_SYS_SERIAL_NUMBER is
'序列号';

comment on column T_SYS_SERIAL_NUMBER.ID is
'主键ID UUID';

comment on column T_SYS_SERIAL_NUMBER.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_SERIAL_NUMBER.VERSION is
'版本号';

comment on column T_SYS_SERIAL_NUMBER.CREATE_USER is
'创建者';

comment on column T_SYS_SERIAL_NUMBER.CREATE_TIME is
'创建时间';

comment on column T_SYS_SERIAL_NUMBER.UPDATE_USER is
'更新者';

comment on column T_SYS_SERIAL_NUMBER.UPDATE_TIME is
'更新时间';

comment on column T_SYS_SERIAL_NUMBER.MODULE_NAME is
'模块名称';

comment on column T_SYS_SERIAL_NUMBER.MODULE_CODE is
'模块编码';

comment on column T_SYS_SERIAL_NUMBER.CONFIG_TEMPLATE is
'流水号配置模板';

comment on column T_SYS_SERIAL_NUMBER.MAX_SERIAL is
'系列号最大值';

comment on column T_SYS_SERIAL_NUMBER.IS_AUTO_INCREMENT is
'是否自动增长标识';

comment on column T_SYS_SERIAL_NUMBER.PRE_MAX_NUM is
'预生成流水号数量';

comment on column T_SYS_SERIAL_NUMBER.REMARK is
'备注';

comment on column T_SYS_SERIAL_NUMBER.YEAR_CLEAR is
'年底清零 是：1 否：0';

/*==============================================================*/
/* Table: T_SYS_USER                                            */
/*==============================================================*/
create table T_SYS_USER  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   LOGIN_NAME           VARCHAR2(36)                    not null,
   CODE                 VARCHAR2(64),
   ORIGINAL_PASSWORD    VARCHAR2(128),
   PASSWORD             VARCHAR2(128),
   USER_TYPE            VARCHAR2(36),
   NAME                 VARCHAR2(36),
   SORT                 INTEGER,
   SEX                  VARCHAR2(36),
   BIRTHDAY             DATE,
   PHOTO                VARCHAR2(512),
   EMAIL                VARCHAR2(64),
   PERSON_EMAIL         VARCHAR2(64),
   MOBILE               VARCHAR2(36),
   TEL                  VARCHAR2(36),
   WEIXIN               VARCHAR2(64),
   QQ                   VARCHAR2(36),
   ADDRESS              VARCHAR2(255),
   DEFAULT_ORGAN_ID     VARCHAR2(36),
   REMARK               VARCHAR2(255),
   constraint PK_T_SYS_USER primary key (ID)
);

comment on table T_SYS_USER is
'用户表';

comment on column T_SYS_USER.ID is
'主键ID';

comment on column T_SYS_USER.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_USER.VERSION is
'版本号';

comment on column T_SYS_USER.CREATE_USER is
'创建者';

comment on column T_SYS_USER.CREATE_TIME is
'创建时间';

comment on column T_SYS_USER.UPDATE_USER is
'更新者';

comment on column T_SYS_USER.UPDATE_TIME is
'更新时间';

comment on column T_SYS_USER.LOGIN_NAME is
'登录名';

comment on column T_SYS_USER.CODE is
'员工编号';

comment on column T_SYS_USER.ORIGINAL_PASSWORD is
'原始密码';

comment on column T_SYS_USER.PASSWORD is
'登录密码';

comment on column T_SYS_USER.USER_TYPE is
'用户类型 （0：系统；1：员工；2：应用集成平台）';

comment on column T_SYS_USER.NAME is
'姓名';

comment on column T_SYS_USER.SORT is
'排序号';

comment on column T_SYS_USER.SEX is
'性别 女(0) 男(1) 保密(2) 默认：保密';

comment on column T_SYS_USER.BIRTHDAY is
'出生日期 格式：yyyy-MM-dd';

comment on column T_SYS_USER.PHOTO is
'头像';

comment on column T_SYS_USER.EMAIL is
'邮箱地址';

comment on column T_SYS_USER.PERSON_EMAIL is
'个人邮箱';

comment on column T_SYS_USER.MOBILE is
'手机号';

comment on column T_SYS_USER.TEL is
'办公电话';

comment on column T_SYS_USER.WEIXIN is
'微信号';

comment on column T_SYS_USER.QQ is
'QQ';

comment on column T_SYS_USER.ADDRESS is
'住址';

comment on column T_SYS_USER.DEFAULT_ORGAN_ID is
'默认组织机构ID';

comment on column T_SYS_USER.REMARK is
'备注';

/*==============================================================*/
/* Index: "Index_13"                                            */
/*==============================================================*/
create index "Index_13" on T_SYS_USER (
   LOGIN_NAME ASC
);

/*==============================================================*/
/* Table: T_SYS_USER_ORGAN                                      */
/*==============================================================*/
create table T_SYS_USER_ORGAN  (
   ORGAN_ID             VARCHAR2(36),
   USER_ID              VARCHAR2(36)
);

comment on table T_SYS_USER_ORGAN is
'用户机构表';

comment on column T_SYS_USER_ORGAN.ORGAN_ID is
'机构ID';

comment on column T_SYS_USER_ORGAN.USER_ID is
'用户ID';

/*==============================================================*/
/* Table: T_SYS_USER_PASSWORD                                   */
/*==============================================================*/
create table T_SYS_USER_PASSWORD  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   USER_ID              VARCHAR2(36),
   MODIFY_TIME          DATE,
   ORIGINAL_PASSWORD    VARCHAR2(128),
   PASSWORD             VARCHAR2(128),
   constraint PK_T_SYS_USER_PASSWORD primary key (ID)
);

comment on table T_SYS_USER_PASSWORD is
'用户密码修改记录';

comment on column T_SYS_USER_PASSWORD.ID is
'主键ID UUID';

comment on column T_SYS_USER_PASSWORD.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_USER_PASSWORD.VERSION is
'版本号';

comment on column T_SYS_USER_PASSWORD.CREATE_USER is
'创建者';

comment on column T_SYS_USER_PASSWORD.CREATE_TIME is
'创建时间';

comment on column T_SYS_USER_PASSWORD.UPDATE_USER is
'更新者';

comment on column T_SYS_USER_PASSWORD.UPDATE_TIME is
'更新时间';

comment on column T_SYS_USER_PASSWORD.USER_ID is
'用户ID';

comment on column T_SYS_USER_PASSWORD.MODIFY_TIME is
'修改时间';

comment on column T_SYS_USER_PASSWORD.ORIGINAL_PASSWORD is
'原始密码';

comment on column T_SYS_USER_PASSWORD.PASSWORD is
'密码';

/*==============================================================*/
/* Table: T_SYS_USER_POST                                       */
/*==============================================================*/
create table T_SYS_USER_POST  (
   USER_ID              VARCHAR2(36),
   POST_ID              VARCHAR2(36),
   ORGAN_ID             VARCHAR2(36)
);

comment on table T_SYS_USER_POST is
'用户岗位表';

comment on column T_SYS_USER_POST.USER_ID is
'用户ID';

comment on column T_SYS_USER_POST.POST_ID is
'岗位ID';

comment on column T_SYS_USER_POST.ORGAN_ID is
'机构ID';

/*==============================================================*/
/* Table: T_SYS_USER_RESOURCE                                   */
/*==============================================================*/
create table T_SYS_USER_RESOURCE  (
   USER_ID              VARCHAR2(36),
   RESOURCE_ID          VARCHAR2(36)
);

comment on table T_SYS_USER_RESOURCE is
'用户资源表';

comment on column T_SYS_USER_RESOURCE.USER_ID is
'用户ID';

comment on column T_SYS_USER_RESOURCE.RESOURCE_ID is
'资源ID';

/*==============================================================*/
/* Table: T_SYS_USER_ROLE                                       */
/*==============================================================*/
create table T_SYS_USER_ROLE  (
   USER_ID              VARCHAR2(36),
   ROLE_ID              VARCHAR2(36)
);

comment on table T_SYS_USER_ROLE is
'用户角色表';

comment on column T_SYS_USER_ROLE.USER_ID is
'用户ID';

comment on column T_SYS_USER_ROLE.ROLE_ID is
'角色ID';

/*==============================================================*/
/* Table: T_SYS_VERSION_LOG                                     */
/*==============================================================*/
create table T_SYS_VERSION_LOG  (
   ID                   VARCHAR2(36)                    not null,
   STATUS               CHAR(1),
   VERSION              INTEGER,
   CREATE_USER          VARCHAR2(36),
   CREATE_TIME          DATE,
   UPDATE_USER          VARCHAR2(36),
   UPDATE_TIME          DATE,
   VERSION_NAME         VARCHAR2(128),
   VERSION_CODE         VARCHAR2(128),
   VERSION_LOG_TYPE     VARCHAR2(36),
   FILE_ID              VARCHAR2(36),
   APP                  VARCHAR2(36),
   IS_PUB               CHAR(1),
   IS_TIP               CHAR(1),
   REMARK               VARCHAR2(4000),
   PUB_TIME             DATE,
   constraint PK_T_SYS_VERSION_LOG primary key (ID)
);

comment on table T_SYS_VERSION_LOG is
'审系统更新日志';

comment on column T_SYS_VERSION_LOG.ID is
'主键ID UUID';

comment on column T_SYS_VERSION_LOG.STATUS is
'状态 正常/删除/审核/锁定 0/1/2/3';

comment on column T_SYS_VERSION_LOG.VERSION is
'版本号';

comment on column T_SYS_VERSION_LOG.CREATE_USER is
'创建者';

comment on column T_SYS_VERSION_LOG.CREATE_TIME is
'创建时间';

comment on column T_SYS_VERSION_LOG.UPDATE_USER is
'更新者';

comment on column T_SYS_VERSION_LOG.UPDATE_TIME is
'更新时间';

comment on column T_SYS_VERSION_LOG.VERSION_NAME is
'版本号名称';

comment on column T_SYS_VERSION_LOG.VERSION_CODE is
'版本编码';

comment on column T_SYS_VERSION_LOG.VERSION_LOG_TYPE is
'版本类型 服务器应用：0 Android应用：1 iPhone应用：2 iPhone下载：3';

comment on column T_SYS_VERSION_LOG.FILE_ID is
'附件ID';

comment on column T_SYS_VERSION_LOG.APP is
'APP标识';

comment on column T_SYS_VERSION_LOG.IS_PUB is
'是否发布 1：是； 0：否';

comment on column T_SYS_VERSION_LOG.IS_TIP is
'是否提示  1：是； 0：否';

comment on column T_SYS_VERSION_LOG.REMARK is
'更新说明';

comment on column T_SYS_VERSION_LOG.PUB_TIME is
'发布时间';

alter table T_NOTICE_FILE
   add constraint FK_T_NOTICE_REFERENCE_T_NOTIC2 foreign key (NOTICE_ID)
      references T_NOTICE (ID);

alter table T_NOTICE_RECEIVE_INFO
   add constraint FK_T_NOTICE_REFERENCE_T_NOTICE foreign key (NOTICE_ID)
      references T_NOTICE (ID);

alter table T_NOTICE_SEND_INFO
   add constraint FK_T_NOTICE_REFERENCE_T_NOTIC3 foreign key (NOTICE_ID)
      references T_NOTICE (ID);

alter table T_SYS_ROLE_RESOURCE
   add constraint FK_T_SYS_RO_REFERENCE_T_SYS_RO foreign key (ROLE_ID)
      references T_SYS_ROLE (ID);

alter table T_SYS_ROLE_RESOURCE
   add constraint FK_T_SYS_RO_REFERENCE_T_SYS_RE foreign key (RESOURCE_ID)
      references T_SYS_RESOURCE (ID);

alter table T_SYS_USER_POST
   add constraint FK_T_SYS_US_REFERENCE_T_SYS_PO foreign key (POST_ID)
      references T_SYS_POST (ID);

alter table T_SYS_USER_POST
   add constraint FK_T_SYS_US_REFERENCE_T_SYS_US foreign key (USER_ID)
      references T_SYS_USER (ID);

alter table T_SYS_USER_ROLE
   add constraint FK_T_SYS_US_REFERENCE_T_SYS_RO foreign key (ROLE_ID)
      references T_SYS_ROLE (ID);

alter table T_SYS_USER_ROLE
   add constraint FK_T_SYS_US_REFERENCE_T_SYS_U2 foreign key (USER_ID)
      references T_SYS_USER (ID);

