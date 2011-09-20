<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<s:if test="task=='Create'">
	<title><s:text name="shieldSite.title.create" /></title>
</s:if>
<s:if test="task=='Edit'">
	<title><s:text name="shieldSite.title.edit" /></title>
</s:if>
<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
</head>

<script type="text/javascript"
	src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>

<script type="text/javascript"><!--
		$(function() {
			
			var name = $("#name"),				
				allFields = $([]).add(name),
				tips = $("#validateTips");
	
			function updateTips(t) {
				tips.text(t);
			}
	
			function checkLength(o,n,min,max) {
	
				if ( o.val().length > max || o.val().length < min ) {
					o.addClass('ui-state-error');
					updateTips(n + " 的长度应该在 " + min + " 和 "+max+"之间.");
					return false;
				} else {
					return true;
				}
	
			}
	
			function checkRegexp(o,regexp,n) {
	
				if ( !( regexp.test( o.val() ) ) ) {
					o.addClass('ui-state-error');
					updateTips(n);
					return false;
				} else {
					return true;
				}
	
			}


		

		$('#shieldSitesubmit').click(function() {
			var shieldSitename = $("#ShieldSite_save_shieldSite_name"),	
			shieldSiteurl = $("#ShieldSite_save_shieldSite_url"),		
			shieldSitesort = $("#ShieldSite_save_shieldSite_sort"),
			allRequiredFields = $([]).add(shieldSitename).add(shieldSiteurl).add(shieldSitesort);
			
			var bValid = true;
			/**
			allRequiredFields.removeClass('ui-state-error');

			bValid = bValid && checkLength(shieldSitename, '栏目名称', 1, 200);

			bValid = bValid && checkLength(shieldSiteurl, '栏目链接地址', 1, 1000);
			bValid = bValid && checkRegexp(shieldSiteurl,/^http\:\/\/(\S)+?$/i,"栏目链接地址格式不正确");
			
			bValid = bValid && checkLength(shieldSitesort, '排序', 1, 300);
			bValid = bValid && checkRegexp(shieldSitesort,/^[0-9]+$/i,"排序为数字");
			*/


			$('#ShieldSite_save').submit();
				
			//return false;
		});

	

	});
--></script>
<body>
<div id="main">
<div id="header"><a href="#" class="logo"><img
	src="img/logo.gif" width="101" height="29" alt="" /></a></div>
<div id="middle"><s:include value="module/shieldleftnav.jsp"></s:include>
<div id="center-column">
<div class="top-bar">
<h1><s:text name="soku.shieldSite.title" /></h1>

</div>
<br />


<div class="table"><s:actionerror cssClass="actionerror" />
<p id="validateTips"></p>
<s:form action="ShieldSite_save" validate="false" cssClass="form" theme="simple">

	<s:token />
	<s:hidden name="task" />
	<s:hidden name="shieldSiteId" />

	站点名称： <s:textfield key="shieldSite.name" cssClass="text" />

	站点url： <s:textfield key="shieldSite.url" cssClass="text" />
	<fieldset>
		<legend>站点属性（单选）</legend> 
		<s:radio list="radioSiteCategoryMap" listKey="key" listValue="value" key="shieldSite.siteCategory"></s:radio>
	</fieldset>

	<fieldset>
		<legend>站点分级（单选）</legend>
		<s:radio list="radioSiteLevelMap" listKey="key" listValue="value" key="shieldSite.siteLevel"></s:radio>
	</fieldset>
</s:form>
<p>&nbsp;</p>
<div class="buttom"><a href="#" class="button"
	id="shieldSitesubmit"> <s:if test="task == 'Create'">
	<s:text name="button.create" />
</s:if> <s:if test="task == 'Edit'">
	<s:text name="button.edit" />
</s:if> </a> <a
	href="<s:url action="ShieldSite_list"><s:param name="pageNumber" value="1" /></s:url>"
	class="button"> <s:text name="button.cancel" /> </a></div>
</div>
</div>

</div>
<div id="footer"></div>
</div>
</body>
</html>