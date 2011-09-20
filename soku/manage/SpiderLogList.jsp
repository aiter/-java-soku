<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="spiderLog.title.list" /></title>


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
	<div id="left-column"></div>
    <div id="center-column">
      <div class="top-bar"> 
      
        <h1>Spider Log</h1>
      </div>
      <br />
      <div class="select-bar">
         <s:form action="SpiderLog_list" validate="false" cssClass="form" theme="simple">
	        起始日期： <s:textfield key="startDate" id="startDate"></s:textfield>
	   结束日期：  <s:textfield key="endDate" id="endDate"></s:textfield>
	        <input type="submit" value="search" />
      	</s:form> 
      </div>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <s:form action="SpiderLog_batchdelete" validate="false" cssClass="form" theme="simple">
        <table class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="20%"><s:text name="spiderLog.heading.site" /></th>
					<th align="center" width="20%"><s:text name="spiderLog.heading.log_date" /></th>
					<th align="center" width="10%"><s:text name="spiderLog.heading.movie_insert" /></th>
					<th align="center" width="10%"><s:text name="spiderLog.heading.movie_create" /></th>
					<th align="center" width="15%"><s:text name="spiderLog.heading.movie_update" /></th>
					<th align="center" width="10%"><s:text name="spiderLog.heading.movie_delete" /></th>
				</tr>

				<s:iterator value="spiderLogList" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="left"><s:property value="site"/></td>
						<td align="left"><s:date name="log_date" format="yyyy-MM-dd" /></td>
						<td align="left"><s:property value="movie_insert" /></td>
						<td align="left"><s:property value="movie_create" /></td>
						<td align="left"><s:property value="movie_update" /></td>
						<td align="left"><s:property value="movie_delete" /></td>
						
					</tr>
				</s:iterator>
				
				
			</table>
			</s:form>
       
      </div>
      
    </div>

  </div>
  <div id="footer"></div>
</div>
</body>
</html>
