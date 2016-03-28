var modelId = modelId;
var modelReceiverEncryptionType = modelReceiverEncryptionType;
var modelSenderEncryptionType = modelSenderEncryptionType;
var modelActivate = modelActivate;
var modelReceiverProtocol = modelReceiverProtocol;


//端口默认值
var receiverPort=$("input[name='receiverPort']");
var senderPort=$("input[name='senderPort']");
var receiverEncryptionType=$("#receiverEncryptionType");
var senderEncryptionType=$("#senderEncryptionType");
$(function(){
    //活动允许单选框判断
    if(modelActivate=='0'){
        $("input[name='activate']").get(1).checked=true;
    }else{
        $("input[name='activate']").get(0).checked=true;
    }

    //认证许可选项
    if(modelReceiverEncryptionType=='SSL'){
        receiverEncryptionType.prop("checked",true);
    }else{
        receiverEncryptionType.prop("checked",false);
    }
    if(modelSenderEncryptionType=='SSL'){
        senderEncryptionType.prop("checked",true);
    }else{
        senderEncryptionType.prop("checked",false);
    }

    //下拉框默认值
    if(modelReceiverProtocol!= ""){
        $("#receiverProtocol").val(modelReceiverProtocol);
    }

    //端口号默认值
    if(modelId == ""){
        getPort();
        getReceiverEncryptionType();
        getSenderEncryptionType();
    }

    //服务器类型onChange方法
    $('#receiverProtocol').combobox({
        onChange: function (n, o) {
            getPort(n);
            getAddress(n);
        }
    });

});

function onChange () {
    var username = '';//邮箱账号
    var address = $("input[name='mailAddress']").val();//邮箱地址
    var arr = address.split('@');
    if (arr.length > 1) {
        username = arr[0];
        var rsaddress = arr[1];
        $("input[name='username']").attr("value", username);
        $("input[name='senderAddress']").attr("value", "smtp." + rsaddress);
        var receiverProtocol = $("#receiverProtocol").val();
        rsaddress = receiverProtocol.replace("3", "").toLowerCase() + "." + rsaddress;
        $("input[name='receiverAddress']").attr("value", rsaddress);
        if(modelId =='') {
            $("input[name='name']").attr("value", address);
        }
    }

}

//接收许可点击事件
function getReceiverEncryptionType(){
    if(receiverEncryptionType.prop("checked")){
        $("input[name='receiverEncryptionType']").val('SSL');
    }else{
        $("input[name='receiverEncryptionType']").val('NONE');
    }
    getPort();
}
//发送许可点击事件
function getSenderEncryptionType(){
    if(senderEncryptionType.prop("checked")){
        $("input[name='senderEncryptionType']").val('SSL');
        senderPort.attr('value','465');
    }else{
        $("input[name='senderEncryptionType']").val('NONE');
        senderPort.attr('value','25');
    }
}

//默认端口号
function getPort(receiverProtocol){
    if(receiverProtocol==undefined){
        receiverProtocol=$("#receiverProtocol").val();
    }
    $("#receiverProtocol").val(receiverProtocol);
    if( receiverProtocol=='IMAP'){

        if(receiverEncryptionType.prop("checked")){
            receiverPort.attr('value','993');
        }else{
            receiverPort.attr('value','143');
        }
    }else{
        if(receiverEncryptionType.prop("checked")){
            receiverPort.attr('value','995');
        }else{
            receiverPort.attr('value','110');
        }
    }
}

//默认服务器
function getAddress(receiverProtocol){
    if(modelId =='') {
        var address = '';
        var send = 'true';
        if (receiverProtocol == undefined) {
            receiverProtocol = $("#receiverProtocol").val();
            send = '';
        }
        var arr = $("input[name='mailAddress']").val().split('@');
        var size = arr.length;
        if (size > 1) {
            address = arr[size - 1];
            if(send=='') {
                $("input[name='senderAddress']").val("smtp." + address);
            }
            address = receiverProtocol.replace("3", "").toLowerCase() + "." + address;
            $("input[name='receiverAddress']").val(address);
        }
    }
}