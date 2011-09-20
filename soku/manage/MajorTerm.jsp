<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="majorTerm.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="majorTerm.title.edit"/></title>
    </s:if>
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
          type="text/css"/>


<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	
	<script type="text/javascript">
		$(document).ready(function() {
			if($('#MajorTerm_save_majorTerm_cateId').val() != 0) { 
				$('#labeltr').hide();
			} 
		});
	
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


		$('#MajorTerm_save_majorTerm_cateId').change(function() {
			if($('#MajorTerm_save_majorTerm_cateId').val() == 0) { 
				$('#labeltr').toggle(0);
			} else {
				$('#labeltr').hide();
			}

		});


		

		$('#majorTermsubmit').click(function() {
			if($('#MajorTerm_save_majorTerm_cateId').val() == 0) { 
				if($("#majorTermLbel").val() == '') {
						alert('标签不能为空');
						return false;
				}
			}


			$('#MajorTerm_save').submit();
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
        <h1>直达区大词管理</h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="MajorTerm_save" validate="false" cssClass="form" >

    <s:token />
    <s:hidden name="task"/>
    <s:hidden name="majorTermId" />
   
    <s:textfield key="majorTerm.keyword" cssClass="text"/>
    <s:select list="categoryList" listKey="id" listValue="name" key="majorTerm.cateId"></s:select>
	<tr id="labeltr"><td>标签：</td> <td>
		<input name="majorTerm.label" id="majorTermLbel" 
			value='<s:if test="majorTerm.label != ''"><s:property value="majorTerm.label"/></s:if><s:else>视频</s:else>'/>
	</td></tr>
		
	<s:textarea key="majorTerm.htmlText" cssClass="text" cols="40"/>
	<s:textarea key="majorTerm.urlText" cssClass="text" cols = "40"/>
    <s:select key="majorTerm.status" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>

   

</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="majorTermsubmit">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
			</a>
			<a href="<s:url action="MajorTerm_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
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