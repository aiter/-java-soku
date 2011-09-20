<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<s:if test="task=='Create'">
	<title><s:text name="teleplayVersion.title.create" /></title>
</s:if>
<s:if test="task=='Edit'">
	<title><s:text name="teleplayVersion.title.edit" /></title>
</s:if>
<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />

<link type="text/css"
	href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />

<script type="text/javascript"
	src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>

<script type="text/javascript">

</script>

</head>
<body>
<div id="main">
<div id="header"><a href="#" class="logo"><img
	src="img/logo.gif" width="101" height="29" alt="" /></a></div>
<div id="middle"><s:include value="module/leftnav.jsp"></s:include>
<div id="center-column">
<div class="top-bar">
<h1><s:property value="videoInfoBo.categoryName" /></h1>

</div>
<br />


<div class="table"><s:actionerror cssClass="actionerror" />
<p id="validateTips"></p>
<s:form action="" validate="false" cssClass="form"
	theme="simple">

	<s:token />
	<s:if test="videoInfoBo.seriesId > 0">
		<label>系列名：</label>
		<s:textfield key="videoInfoBo.seriesName" cssClass="text"
				readonly="true" />
		<label>别名：</label>
		<s:textfield key="videoInfoBo.serialAlias" cssClass="text" readonly="true"/>
		<br />
	</s:if>	


	<label>节目名：</label>
	<s:textfield key="videoInfoBo.name" cssClass="text" readonly="true"/>
	<br />
	<label>上映时间：</label>
	<s:textfield key="videoInfoBo.releaseDate" cssClass="text" readonly="true"/>
	<br />
		
	排序： <s:textfield key="videoInfoBo.seriesOrder" cssClass="text"/>
	<label>默认展示站点： <s:select
		key="videoInfoBo.defaultDisplaySite" list="sitesMap" listKey="key"
		listValue="value" /></label>
	<br />
	<label>总集数：</label>
	<s:textfield key="videoInfoBo.episodeTotal" cssClass="text" readonly="true"/>
	<input type="button" id="editTeleplayEpisode"
		class="ui-button ui-state-default ui-corner-all" value="剧集搜索" />
	
	<br />
	<label>分类：</label>
	<s:textfield key="videoInfoBo.genre" cssClass="text" readonly="true"/>
	<br />
	<label>封面图片：</label>
	<img src="<s:property value="videoInfoBo.poster" />" id="posterImg"
		<s:if test="videoInfoBo.poster == null">style="visibility: hidden"</s:if> />
	
	<br />
	<label>剧集截图：</label>
	<img src="<s:property value="videoInfoBo.thumb" />"
		id="firstLogo"
		<s:if test="tvideoInfoBo.thumb == null">style="visibility: hidden"</s:if> />
	<br />
 			地区          ： <s:textfield key="videoInfoBo.area" cssClass="text" readonly="true"/>
		
	<br />

			导演：　<s:textfield key="videoInfoBo.directors" cssClass="text"
		size="60" />
	<br />
	
			演员：　<s:textfield key="videoInfoBo.performer" cssClass="text"
		size="60" />
	<br />


	<br />
			
			简介： <s:textarea key="videoInfoBo.brief" cssClass="textarea"
		cols="60" rows="10" />




</s:form>
<p>&nbsp;</p>
<div class="buttom"><a href="#" class="button"
	id="teleplayVersionsubmit"> <s:if test="task == 'Create'">
	<s:text name="button.create" />
</s:if> <s:if test="task == 'Edit'">
	<s:text name="button.edit" />
</s:if> </a> <a
	href="<s:url action="TeleplayVersion_list"><s:param name="pageNumber" value="1" /></s:url>"
	class="button"> <s:text name="button.cancel" /> </a></div>
</div>
</div>

</div>
<div id="footer"></div>
</div>


</body>
</html>