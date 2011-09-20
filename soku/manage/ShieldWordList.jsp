<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>屏蔽词管理</title>


	
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

			var options = { 
			        //target:        '#output',   // target element(s) to be updated with server response 
			        beforeSubmit:  showRequest,  // pre-submit callback 
			        success:       showResponse  // post-submit callback 
			    }; 
			 
			    // bind form using 'ajaxForm' 
			    $('#ShieldWord_importCsv').ajaxForm(options); 
			
			 
			// pre-submit callback 
			function showRequest(formData, jqForm, options) { 
			    return true; 
			} 
			 
			// post-submit callback 
			function showResponse(responseText, statusText)  { 
			    var o = eval('(' + responseText + ')');  			
		        var html = o.notImportWords;
		        alert(html);		     	   
			} 
			

			$("#dialog").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				modal: true,
				buttons: {
				'取消' : function() {
					$(this).dialog('close');
				},
					'确定': function() {
						var bValid = true;
							
						if (bValid) {
							$('#ShieldWord_importCsv').submit();
							$(this).dialog('close');
						}
				}
				
			},
			close : function() {
			}
		});

			$('#exportcsv').click(function() {
				$('#exportcsvflag').val(1);
				$('#ShieldWord_list').submit();
			});


			$("#expireDate").datepicker({dateFormat: 'yy-mm-dd'});
		
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
				$("#ShieldWord_batchdelete").submit();
			 } else {
				 alert("没有数据要删除");
				 return false;
			 }
		 });
		});

		function exportfile() {
			$('#exportcsvflag').val(1);
			$('#ShieldWord_list').submit();
			$('#exportcsvflag').val(0);
		}

		function importfile() {
			$('#dialog').dialog('open');
		}

		function clearexportflag() {
			$('#exportcsvflag').val(0);
		}
		
	</script>
