<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="video.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="video.title.edit"/></title>
    </s:if>
    
    <link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
          type="text/css"/>
    <style type="text/css">

		fieldset { padding:0; border:0; margin-top:25px; }
		
	</style>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	
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
		        $('#Video_save_video_picturePath').val(o.filePath);	
		        $('#videoPicture').attr('src', o.filePath);
		       // $('#Video_save_video_picturePath').attr("disabled","disabled");
		        	         
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

		

		$('#submitvideo').click(function() {
			var videoname = $("#Video_save_video_name"),
			videourl = $("#Video_save_video_url"),
			videolength = $("#Video_save_video_videoLength"),
			videosource = $("#Video_save_video_source"),
			videocategory = $("#Video_save_video_category"),
			videosort = $("#Video_save_video_sort"),
			videopicture = $("#Video_save_video_picturePath"),
			allRequiredFields = $([]).add(videoname).add(videourl).add(videolength).add(videosource)
								.add(videocategory).add(videosort).add(videopicture);
			
			var bValid = true;
			allRequiredFields.removeClass('ui-state-error');

			bValid = bValid && checkLength(videoname, '视频名', 1, 500);
			//bValid = bValid && checkRegexp(videoname,/^[a-z]([0-9a-z_])+$/i,"视频名格式不正确");

			bValid = bValid && checkLength(videourl, '链接地址', 1, 500);
			bValid = bValid && checkRegexp(videourl,/^http\:\/\/(\S)+?$/i,"链接地址格式不正确");

			bValid = bValid && checkLength(videolength, '视频长度', 1, 10);
			bValid = bValid && checkRegexp(videolength,/^([0-5])[0-9]:([0-5])[0-9]:([0-5])[0-9]$/,"视频长度格式不正确");

			bValid = bValid && checkLength(videosource, '视频来源', 1, 100);
			//bValid = bValid && checkRegexp(videosource,/^[a-z]([0-9a-z_])+$/i,"视频来源格式不正确");
			
			bValid = bValid && checkLength(videocategory, '视频分类', 1, 100);
			//bValid = bValid && checkRegexp(videocategory,/^[a-z]([0-9a-z_,])+$/i,"视频分类源格式不正确");

			bValid = bValid && checkLength(videosort, '视频排序', 1, 100);
			bValid = bValid && checkRegexp(videosort,/^[0-9]+$/i,"视频排序为数字");

			bValid = bValid && checkLength(videopicture, '视频图片', 1, 500);

			if(!bValid)
				return false;
			else
				$('#Video_save').submit();
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
        <h1><s:text name="soku.video.title" /></h1>
      </div>
      <br />     
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="Video_save" validate="false" cssClass="form" >
    <s:token />
    <s:hidden name="task"/>
    <s:hidden name="videoId" />
    <s:hidden key="video.videoId" />
    <s:hidden key="video.createDate" />
    <s:hidden name="pageNumber" />
    <s:hidden name="searchWord" />
    <s:textfield key="video.name" cssClass="text"/>
    <s:select list="channelList" listKey="name" listValue="label" key="video.itemId"></s:select>
	<s:textfield key="video.url" cssClass="text"/>
	<tr>
		<td>
		<s:text name="video.videoLength"></s:text>: 
		</td>
		<td>
		<s:textfield key="video.videoLength" cssClass="text" tooltip="" theme="simple"/>
		格式（hh:mm:ss）
		</td>
	</tr>
	<s:textfield key="video.source" cssClass="text"/>
	<s:textfield key="video.category" cssClass="text"/>   
    <s:select key="video.indexType" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
    <s:textfield key="video.sort" cssClass="text"/>
    <s:textfield key="video.picturePath" cssClass="text"/>
    <tr>
    	<td>
    		
    	</td>
    	<td align="center">
    		<table>
    			<tr>
    				<td align="left">
    					<s:if test="video.picturePath != null">
    						<img src="<s:property value="video.picturePath" />" id="videoPicture"/>
    					</s:if>
    				</td>
    				<td><input type="button" id="uploadpic" class="ui-button ui-state-default ui-corner-all" value="<s:text name="video.uploadpicture"/>"></input>
    				</td>
    			</tr>
    		</table>
    		
    		
    	</td>
    </tr>
</s:form>

        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="submitvideo">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
			</a>
			<a href="<s:url action="Video_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
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