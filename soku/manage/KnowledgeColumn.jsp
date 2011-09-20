<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title>创建知识栏目</title>
    </s:if>
    <s:if test="task=='Edit'">
        <title>修改知识栏目</title>
    </s:if>
    <link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
	<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
	
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	


	
	<script type="text/javascript">
		$(function() {

			// pre-submit callback 
			function showRequest(formData, jqForm, options) { 
			    			 
			    return true; 
			} 
			 
			// post-submit callback 
			function showResponse(responseText, statusText)  { 
				var orderId = $("#currentOrderId").val();
			    var o = eval('(' + responseText + ')');  				
		        $('#KnowledgeColumn_save_knowledgeColumn_pic').val(o.filePath);	
		        $('#logoImg').attr('src', o.filePath);
		       // $('#Video_save_video_picturePath').attr("disabled","disabled");
		        	         
			} 

			 var options = { 
				        //target:        '#output',   // target element(s) to be updated with server response 
				        beforeSubmit:  showRequest,  // pre-submit callback 
				        success:       showResponse  // post-submit callback 
				 
				      
				    }; 
				 
				    // bind form using 'ajaxForm' 
			 $('#FileUpload').ajaxForm(options); 

			$("#imagedialog").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				width: 400,
				modal: true,
				buttons: {
					'取消' : function() {
						$(this).dialog('close');
					},
				
					'确认': function() {						
						var bValid = true;

						/*
						if($("#FileUpload_upload").val().trim() == "") {
							bValid = false;
							if($("#picPath").val().trim() != "") {
								var orderId = $("#currentOrderId").val();							     				
						        $('#logoUrl' + orderId).val($("#picPath").val());	
						        $('#logoImg' + orderId).attr('src', $("#picPath").val());
						        $(this).dialog('close');
							}
						}*/
							
						if (bValid) {
						$('#FileUpload').submit();
						$(this).dialog('close');
				}
			}
			},
			close : function() {
				//allFields.val('').removeClass('ui-state-error');
			}
		});
			
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


		

		$('#knowledgeColumnsubmit').click(function() {
			<s:if test="task=='Create'">
			var knowledgeColumnname = $("#KnowledgeColumn_save_knowledgeColumn_name"),	
			allRequiredFields = $([]).add(knowledgeColumnname);
			
			var bValid = true;
			allRequiredFields.removeClass('ui-state-error');

			bValid = bValid && checkLength(knowledgeColumnname, '栏目名称', 1, 50);
			


			if(!bValid)
				return false;
			</s:if>

			$('#KnowledgeColumn_save').submit();
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
    <s:if test='shieldKnowledgeColumn'>
   	<s:include value="module/shieldleftnav.jsp"></s:include>
   </s:if>
   <s:else><s:include value="module/leftnav.jsp"></s:include></s:else>
    <div id="center-column">
      <div class="top-bar"> 
        <h1>知识栏目</h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="KnowledgeColumn_save" validate="false" cssClass="simple" >

    <s:token />
    <s:hidden name="task"/>
    <s:hidden name="knowledgeColumnId" />
    <s:hidden name="parentId" />
   
    
    
   		<s:textfield key="knowledgeColumn.name"  label="栏目名" cssClass="text"/>
		<s:textfield key="knowledgeColumn.pic" label="图片" cssClass="text"/>
   		<img id="logoImg" src="<s:property value="knowledgeColumn.pic"/>" />
    	<input id="changelogo" type="button" onclick='changeLogo();' 
										 				value="更改图片" />
  

</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="knowledgeColumnsubmit">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
			</a>
			<a href="<s:url action="KnowledgeColumn_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
				<s:text name="button.cancel" />
			</a>
		</div>
      </div>
    </div>
    
  </div>
  <div id="footer"></div>
</div>

 <div id="imagedialog" title="Upload Picture">

	<s:form action="FileUpload" validate="false" cssClass="form" enctype="multipart/form-data">
	图片路径： <input type="text"  id="picPath" name="imageUrl"/>
	<s:file name="upload" label="上传图片"/>	
	</s:form>
 </div>
</body>
</html>