<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="user.title.list" /></title>


<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
</head>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="img/logo.gif" width="101" height="29" alt="" /></a>
   		
  </div>
  <div id="middle">
    <s:if test='shieldUser'>
   	<s:include value="module/shieldleftnav.jsp"></s:include>
   </s:if>
   <s:else><s:include value="module/leftnav.jsp"></s:include></s:else>
    <div id="center-column">
   		<s:property value="session.user_permission" />
    </div>
    
  </div>
  <div id="footer"></div>
</div>
</body>
</html>
