<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title>Insert title here</title>
<link href="<s:url value="/soku/manage/css/index.css"/>" rel="stylesheet"
	type="text/css" />
<link href="<s:url value="/soku/manage/css/youku.css"/>" rel="stylesheet"
	type="text/css" />
</head>
<body>

<div class="s_subNav">
	<div class="subNav">

	
		
			
		<div class="items">
			<li class="current">
		<a href="http://movie.youku.com/index">首页</a>
		</li>
			<s:iterator value="itemList" id="item">
				
				<s:if test="#item.navigation == 1">
					<li>
						<a href="<s:property value="#item.url"/>"><s:property value="#item.name"/></a>
					</li>
				</s:if>
				
			</s:iterator>
	   </div>
	</div>
</div>


<div class="s_main col2_21">
	<div class="left" >


<s:iterator value="itemList" id="item">
		<div class="box">
			<div class="box_title">
				<a class="videoClass f_r" href="http://dv.youku.com/" target="_blank" charset="100-011-0-1">[更多原创视频]</a>
				<h3 class="title"><a href="http://dv.youku.com/" target="_blank" charset="100-011-0-0"><s:property value="#item.name"/></a></h3>
			</div>
			<div class="videoColl">
					<s:iterator value="videoMap.get(#item.itemId)" >										
						<ul class="video" id="index_vid_10">
							<li class="videoImg">
								<div class="vTip">
									<div class="intro">             </div>
								</div>
										<a class="tipAnchor" href="http://v.youku.com/v_show/id_XMTQ0MjYzOTI4.html" target="video" charset="100-011-4">
											<img src="
												<s:if test="%{picturePath.indexOf('http')>=0}">
													<s:property value="picturePath"/>
												</s:if>
												<s:if test="%{picturePath.indexOf('http')<0}">
													<s:property value="picturePath"/>
												</s:if>"
										 alt="  " title="  "></a>
							</li>
							<li class="playMenu"><img src="%E4%BC%98%E9%85%B7%E7%BD%91_files/qls.gif" style="display: block;" title="添加到点播单" id="PlayListFlag_XMTQ0MjYzOTI4"></li>
														<li><h1><a href="<s:property value="url"/>" target="video" charset="100-011-4"><s:property value="name"/></a></h1></li>
														<li class="vUser">时长: <s:property value="videoLength"/></li>
							<li>分类: <span class="num"><s:property value="category"/></span></li>
							<li>来源: <span class="num"><s:property value="source"/></span></li>
						</ul>
					</s:iterator>
						<input name="vTipHD" type="hidden">	
													<div class="clear"></div>
			</div>
		</div>
</s:iterator>
	</div>
	
	
	
<div class="right">
	  <div class="box_border mod">
	  	<s:iterator value="itemList" id="item">
				<div class="vRanks">
							<div class="box_title">

					<div class="f_r"><a class="arrow" href="/v_olist/c_97.html" target="_blank" charset="100-021-0">更多排行&gt;&gt;</a></div>
					<h3 class="title"><a href="/v_olist/c_97.html" target="_blank" charset="100-021-0"><s:property value="#item.name"/></a></h3>
				</div>
					<div class="vRank rankingShow">
						
						<ul class="rank">
							<s:iterator value="hotwordMap.get(#item.itemId)" status="count">
								<s:if test="#count.count <= 3">	
									<li><span class="sn top3"><s:property value="#count.count"/></span>
										<a href="" target="_blank"><s:property value="name"/></a></li>
								</s:if>
								
								<s:if test="#count.index > 3">	
									<li><span class="sn"><s:property value="#count.count"/></span>
										<a href="" target="_blank"><s:property value="name"/></a></li>
								</s:if>
							</s:iterator>
											
						</ul>
					<div class="clear"></div>
				</div>
				
				</div>	
			</s:iterator>
		</div>
	  
	
</div>
</div>



</body>

</html>