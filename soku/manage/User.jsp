<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="user.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="user.title.edit"/></title>
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


		

		$('#usersubmit').click(function() {
			<s:if test="task=='Create'">
			var username = $("#User_save_user_name"),	
			userpassword = $("#User_save_user_password"),		
			userrepassword = $("#User_save_repeatPassword"),			
			allRequiredFields = $([]).add(username).add(userpassword).add(userrepassword);
			
			var bValid = true;
			allRequiredFields.removeClass('ui-state-error');

			bValid = bValid && checkLength(username, '用户名称', 1, 50);
			//bValid = bValid && checkRegexp(videoname,/^[a-z]([0-9a-z_])+$/i,"视频名格式不正确");

			bValid = bValid && checkLength(userpassword, '用户密码', 1, 30);
			
			bValid = bValid && checkLength(userrepassword, '重复密码', 1, 30);
			//bValid = bValid && checkRegexp(videosort,/^[0-9]+$/g,"视频排序为数字");


			if(!bValid)
				return false;
			</s:if>

			$('#User_save').submit();
			return false;
		});

	

	});
</script>
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
      <div class="top-bar"> 
        <h1><s:text name="soku.user.title" /></h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="User_save" validate="false" cssClass="form" >

    <s:token />
    <s:hidden name="task"/>
    <s:hidden name="userId" />
   
    
    
    <s:if test="task=='Create'">
        <s:textfield key="user.name" cssClass="text"/>
		<s:password key="user.password" showPassword="true"/>
    	<s:password key="repeatPassword" showPassword="true"/>
    </s:if>
    <s:if test="task=='Edit'">    	
        <s:hidden key="user.name" cssClass="text"/>	
        <tr>
    		<td><s:text name="user.name" /></td>
    		<td>
    			<s:label key="user.name" cssClass="text" theme="simple"/>
    			<span class="changepassword">
    				<a href="
	    				<s:url action="User_changepwd">
	    					<s:param name="userId" value="userId"/>
	    				</s:url>" class="blue">
    					<s:text name="changepassword" />
    				</a></span>
    			
    		</td>
    	</tr>
    	<s:textfield key="user.actualName" cssClass="text"/>
    	<s:textfield key="user.email" cssClass="text"/>
    	
    	<s:select key="user.isActive" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
    	<s:select key="user.isSuperUser" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
    	
    	<s:checkboxlist  list="permissionList" listKey="id" listValue="name" key="user.permissionList"></s:checkboxlist >
    </s:if>

   
    	
  

</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="usersubmit">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
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