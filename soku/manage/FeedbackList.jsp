<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="feedback.title.list" /></title>


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
		if($("#startDate").val() == "") {
			$("#startDate").datepicker("setDate", new Date());
		}

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
      
        <h1>Feedback</h1>
      </div>
      <br />
      <div class="select-bar">
         <s:form action="Feedback_list" validate="false" cssClass="form" theme="simple">
	        起始日期： <s:textfield key="startDate" id="startDate"></s:textfield>
	   结束日期：  <s:textfield key="endDate" id="endDate"></s:textfield>
	        <input type="submit" value="search" />
      	</s:form> 
      </div>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <table class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="20%">错误类型</th>
					<th align="center" width="20%">关键字</th>
					<th align="center" width="10%">url</th>
					<th align="center" width="20%">描述</th>
					<th align="center" width="10%">时间</th>
					<th align="center" width="10%">用户qq</th>
					<th align="center" width="10%">操作</th>
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						
						<td align="center">
							<s:if test="errorType==0">
								其他
							</s:if>
							<s:if test="errorType==1">
								电视、电影、综艺节目、动慢，剧集有错误 
							</s:if>
							<s:if test="errorType==2">
								搜索找不到您需要的内容 
							</s:if>
							<s:if test="errorType==3">
								结果包含不健康信息  
							</s:if>
						</td>
						<td align="center"><s:property value="keyword" /></td>
						<td align="center"><a href='<s:property value="url" />'>
								<s:if test="url.length() > 30">
									<s:property value="url.substring(0, 30)" />...
								</s:if>
								<s:else>
									<s:property value="url" />
								</s:else>
							</a>
						</td>
						<td align="center">
						<textarea <s:if test="status == 1">style="color:blue"</s:if> cols="50" class="label"><s:property value="description" /></textarea>
							
								
						
						</td>
						<td align="center"><s:date name="createtime" format="yyyy-MM-dd HH:mm:ss" /></td>
						<td align="center">QQ: <s:property value="qq" /> <br/>
							email: <s:property value="email" />
						</td>
						<td align="center">
							<a class="listbutton" href="<s:url action="Feedback_process">
									<s:param name="feedbackId" value="id"/>		
									<s:param name="startDate" value="startDate"/>	
									<s:param name="endDate" value="endDate"/>								
										</s:url>">标记处理
							</a> <br />
						
							<a class="listbutton" href="<s:url action="Feedback_delete">
									<s:param name="feedbackId" value="id"/>		
									<s:param name="startDate" value="startDate"/>	
									<s:param name="endDate" value="endDate"/>								
										</s:url>">删除
							</a> <br />
							<a class="listbutton" href="<s:url action="Feedback_input">
									<s:param name="feedbackId" value="id"/>									
										</s:url>">详细
							</a> 
						</td>
						
						
						
					</tr>
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td colspan="7">
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="Feedback_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="startDate" value="startDate"/>	
								<s:param name="endDate" value="endDate"/>
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="Feedback_list">
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
