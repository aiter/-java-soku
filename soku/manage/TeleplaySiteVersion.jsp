<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="teleplaySiteVersion.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="teleplaySiteVersion.title.edit"/></title>
    </s:if>
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	
	<script type="text/javascript">
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


		

		$('#teleplaySiteVersionsubmit').click(function() {
			/**var teleplaySiteVersionname = $("#TeleplaySiteVersion_save_teleplaySiteVersion_name"),	
			teleplaySiteVersionurl = $("#TeleplaySiteVersion_save_teleplaySiteVersion_url"),		
			teleplaySiteVersionsort = $("#TeleplaySiteVersion_save_teleplaySiteVersion_sort"),
			allRequiredFields = $([]).add(teleplaySiteVersionname).add(teleplaySiteVersionurl).add(teleplaySiteVersionsort);
			
			var bValid = true;
			allRequiredFields.removeClass('ui-state-error');

			bValid = bValid && checkLength(teleplaySiteVersionname, '栏目名称', 1, 200);

			bValid = bValid && checkLength(teleplaySiteVersionurl, '栏目链接地址', 1, 1000);
			bValid = bValid && checkRegexp(teleplaySiteVersionurl,/^http\:\/\/(\S)+?$/i,"栏目链接地址格式不正确");
			
			bValid = bValid && checkLength(teleplaySiteVersionsort, '排序', 1, 300);
			bValid = bValid && checkRegexp(teleplaySiteVersionsort,/^[0-9]+$/i,"排序为数字");

			
			if(!bValid)
				return false;
			else
				$('#TeleplaySiteVersion_save').submit();
			return false;**/
			$('#TeleplaySiteVersion_save').submit();
		});

	

	});
</script>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="img/logo.gif" width="101" height="29" alt="" /></a>
    
  </div>
  <div id="middle">
    <s:include value="module/leftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
        <h1><s:text name="soku.teleplaySiteVersion.title" /></h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="TeleplaySiteVersion_save" validate="false" cssClass="form" >

    <s:token />
    <s:hidden name="task"/>
    <s:hidden name="teleplaySiteVersionId" />
    <s:hidden name="namesId" />
    <s:hidden name="teleVersionId" />
    <s:hidden name="pageNumber" />
    <s:hidden name="searchWord" />
    <s:hidden name="siteFilter" />
    <s:hidden name="statusFilter" />
    <s:textfield key="teleplaySiteVersion.names" cssClass="text" disabled="true"></s:textfield>
    <s:textfield key="teleplaySiteVersion.versionName" cssClass="text" disabled="true"></s:textfield>
    <s:select key="teleplaySiteVersion.sourceSite" list="sitesMap" listKey="key" listValue="value"></s:select>
    <s:textfield key="teleplaySiteVersion.orderId" cssClass="text"/>
    <s:textfield key="teleplaySiteVersion.firstLogo" cssClass="text"/>    
	<s:textfield key="teleplaySiteVersion.episodeCollected" cssClass="text"/>
	<s:select key="teleplaySiteVersion.hd" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
	<s:select key="teleplaySiteVersion.completed" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
	<s:select key="teleplaySiteVersion.locked" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
	<s:select key="teleplaySiteVersion.blocked" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
	<s:textfield key="teleplaySiteVersion.viewUrl" cssClass="text"/>
	<s:textfield key="teleplaySiteVersion.detailUrl" cssClass="text"/>
    

</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="teleplaySiteVersionsubmit">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
			</a>
			<a href="<s:url action="TeleplaySiteVersion_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
				<s:text name="button.cancel" />
			</a>
		</div>
      </div>
    </div>
    
  </div>
  <div id="footer"></div>
</div>
</body>
</html>