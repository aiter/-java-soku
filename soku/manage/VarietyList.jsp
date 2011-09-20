<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="variety.title.list" /></title>

	<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
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
				$("#Variety_batchdelete").submit();
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
    <s:include value="module/leftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
      
        <h1><s:text name="soku.variety.title" /></h1>
      </div>
      <br />
      <div class="select-bar">
         <s:form action="Variety_list" validate="false" cssClass="form" theme="simple">
	        <s:if test="varietyFilter!=null">
			</s:if>
      		<label>
	        <s:textfield key="searchWord" cssClass="text"/>
	        </label>
	        <label>
	        <input type="submit" name="Submit" class="ui-button ui-state-default ui-corner-all" value="Search" />
	        </label>
      	</s:form> 
      </div>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <s:form action="Variety_batchdelete" validate="false" cssClass="form" theme="simple">
         <table class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="5%"><input id="thcheck" type="checkbox" /></th>
					<th align="center" width="10%"><s:text name="variety.heading.names" /></th>
					<th align="center" width="10%"><s:text name="variety.heading.name" /></th>
					<th align="center" width="10%"><s:text name="variety.heading.cates" /></th>
					<th align="center" width="15%"><s:text name="variety.heading.area" /></th>
					<th align="center" width="10%"><s:text name="variety.heading.haveRight" /></th>
					<th align="center" width="10%"><s:text name="variety.heading.operation" /></th>
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="center"><input type="checkbox" name="batchdeleteids" value="<s:property value="id" />" /></td>
						<td align="left"><s:property value="names"/></td>
						<td align="left"><s:property value="name" /></td>
						<td align="left"><s:property value="cates" /></td>
						<td align="center"><s:property value="area" /></td>
						<td align="left">
							<s:if test='haveRight == 0'>
								否
							</s:if>
							<s:if test='haveRight == 1'>
								是
							</s:if>
						</td>
						<td align="center">
						<a name="deleteLink" class="listbutton"
							href="<s:url action="Variety_delete"><s:param name="varietyId" value="id"/><s:param name="searchWord" value="searchWord" />
									<s:param name="pageNumber" value="pageNumber" /></s:url>">
									<s:text name="variety.delete" /> </a> &nbsp; 
						<a class="listbutton"
							href="<s:url action="Variety_input"><s:param name="varietyId" value="id"/><s:param name="searchWord" value="searchWord" />
									<s:param name="pageNumber" value="pageNumber" /></s:url>">
									<s:text name="variety.edit" /> </a>   &nbsp;  
						<a class="listbutton" 
							href="<s:url action="VarietySub_list"><s:param name="varietyId" value="id" /><s:param name="varietySubId" value="-1" />
									</s:url>"><s:text
            							name="varietySub.list"/></a></td>
					</tr>
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td><a href="#" id="batchdelete">删除</a></td>
						<td colspan="7">
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="Variety_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="searchWord" value="searchWord" />
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="Variety_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="searchWord" value="searchWord" />
							</s:url>"><s:text
								name="nextpage"/></a>
						</s:if></td>
					</tr>
				</tfoot>
			</table>
			<s:hidden name="pageNumber" />
    		<s:hidden name="searchWord" />
			</s:form>
        <div class="select"> <strong>Other Pages: </strong>
           <input type="hidden" id="pageurl" value="<s:url action="Variety_list">
								<s:param name="searchWord" value="searchWord" />
								<s:param name="varietyFilter" value="varietyFilter" />
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
