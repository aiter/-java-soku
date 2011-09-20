<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
     <title><s:text name="soku.login.title"/></title>
     
     <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	
	<script type="text/javascript">
		$(document).ready(function() {
			$("#Login_name").focus();
		});
		
		$(function() {			
			var tips = $("#validateTips");	
			function updateTips(t) {
				tips.text(t);
		}	
			
		$('#usersubmit').click(function() {
			submit();
		});

		$("#Login_password").keydown(function(event) {
			event.keyCode==13 && submit();
		});
		function submit() {
			var username = $("#Login_name"),		
			userpassword = $("#Login_password"),
			allRequiredFields = $([]).add(username).add(userpassword);
			
			allRequiredFields.removeClass('ui-state-error');

			if(username.val() == 0) {
				updateTips("用户名不能为空");
				return false;
			}
			
			if(userpassword.val() == 0) {
				updateTips("密码不能为空");
				return false;
			}
			
			$('#Login').submit();
			return false;
		}

	

	});
</script>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="img/logo.gif" width="101" height="29" alt="" /></a>
    
  </div>
  <div id="middle">
    <div id="left-column">
     
	</div>
    <div id="center-column">
   
      	<div class="login">
      		<s:actionerror cssClass="actionerror"/>
			<p id="validateTips"></p>
			<s:form action="Login" validate="true">
			    <s:textfield key="name" />
			    <s:password key="password" showPassword="true"/>
			    <s:checkbox name="rememberMe"  label="下次自动登录"/>
			</s:form>
		 	<div class="buttom">
		 		
				<a href="#" class="button" id="usersubmit">
					<s:text name="button.login" />
				</a>
	    	</div>
      	</div> 
     
     
    </div>
  
  </div>
  <div id="footer"></div>
</div>
</body>
</html>