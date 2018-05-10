var roleIds = roleIds;

var $roleCombobox;

$(function() {
    loadUserRole();
});
// 加載用户角色信息
function loadUserRole() {
    $roleCombobox = $('#user_role_form-roleIds').combobox({
        url: ctxAdmin + '/sys/role/combobox',
        multiple: true,
        editable:false
    });
    if(roleIds){
        $roleCombobox.combobox("setValues",roleIds);
    }
}