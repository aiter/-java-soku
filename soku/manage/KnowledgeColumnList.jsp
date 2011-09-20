<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>知识栏目列表</title>


	<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
	<link href="<s:url value="/soku/manage/css/css_tree/_styles.css"/>" rel="stylesheet"
	type="text/css" />
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	
	<script type="text/javascript">
		$(function() {
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
				$("#KnowledgeColumn_batchdelete").submit();
			 } else {
				 alert("没有数据要删除");
				 return false;
			 }
			 return false;
		 });
		});
	</script>
</head>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="img/logo.gif" width="101" height="29" alt="" /></a>
   		
  </div>
  <div id="middle">
   <s:if test='shieldKnowledgeColumn'>
   	<s:include value="module/shieldleftnav.jsp"></s:include>
   </s:if>
   <s:else><s:include value="module/leftnav.jsp"></s:include></s:else>
    
    <div id="center-column">
      <div class="top-bar"> 
      <a href="<s:url action="KnowledgeColumn_input">
      		<s:param name="parentId" value="parentId"/>
			<s:param name="knowledgeColumnId" value="-1" />
		</s:url>"  class="button">添加本级栏目</a>
        <h1>知识栏目列表</h1>
        
      
      </div>
      <br />
      <div class="select-bar">
           <s:form action="KnowledgeColumn_list" validate="false" cssClass="form" theme="simple">
	        <s:if test="itemFilter!=null">
			</s:if>
      		<label>
	        <s:textfield key="searchWord" cssClass="text"/>
	        </label>
	        <label>
	        <input type="submit" name="Submit" class="ui-button ui-state-default ui-corner-all" value="Search" />
	        </label>
      	</s:form> 
      </div>
        <h2>
        <s:if test="parentColumns.size() > 0">
        				<a name="addLink" class="listbutton"
										href="<s:url action="KnowledgeColumn_list"><s:param name="parentId" value="0"/></s:url>">
											知识首页</a> --&gt;</s:if>
        <s:iterator value="parentColumns" status="index">
        	<s:if test="!#index.last">
        		<a name="addLink" class="listbutton"
										href="<s:url action="KnowledgeColumn_list"><s:param name="parentId" value="id"/></s:url>">
											<s:property value="name" /></a>---&gt;
			</s:if>
			<s:else>
				<s:property value="name" />
			</s:else>
        </s:iterator></h2>
        <div class="table"><s:property value="treeView" escape="false"/></div>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
       	<s:form action="KnowledgeColumn_batchdelete" validate="false" cssClass="form" theme="simple">
        <table class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="5%"><input id="thcheck" type="checkbox" /></th>
					<th align="center" width="20%">创建时间</th>
					<th align="center" width="20%">栏目名称</th>
					<th align="center" width="35%">子栏目</th>
					<th align="center" width="20%">操作</th>
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="center"><input type="checkbox" name="batchdeleteids" value="<s:property value="knowledgeColumnId" />" /></td>
						<td align="left"><s:date name="createTime" format="yyyy-MM-dd"/></td>						
						<td align="left"><s:property value="name" /></td>
						<td align="center" >
							<!--<table>
							<s:iterator value="childColumn" status="siteIndex">
								<tr>
									<td><s:property value="name" /></td>
									<td><s:if test="pic.length() > 0"><img src="<s:property value="pic" />" /></s:if></td>
									<td align="center"><a name="deleteLink" class="listbutton"
										href="<s:url action="KnowledgeColumn_delete"><s:param name="knowledgeColumnId" value="id"/></s:url>">
									删除 </a> &nbsp; <a class="listbutton"
										href="<s:url action="KnowledgeColumn_input"><s:param name="knowledgeColumnId" value="id"/></s:url>">
									修改 </a> <br />
									<a name="addLink" class="listbutton"
										href="<s:url action="KnowledgeColumn_list"><s:param name="parentId" value="id"/></s:url>">
											查看子栏目 </a> &nbsp; 
									<a name="addLink" class="listbutton"
										href="<s:url action="KnowledgeColumn_input"><s:param name="parentId" value="id"/><s:param name="knowledgeColumnId" value="-1" /></s:url>">
											添加子栏目 </a> &nbsp; 
						
									</td>
								</tr>
							</s:iterator>
							</table> -->
							<s:if test="pic.length() > 0"><img src="<s:property value="pic" />" /></s:if>
						</td>
						<td align="center">
						
						<a name="addLink" class="listbutton"
										href="<s:url action="KnowledgeColumn_list"><s:param name="parentId" value="id"/></s:url>">
											查看子栏目 </a> &nbsp; <br />
						<a name="addLink" class="listbutton"
							href="<s:url action="KnowledgeColumn_input"><s:param name="parentId" value="id"/><s:param name="knowledgeColumnId" value="-1" /></s:url>">
						添加子栏目 </a> &nbsp; <br />
						
						
						<a class="listbutton"
							href="<s:url action="KnowledgeColumn_input"><s:param name="knowledgeColumnId" value="id"/></s:url>">
						修改 </a> <br />
						
						<a name="deleteLink" class="listbutton"
							href="<s:url action="KnowledgeColumn_delete"><s:param name="knowledgeColumnId" value="id"/></s:url>">
						删除 </a> &nbsp; </td> 
					</tr>
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td><a href="#" id="batchdelete">删除</a></td>
						<td colspan="4">
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
						<a href="<s:url action="KnowledgeColumn_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="searchWord" value="searchWord" />
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="KnowledgeColumn_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="searchWord" value="searchWord" />
							</s:url>"><s:text
								name="nextpage"/></a>
						</s:if></td>
					</tr>
				</tfoot>
			</table>
			</s:form>
        <div class="select"> <strong>Other Pages: </strong>
           <input type="hidden" id="pageurl" value="<s:url action="KnowledgeColumn_list">
								<s:param name="searchWord" value="searchWord" />
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
</body>
</html>
