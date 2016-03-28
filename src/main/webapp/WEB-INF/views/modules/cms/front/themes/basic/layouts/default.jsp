<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<!DOCTYPE html>
<!--[if lt IE 7]><html class="ie ie6 ie-lte9 ie-lte8 ie-lte7 no-js"><![endif]-->
<!--[if IE 7]><html class="ie ie7 ie-lte9 ie-lte8 ie-lte7 no-js"><![endif]-->
<!--[if IE 8]><html class="ie ie8 ie-lte9 ie-lte8 no-js"><![endif]-->
<!--[if IE 9]><html class="ie9 ie-lte9 no-js"><![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html class="no-js"> <!--<![endif]-->
<html>
<head>
	<title><sitemesh:title default="欢迎光临"/> - ${site.title}</title>
	<%@include file="/WEB-INF/views/modules/cms/front/include/head.jsp" %>
	<sitemesh:head/>
    <script>
        $(document).ready(function() {
            //下拉菜单自动弹出子菜单
            $('.js-activated').dropdownHover();        
        });
        function AddFavorite(sURL, sTitle) {
        	sURL = encodeURI(sURL);          
        	try{                   
        		window.external.addFavorite(sURL, sTitle);
        		}catch(e) {                   
        			try{                       
        				window.sidebar.addPanel(sTitle, sURL, "");                   
        				}catch (e) {alert("加入收藏失败，请使用Ctrl+D进行添加,或手动在浏览器里进行设置.");}               
        			}        
        	}        
    </script>
    
    
    <style type="text/css">
    /* @font-face {
 	font-family: 'rtws_yuegothic_trial_regulaRg';
    src: url(${ctxStatic}/img/jfit-css/rtwsyuegotrial-regular-webfont.eot);
    src: url(${ctxStatic}/img/jfit_css/rtwsyuegotrial-regular-webfont.eot?#iefix) format('embedded-opentype'),
         url(${ctxStatic}/img/jfit_css/rtwsyuegotrial-regular-webfont.woff2) format('woff2'),
         url(${ctxStatic}/img/jfit_css/rtwsyuegotrial-regular-webfont.woff) format('woff'),
         url(${ctxStatic}/img/jfit_css/rtwsyuegotrial-regular-webfont.ttf) format('truetype'),
         url(${ctxStatic}/img/jfit_css/rtwsyuegotrial-regular-webfont.svg#rtws_yuegothic_trial_regulaRg) format('svg');
    font-weight: normal;
    font-style: normal;
   	} */

    .login a{color:#7f7f7f;display:block;margin-top:30px;float:right;}
    .sousuo{padding:30px 0 19px;overflow:hidden;}
    #sousuozi{height:20px;width:142px;font-size:12px;background:url(${ctxStatic}/img/jfit/sousuozi.png) no-repeat;border:none;padding:0;margin:0;float:left;border-top-left-radius:12px;border-top-right-radius:0px;border-bottom-right-radius:0px;border-bottom-left-radius:12px;}
    .sousuotu{height:20px;width:31px;background:url(${ctxStatic}/img/jfit/sousuotu.png) no-repeat;border:none;padding:0;margin:0;float:left;}
    #main_nav,#main_nav li{height:48px;}
    #main_nav a{height:23px;font-size:14px;}
    #main_nav span{line-height:23px;font-family:"rtws_yuegothic_trial_regulaRg","黑体";}
    .copyright p{margin:0px;}  
    #bottom-list a{font:'微软雅黑';font-size:12px;line-height:25px;color:#888;}
    .jf_lyl dd{margin-left:0px;}
    .jf_lyl dt{line-height:50px;}
    #main_nav .active span{color:#2586ab;font-weight:bold;}
    #main_nav a:hover,#lianjie a:hover{
    	color:#2586ab;
    	font-weight:bold;
    	background-color:#e5e5e5;
    	box-shadow:inset 0 3px 8px rgba(0,0,0,0.125);
    	-webkit-box-shadow:inset 0 3px 8px rgba(0,0,0,0.125);
    	-moz-box-shadow:inset 0 3px 8px rgba(0,0,0,0.125);
    	text-decoration:none;
    	-webkit-border-radius:5px;
    	-moz-border-radius:5px;
    	border-radius:5px;
    	background-image:none;
    }
   /*  #main_nav .active a{background-color:transparent;box-shadow:none;webkit box shadow:none;} */
    @media(max-width:767px){
    	.jf_lyl dd{text-align:center;}
    	.jf_lyl dt{text-align:center;}
    }
 
    
    </style>
    
</head>
<body>
	<div class="container-flow" style="background:url(${ctxStatic}/img/jfit/head_background.png) repeat-x;">
	<div class="container">
	  <div class="row-flow">
		<div class="span6" style="margin-left:0px;">
          <c:choose>
   			<c:when test="${not empty site.logo}">
   				<img style="margin:13px 0 5px;height:51px;" alt="${site.title}" src="${site.logo}" onclick="location='${ctxFront}/index-${site.code}${fns:getUrlSuffix()}'">
   			</c:when>
   			<c:otherwise><a class="brand" href="${ctxFront}/index-${site.code}${fns:getUrlSuffix()}">${site.title}</a></c:otherwise>
   		  </c:choose>
   		</div>   		
	   <div class="span6 pull-right" >
         <%--  <form class="input-append  sousuo pull-right" action="${ctxFront}/search" method="get">
                  <input class="span2" type="text" name="q" maxlength="20" style="width:100px;" placeholder="全站搜索..." value="${q}">
                  <button class="btn" type="submit">搜索</button>
          </form> --%>
          		<!-- <img class="pull-right" id="bg_music_img" alt="背景音乐控制" style="margin:29px 0 20px;" title="" src="" onclick="toggle()"> -->    
          		<a class="pull-right" onclick="AddFavorite(window.location,document.title)" href="javascript:void(0)"><img src="${ctxStatic}/img/jfit/icon_star.png" style="height:20px;width:20px;margin:29px 8px 0;"/></a>    
          		<form class="sousuo pull-right" style="margin:0;" action="${ctxFront}/search" method="get">
          			<input id="sousuozi" type="text" name="q" maxlength="20" value="${q}" style="padding-left:10px;">
          			<button class="sousuotu" type="submit"></button>
        		</form>
        		
	   	<%--  <div class="span3 pull-right login" style="overflow:hidden;white-space:nowrap;text-align:right">

    		<a href="${ctxAdmin}/login/index" target="_blank">综合事务</a>
    		|
    		<a href="#">专项服务</a>
    		|  
    		<img src="${ctxStatic}/img/jfit/icon_star.png" style="width:20px;height:20px;margin:28px 5px 0 0;"/>
    		<a onclick="AddFavorite(window.location,document.title)" href="javascript:void(0)">收藏本站</a>

		 </div>
		  --%>
       </div>
       <div style="clear:both;"></div>
     </div>
     </div>
</div>

	<div class="navbar navbar-fixed-top" style="position:static;margin-bottom:0;">
      <div class="navbar-inner" style="background:url(${ctxStatic}/img/jfit/nav_background.png) repeat-x;border:none;">
        <div class="container">
            <button type="button" class="btn btn-navbar " data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
           
          <div class="nav-collapse" style="">
            <ul id="main_nav" class="nav nav-pills">
             	<li class="${isIndex?'active':''}"><a href="${ctxFront}/index-1${fns:getUrlSuffix()}"><span>${site.code eq '1'?'首　 页':'返回主站'}</span></a></li>
                <c:forEach items="${fnc:getMainNavList(site.code)}" var="category" varStatus="status">
                    <c:if test="${status.index lt 6}">
                        <c:set var="menuCategoryId" value=",${category.id},"/>
                        <li class="${requestScope.category.id eq category.id||fn:indexOf(requestScope.category.parentIds,menuCategoryId) ge 1?'active':''}">
                            <a href="${category.url}" target="${category.target}"><span><i class="${category.icon}"></i> ${category.name}</span></a></li>
                    </c:if>
                </c:forEach>
			    <%--<li id="siteSwitch" class="dropdown">--%>
			       	<%--<a class="dropdown-toggle" data-toggle="dropdown" href="#" title="站点"><i class="icon-retweet"></i></a>--%>
					<%--<ul class="dropdown-menu">--%>
					  <%--<c:forEach items="${fnc:getSiteList()}" var="site"><li><a href="#" onclick="location='${ctxFront}/index-${site.id}${urlSuffix}'">${site.title}</a></li></c:forEach>--%>
					<%--</ul>--%>
				<%--</li>--%>
                <%-- <li id="siteSwitch" class="dropdown">
                    <a class="dropdown-toggle js-activated" data-toggle="dropdown" href="#" title="站点"><i class="icon-retweet"></i></a>
                    <ul class="dropdown-menu" id="lianjie">
                        <c:forEach items="${fnc:getLinkList(site.code,'link_xtlj' , 0, null)}" var="link" varStatus="status">
                            <li style="height:30px;"><a href="${link.href}" target="_blank"><span>${link.title}</span></a></li>
                        </c:forEach>
                        
                        <li><a href="http://10.36.10.1:9080/nportal/portal/" target="_blank">江西省烟草专卖局（公司）</a></li>
                        <li><a href="http://10.37.0.1/" target="_blank">南昌市烟草专卖局（公司）</a></li>
                    </ul>
                </li> --%>
		    	<%-- <li id="themeSwitch" class="dropdown">
			       	<a class="dropdown-toggle  js-activated" data-toggle="dropdown" href="#" title="主题切换"><i class="icon-th-large"></i></a>
				    <ul class="dropdown-menu">
				      <c:forEach items="${fns:getDictList('theme')}" var="dict"><li><a href="#" onclick="location='${ctxFront}/theme/${dict.value}?url='+location.href">${dict.name}</a></li></c:forEach>
				    </ul>
				    <!--[if lte IE 6]><script type="text/javascript">$('#themeSwitch').hide();</script><![endif]-->
			    </li> --%>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>
	<!-- <div class="container"> -->
		<div class="content">
			<sitemesh:body/>
		</div>
		<!-- <hr style="margin:20px 0 10px;"> -->
		
		<!-- 底部灰色部分 -->
		<%-- <footer style="background-color:#d3d3d3;">
		<div class="container" style="padding-bottom:80px;">       
            <div class="row" id="bottom-list" >
            	<div class="span1"></div>
            	<div class="span2 jf_lyl">
            		<dl>
            			<dt><a href="#">关于我们</a></dt>
            			<dd><a href="#">公司简介</a></dd>
            			<dd><a href="#">管理团队</a></dd>
            			<dd><a href="#">社会责任</a></dd>
            			<dd><a href="#">财务状况</a></dd>
            			<dd><a href="#">可持续发展</a></dd>
            			<dd><a href="#">核心价值观</a></dd>
            		</dl>
            	</div>
            	<div class="span2 jf_lyl">
            		<dl>
            			<dt><a href="#">核心优势</a></dt>
            			<dd><a href="#">丰富的项目服务</a></dd>
            			<dd><a href="#">深厚的行业积累</a></dd>
            			<dd><a href="#">先进的平台软件</a></dd>
            			<dd><a href="#">领先的技术优势</a></dd>
            			<dd><a href="#">全球化的提交能力</a></dd>
            			<dd><a href="#">充足的人才供应</a></dd>
            		</dl>
            	</div>
            	<div class="span2 jf_lyl">
            		<dl>
            			<dt><a href="#">加入我们</a></dt>
            			<dd><a href="#">总经理致辞</a></dd>
            			<dd><a href="#">公司理念</a></dd>
            			<dd><a href="#">社会招聘</a></dd>
            			<dd><a href="#">薪酬福利</a></dd>
            			<dd><a href="#">工作机会</a></dd>
            			<dd><a href="#">人才供应</a></dd>
            		</dl>
            	</div>
            	<div class="span2 jf_lyl">
            		<dl>
            			<dt><a href="#">常用链接</a></dt>
            			<dd><a href="#">新闻中心</a></dd>
            			<dd><a href="#">展会活动</a></dd>
            			<dd><a href="#">公司刊物</a></dd>
            			<dd><a href="#">网络安全</a></dd>
            			<dd><a href="#">安全通告</a></dd>
            			<dd><a href="#">成功故事</a></dd>
            		</dl>
            	</div>
            	<div class="span2 jf_lyl">
            		<dl>
            			<dt><a href="#">按访问者</a></dt>
            			<dd><a href="#">运营商</a></dd>
            			<dd><a href="#">企业用户</a></dd>
            			<dd><a href="#">最终消费者</a></dd>
            			<dd><a href="#">合作伙伴</a></dd>
            			<dd><a href="#">供应商</a></dd>
            			<dd><a href="#">新闻媒体</a></dd>
            		</dl>
            	</div>
            	<div class="span1"></div>
            </div> 
          </div>          
          	<div class="footer_nav text-center" style="margin:0px;"><a href="${ctxFront}/guestbook" target="_blank">留言板</a> | <a href="${ctx}/search" target="_blank">全站搜索</a> | <a href="${ctxFront}/map-${site.code}${fns:getUrlSuffix()}" target="_blank">站点地图</a> |  <a href="${ctxAdmin}/login/index" target="_blank">综合事务</a></div>
			<!--  <div class="pull-right">${fns:getDate('yyyy年MM月dd日 E')}</div>--><div class="copyright" style="padding:5px 0px;margin:0px;">${site.copyright}</div>
      	</footer> --%>
      	<!-- 底部灰色部分 -->
      	
      	
      	<!-- 更改后的页面底部 -->
      	<footer>
			<div class="container" style="margin-top:200px;">
				<div style="height:4px;width:100%;background:url(${ctxStatic}/img/jfit/footer_img.png) no-repeat right;margin-bottom:15px;"></div>
      			<div class="footer_nav text-center" style="margin:0px;"><a href="${ctxFront}/guestbook" target="_blank">留言板</a> | <a href="${ctxFront}/search" target="_blank">全站搜索</a> | <a href="${ctxFront}/map-${site.code}${fns:getUrlSuffix()}" target="_blank">站点地图</a> |  <a href="${ctxAdmin}" target="_blank">综合事务</a></div>
				<div class="copyright" style="padding:5px 0px;margin:0px;">${site.copyright}</div>
      	</footer>
 
    <!-- </div> --> <!-- /container -->
</body>
</html>