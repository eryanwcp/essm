/**
 * 地图模块
 */
/**
 * 定义全局对象.
 */
var M = $.extend({}, M);

/**
 * 默认坐标点
 * @returns {{longitude: number, latitude: number}}
 */
M.defaultPoint = function() {
    return {
        longitude: 115.890076,
        latitude: 28.658231
    };
};
/**
 * 根据坐标获取两点距离
 */
M.getDistance = function(lat1, lng1, lat2, lng2) {
    var radLat1 = lat1 * Math.PI / 180.0;
    var radLat2 = lat2 * Math.PI / 180.0;
    var a = radLat1 - radLat2;
    var b = lng1 * Math.PI / 180.0 - lng2 * Math.PI / 180.0;
    var s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
    s = s * 6378137.0 / 1000; //单位km
    s = Math.round(s * 10000) / 10000.0;
    return s.toFixed(2);
};
/**
 * 百度坐标转换成火星坐标
 * @param longitude
 * @param latitude
 * @returns {string}
 */
M.bdConverToGPS = function(longitude,latitude) {
    var x_pi = 3.14159265358979324 * 3000.0 / 180.0;
    var x = longitude - 0.0065;
    var y = latitude - 0.006;
    var z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
    var theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
    var gg_lon = z * Math.cos(theta);
    var gg_lat = z * Math.sin(theta);
    return {"lng":gg_lon,"lat":gg_lat};
};

/**
 * 导航
 * @param conf
 * longitude：精度
 * latitude：纬度
 * name：
 * title：
 * content：
 */
M.toNavigation = function(conf) {
    var config = M.defaultPoint();
    config = $.extend(config, conf);
    var ua = window.navigator.userAgent.toLowerCase();
    if(ua.match(/MicroMessenger/i) == 'micromessenger') {//微信
        var point = M.bdConverToGPS(config.longitude, config.latitude);
        $.getJSON(appURL + '/m/qyweixin/jssdk?callback=?&url=' + location.href, function(json) {
            wx.config({
                beta: true,// 必须这么写，否则在微信插件有些jsapi会有问题
                debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                appId: json.obj.appid,
                timestamp: json.obj.timestamp,
                nonceStr: json.obj.nonceStr,
                signature: json.obj.signature,
                jsApiList: ['openLocation']
            });
            wx.openLocation({
                latitude: point.lat, // 纬度
                longitude: point.lng, // 经度
                name: config.name, // 位置名
                address: config.name, // 地址详情说明
                scale: 25, // 地图缩放级别,整形值,范围从1~28。默认为最大
                infoUrl: '' // 在查看位置界面底部显示的超链接,可点击跳转
            });
        });
    } else {
        try {
            if(config['mapExtend']){
                location.href = 'baidumap://map/marker?location=' + config.latitude + ',' + config.longitude + '&title=' + config.name + '&name=' + config.name + '&content=' + config.name + '&output=html&src=江西省锦峰软件科技有限公司|MP_BROWSER';
            }else{
                location.href = 'http://api.map.baidu.com/marker?location=' + config.latitude + ',' + config.longitude + '&title=' + config.name + '&name=' + config.name + '&content=' + config.name + '&output=html&src=江西省锦峰软件科技有限公司|MP_BROWSER';
            }
        } catch (e) {
        }
    }
};

/**
 * 获取当前GPS位置信息
 * @param conf 参数配置
 * @param success（point） 调用成功
 * 经度 longitude = point.coords.longitude;
 * 纬度 latitude = point.coords.latitude;
 * 精度 accuracy = point.coords.accuracy;
 * @param error（e） 调用失败
 * @param notTip 不提示消息
 */
M.getCurrentPosition = function(conf,success,error,notTip) {
    var config = { enableHighAccuracy: true, maximumAge: 60000,timeout:20000};
    config = $.extend(config, conf);
    function _error(error){
        var errorMessage = "";
        switch (error.code) {
            case 1:
                errorMessage = "位置服务被拒绝";
                break;
            case 2:
                errorMessage = "暂时获取不到位置信息";
                break;
            case 3:
                errorMessage = "获取信息超时";
                break;
            case 4:
                errorMessage = "未知错误" ;
                break;
        }
        var errorMessage_detail = errorMessage+"，" + error.message;
        console.error(errorMessage_detail);
        if(!notTip){
            toastr.error(errorMessage);
        }
    }
    _error = error ? error:_error;

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(success, _error, config);
    } else {
        if(!notTip){
            toastr.error("未开启或不支持GPS地理位置服务");
        }
    }
};

