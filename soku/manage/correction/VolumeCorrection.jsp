<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
        <title><s:text name="soku.correction.volumeAdd"/></title>
    
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
          type="text/css"/>
    <style type="text/css">

		fieldset { padding:0; border:0; margin-top:25px; }
		
	</style>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	
	<script type="text/javascript"><!--
		$(function() {
			tips = $("#validateTips");
	
			function updateTips(t) {
				tips.text(t);
			}
	
			function checkLength(o,n,min) {
	
				if (o.val().length < min ) {
					o.addClass('ui-state-error');
					updateTips(n + " 的长度应该大于 " + min);
					return false;
				} else {
					return true;
				}
			}
		

		$('#submitCorrectVolumeAdd').click(function() {
			var volume_keyword = $("#all_correct_keyword");
			allRequiredFields = $([]).add(volume_keyword)
			allRequiredFields.removeClass('ui-state-error');
			bValid = checkLength(volume_keyword, '批量添加的纠错词', 1);
			if(!bValid) return false;
			$('#Correction_volumeSave').submit();
		});

	});
--></script>
</head>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="soku/manage/img/logo.gif" width="101" height="29" alt="" /></a>
    
  </div>
  <div id="middle">
    <s:include
	value="/soku/manage/module/leftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
        <h1><s:text name="soku.correction.volumeAdd" /></h1>
      </div>
      <br />     
      <div class="table">
        <p id="validateTips"></p>
   		<s:form id="Correction_volumeSave" action="Correction_volumeSave" validate="false" cssClass="form" >
   			<table id="sorttable" cellpadding="0" cellspacing="0">
			
				<tr>
					<th align="left">每行仅写一组纠错词，前面为纠正词，后面为错词，纠正词和错词用&隔开，错词用|隔开。</th>
					<th align="left">例如：火影忍者&火影|huoying|huoyingrenzhe|火影忍着</th>
				</tr>
				<tr>
					<th><s:textarea id="all_correct_keyword" name="all_correct_keyword" cssClass="text" theme="simple" cols="120" rows="20"/> </th>
					<th align="left" valign="top"><s:actionerror cssClass="actionerror"/></th>
				</tr>
		</table>
        <p>&nbsp;</p>
			<div class="buttom">
				<a href="#" class="button" id="submitCorrectVolumeAdd">
					<s:text name="button.edit" />
				</a>
				<a href="<s:url action="Correction_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
					<s:text name="button.cancel" />
				</a>
			</div>
			</s:form>
      </div>
      
    </div>
    
  </div>
  <div id="footer"></div>
  
  




</div>
</body>
</html>