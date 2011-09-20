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
	<script type="text/javascript">
	$(function() {

		$("#pageselect").change(function() {
			location.href = $("#pageurl").val() + $("#pageselect").val();
		});

		$(".site_toggle").each(function() {

			$(this).find("h3").click(function() {
				$(this).parent().find("div").toggle();

			});
			$(this).find("div").each(function() {
				$(this).toggle();
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
      
        <h1>新剧发现</h1>
      </div>
      <br />
      <div class="select-bar">
       	<s:form action="NewProgramme_list" validate="false" cssClass="form" theme="simple">	      
	        <s:textfield key="searchWord" cssClass="text"/>	       
	        <input type="submit" name="Submit" class="ui-button ui-state-default ui-corner-all" value="Search" />
      	</s:form> 
      </div>
      

    
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <table id="sorttable" class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="20%">剧名</th>
					<th align="center" width="10%">类型</th>
					
					<th align="center" width="30%">收录集数</th>
					<th align="center" width="30%">详细资料</th>
					<th align="center" width="10%"></th>
				</tr>

				<s:iterator value="pageInfo.results" status="index" >
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="center">
							<s:property value="name"/>							
						</td>
						<td align="center">
						<s:property value="cateName"/>
						</td>
						<td align="left">
								<div class="site_toggle">
										<s:if test="urls.size > 0">
											<h3>查看剧集</h3>		
										</s:if>															
										<div>
										<table width="100%" cellpadding="0" cellspacing="0">
											<s:iterator value="urls" >
												<tr>													
												
													<td width="50%">
														<s:if test="cate == 1 || cate == 5"><a target="_blank" title="<s:property value="title"/>" href="<s:property value="url"/>">第<s:property value="orderId"/>集</a></s:if>
														<s:if test="cate == 2"><a target="_blank" title="<s:property value="title"/>" href="<s:property value="url"/>">播放</a></s:if>
														<s:if test="cate == 3"><a target="_blank" title="<s:property value="title"/>" href="<s:property value="url"/>">第<s:property value="orderStage"/>期</a></s:if>
													</td>
														
												</tr>
											</s:iterator>
										</table>
										</div>
									</div>
							
						</td>
						
						<td align="center">
							<table>
								<tr><td>站点：</td><td><a href="<s:property value="playDefault"/>" ><s:property value="siteName"/></a></td></tr>
								<tr><td>别名: </td><td><s:property value="alias"/></td></tr>
								<tr><td>类型：</td><td><s:property value="type"/></td></tr>
								<tr><td>导演：</td><td><s:property value="director"/></td></tr>
								<tr><td>演员: </td><td><s:property value="actor"/></td></tr>
								<tr><td>图片： </td><td><s:if test="image.length() > 0"><img src="<s:property value="image"/>"/></s:if></td></tr>
								<tr><td>年代</td><td><s:property value="year"/></td></tr>
								<tr><td></td><td></td></tr>
								<tr><td></td><td></td></tr>
								
							</table>
						</td>
						
						<td><a name="deleteLink" class="listbutton"
							href="<s:url action="NewProgramme_delete"><s:param name="programmeId" value="id"/></s:url>">
						删除 </a></td>
						
					</tr>
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td colspan="7"> <s:property
					value="pageInfo.totalRecords" /> ----
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="NewProgramme_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="searchWord" value="searchWord"/>	
								<s:param name="accuratelyMatched" value="accuratelyMatched"/>
								<s:param name="concernLevel" value="concernLevel"/>
								<s:param name="siteId" value="siteId"></s:param>
								<s:param name="categoryFilter" value="categoryFilter"></s:param>
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="NewProgramme_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="searchWord" value="searchWord"/>	
								<s:param name="accuratelyMatched" value="accuratelyMatched"/>
								<s:param name="concernLevel" value="concernLevel"/>
								<s:param name="siteId" value="siteId"></s:param>
								<s:param name="categoryFilter" value="categoryFilter"></s:param>
							</s:url>"><s:text
								name="nextpage"/></a>
						</s:if></td>
					</tr>
				</tfoot>
			</table>
       
				       <div class="select"><strong>Other Pages: </strong> <input
					type="hidden" id="pageurl"
					value="<s:url action="NewProgramme_list">
												<s:param name="searchWord" value="searchWord"/>	
												<s:param name="accuratelyMatched" value="accuratelyMatched"/>
												<s:param name="concernLevel" value="concernLevel"/>
												<s:param name="siteId" value="siteId"></s:param>
												<s:param name="categoryFilter" value="categoryFilter"></s:param>
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
