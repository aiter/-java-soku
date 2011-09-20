<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<title>节目跳转词</title>
    <link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
	<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
	
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	


	
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


		

		$('#programmeForwardWordsubmit').click(function() {
			<s:if test="task=='Create'">
			var programmeForwardWordname = $("#ProgrammeForwardWord_save_forwardWord_forwardWord"),	
			allRequiredFields = $([]).add(programmeForwardWordname);
			
			var bValid = true;
			allRequiredFields.removeClass('ui-state-error');

			bValid = bValid && checkLength(programmeForwardWordname, '跳转词', 1, 50);
			


			if(!bValid)
				return false;
			</s:if>

			$('#ProgrammeForwardWord_save').submit();
			return false;
		});

	

	});

		function changeLogo() {
			$('#imagedialog').dialog('open');
			$("#FileUpload_upload").val("");
			$("#picPath").val("");
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
        <h1>节目跳转词</h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="ProgrammeForwardWord_save" validate="false" cssClass="simple" >

    <s:token />
    <s:hidden name="task"/>
    <s:hidden name="forwardWordId" />
    
    	<s:select key="forwardWord.cate" label="分类" list="forwardWordCategory" listKey="key" listValue="value" cssClass="text"></s:select>
   		<s:textfield key="forwardWord.forwardWord"  label="跳转词" cssClass="text"/>
		<s:textfield key="forwardWord.forwardUrl" label="跳转Url" cssClass="text"/>	

</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="programmeForwardWordsubmit">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
			</a>
			<a href="<s:url action="ProgrammeForwardWord_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
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