</head>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"></a>
   
  </div>
  <div id="middle">
    <s:include value="module/shieldleftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
      <h1>屏蔽词管理</h1>
      <a href="<s:url action="ShieldWord_input">
			<s:param name="wordId" value="-1" />
		</s:url>"  class="shieldbutton">添加屏蔽词</a>	
      <a href="<s:url action="ShieldVideo_list"></s:url>" class="shieldbutton">删除单个视频</a>
      <a href="#" class="shieldbutton" onclick="importfile(); return false">批量上传</a>
      <a href="#" class="shieldbutton" onclick="exportfile(); return false">批量下载</a>
      <a href="<s:url action="ShieldMailSetting_list"></s:url>" class="shieldbutton">邮件订阅管理</a>
      </div>
      <br />
      <div class="select-bar">
         <s:form action="ShieldWord_list" validate="false" cssClass="form" theme="simple">
	       
      		关键词搜索： <s:textfield key="searchParameter.keyword" cssClass="text"/>
      		
      		选择频道： <s:select key="searchParameter.channel" list="shieldChannelMap" listKey="key" listValue="value"></s:select>
      		
	                     关键词分类： <s:select key="searchParameter.wordCategory" list="shieldCategoryMap" listKey="key" listValue="value"></s:select>
	                  
	                     站点级别：<s:select key="searchParameter.siteLevel" list="siteLevelMap" listKey="key" listValue="value"></s:select>
	       
	                     命中规则：<s:select key="searchParameter.hitRole" list="hitRoleMap" listKey="key" listValue="value"></s:select>
	       	<br /> 
	                 
	                     添加时间：<s:textfield key="searchParameter.expireDate" id="expireDate"></s:textfield>
	                     修改人员: <s:textfield key="searchParameter.modifier" cssClass="text"></s:textfield>
	                 <s:hidden name="searchParameter.type" />
	                     排序字段：<s:select key="searchParameter.orderby" list="orderByMap" listKey="key" listValue="value"></s:select>
	       	排序：          <s:select key="searchParameter.trend" list="trendMap" listKey="key" listValue="value"></s:select>
	         
	        <label>
	        <input type="submit" name="Submit" class="ui-button ui-state-default ui-corner-all" value="Search" />
	        <input type="hidden" name="exportcsvflag" id="exportcsvflag" value="0" />
	        
	        </label>
      	</s:form> 
      </div>
      <div class="table"> <img src="/soku/manage/img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="/soku/manage/img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <s:form action="ShieldWord_batchdelete" validate="false" cssClass="form" theme="simple">
        <table class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="5%"><input id="thcheck" type="checkbox" />多选</th>
					<th align="center" width="15%">关键词</th>
					<th align="center" width="10">关键词分类</th>
					<th align="center" width="10%">命中规则</th>
					<th align="center" width="20%">生效频道</th>
					<th align="center" width="10%">站点级别</th>
					<th align="center" width="10%">更新时间</th>
					<th align="center" width="10%">修改人员</th>
					<th align="center" width="10%"><s:text name="item.heading.operation" /></th>
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="center"><input type="checkbox" name="batchdeleteids" value="<s:property value="id" />" /></td>
						<td align="left"><s:property value="word"/></td>
						<td align="left"><s:property value="wordCategoryStr" /></td>
						<td align="left"><s:property value="hitRoleStr" /></td>
						<td align="left"><s:property value="shieldChannelStr" /></td>
						<td align="left"><s:property value="determinWordInfo" /></td>
						<td align="left"><s:date name="updateTime" format="yyyy-MM-dd HH:mm:ss" /></td>
						<td align="left"><s:property value="modifier" /></td>
						<td align="center"><a name="deleteLink" class="listbutton"
							href="<s:url action="ShieldWord_delete"><s:param name="wordId" value="id"/>
							<s:param name="searchParameter.type" value="searchParameter.type" />
							</s:url>">
						<s:text name="item.delete" /> </a> &nbsp; <a class="listbutton"
							href="<s:url action="ShieldWord_input"><s:param name="wordId" value="id"/>
							<s:param name="searchParameter.type" value="searchParameter.type" />
							</s:url>">
						<s:text name="item.edit" /> </a></td>
					</tr>
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td><a href="#" id="batchdelete">删除</a></td>
						<td colspan="8">
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="ShieldWord_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="searchParameter.trend" value="searchParameter.trend" />
								<s:param name="searchParameter.orderby" value="searchParameter.orderby" />
								<s:param name="searchParameter.type" value="searchParameter.type" />
								<s:param name="searchParameter.modifier" value="searchParameter.modifier" />
								<s:param name="searchParameter.expireDate" value="searchParameter.expireDate" />
								<s:param name="searchParameter.hitRole" value="searchParameter.hitRole" />
								
								<s:param name="searchParameter.siteLevel" value="searchParameter.siteLevel" />
								<s:param name="searchParameter.wordCategory" value="searchParameter.wordCategory" />
								<s:param name="searchParameter.channel" value="searchParameter.channel" />
								<s:param name="searchParameter.keyword" value="searchParameter.keyword" />
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="ShieldWord_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="searchParameter.trend" value="searchParameter.trend" />
								<s:param name="searchParameter.orderby" value="searchParameter.orderby" />
								<s:param name="searchParameter.type" value="searchParameter.type" />
								<s:param name="searchParameter.modifier" value="searchParameter.modifier" />
								<s:param name="searchParameter.expireDate" value="searchParameter.expireDate" />
								<s:param name="searchParameter.hitRole" value="searchParameter.hitRole" />
								
								<s:param name="searchParameter.siteLevel" value="searchParameter.siteLevel" />
								<s:param name="searchParameter.wordCategory" value="searchParameter.wordCategory" />
								<s:param name="searchParameter.channel" value="searchParameter.channel" />
								<s:param name="searchParameter.keyword" value="searchParameter.keyword" />
							</s:url>"><s:text
								name="nextpage"/></a>
						</s:if></td>
					</tr>
				</tfoot>
			</table>
			</s:form>
        <div class="select"> <strong>Other Pages: </strong>
           <input type="hidden" id="pageurl" value="<s:url action="ShieldWord_list">
								<s:param name="searchWord" value="searchWord" />
								<s:param name="shieldWordFilter" value="shieldWordFilter" />
								<s:param name="searchParameter.trend" value="searchParameter.trend" />
								<s:param name="searchParameter.orderby" value="searchParameter.orderby" />
								<s:param name="searchParameter.type" value="searchParameter.type" />
								<s:param name="searchParameter.modifier" value="searchParameter.modifier" />
								<s:param name="searchParameter.expireDate" value="searchParameter.expireDate" />
								<s:param name="searchParameter.hitRole" value="searchParameter.hitRole" />
								
								<s:param name="searchParameter.siteLevel" value="searchParameter.siteLevel" />
								<s:param name="searchParameter.wordCategory" value="searchParameter.wordCategory" />
								<s:param name="searchParameter.channel" value="searchParameter.channel" />
								<s:param name="searchParameter.keyword" value="searchParameter.keyword" />
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
  
	<div id="dialog" title="上传CSV文件"><s:form
		action="ShieldWord_importCsv" validate="false" cssClass="form"
		enctype="multipart/form-data">		
		<input type="hidden" value="1" name="importWordType" />
		<s:file name="file" label="上传文件" />
	</s:form></div>
</div>
</body>
</html>
