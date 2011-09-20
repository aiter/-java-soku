<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="keywordIntervenVideo.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="keywordIntervenVideo.title.edit"/></title>
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
	
	<script type="text/javascript">
		$(function() {

			$("#KeywordIntervenVideo_save_keywordIntervenVideo_expiredStrDate").datepicker({dateFormat: 'yy-mm-dd'});
			
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
		        $('#KeywordIntervenVideo_save_keywordIntervenVideo_picturePath').val(o.filePath);	
		        $('#keywordIntervenVideoPicture').attr('src', o.filePath);
		       // $('#KeywordIntervenVideo_save_keywordIntervenVideo_picturePath').attr("disabled","disabled");
		        	         
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

		

		$('#submitkeywordIntervenVideo').click(function() {
			var keywordIntervenVideoname = $("#KeywordIntervenVideo_save_keywordIntervenVideo_name"),
			keywordIntervenVideourl = $("#KeywordIntervenVideo_save_keywordIntervenVideo_url"),
			keywordIntervenVideolength = $("#KeywordIntervenVideo_save_keywordIntervenVideo_videoLength"),
			keywordIntervenVideosource = $("#KeywordIntervenVideo_save_keywordIntervenVideo_source"),
			keywordIntervenVideocategory = $("#KeywordIntervenVideo_save_keywordIntervenVideo_category"),
			keywordIntervenVideosort = $("#KeywordIntervenVideo_save_keywordIntervenVideo_sort"),
			keywordIntervenVideopicture = $("#KeywordIntervenVideo_save_keywordIntervenVideo_picturePath"),
			allRequiredFields = $([]).add(keywordIntervenVideoname).add(keywordIntervenVideourl).add(keywordIntervenVideolength).add(keywordIntervenVideosource)
								.add(keywordIntervenVideocategory).add(keywordIntervenVideosort).add(keywordIntervenVideopicture);
			
			var bValid = true;
			allRequiredFields.removeClass('ui-state-error');

			bValid = bValid && checkLength(keywordIntervenVideoname, '视频名', 1, 500);
			//bValid = bValid && checkRegexp(keywordIntervenVideoname,/^[a-z]([0-9a-z_])+$/i,"视频名格式不正确");

			bValid = bValid && checkLength(keywordIntervenVideourl, '链接地址', 1, 500);
			bValid = bValid && checkRegexp(keywordIntervenVideourl,/^http\:\/\/(\S)+?$/i,"链接地址格式不正确");

			bValid = bValid && checkLength(keywordIntervenVideolength, '视频长度', 1, 10);
			bValid = bValid && checkRegexp(keywordIntervenVideolength,/^([0-5])[0-9]:([0-5])[0-9]:([0-5])[0-9]$/,"视频长度格式不正确");

			bValid = bValid && checkLength(keywordIntervenVideosource, '视频来源', 1, 100);
			//bValid = bValid && checkRegexp(keywordIntervenVideosource,/^[a-z]([0-9a-z_])+$/i,"视频来源格式不正确");
			
			bValid = bValid && checkLength(keywordIntervenVideocategory, '视频分类', 1, 100);
			//bValid = bValid && checkRegexp(keywordIntervenVideocategory,/^[a-z]([0-9a-z_,])+$/i,"视频分类源格式不正确");

			bValid = bValid && checkLength(keywordIntervenVideosort, '视频排序', 1, 100);
			bValid = bValid && checkRegexp(keywordIntervenVideosort,/^[0-9]+$/i,"视频排序为数字");

			bValid = bValid && checkLength(keywordIntervenVideopicture, '视频图片', 1, 500);

			if(!bValid)
				return false;
			else
				$('#KeywordIntervenVideo_save').submit();
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
        <h1><s:text name="soku.keywordIntervenVideo.title" /></h1>
      </div>
      <br />     
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="KeywordIntervenVideo_save" validate="false" cssClass="form" >
    <s:token />
    <s:hidden name="task"/>
    <s:hidden key="keywordIntervenVideo.keywordId" />
    <s:hidden key="keywordId" />
    <s:hidden key="keywordIntervenVideoId" />
    <s:hidden key="keywordIntervenVideo.createDate" />
    <s:hidden name="searchWord"/>
    <s:hidden name="pageNumber" />
    <s:textfield key="keywordIntervenVideo.name" cssClass="text"/>
	<s:textfield key="keywordIntervenVideo.url" cssClass="text"/>
	<tr>
		<td>
		<s:text name="keywordIntervenVideo.videoLength"></s:text>: 
		</td>
		<td>
		<s:textfield key="keywordIntervenVideo.videoLength" cssClass="text" tooltip="" theme="simple"/>
		格式（hh:mm:ss）
		</td>
	</tr>
	<s:textfield key="keywordIntervenVideo.source" cssClass="text"/>
	<s:textfield key="keywordIntervenVideo.category" cssClass="text"/>   
    <s:textfield key="keywordIntervenVideo.expiredStrDate" cssClass="text" />
    <s:textfield key="keywordIntervenVideo.sort" cssClass="text"/>
    <s:textfield key="keywordIntervenVideo.picturePath" cssClass="text"/>
    <tr>
    	<td>
    		
    	</td>
    	<td align="center">
    		<table>
    			<tr>
    				<td align="left">
    					<s:if test="keywordIntervenVideo.picturePath != null">
    						<img src="<s:property value="keywordIntervenVideo.picturePath" />" id="keywordIntervenVideoPicture"/>
    					</s:if>
    				</td>
    				<td><input type="button" id="uploadpic" class="ui-button ui-state-default ui-corner-all" value="<s:text name="keywordIntervenVideo.uploadpicture"/>"></input>
    				</td>
    			</tr>
    		</table>
    		
    		
    	</td>
    </tr>
</s:form>

        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="submitkeywordIntervenVideo">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
			</a>
			<a href="<s:url action="KeywordIntervenVideo_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
				<s:text name="button.cancel" />
			</a>
		</div>
      </div>
    </div>
    
  </div>
  <div id="footer"></div>
  
  
  <div id="dialog" title="Upload Picture">

	<s:form action="FileUpload" validate="false" cssClass="form" enctype="multipart/form-data">
	<fieldset>
		<s:file name="upload" label="File"/>
	</fieldset>
	</s:form>
 </div>
 


</div>
</body>
</html>