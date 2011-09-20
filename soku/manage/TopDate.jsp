<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="topWord.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="topWord.title.edit"/></title>
    </s:if>
    
   <link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"   type="text/css"/>
    <link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
    <style type="text/css">

		fieldset { padding:0; border:0; margin-top:25px; }
		
	</style>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.datepicker.js"/>"></script>
	<script type="text/javascript"><!--
		$(function() {

			$("input[id][name$='topDate']").datepicker({dateFormat: 'yy-mm-dd'});
			
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

			$(document).ready(function() { 
			    var options = { 
			        //target:        '#output',   // target element(s) to be updated with server response 
			        beforeSubmit:  showRequest,  // pre-submit callback 
			        success:       showResponse  // post-submit callback 
			 
			      
			    }; 
			 
			    // bind form using 'ajaxForm' 
			    $('#FileUpload').ajaxForm(options); 
			}); 
			 
			// pre-submit callback 
			function showRequest(formData, jqForm, options) { 
			    			 
			    return true; 
			} 
			 
			// post-submit callback 
			function showResponse(responseText, statusText)  { 
			    var o = eval('(' + responseText + ')');  				
		        $('#TopWord_save_topWord_pic').val(o.filePath);	
		        $('#topWordPicture').attr('src', o.filePath);
		       // $('#TopWord_save_topWord_picturePath').attr("disabled","disabled");
		        	         
			} 

			
			
			
			$("#dialog").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				modal: true,
				buttons: {
					'上传': function() {
						var bValid = true;
						allFields.removeClass('ui-state-error');
							
						if (bValid) {
						$('#FileUpload').submit();
						$(this).dialog('close');
				}
			},
			'取消' : function() {
				$(this).dialog('close');
			}
			},
			close : function() {
				allFields.val('').removeClass('ui-state-error');
			}
		});

		$('#uploadpic').click(function() {
			$('#dialog').dialog('open');
		}).hover(function() {
			$(this).addClass("ui-state-hover");
		}, function() {
			$(this).removeClass("ui-state-hover");
		}).mousedown(function() {
			$(this).addClass("ui-state-active");
		}).mouseup(function() {
			$(this).removeClass("ui-state-active");
		});

		

		$('#submittopWord').click(function() {
			var topWordname = $("#TopWord_save_topWord_name"),
			topWordurl = $("#TopWord_save_topWord_url"),
			topWordlength = $("#TopWord_save_topWord_topWordLength"),
			topWordsource = $("#TopWord_save_topWord_source"),
			topWordcategory = $("#TopWord_save_topWord_category"),
			topWordsort = $("#TopWord_save_topWord_sort"),
			topWordpicture = $("#TopWord_save_topWord_picturePath"),
			allRequiredFields = $([]).add(topWordname).add(topWordurl).add(topWordlength).add(topWordsource)
								.add(topWordcategory).add(topWordsort).add(topWordpicture);
			
			var bValid = true;
		

			$('#TopDate_save').submit();
		});

	});
--></script>
</head>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="img/logo.gif" width="101" height="29" alt="" /></a>
    
  </div>
  <div id="middle">
    <s:include value="module/leftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
        <h1>排行榜上线管理</h1>
      </div>
      <br />     
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>

   		<s:form action="TopDate_save" validate="false" cssClass="form" >
   
		   <s:iterator value="cswList" status="index">
		   	<s:hidden key="cswList[%{#index.index}].fkChannelName" />
		   	<s:property value="cswList[#index.index].channelLabel" />
		   	<s:textfield key="cswList[%{#index.index}].topDate" cssClass="text" theme="simple" /> 
		   	<br />
		   </s:iterator>
		   


        <p>&nbsp;</p>
			<div class="buttom">
				<a href="#" class="button" id="submittopWord">
					<s:text name="button.edit" />
				</a>
				<a href="<s:url action="TopWord_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
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