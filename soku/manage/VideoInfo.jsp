<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="videoInfoBo.name" /></title>
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



<div class="table"><s:actionerror cssClass="actionerror" />
<p id="validateTips"></p>
<table class="listing" cellpadding="0" cellspacing="0">
<s:form action="" validate="false" cssClass="form"
theme="simple">

<s:token />
<s:if test="videoInfoBo.seriesId > 0">
<tr><td colspan="2">
系列名：
<s:property value="videoInfoBo.seriesName" />
别名：
<s:property value="videoInfoBo.serialAlias" />
</td></tr>
</s:if>

<tr><td>
节目名：</td><td>
<s:property value="videoInfoBo.name" />
</td></tr><tr><td>
节目别名：</td><td>
<s:property value="videoInfoBo.alias" />
</td></tr><tr><td>
上映时间：</td><td>
<s:property value="videoInfoBo.releaseDate" />
</td></tr><tr><td>

排序： </td><td><s:property value="videoInfoBo.seriesOrder" />
</td></tr><tr><td>
默认展示站点：</td><td> <s:property value="videoInfoBo.defaultDisplaySiteName" />
</td></tr><tr><td>
总集数：</td><td>
<s:property value="videoInfoBo.episodeTotal" />


</td></tr><tr><td>
分类：</td><td>
<s:property value="videoInfoBo.genre" />
</td></tr><tr><td>
封面图片：</td><td>
<img src="<s:property value="videoInfoBo.poster" />" id="posterImg"
<s:if test="videoInfoBo.poster == null">style="visibility: hidden"</s:if> />

</td></tr><tr><td>
剧集截图：</td><td>
<img src="<s:property value="videoInfoBo.thumb" />"
id="firstLogo"
<s:if test="tvideoInfoBo.thumb == null">style="visibility: hidden"</s:if> />
</td></tr><tr><td>
 地区：</td><td>
<s:property value="videoInfoBo.area" />
 

</td></tr><tr><td>

导演：</td><td>
<s:property value="videoInfoBo.director" />
</td></tr><tr><td>

演员：　</td><td><s:property value="videoInfoBo.performers" />
</td></tr><tr><td>

简介：</td><td> <s:property value="videoInfoBo.showDescription" />


</td></tr>

</s:form>
</table>
</div>
</div>

</div>
<div id="footer"></div>
</div>


</body>
</html>