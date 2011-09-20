<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>直达区导入详情</title>


    <link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"   type="text/css"/>
    <link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
    
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.datepicker.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.tablednd_0_5.js"/>"></script>
	<script type="text/javascript">
	$(function() {

		$("#startDate").datepicker({dateFormat: 'yy-mm-dd'});
		$("#endDate").datepicker({dateFormat: 'yy-mm-dd'});
		if($("#startDate").val() == "") {
			$("#startDate").datepicker("setDate", new Date());
		}

		if($("#endDate").val() == "") {
			$("#endDate").datepicker("setDate", new Date());
		}

		$("#pageselect").change(function() {
			location.href = $("#pageurl").val() + $("#pageselect").val();
		});

		//$("#sorttable").tableDnD();
	});
	</script>
</head>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="img/logo.gif" width="101" height="29" alt="" /></a>
   
  </div>
  <div id="middle">
	<s:include value="module/leftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
      
        <h1>直达区导入详情</h1>
      </div>
      <br />
      <div class="select-bar">
         <s:form action="VersionImportLog_list" validate="false" cssClass="form" theme="simple">
	        状态： 
	   <s:select key="siteVersion" list="statusMap" listKey="key" listValue="value"></s:select>
	        类型： 
	   <s:select key="cateId" list="cateMap" listKey="key" listValue="value"></s:select>
	        <input type="submit" value="search" />
      	</s:form> 
      </div>
  
   
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <table id="sorttable" class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="20%">直达区剧集名称</th>
					<th align="center" width="20%">待导入剧集名称</th>
					<th align="center" width="10%">类型</th>
					<th align="center" width="10%">状态</th>
					<th align="center" width="10%">时间</th>
					<th align="center" width="10%">查看详情</th>
					
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td><s:property value="versionName" /></td>
						<td><s:property value="importingName" /></td>
						<td align="left">
						<s:if test="cateId == 1">
							电视剧
						</s:if>
						<s:if test="cateId == 2">
							电影
						</s:if>
						<s:if test="cateId == 3">
							综艺
						</s:if>
						<s:if test="cateId == 4">
							音乐
						</s:if>
						<s:if test="cateId == 5">
							动漫
						</s:if>
						</td>
						<td align="left">
							<s:if test="fkSiteVersionId > 0">
								已导入
							</s:if>
							<s:if test="fkSiteVersionId < 0">
								未导入
							</s:if>
						</td>
						<td align="left"><s:date name="createTime" format="yyyy-MM-dd" /></td>
						<td><a href="<s:url action="VersionImportDetail_list"> 
								<s:param name="pageNumber" value="pageNumber" />
								<s:param name="importLogId" value="id"/>	
								<s:param name="siteVersion" value="siteVersion"/>	
								<s:param name="cateId" value="cateId"/>
							</s:url>">查看详情</a></td>
					</tr>
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td colspan="7"> <s:property
					value="pageInfo.totalRecords" /> ----
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="VersionImportLog_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="siteVersion" value="siteVersion"/>	
								<s:param name="cateId" value="cateId"/>
								<s:param name="type" value="type"/>
								<s:param name="statusFilter" value="statusFilter"></s:param>
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="VersionImportLog_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="siteVersion" value="siteVersion"/>	
								<s:param name="cateId" value="cateId"/>>
								<s:param name="type" value="type"/>
								<s:param name="statusFilter" value="statusFilter"></s:param>
							</s:url>"><s:text
								name="nextpage"/></a>
						</s:if></td>
					</tr>
				</tfoot>
			</table>
       
				       <div class="select"><strong>Other Pages: </strong> <input
					type="hidden" id="pageurl"
					value="<s:url action="VersionImportLog_list">
												<s:param name="startDate" value="startDate"/>	
												<s:param name="siteVersion" value="siteVersion"/>	
												<s:param name="cateId" value="cateId"/>
												<s:param name="type" value="type"/>
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
