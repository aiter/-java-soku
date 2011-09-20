<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="music.title.list" /></title>
	
	<style type="text/css">
		  body{font-family:helvetica,arial,sans-serif;font-size:12px;}
		 span{display:none;}
		 table{border-collapse:collapse;margin:10px 0;}
		 caption{text-align:left;font-size:18px;font-weight:bold;margin:10px 0;}
		 td{padding:10px;border:2px solid #ccc;}
		 td.bench{color:#c60;font-weight:normal;}
		 td.code{font-family:"courier new",monospace;} 
	</style>

	<link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
    <link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
	
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.tablednd_0_5.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.datepicker.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	
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
<body style="background:#ffffff;">
<div align="center">


<div>
         <s:form action="Music_list" validate="false" cssClass="form" theme="simple">
	        <s:if test="musicFilter!=null">
			</s:if>
      		<label>
	        <s:textfield key="startDate" id="startDate" cssClass="text"/>
	        <s:textfield key="endDate" id="endDate" cssClass="text"/>
	        </label>	        
	      	<label>
	        <input type="submit" name="Submit" class="ui-button ui-state-default ui-corner-all" value="Search" />
	        </label>
      	</s:form> 
      </div>

<div> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <s:form action="Music_batchdelete" validate="false" cssClass="form" theme="simple">
        <table cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" >编号</th>
					<th align="center" >名称</th>
					<th align="center" >相关人物</th>
					<th align="center" >入库时间</th>
					
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="left"><s:property value="id"/></td>
						<td align="left"><s:property value="name"/></td>
						<td align="left"><s:property value="singerName"/></td>
						<td align="left"><s:property value="createTime"/></td>
						
					</tr>
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td colspan="4"><s:property
					value="pageInfo.totalRecords" /> -----
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="Music_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="searchWord" value="searchWord" />
								<s:param name="siteFilter" value="siteFilter" />
								<s:param name="startDate" value="startDate"/>	
								<s:param name="statusFilter"><s:property value="statusFilter"/></s:param>
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="Music_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="searchWord" value="searchWord" />
								<s:param name="siteFilter" value="siteFilter" />
								<s:param name="startDate" value="startDate"/>	
								<s:param name="statusFilter"><s:property value="statusFilter"/></s:param>
							</s:url>"><s:text
								name="nextpage"/></a>
						</s:if></td>
					</tr>
				</tfoot>
			</table>
			<s:hidden name="pageNumber" />
    		<s:hidden name="searchWord" />
    		<s:hidden name="siteFilter" />
    		<s:hidden name="statusFilter" />
			</s:form>
       
      </div>


</div>
</body>
</html>
