<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var modelId = '${model.id}';
    var modelStatus = '${model.status}';
    var jsessionid = '<%=session.getId()%>';
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/user-input${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="user_form" class="dialog-form" method="post" novalidate>
        <input type="hidden" id="id" name="id" value="${model.id}"/>
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>
        <div>
            <label>所属组织机构:</label>
            <input type="select" id="defaultOrganId" name="defaultOrganId" value="${model.defaultOrganId}"
                   style="width: 260px;height: 28px;"/>
        </div>
        <div>
            <label>登录名:</label>
            <input type="text" id="loginName" name="loginName" maxLength="36" value="${model.loginName}"
                   class="easyui-validatebox textbox"
                   data-options="required:true,missingMessage:'请输入登录名.',validType:['minLength[1]']"/>
        </div>
        <div id="password_div">
            <label>密码:</label>
            <input type="password" id="password"
                   name="password" class="easyui-validatebox textbox" maxLength="36"
                   data-options="required:true,missingMessage:'请输入密码.',validType:['minLength[1]']">
        </div>
        <div id="repassword_div">
            <label>确认密码:</label>
            <input type="password" id="repassword"
                   name="repassword" class="easyui-validatebox textbox" required="true"
                   missingMessage="请再次填写密码." validType="equalTo['#password']"
                   invalidMessage="两次输入密码不匹配.">
        </div>

        <div>
            <label>姓名:</label>
            <input name="name" type="text" maxLength="12" value="${model.name}" class="easyui-validatebox textbox"
                   data-options="required:true,missingMessage:'请输入登录名.',validType:['CHS','length[2,6]']" />
        </div>
        <div>
            <label>公司邮箱:</label>
            <input name="email" type="text" value="${model.email}"  class="easyui-validatebox textbox" validType="email" maxLength="64" />
        </div>
        <div>
            <label>办公电话:</label>
            <input name="tel" type="text"  value="${model.tel}"  class="easyui-validatebox textbox" validType="phone">
        </div>
        <div>
            <label>头像:</label>
            <input id="photo" name="photo" readonly="readonly" value="${model.photo}" style="display: none" />
            <img id="photo_pre" class="img-rounded" src="" alt="头像" style="width: 64px; height: 64px;">
            <input id="file" name="file"  multiple="true" >
        </div>
        <div>
            <label>性别:</label>
            <input id="sex" name="sex" value="${model.sex}"  style="width: 120px;height: 28px;" />
        </div>
        <div>
            <label>出生日期:</label>
            <input id="birthday" name="birthday" value="${model.birthday}"  type="text" class="easyui-my97" />
        </div>
        <div>
            <label>手机号:</label>
            <input name="mobile" type="text" value="${model.mobile}" class="easyui-validatebox textbox" validType="mobile">
        </div>
        <div>
            <label>QQ:</label>
            <input name="qq" type="text" value="${model.qq}" class="easyui-numberbox textbox" validType="QQ" maxLength="64" style="height: 28px;" />
        </div>
        <div>
            <label>个人邮箱:</label>
            <input name="personalEmail" type="text" value="${model.personalEmail}" class="easyui-validatebox textbox" validType="email" maxLength="64" />
        </div>
        <div>
            <label>地址:</label>
            <input name="address" type="text" value="${model.address}" class="easyui-validatebox textbox" validType="legalInput" maxLength="255" />
        </div>
        <div>
            <label >备注:</label>
            <input name="remark" maxLength="1000" value="${model.remark}"  class="easyui-textbox" data-options="multiline:true" style="width:260px;height:75px;">
        </div>
        <div>
            <label>排序:</label>
            <input type="text" id="orderNo" name="orderNo" value="${model.orderNo}"  class="easyui-numberspinner"
                   data-options="min:1,max:99999999,size:9,maxlength:9,height:28" />
        </div>
        <div>
            <label>状态:</label>
            <label style="text-align: left;width: 60px;">
                <input type="radio" name="status" style="width: 20px;" value="0" /> 启用
            </label>
            <label style="text-align: left;width: 60px;">
                <input type="radio" name="status" style="width: 20px;" value="3" /> 停用
            </label>
        </div>
    </form>
</div>