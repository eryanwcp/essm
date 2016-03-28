/**
 * 移动端公用javascript
 */
/**
 * 打开URL
 * @param url
 */
function openURL(url){
    try {
        WebViewPlugin.webView(url);//移动端接口
    } catch (e) {
        try {
            top.WebViewPlugin.webView(url);
        } catch (e) {
            window.location.href = url;
        }
    }
}

/**
 * 在浏览器中打开网页
 * @param url
 */
function browser(url){
    try {
        WebViewPlugin.browser(url);
    } catch (e) {
        try {
            top.WebViewPlugin.browser(url);
        } catch (e) {
            window.location.href = url;
        }
    }
}



/**
 * 关闭或返回
 */
function goBack(){
    try {
        WebViewPlugin.goBack();
    } catch (e) {
        try {
            top.WebViewPlugin.goBack(url);
        } catch (e) {
            history.go(-1);
        }
    }
}


/**
 * 重定向到登录页面
 */
function redirectLogin(url){
    try {
        WebViewPlugin.redirectLogin();
    } catch (e) {
        try {
            top.WebViewPlugin.redirectLogin();
        } catch (e) {
            if(url){
                window.location.href = url;
            }
        }

    }
}

/**
 * 设置a标签忽略属性 不异步加载
 * @param parentSelect
 */
function resetAToNormal(parentSelect){
    var select = "a";
    if(parentSelect != undefined){
        $(parentSelect).find("a").attr({"data-ignore":"true"});
    }
    $(select).attr({"data-ignore":"true"});
}

/**
 * 计算总页数
 * @param total 总记录数
 * @param pageSize 分页大小
 * @returns {number}
 */
function getTotalPages(total,pageSize) {
    if (total < 0) {
        return -1;
    }

    var count = parseInt(total / pageSize);
    if (total % pageSize > 0) {
        count++;
    }
    return count;
}
