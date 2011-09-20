<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>手动导入</title>

	<link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
	<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
	
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	
	<script type="text/javascript"><!--
		
	--></script>
</head>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="img/logo.gif" width="101" height="29" alt="" /></a>
   
  </div>
  <div id="middle">
    <s:include value="module/leftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
      	
        <h1><s:property value="subject.s.name"/> (版本名： <s:property value="subject.v.name"/>)</h1>
      </div>
      <br />
      <div class="select-bar">
     	<s:form action="VersionImportDetail_save" validate="false" cssClass="form" theme="simple">
      	<s:if test="importStatus < 0">
      	库中已存在的版本：
      	<input type="hidden" value='<s:property value="subject.s.cates" />' name="cateId"/>
      	<input type="hidden" value='<s:property value="siteVersion" />' name="siteVersion"/>
      	<input type="hidden" value='<s:property value="subject.s.id" />' name="spiderVersionId"/>
      	<s:if test="subject.s.cates == 1">
      	    选择一个版本导入：
      		<s:iterator value="teleplayVersionList" status="index">
      			<input type="radio" value='<s:property value="id"/>' name="versionId"/><label><s:property value="name"/></label>
      		</s:iterator> <br />
      		<input type="radio" value='-1' name="versionId"/><label> 新建一个</label>
      	
      	  剧集名称：<input name="names" type="text" value='<s:property value="subject.s.name"/>'/> 版本名: <input name="versionName" type="text" />
      	</s:if>
      	
      	<s:if test="subject.s.cates == 2">
      	    选择一个版本导入：
      		<s:iterator value="movieList" status="index">
      			<input type="radio" value='<s:property value="id"/>' name="versionId"/><label><s:property value="name"/></label>
      		</s:iterator> <br />
      		<input type="radio" value='-1' name="versionId"/><label> 新建一个</label>
      	
      	  剧集名称：<input name="names" type="text" value='<s:property value="subject.s.name"/>'/> 版本名: <input name="versionName" type="text" />
      	</s:if>
      	
      	<s:if test="subject.s.cates == 3">
      		    选择一个版本导入：
      		<s:iterator value="varietyList	" status="index">
      			<input type="radio" value='<s:property value="id"/>' name="versionId"/><label><s:property value="name"/></label>
      		</s:iterator> <br />
      		<input type="radio" value='-1' name="versionId"/><label> 新建一个</label>
      	
      	  剧集名称：<input name="names" type="text" value='<s:property value="subject.s.name"/>'/> 
      	</s:if>
      	
      	<s:if test="subject.s.cates == 5">
      	    选择一个版本导入：
      		<s:iterator value="animeVersionList" status="index">
      			<input type="radio" value='<s:property value="id"/>' name="versionId"/><label><s:property value="name"/></label>
      		</s:iterator> <br />
      		<input type="radio" value='-1' name="versionId"/><label> 新建一个</label>
      	
      	  剧集名称：<input name="names" type="text" value='<s:property value="subject.s.name"/>'/> 版本名: <input name="versionName" type="text" />
      	</s:if>
      	
      	 <input type="submit" value="导入" />
      	 </s:if>	
      	</s:form> 
      </div>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <s:form action="CopyRightImport_save" validate="false" cssClass="form" theme="simple">
        <table class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" colspan="3"><s:property value="subject.s.name"/> -- <s:property value="subject.v.name"/></th>
				</tr>

			
				<s:iterator value="subject.es" status="index">
					<s:if test="subject.s.cates == 1">
					<tr>
						<td>第<s:property value="order_id"/>集</td>
						<td><img src='<s:property value="logo"/>' /></td>
						<td><a href='<s:property value="url"/>'>播放</a></td>
					</tr>
					</s:if>
					
					<s:elseif test="subject.s.cates == 5">
					<tr>
						<td>第<s:property value="order_id"/>集</td>
						<td><img src='<s:property value="logo"/>' /></td>
						<td><a href='<s:property value="url"/>'>播放</a></td>
					</tr>
					</s:elseif>
					
					<s:else>
					<tr>
						<td><s:property value="title"/></td>
						<td><img src='<s:property value="logo"/>' /></td>
						<td><a href='<s:property value="url"/>'>播放</a></td>
					</tr>
					</s:else>
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
