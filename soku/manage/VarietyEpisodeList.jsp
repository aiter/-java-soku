<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="varietyEpisode.title.list" /></title>


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
				$("#VarietyEpisode_batchdelete").submit();
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
      	
      	<a href="<s:url action="VarietyEpisode_input">
			<s:param name="varietyEpisodeId" value="-1" />
			<s:param name="varietySubId" value="varietySubId" />
		</s:url>"  class="button"><s:text
            name="varietyEpisode.add"/></a>
        <h1><a href="<s:url action="VarietySub_list">			
			<s:param name="varietyId" value="varietyId" />
			<s:param name="varietySubId" value="-1" />
		</s:url>"  >子节目列表</a> <span class="chevron"> » </span> <s:text name="soku.varietyEpisode.title" /></h1>
      </div>
      <br />
      <div class="select-bar">
         <s:form action="VarietyEpisode_list" validate="false" cssClass="form" theme="simple">
	        <s:if test="varietyEpisodeFilter!=null">
			</s:if>
      		
      	</s:form> 
      </div>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <s:form action="VarietyEpisode_batchdelete" validate="false" cssClass="form" theme="simple">
        <table class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="5%"><input id="thcheck" type="checkbox" /></th>
					<th align="center" width="10%"><s:text name="varietyEpisode.heading.names" /></th>
					<th align="center" width="10%"><s:text name="varietyEpisode.heading.title" /></th>
					<th align="center" width="10%"><s:text name="varietyEpisode.heading.siteName" /></th>
					<th align="center" width="10%"><s:text name="varietyEpisode.heading.logo" /></th>
					<th align="center" width="5%"><s:text name="varietyEpisode.heading.hd" /></th>
					<th align="center" width="5%">排序</th>
					<th align="center" width="10%"><s:text name="varietyEpisode.heading.operation" /></th>
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="center"><input type="checkbox" name="batchdeleteids" value="<s:property value="id" />" /></td>
						<td align="left"><s:property value="names"/></td>
						<td align="left"><s:property value="title"/></td>
						<td align="left"><s:property value="siteName" /></td>
						<td align="left"><img src="<s:property value="logo"/>" /></td>
						<td align="left">
							<s:if test='hd == 0'>
								否
							</s:if>
							<s:if test='hd == 1'>
								是
							</s:if>
						</td>
						<td align="left">
							<s:property value="orderId" />
						</td>
						<td align="center"><a name="deleteLink" class="listbutton"
							href="<s:url action="VarietyEpisode_delete"><s:param name="varietyEpisodeId" value="id"/><s:param name="varietySubId" value="varietySubId"/></s:url>">
						<s:text name="varietyEpisode.delete" /> </a> &nbsp; <a class="listbutton"
							href="<s:url action="VarietyEpisode_input"><s:param name="varietyEpisodeId" value="id"/><s:param name="varietySubId" value="varietySubId"/></s:url>">
						<s:text name="varietyEpisode.edit" /> </a>   </td>
					</tr>
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td><a href="#" id="batchdelete">删除</a></td>
						<td colspan="10">
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="VarietyEpisode_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="searchWord" value="searchWord" />
								<s:param name="siteFilter" value="siteFilter" />
								<s:param name="statusFilter"><s:property value="statusFilter"/></s:param>
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="VarietyEpisode_list">
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
			</s:form>
        <div class="select"> <strong>Other Pages: </strong>
           <input type="hidden" id="pageurl" value="<s:url action="VarietyEpisode_list">
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
    
    <div id="right-column"> <strong class="h"><s:text name="soku.categoryfilter" /></strong>
    	<div class="box">
    	<s:text name="soku.categoryfilter.name" />
    	<ul class="nav">
    		<li>
	    		<a href="<s:url action="VarietyEpisode_list"><s:param name="pageNumber" value="1" /></s:url>">
	    			<s:if test="siteFilter==null">
						<strong>
					</s:if>
        			<s:text name="soku.filter.all" />
        			<s:if test="siteFilter==null">
						</strong>
					</s:if>
	    		</a>
    		</li>
      		<s:iterator value="sitesMap" status="index">					
        	<li>
        		<a href="<s:url action="VarietyEpisode_list"><s:param name="siteFilter"><s:property value="key"/></s:param>
        					<s:param name="statusFilter"><s:property value="statusFilter"/></s:param>
        					<s:param name="searchWord" value="searchWord" />
        				</s:url>">
        			<s:if test="siteFilter==key">
						<strong>
					</s:if>
        			<s:property value="value"/>
        			<s:if test="siteFilter==key">
						</strong>
					</s:if>
        		</a>
        	</li>
       		</s:iterator>
       </ul>
</div>
    </div>

  </div>
  <div id="footer"></div>
</div>
</body>
</html>
