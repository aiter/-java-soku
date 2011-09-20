<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="programmeSite.title.list" /></title>


	<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
	<link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript">
		$(function() {


			$("#urldialog").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				width: 400,
				modal: true,
				buttons: {
					'取消' : function() {
						$(this).dialog('close');
					},
				
					'确认': function() {		
						var programmeId = $("#programmeId").val();
						var importingUrl = $("#importingUrl").val();				
						$.ajax({
							url: '<s:url action="ProgrammeSite_importEpisodeFromOldDb" />',
							type: 'POST',
							data: 'programmeId=' + programmeId + '&importingUrl=' + importingUrl,
							success: function(data) {
								var o = eval('(' + data + ')');
								if(o.status == 'success') {
									alert('导入成功');
								} else {
									alert('error');
								}
							}
						});
						$(this).dialog('close');
					}
				
				},
				close : function() {
					//allFields.val('').removeClass('ui-state-error');
				}
			});
			$("#pageselect").change(function() {
				location.href = $("#pageurl").val() + $("#pageselect").val();
			});

		

			$("a[name='deleteLink']").click(function() {
				return confirm("确认删除？");
			});

			$("#thcheck").click(function() {
				if($(this).attr("checked")) {
					$("[name=batchdeleteids]").each(function(){
						$(this).attr("checked", true);
					});
				} else {
					$("[name=batchdeleteids]").each(function() {
						$(this).attr("checked", false);
					});
				}
			});

		 $("#batchdelete").click(function() {
			 
			 haschecked = false;
			 $("[name=batchdeleteids]").each(function(){
					if($(this).attr("checked"))
						haschecked = true;
				});
			 if(haschecked) {
				if(!confirm("确认删除？"))
					 return false;
				$("#ProgrammeSite_batchdelete").submit();
			 } else {
				 alert("没有数据要删除");
				 return false;
			 }
			 return false;
		 });
		});

		function importingUrl(programmeId) {
			$('#urldialog').dialog('open');
			$('#programmeId').val(programmeId);
		}
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
      <s:if test="programmeId > 0">
      		<a href="<s:url action="ProgrammeSite_input">
			<s:param name="programmeId" value="programmeId" />
			<s:param name="programmeSiteId" value="-1" />
				</s:url>"  class="button"><s:text
            name="programmeSite.add"/></a>
      </s:if>      	
      <a class="button" onclick="importingUrl(<s:property value="programmeId" />);return false;">导入历史数据</a>
        <h1><s:text name="soku.programmeSite.title" /></h1>
      </div>
      <br />
      <div class="select-bar">
        
      </div>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <s:form action="ProgrammeSite_batchdelete" validate="false" cssClass="form" theme="simple">
        <table class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="5%"><input id="thcheck" type="checkbox" /></th>
					<th align="center" width="10%"><s:text name="programmeSite.heading.names" /></th>
					<th align="center" width="10%"><s:text name="programmeSite.heading.siteName" /></th>
					<th align="center" width="5%"><s:text name="programmeSite.heading.orderId" /></th>
					<th align="center" width="10%"><s:text name="programmeSite.heading.firstLogo" /></th>
					<th align="center" width="5%"><s:text name="programmeSite.heading.episodeCollected" /></th>
					<th align="center" width="5%"><s:text name="programmeSite.heading.completed" /></th>
					<th align="center" width="5%"><s:text name="programmeSite.heading.blocked" /></th>
					<th align="center" width="10%"><s:text name="programmeSite.heading.operation" /></th>
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="center"><input type="checkbox" name="batchdeleteids" value="<s:property value="id" />" /></td>
						<td align="left"><s:property value="name"/></td>
						<td align="left"><s:property value="siteName" /></td>
						<td align="left"><s:property value="orderId" /></td>
						<td align="left"><img src="<s:property value="firstLogo"/>" /></td>
						<td align="left"><s:property value="episodeCollected" /></td>
						<td align="left">
							<s:if test='completed == 0'>
								否
							</s:if>
							<s:if test='completed == 1'>
								是
							</s:if>
						</td>
						<td align="left">
							<s:if test='blocked == 0'>
								否
							</s:if>
							<s:if test='blocked == 1'>
								是
							</s:if>
						</td>
						<td align="center">
						<a class="listbutton"
							href="<s:url action="ProgrammeSite_input">
								<s:param name="programmeSiteId" value="id"/>
								<s:param name="siteFilter" value="siteFilter"/>
								<s:param name="statusFilter" value="statusFilter"/>
								<s:param name="searchWord" value="searchWord"/>
								<s:param name="pageNumber" value="pageNumber"/>
							</s:url>">
						<s:text name="programmeSite.edit" /> </a> 
							<br />
						   &nbsp; <a class="listbutton"
							href="<s:url action="ProgrammeEpisode_list"><s:param name="programmeSiteId" value="id"/></s:url>">
						<s:text name="programmeSite.episodeSearch" /> </a></td>
					</tr>
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td><!-- <a href="#" id="batchdelete">删除</a>--></td>
						<td colspan="10">
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="ProgrammeSite_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="searchWord" value="searchWord" />
								<s:param name="siteFilter" value="siteFilter" />
								<s:param name="statusFilter"><s:property value="statusFilter"/></s:param>
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="ProgrammeSite_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="searchWord" value="searchWord" />
								<s:param name="siteFilter" value="siteFilter" />
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
        <div class="select"> <strong>Other Pages: </strong>
           <input type="hidden" id="pageurl" value="<s:url action="ProgrammeSite_list">
								<s:param name="searchWord" value="searchWord" />
								<s:param name="siteFilter" value="siteFilter" />
								<s:param name="statusFilter"><s:property value="statusFilter"/></s:param>
								<s:param name="pageNumber" value="0" />
							</s:url>" />
          <select id="pageselect">
          		<s:bean name="org.apache.struts2.util.Counter" id="counter">  
     			 <s:param name="first" value="1"/>  
     			 <s:param name="last" value="10"/>  
   				 	<s:iterator status="count">  
   				 		<s:if test="(pageInfo.currentPageNumber - #count.index) > 0">
     					 <option><s:property value="pageInfo.currentPageNumber - #count.index"/></option>
     					 </s:if> 
    				</s:iterator>  
   			 	</s:bean> 
          		 
          	   <s:bean name="org.apache.struts2.util.Counter" id="counter">  
     			 <s:param name="first" value="1"/>  
     			 <s:param name="last" value="10"/>  
   				 	<s:iterator status="count">  
   				 		<s:if test="(pageInfo.currentPageNumber + #count.index) < pageInfo.totalPageNumber">
     					 	<option><s:property value="#count.index + pageInfo.currentPageNumber + 1"/></option>
     					</s:if>  
    				</s:iterator>  
   			 	</s:bean>  
          </select>
        </div>
      </div>
      
    </div>
    
  

  </div>
  <div id="footer"></div>
</div>

 <div id="urldialog" title="搜酷直达区版本url">

	
	URL： <input type="text"  id="importingUrl" name="importingUrl"/>
	<input id="programmeId" type="hidden" value="" />
 </div>
</body>
</html>
