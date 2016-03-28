CREATE TABLE Document (
	DocumentID INT(10) NOT NULL auto_increment,
	RecordID varchar (16) NULL ,
	Template varchar (16) NULL ,
	Subject varchar (254) NULL ,
	Author varchar (64) NULL ,
	FileDate datetime NULL ,
	FileType varchar (50) NULL ,
	HtmlPath varchar (128) NULL ,
	Status varchar (4) NULL ,
	Copies INT(10) NULL ,
	DLCount INT(10) NULL ,
	PRIMARY KEY (DocumentID)
) ;

CREATE TABLE Document_File (
	FileID INT(10) NOT NULL auto_increment,
	RecordID varchar (16) NULL ,
	FileName varchar (254) NULL ,
	FileType varchar (4) NULL ,
	FileSize INT(10) NULL ,
	FileDate datetime NULL ,
	FileBody LONGBLOB NULL ,
	FilePath varchar (128) NULL ,
	UserName varchar (64) NULL ,
	Descript varchar (255) NULL ,
	PRIMARY KEY (FileID)
) ;

CREATE TABLE Version_File (
	FileID INT(10) NOT NULL auto_increment,
	RecordID varchar (16) NULL ,
	FileName varchar (254) NULL ,
	FileType varchar (4) NULL ,
	FileSize INT(10) NULL ,
	FileDate datetime NULL ,
	FileBody LONGBLOB NULL ,
	FilePath varchar (128) NULL ,
	UserName varchar (64) NULL ,
	Descript varchar (255) NULL ,
	PRIMARY KEY (FileID)
) ;

CREATE TABLE Document_Signature (
	SignatureID INT(10) NOT NULL auto_increment,
	RecordID varchar (50) NULL ,
	MarkName varchar (64) NULL ,
	UserName varchar (64) NULL ,
	DateTime datetime NULL ,
	HostName varchar (50) NULL ,
	MarkGuid varchar (128) NULL ,
	PRIMARY KEY (SignatureID)
) ;

CREATE TABLE Signature(
	SignatureID INT(10) NOT NULL auto_increment,
	UserName VARCHAR (64) NULL ,
	PassWord VARCHAR (64) NULL ,
	MarkName VARCHAR (250) NULL ,
	MarkType VARCHAR (50) NULL ,
	MarkBody LONGBLOB NULL ,
	MarkPath VARCHAR (250) NULL ,
	MarkSize INT(10) NULL ,
	MarkDate DATETIME NULL ,
	PRIMARY KEY  (SignatureID)
) ;

CREATE TABLE BookMarks (
	BookMarkID INT(10) NOT NULL auto_increment,
	BookMarkName varchar(64) NULL ,
	BookMarkDesc varchar(128) NULL ,
	BookMarkText varchar(200) NULL ,
	PRIMARY KEY (BookMarkID)
) ;

CREATE TABLE Template_File (
	TemplateID INT(10) NOT NULL auto_increment,
	RecordID varchar (16) NOT NULL ,
	FileName varchar (254) NULL ,
	FileType varchar (50) NULL ,
	FileSize INT(10) NULL ,
	FileDate datetime NULL ,
	FileBody LONGBLOB NULL ,
	FilePath varchar (255) NULL ,
	UserName varchar (64) NULL ,
	Descript varchar (255) NULL ,
	PRIMARY KEY (TemplateID)
) ;

CREATE TABLE Template_BookMarks (
	BookMarkID INT(10) NOT NULL auto_increment,
	RecordID varchar (50) NULL ,
	BookMarkName varchar (120) NULL ,
	PRIMARY KEY (BookMarkID)
) ;

Insert Into BookMarks (BookMarkID,BookMarkName,BookMarkDesc,BookMarkText) values (1,'Caption','[请输入标题]','关于信息化建设的题案');
Insert Into BookMarks (BookMarkID,BookMarkName,BookMarkDesc,BookMarkText) values (2,'Type','[请输入类型]','科普');
Insert Into BookMarks (BookMarkID,BookMarkName,BookMarkDesc,BookMarkText) values (3,'Item','[请输入专题]','专题');
Insert Into BookMarks (BookMarkID,BookMarkName,BookMarkDesc,BookMarkText) values (4,'KeyWord','[请输入关键字]','电脑、拓扑');
Insert Into BookMarks (BookMarkID,BookMarkName,BookMarkDesc,BookMarkText) values (5,'Author','[请输入作者]','作者');
Insert Into BookMarks (BookMarkID,BookMarkName,BookMarkDesc,BookMarkText) values (6,'Sec','[公文密机]','绝密');
Insert Into BookMarks (BookMarkID,BookMarkName,BookMarkDesc,BookMarkText) values (7,'Subject','[公文主题词]','计算机、信息、网络');
Insert Into BookMarks (BookMarkID,BookMarkName,BookMarkDesc,BookMarkText) values (8,'Content','[公文内容]','公文内容：请详细填写');
Insert Into BookMarks (BookMarkID,BookMarkName,BookMarkDesc,BookMarkText) values (9,'Title','[公文标题]','江西省南昌市XXXX公文');
Insert Into BookMarks (BookMarkID,BookMarkName,BookMarkDesc,BookMarkText) values (10,'Description','[公文说明或描叙]','内部公文');
