$(function() {
    loadUserRole();
});
// 加載用户角色信息
function loadUserRole() {
    $('#user_role_form-roleIds').combobox({
        url: ctxAdmin + '/sys/role/combobox',
        multiple: true,
        editable:false
    });
}