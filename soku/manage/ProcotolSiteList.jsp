<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="ProtocolSite.title.list" /></title>


    <link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"   type="text/css"/>
    <link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
    
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.datepicker.js"/>"></script>
	
	<script type="text/javascript">
	$(function() {

		$("#startDate").datepicker({dateFormat: 'yy-mm-dd'});
		$("#endDate").datepicker({dateFormat: 'yy-mm-dd'});
		

		if($("#endDate").val() == "") {
			$("#endDate").datepicker("setDate", new Date());
		}
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
      
        <h1>ProtocolSite</h1>
      </div>
      <br />
      <div class="select-bar">
         <s:form action="ProtocolSite_list" validate="false" cssClass="form" theme="simple">
	        起始日期： <s:textfield key="startDate" id="startDate"></s:textfield>
	   结束日期：  <s:textfield key="endDate" id="endDate"></s:textfield>
	        <input type="submit" value="search" />
      	</s:form> 
      </div>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <table class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="15%">站点名</th>
					<th align="center" width="10%">url</th>
					<th align="center" width="10%">描述</th>
					<th align="center" width="10%">时间</th>
					<th align="center" width="10%">操作</th>
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						
						
						<td align="center"><s:property value="name" /></td>
						<td align="center"><s:property value="url" /></td>
						<td align="center">
								<s:property value="remark" />
							
						</td>
						<td align="center"><s:property value="createtime" /></td>
						
						<td align="center"><a name="deleteLink" class="listbutton"
							href="<s:url action="ProtocolSite_delete"><s:param name="protocolSiteId" value="id"/></s:url>">
						<s:text name="person.delete" /> </a> </td>
						
						
					</tr>
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td colspan="6">
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="ProtocolSite_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="startDate" value="startDate"/>	
								<s:param name="endDate" value="endDate"/>
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="ProtocolSite_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="startDate" value="startDate"/>	
								<s:param name="endDate" value="endDate"/>
							</s:url>"><s:text
								name="nextpage"/></a>
						</s:if></td>
					</tr>
				</tfoot>
			</table>
       
      </div>
      
    </div>

  </div>
  <div id="footer"></div>
</div>
</body>
</html>
