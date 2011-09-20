<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
     <title><s:text name="user.title.password"/></title>
     
     <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	
	<script type="text/javascript">
		$(function() {
			
			var tips = $("#validateTips");
	
			function updateTips(t) {
				tips.text(t);
			}
	
			
		$('#usersubmit').click(function() {
			var userpassword = $("#User_changepwd_password"),		
			userrepassword = $("#User_changepwd_repeatPassword"),
			allRequiredFields = $([]).add(userpassword).add(userrepassword);
			
			allRequiredFields.removeClass('ui-state-error');

			
			if(userpassword.val() == 0) {
				updateTips("密码不能为空");
				return false;
			}

			if(userrepassword.val() == 0) {
				updateTips("重复密码不能为空");
				return false;
			}
			
			if(userpassword.val() != userrepassword.val()) {
				updateTips("两次输入密码不一致");
				return false;
			}

			
			$('#User_changepwd').submit();
			return false;
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
        <h1><s:text name="soku.changepassword.title" /></h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="User_changepwd" validate="false" cssClass="form" >

    <s:token />
    <s:hidden name="task"/>
    <s:hidden name="userId" />
  
    <s:hidden key="user.name" cssClass="text"/>
	<s:password key="password" showPassword="true"/>
   	<s:password key="repeatPassword" showPassword="true"/>
  

</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="usersubmit">
				<s:text name="button.edit" />
			</a>
			<a href="<s:url action="User_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
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