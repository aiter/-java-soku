<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>节目跳转词</title>


<link type="text/css"
	href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
<link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>"
	rel="stylesheet" type="text/css" />

<script type="text/javascript"
	src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
<script type="text/javascript">
	$(function() {

		$("#pageselect").change(function() {
			location.href = $("#pageurl").val() + $("#pageselect").val();
		});

		$(".site_toggle").each(function() {

			$(this).find("h3").click(function() {
				$(this).parent().find("div").toggle();

			});

		});

		//$("#sorttable").tableDnD();
	});
	</script>
</head>
<body>
<div id="main">
<div id="header"><a href="#" class="logo"><img
	src="img/logo.gif" width="101" height="29" alt="" /></a></div>
<div id="middle"><s:include value="module/leftnav.jsp"></s:include>
<div id="center-column">

<div class="top-bar"><a
	href="<s:url action="ProgrammeForwardWord_input">
			<s:param name="forwardWordId" value="-1" />
		</s:url>"
	class="button">添加</a>
<h1>跳转词</h1>
</div>
<br />
<div class="select-bar"><s:form action="ProgrammeForwardWord_list"
	validate="true" cssClass="form" theme="simple">
	<label> <s:textfield key="searchWord" cssClass="text" /> </label>
			类型：
			<s:select key="categoryFilter" list="forwardWordCategory" listKey="key"
		listValue="value" cssClass="text"></s:select>

	<input type="submit" name="Submit"
		class="ui-button ui-state-default ui-corner-all" value="Search" />
</s:form></div>



<div class="table"><img src="img/bg-th-left.gif" width="8"
	height="7" alt="" class="left" /> <img src="img/bg-th-right.gif"
	width="7" height="7" alt="" class="right" />
<table id="sorttable" class="listing" cellpadding="0" cellspacing="0">

	<tr>
		<th align="center" width="20%">跳转词</th>
		<th align="center" width="5%">类型</th>
		<th align="center" width="60%">跳转链接</th>
		<th align="center" width="15%">操作</th>
	</tr>

	<s:iterator value="pageInfo.results" status="index">
		<tr <s:if test="#index.odd">class="bg"</s:if>>

			<td align="center"><s:property value="forwardWord" /></td>
			<td align="center"><s:if test="cate==1">电视剧</s:if> <s:if
				test="cate==2">电影</s:if> <s:if test="cate==3">综艺</s:if> <s:if
				test="cate==5">动漫</s:if></td>

			<td align="center"><s:property value="forwardUrl" /></td>

			<td align="center"><a
				href="<s:url action="ProgrammeForwardWord_input"> <s:param name="forwardWordId" value="id" /></s:url>"
				class="listbutton">编辑</a><br />
				<a
				href="<s:url action="ProgrammeForwardWord_delete"> <s:param name="forwardWordId" value="id" /></s:url>"
				class="listbutton">删除</a></td>
		</tr>
	</s:iterator>

	<tfoot>
		<tr>
			<td colspan="7"><s:property value="pageInfo.totalRecords" />
			---- <s:text name="totalpagenumber" />: <s:property
				value="pageInfo.totalPageNumber" /> <s:text
				name="currentpagenumber" />: <s:property
				value="pageInfo.currentPageNumber" /> <s:if
				test="pageInfo.hasPrevPage">
				<a
					href="<s:url action="ProgrammeForwardWord_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="searchWord" value="searchWord"/>	
								<s:param name="categoryFilter" value="categoryFilter"></s:param>
							</s:url>"><s:text
					name="prevpage" /></a>
			</s:if> <s:if test="pageInfo.hasNextPage">
				<a
					href="<s:url action="ProgrammeForwardWord_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="searchWord" value="searchWord"/>	
								<s:param name="categoryFilter" value="categoryFilter"></s:param>
							</s:url>"><s:text
					name="nextpage" /></a>
			</s:if></td>
		</tr>
	</tfoot>
</table>

<div class="select"><strong>Other Pages: </strong> <input
	type="hidden" id="pageurl"
	value="<s:url action="ProgrammeForwardWord_list">
												<s:param name="searchWord" value="searchWord"/>	
												<s:param name="accuratelyMatched" value="accuratelyMatched"/>
												<s:param name="concernLevel" value="concernLevel"/>
												<s:param name="siteId" value="siteId"></s:param>
												<s:param name="categoryFilter" value="categoryFilter"></s:param>
												<s:param name="pageNumber" value="0" />
											</s:url>" />
<select id="pageselect">
	<s:bean name="org.apache.struts2.util.Counter" id="counter">
		<s:param name="first" value="1" />
		<s:param name="last" value="10" />
		<s:iterator status="count">
			<s:if test="(pageInfo.currentPageNumber - #count.index) > 0">
				<option><s:property
					value="pageInfo.currentPageNumber - #count.index" /></option>
			</s:if>
		</s:iterator>
	</s:bean>

	<s:bean name="org.apache.struts2.util.Counter" id="counter">
		<s:param name="first" value="1" />
		<s:param name="last" value="10" />
		<s:iterator status="count">
			<s:if
				test="(pageInfo.currentPageNumber + #count.index) < pageInfo.totalPageNumber">
				<option><s:property
					value="#count.index + pageInfo.currentPageNumber + 1" /></option>
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
