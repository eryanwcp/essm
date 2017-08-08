<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="replace" type="java.lang.String" required="true" description="需要替换的textarea编号"%>
<%@ attribute name="uploadPath" type="java.lang.String" required="false" description="文件上传路径，路径后自动添加年份。若不指定，则编辑器不可上传文件"%>
<%@ attribute name="height" type="java.lang.String" required="false" description="编辑器高度"%>
<script type="text/javascript">include('ckeditor_lib','${ctxStatic}/js/ckeditor/',['ckeditor.js']);</script>
<script type="text/javascript">
	var ${replace}Ckeditor = CKEDITOR.replace("${replace}");
	${replace}Ckeditor.config.height = "${height}";//<c:if test="${not empty uploadPath}">
	${replace}Ckeditor.config.ckfinderPath="${ctxStatic}/js/ckfinder";

	//是否强制复制来的内容去除格式 plugins/pastetext/plugin.js
	${replace}Ckeditor.config.forcePasteAsPlainText =false;//不去除
	//是否使用等标签修饰或者代替从word文档中粘贴过来的内容 plugins/pastefromword/plugin.js
	${replace}Ckeditor.config.pasteFromWordKeepsStructure = false;
	//从word中粘贴内容时是否移除格式 plugins/pastefromword/plugin.js
	${replace}Ckeditor.config.pasteFromWordRemoveStyle = false
	${replace}Ckeditor.config.pasteFromWordRemoveFontStyles = false;
	//是否使用完整的html编辑模式 如使用，其源码将包含：<html><body></body></html>等标签
	${replace}Ckeditor.config.fullPage = true;

	//重新设置字体
	${replace}Ckeditor.config.fontSize_sizes = '初号/56px;小初/48px;一号/34px;小一/32px;二号/29px;小二/24px;三号/21px;小三/20px;四号/18px;小四/16px;五号/14px;小五/12px;六号/10px;小六/8px;';

	var date = new Date(), year = date.getFullYear(), month = (date.getMonth()+1)>9?date.getMonth()+1:"0"+(date.getMonth()+1);
	${replace}Ckeditor.config.ckfinderUploadPath="${uploadPath}/"+year+"/"+month+"/";//</c:if>
</script>