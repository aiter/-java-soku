<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="episodeLog.title.list" /></title>


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

		$(".site_toggle").each(function() {

			var handle = $(this).find("p");
			var historyPanel = handle.parent().find("div");	
			handle.click(function() {
				var pos = handle.offset();
				historyPanel.css({'top': pos.top - 300, 'left': pos.left - 600});
				historyPanel.show();
			});

			historyPanel.find("p").click(function(){

				historyPanel.hide();
			});

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
      
        <h1>日志查询</h1>
      </div>
      <br />
      <div class="select-bar">
         <s:form action="EpisodeLog_list" validate="false" cssClass="form" theme="simple">
	        起始日期： <s:textfield id="startDate" name="startDate" />
	   结束日期：  <s:textfield id="endDate" name="endDate" />
	   所填URL：  <s:textfield name="searchUrl" /> <br />
	   编辑：  <s:textfield name="operator" />
	   工作内容： 
	   <s:select key="workType" list="workTypeMap" listKey="key" listValue="value"></s:select>
	        <input type="submit" value="search" />
      	</s:form> 
      </div>
      
      
    
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <table id="sorttable" class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="30%">日期</th>
					<th align="center" width="20%">操作人</th>
					<th align="center" width="30">标题</th>
					<th align="center" width="20%">添加链接</th>
					
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="left"><s:date name="updateTime" format="yyyy-MM-dd" /></td>
						<td align="left"><s:property value="operator" /></td>
						<td align="left"><s:property value="title" /></td>
						<td align="left">
							<s:property value="url" />
							<div class="site_toggle" >
								<p>查看历史记录</p>			
								<div style="position: absolute; display: none; top: 290px; left: 466px;"  class="ui-widget ui-widget-content ui-corner-all ">
									<p>关闭</p>
									<div style="width:660px;height:420px; overflow:scroll;">
									<table cellpadding="0" cellspacing="0">
										<tr>
											<th align="center" width="20%">日期</th>
											<th align="center" width="20%">操作人</th>
											<th align="center" width="20">标题</th>
											<th align="center" width="10%">添加链接</th>
											
										</tr>
									<s:iterator value="historyOperation" status="index">							
												<tr>
												<td align="left"><s:date name="updateTime" format="yyyy-MM-dd" /></td>
												<td align="left"><s:property value="operator" /></td>
												<td align="left"><s:property value="title" /></td>
												<td align="left">
													<s:property value="url" />
												</td>						
								
											</tr>
									</s:iterator>
									</table>
									</div>
								</div>
							</div>	
						</td>						
						
					</tr>
					
				
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td colspan="7"> <s:property
					value="pageInfo.totalRecords" /> ----
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="EpisodeLog_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="startDate" value="startDate"/>	
								<s:param name="endDate" value="endDate"/>
								<s:param name="type" value="type"/>
								<s:param name="statusFilter" value="statusFilter"></s:param>
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="EpisodeLog_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="startDate" value="startDate"/>	
								<s:param name="endDate" value="endDate"/>
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
					value="<s:url action="EpisodeLog_list">
												<s:param name="startDate" value="startDate"/>	
												<s:param name="endDate" value="endDate"/>
												<s:param name="statusFilter" value="statusFilter"></s:param>
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
