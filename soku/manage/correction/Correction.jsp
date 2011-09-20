<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="soku.correction.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="soku.correction.title.edit"/></title>
    </s:if>
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	
	<script type="text/javascript">
		$(function() {
			
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

		$('#correctionsubmit').click(function() {
			var bValid = true;
			<s:if test="task=='Create'">
				keyword = $("#soku_correction_keyword");
				allRequiredFields = $([]).add(keyword)
				allRequiredFields.removeClass('ui-state-error');
				bValid = bValid && checkLength(keyword, '错词', 1, 1000);
			</s:if>
			correct_keyword = $("#soku_correction_correctKeyword");
			allRequiredFields = $([]).add(correct_keyword);
			allRequiredFields.removeClass('ui-state-error');
			bValid = bValid && checkLength(correct_keyword, '纠正词', 1, 255);
			if(!bValid)
				return false;

			$('#Correction_save').submit();
		});

	

	});
</script>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="/soku/manage/img/logo.gif" width="101" height="29" alt="" /></a>
    
  </div>
  <div id="middle"><s:include value="/soku/manage/module/leftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
        <h1><s:text name="soku.correction.title" /></h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form id="Correction_save" action="Correction_save" validate="false" cssClass="form" >

    <s:token />
    <s:hidden name="task"/>
    
    <s:if test="task=='Create'">
		<s:textfield label="纠正词" id="soku_correction_correctKeyword" name="correction.correctKeyword"/>
		<s:textfield label="错词" id="soku_correction_keyword" name="correction.keyword" cssClass="text"/>
		<s:select label="状态" id="soku_correction_status" name="correction.status" list="#{1:'有效',0:'无效'}" listKey="key" listValue="value"></s:select>
    </s:if>
    <s:if test="task=='Edit'">
        <s:hidden name="correction.id" cssClass="text"/>
        <s:hidden name="correction.createTime" cssClass="text"/>
        <tr>
    		<td><s:textfield label="纠正词" id="soku_correction_correctKeyword" name="correction.correctKeyword" readonly="true"/></td>
    	</tr>
        <s:textfield label="错词" name="correction.keyword" id="soku_correction_keyword"   cssClass="text"/>
    	<s:select  label="状态" id="soku_correction_status" name="correction.status" list="#{1:'有效',0:'无效'}" listKey="key" listValue="value"></s:select>
    	
    </s:if>

   
    	
  

</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="correctionsubmit">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
			</a>
			<a href="<s:url action="Correction_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
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