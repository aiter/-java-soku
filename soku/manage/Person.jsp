<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="person.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="person.title.edit"/></title>
    </s:if>
    <link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"   type="text/css"/>
    <link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>

	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.datepicker.js"/>"></script>
	
	<script type="text/javascript">
		$(function() {

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
		        

			    $('#Person_save_person_logo').val(o.filePath);
	        	$('#firstLogo').attr('style', '');	
	        	$('#firstLogo').attr('src', o.filePath);
		       // $('#Video_save_video_picturePath').attr("disabled","disabled");
			     
		        	         
			} 

			$("#Person_save_person_birthday").datepicker({dateFormat: 'yy-mm-dd'});
			$("#xxx").datepicker({dateFormat: 'yy-mm-dd'});
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

			$('#uploadlogo').click(function() {
				$('#imagedialog').dialog('open');

			});
		

		$('#personsubmit').click(function() {
			var personname = $("#Person_save_person_name"),	
			personurl = $("#Person_save_person_url"),		
			personsort = $("#Person_save_person_sort"),
			allRequiredFields = $([]).add(personname).add(personurl).add(personsort);
			
			var bValid = true;
			allRequiredFields.removeClass('ui-state-error');

			$('#Person_save').submit();
			if(!bValid)
				return false;
			else
				$('#Person_save').submit();
			return false;
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
        <h1><s:text name="soku.person.title" /></h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
	<s:form action="Person_save" validate="false" cssClass="form" >

    <s:token />
    <s:hidden name="task"/>
    <s:hidden name="personId" />
   
    <s:textfield key="person.name" cssClass="text"/>	
    <s:textfield key="person.alias" cssClass="text"/>	
    <s:select key="person.sex" list="#{2:'男', 1:'是', 0:'未知'}" listKey="key" listValue="value"></s:select>
    <s:textfield key="person.homeplace" cssClass="text"/>
	<tr>
		<td><s:text name="person.birthday" /></td>
		<td><input name="person.birthday" type="text" value="<s:date name="person.birthday" format="yyyy-MM-dd"/>" id="xxx" class="text"/></td>
	</tr>
	
	<s:textfield key="person.height" cssClass="text"/>
	<s:textfield key="person.weight" cssClass="text"/>
	<s:textfield key="person.bloodtype" cssClass="text"/>
	<s:textfield key="person.persontype" cssClass="text"/>
	<s:textfield key="person.occupation" cssClass="text"/>
	<s:select key="person.locked" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
	<s:select key="person.blocked" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
   	<tr>
   		<td>图片</td><td>
   		<s:textfield key="person.logo" cssClass="text" theme="simple"/> <img
				src="<s:property value="person.logo" />" id="firstLogo"
				<s:if test="movie.logo == null">style="visibility: hidden" </s:if> />
		
			<input type="button" id="uploadlogo"
				class="ui-button ui-state-default ui-corner-all" value="更改图片"></input>
		</td>
   	</tr>
   	
   	<s:textarea key="person.brief" cssClass="textarea" cols="44" rows="10"/>

	</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="personsubmit">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
			</a>
			<a href="<s:url action="Person_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
				<s:text name="button.cancel" />
			</a>
		</div>
      </div>
    </div>
    
  </div>
  <div id="footer"></div>
</div>

<div id="imagedialog" title="Upload Picture"><s:form
	action="FileUpload" validate="false" cssClass="form"
	enctype="multipart/form-data">
	图片路径： <input type="text" id="picPath" name="imageUrl" />
	<s:file name="upload" label="上传图片" />
</s:form></div>
</body>
</html>