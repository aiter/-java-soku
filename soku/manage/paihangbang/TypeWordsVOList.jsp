<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="搜索词榜单" /></title>


<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
<link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"   type="text/css"/>
    <link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
        
        <script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.tablednd_0_5.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/soku/manage/js/ui.datepicker.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
<script type="text/javascript">

$(function() {
	
	$("#pageselect").change(function() {
		location.href = $("#pageurl").val() + $("#pageselect").val();
	});
	
	$("#list").click(function () {
		$("#topwordsForm").attr("action","TopWords_typeList.do");
		$("#topwordsForm").submit();
	});
});
	</script>
</head>
<body>
<div id="main">
<div id="middle"><s:include
	value="/soku/manage/module/leftnav.jsp"></s:include>

<div id="center-column">
<div class="top-bar">
<h1><s:text name="搜索词排行" /></h1>
</div>
<br />
<div>
<s:property value="bangdandateMsg"/>
<s:property value="fundateMsg"/>
<s:property value="muludateMsg"/>
</div>
<div class="select-bar"><s:form id="topwordsForm"
	action="TopWords_typeList" validate="false" cssClass="form"
	theme="simple">
	<label> <s:select key="cate"
		list="typeCatevos" listKey="catevalue" listValue="videoType.get(catevalue)"></s:select>
	</label>
	<label> <input id="list" type="button"
		class="ui-button ui-state-default ui-corner-all" value="查询" /> </label>&nbsp;&nbsp;&nbsp;&nbsp;
</div>

<div class="table"><img src="soku/manage/img/bg-th-left.gif" width="8"
	height="7" alt="" class="left" /> <img src="soku/manage/img/bg-th-right.gif"
	width="7" height="7" alt="" class="right" />
	<table class="listing" cellpadding="0" cellspacing="0">
		
		<s:iterator value="pageInfo.results" status="index">
			<tr <s:if test="#index.odd">class="bg"</s:if>>
				<td align="left" width="50%">
					<s:property value="keyword" />/<s:property value="videoType.get(cate)" />
				</td>
			</tr>
		</s:iterator>

		<tfoot>
			<tr>
				<td colspan="4"><s:text name="totalpagenumber" />: <s:property
					value="pageInfo.totalPageNumber" /> <s:text
					name="currentpagenumber" />: <s:property
					value="pageInfo.currentPageNumber" /> <s:if
					test="pageInfo.hasPrevPage">
					<a
						href="<s:url action="TopWords_typeList">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="cate" value="cate" />
							</s:url>"><s:text
						name="prevpage" /></a>
				</s:if> <s:if test="pageInfo.hasNextPage">
					<a
						href="<s:url action="TopWords_typeList">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="cate" value="cate" />
							</s:url>"><s:text
						name="nextpage" /></a>
				</s:if></td>
			</tr>
		</tfoot>
	</table>
<div class="select"><strong>Other Pages: </strong> <input
	type="hidden" id="pageurl"
	value="<s:url action="TopWords_typeList">
								<s:param name="cate" value="cate" />
								<s:param name="pageNumber" value="0" />
							</s:url>" />
<select id="pageselect">
	<s:bean name="org.apache.struts2.util.Counter" id="counter">
		<s:param name="first" value="1" />
		<s:param name="last" value="10" />
		<s:iterator status="count">
			<s:if
				test="(pageInfo.currentPageNumber - #count.index) >0">
				<option><s:property
					value="pageInfo.currentPageNumber - #count.index " /></option>
			</s:if>
		</s:iterator>
	</s:bean>
	<s:bean name="org.apache.struts2.util.Counter" id="counter">
		<s:param name="first" value="1" />
		<s:param name="last" value="10" />
		<s:iterator status="count">
			<s:if
				test="(pageInfo.currentPageNumber + #count.index) <= pageInfo.totalPageNumber">
				<option><s:property
					value="#count.index + pageInfo.currentPageNumber" /></option>
			</s:if>
		</s:iterator>
	</s:bean>
</select></div>
</div>

</div>

</div>
<div id="footer"></div>


</div>
</body>
</html>
