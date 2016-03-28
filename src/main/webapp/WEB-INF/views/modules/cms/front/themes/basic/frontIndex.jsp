<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>首页</title>
	<meta name="decorator" content="cms_default_index_${site.theme}"/>
	<meta name="description" content="${site.description}" />
	<meta name="keywords" content="${site.keywords}" />
    <%--<script src="${ctxStatic}/js/bootstrap/holder/holder.js" type="text/javascript"></script>--%>
   	<style type="text/css">
        body{background-color:#ecedef;}
	   	.center_blank{width:100%;height:25px;background:url(${ctxStatic}/img/jfit/center_blank.png) repeat-x;}
	    .center_content{width:100%;padding-bottom:80px;background-color:#e9e9e9;}
	    .span4{height:167px;background-color:#fff;overflow:hidden;}
	    .list-div{width:200px;display:table-cell;height:157px;padding:0px 10px;overflow:hidden;}
	    .list-head{line-height:50px;color:#7f7f7f;font-weight:normal;white-space:nowrap;overflow:hidden;border-left:none;padding:0;margin:0;background-color:transparent;}
	    .list-content{line-height:27px;margin:0px;height:107px;}
	    .list-content a{color:#7f7f7f;}
	    .list-content a:hover{text-decoration:none;color:#ff0000;}
	    .list-image-div{width:80px;text-align:center;display:table-cell;vertical-align:middle;height:167px;}
	    .list-dt{width:50px;float:left;color:#7f7f7f;font-weight:normal;padding-right:30px;line-height:25px;}
	    .img_css{width:100%;height:100%;}
	    .list-dd1{float:left;padding-right:40px;color:#7f7f7f;line-height:25px;}
	    .list-dd2{color:#7f7f7f;line-height:25px;}
	    .body{background-color:#e9e9e9;}
	    #rollText{font:12px /20px verdana;}
        #myCarousel{margin-bottom: 0;}
        @media(min-width:1280px) and (max-width:1920px){
        .list-div{width:215px;padding:0px 20px;}
        .list-image-div{width:115px;}
        }
    </style>
    
 
</head>
<body>
    <!-- Carousel
        ================================================== -->
        <div id="myCarousel" class="carousel slide" data-ride="carousel">
            <%--首页推荐栏文章--%>
            <c:set var="articles" value="${fnc:getArticleList(site.code,null , 1, 'posid:1')}"></c:set>

            <!-- Indicators -->
            <ol class="carousel-indicators" id="yuandian" style="position:absolute;right:50%;top:0px;">
                <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
                <li data-target="#myCarousel" data-slide-to="1"></li>
                <c:forEach items="${articles}" var="content" varStatus="i">
                    <li data-target="#myCarousel" data-slide-to="${i.index + 2}" ></li>
                </c:forEach>
            </ol>

           <div class="carousel-inner" >
               <%--静态内容 flash--%>
               <div class="item active">
				   <a href="#" target="_blank" ><img src="#" alt="" style="width:100%;height:410px;"></a>
               </div>
               <div class="item">
				   <a href="#" target="_blank" ><img src="#" alt="" style="width:100%;height:410px;"></a>
               </div>
               <%--动态内容 image--%>
               <c:forEach items="${articles}" var="content" varStatus="i" >
                   <%--<div class="item ${i.index == 0 ?'active':''}">--%>
                   <div class="item ruru">
                       <a href="${content.url}" target="_blank" ><img src="${ctx}${content.image}" alt="" style="width:100%;height:410px;"></a>
                   </div>
               </c:forEach>
           </div>
         </div>
         



	<div class="center_content">
	 <div class="container"> 
		<div id="rollAD" style="background-color:#ecedef;height:32px;border:2px;margin-bottom:2px;margin-top:3px;margin-left:auto; position:relative; overflow:hidden;">
					<span style="color: rgb(187, 0, 0); font-weight: bold;line-height:32px;font-family:'微软雅黑';">新闻:</span>
					<div id="rollText">
                        <%--滚动内容start--%>
					    <c:forEach items="${fnc:getArticleList(site.code,'xwzx_gsdt' , 7, 'posid:1')}" var="content" varStatus="status">
		         			<li style="list-style-type:none">
				                <a href="${content.url}" target="_blank" style="color:${content.color};text-decoration:none;">
				                <span>${fns:abbr(content.title,39)}
				                 <fmt:formatDate value="${content.updateTime}" pattern="yyyy.MM.dd"/></span>
				                </a>
		          		 	</li><br />
		            	</c:forEach>
                        <%--滚动内容end--%>
					</div>
				</div>
			</div>
	<div class="center_blank"></div>
    <div class="container">    
        <%--<hr class="featurette-divider">--%>
            <!-- Example row of columns -->
            <div class="row">
	            <c:forEach items="${fnc:getArticleList(site.code,null , 3, 'posid:3')}" var="content" varStatus="status">
             		<div class="span4">
		            	<div class="list-div pull-left">
		            		<h4 class="list-head">
		            			<a href="${content.url}" title="${content.title}" target="_blank" style="color:${content.color};text-decoration:none;">
		            				<abbr >${fns:abbr(content.title,25)}</abbr>
		            			</a>
		            		</h4>
		            		<p class="list-content">
		            			<a href="${content.url}" title="${content.description}" target="_blank" <%-- style="color:${article.color};text-decoration:none;" --%>>
		            				<abbr >${fns:abbr(content.description,117)}</abbr>
		            			</a>
		            		</p>
<!-- 		         			<li style="list-style-type:none"> -->
<%-- 				                <a href="${article.url}" target="_blank" style="color:${article.color};text-decoration:none;"> --%>
<%-- 				                <span><abbr title="${article.title}">${fns:abbr(article.title,39)}</abbr></span> --%>
<%-- 				                 --<fmt:formatDate value="${article.updateTime}" pattern="yyyy.MM.dd"/></span> --%>
<!-- 				                </a> -->
<!-- 		          		 	</li> -->
	          		 	</div>
	          		 	<div class="list-image-div pull-right">
	          		 		<a href="${content.url}" target="_blank" style="color:${content.color};text-decoration:none;">
	          		 			<img src="${content.thumb}" alt="${content.title}" class="img_css" href="${content.url}" target="_blank"/>
<%-- 	          		 		<img src="${ctxStatic}/img/jfit/yancaologo.png" alt="" /> --%>
							</a>
	          		 	</div>
	            	</div>
            	</c:forEach>
            </div>
    </div>
  </div>

<script type="text/javascript">
	$(document).ready(function(){
			 // <![CDATA[
				var textDiv = document.getElementById("rollText");
				var textList = textDiv.getElementsByTagName("li");
			if(textList.length > 1){
				var textDat = textDiv.innerHTML;
				var br = textDat.toLowerCase().indexOf("<br",textDat.toLowerCase().indexOf("br")+2);
				//var textUp2 = textDat.substr(0,br);
				textDiv.innerHTML = textDat+textDat+textDat.substr(0,br);
				textDiv.style.cssText = "position:absolute; top:0;margin-left:40px;padding-top:7px;";
				var textDatH = textDiv.offsetHeight;
				MaxRoll();
			}
			var minTime,maxTime,divTop,newTop=0;
			function MinRoll(){
				newTop++;
				if(newTop<=divTop+40){
					textDiv.style.top = "-" + newTop + "px";
				}else{
					clearInterval(minTime);
					maxTime = setTimeout(MaxRoll,4000);
				}
			}
			function MaxRoll(){
				divTop = Math.abs(parseInt(textDiv.style.top));
				if(divTop>=0 && divTop<textDatH-40){
					minTime = setInterval(MinRoll,1);
				}else{
					textDiv.style.top = 0;divTop = 0;newTop=0;MaxRoll();
				}
			}
			$('#rollText a').hover(function(){ 
				clearInterval(maxTime);
			},function(){
				maxTime = setTimeout(MaxRoll,4000);
			});
			 
			$(".tt1,.tt2,.ruru").height(parseInt($(window).width()*410/1920));
			$("#yuandian").css("paddingTop",parseInt($(window).width()*369/1920));
			$(window).resize(function(){
				$("#yuandian").css("paddingTop",parseInt($(window).width()*369/1920));			
				$(".tt1,.tt2,.ruru").height(parseInt($(window).width()*410/1920));
			});
	});
</script>
</body>
</html>