<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="episodeLog.title.list" /></title>


    <link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"   type="text/css"/>
    <link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
    
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	<script type="text/javascript"><!--
	var programmeId = 0;
	$(function() {

		$("#pageselect").change(function() {
			location.href = $("#pageurl").val() + $("#pageselect").val();
		});

		$('#orderStage').blur(function() {
			$('#orderStageTip').html("");	
			if(!$(this).val().match(/\d{8}/)) {
				$('#orderStageTip').html("格式错误");	
			}
		});

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

		$("#changeEpisode").dialog({
			bgiframe: true,
			autoOpen: false,
			height: 350,
			width: 400,
			modal: true,
			buttons: {
				'取消' : function() {
					$(this).dialog('close');
				},
			
				'确认': function() {
						saveEpisode();		
						$(this).dialog('close');									
					}
				},
				close : function() {
					//allFields.val('').removeClass('ui-state-error');
				}
			});

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
        $('#logoUrl').val(o.filePath);	
        $('#logoImg').attr('src', o.filePath);
       // $('#Video_save_video_picturePath').attr("disabled","disabled");
        	         
	}

	function getEpisode(episodeId, orderId, sourceSiteId, fkProgrammeId) {  //借用ProgrammeEpisode中的ViewOrder字段传递sourceSiteId
		programmeId = fkProgrammeId;
		$('#sourceSiteId').val(sourceSiteId);
		if(episodeId <= 0) {
			$('#episodeId').val(-1);
			$('#orderId').val("");
			if(orderId >0) {
				$('#orderId').val(orderId);
				$('#orderId').attr("readonly", "readonly");
			} else {
				$('#orderId').attr("readonly", "");
			}
			$('#orderStage').val($('#hstage' + orderId).val());
			$('#logoImg').attr('src', '');
			$('#logoUrl').val('');
			$('#url').val('');
			$('#seconds').val('');
			$('#secondsH').val('');
			$('#hd').attr('checked', '');
			$('#changeEpisode').dialog('open');
			return;
		}
	
		$.ajax({
			url: '<s:url action="ProgrammeEpisode_getProgrammeDetail" />',
			type: 'POST',
			data: 'episodeId=' + episodeId,
			success: function(data) {
				var o = eval('(' + data + ')');	
				$('#episodeId').val(o.id);
				$('#orderId').val(o.orderId);
				$('#orderStage').val(o.orderStage);
				$('#orderId').attr("readonly", "readonly");
				/*if(o.orderStage > 0) {
					$('#orderStage').attr("readonly", "readonly");
				}*/
				$('#logoImg').attr('src', o.logo);
				$('#logoUrl').val(o.logo);
				$('#url').val(o.url);
				$('#seconds').val(formateSeconds(o.seconds));
				$('#secondsH').val(o.seconds);
				if(o.hd == 1) {
					$('#hd').attr('checked', 'checked');
				} else {
					$('#hd').attr('checked', '');
				}

				$('#changeEpisode').dialog('open');
			}
		});
	}

	function saveEpisode() {
		var url = $("#url").val();
		var sourceSiteId = $('#sourceSiteId').val();
		var episodeId = $("#episodeId").val();
		var logoUrl = $("#logoUrl").val();
		var seconds = $("#secondsH").val();
		var hd = $("#hd").attr("checked") ? 1 : 0;
		var orderId = $('#orderId').val();
		var orderStage = $('#orderStage').val() ? $('#orderStage').val() : 0;
		//var programmeId = $('#programmeId').val();
		var cateId = $('#cateId').val();
		
		if(orderStage <=0 && cateId == 3) {
			alert("集数或者期数不能为0");
			return;
		}

		/*
		if(cateId !=3) {
			orderStage = orderId;
		}*/
		$.ajax({
			url: '<s:url action="ProgrammeEpisode_save" />',
			type: 'POST',
			data: 'url=' + url + '&id=' + episodeId + '&orderId=' + orderId + '&logoUrl=' + logoUrl + '&seconds=' + seconds + '&hd=' + hd + '&sourceSiteId=' + sourceSiteId + '&programmeId=' + programmeId + '&orderStage=' + orderStage,
			success: function(data) {
				var o = eval('(' + data + ')');					
				$("#hiddenId" + orderId).val(o.newId);
				if(o.status == 'success') {
					$("#lock" + orderId).attr("style", "");
					alert('保存成功');
					//window.location.reload();
					if(hd==1){
						document.getElementById('pe'+episodeId).style.display='none';
						if(document.getElementById('ppe'+programmeId).innerHTML.indexOf("block")==-1)
							document.getElementById('p'+programmeId).style.display='none';
					}
				} else {
					alert('发生错误');
				}
			    
			}
		});
	}


	function getVideoDetail() {
		var url = $("#url").val();
		
		var sourceSiteId = $('#sourceSiteId').val();
		var id = $("#hiddenId").val();
		var logoUrl = $("#logoUrl").val();
		var seconds = $("#seconds").val();
		var hd = $("#hd").attr("checked") ? 1 : 0;
		$.ajax({
			url: '<s:url action="ProgrammeEpisode_searchProgrammeDetail" />',
			type: 'POST',
			data: 'sourceSiteId=' + sourceSiteId + '&url=' + url,
			success: function(data) {
				var o = eval('(' + data + ')');	
				if(o.logo.indexOf("0900641F464A6318D600000000000000000000-0000-0000-0000-000042145BB0") < 0) {
					$("#logoUrl").val(o.logo);
					$("#logoImg").attr('src', o.logo);
				}
				$("#seconds").val(formateSeconds(o.seconds));
				$("#secondsH").val(o.seconds);
			}
		});
	}

	function addEpisodeSize() {
		var url = $("#url").val();
		
		var sourceSiteId = $('#maxOrderSite').val();
		if(sourceSiteId == 0) sourceSiteId = 14;
		//var programmeId = $('#programmeId').val();
		var maxOrder = $('#maxOrder').val();
		$.ajax({
			url: '<s:url action="ProgrammeEpisode_addEpisodeSize" />',
			type: 'POST',
			data: 'sourceSiteId=' + sourceSiteId + '&programmeId=' + programmeId + '&maxOrder=' + maxOrder,
			success: function(data) {
				var o = eval('(' + data + ')');	
				if(o.status == "success") {
					alert("添加成功");
					window.location.reload();
				}
			}
		});
	}

	function changeLogo() {
		$('#imagedialog').dialog('open');
		$("#FileUpload_upload").val("");
		$("#picPath").val("");
	}

	function sethiddenhd() {
		var hd = $("#hd").attr("checked") ? 1 : 0;
		$("#hdH").val(hd);
	}

	function formateSeconds(seconds) {
		var h = seconds / 60;
		var s = seconds % 60;
		s = Math.floor(s);
		if(s < 10) {
			s = '0' + s;
		}
		return Math.floor(h) + ':' + s ;
	}

	function sethiddenseconds() {
		var seconds = $("#seconds").val();
		ms = seconds.split(":");
		var mm = parseInt(ms[0]) * 60;
		var ss = parseInt(ms[1]);
		var time = mm + ss;
		$("#secondsH").val(time);
	}
	
	function editResource(peid,pid){
		var resource = 1;
		var res = <s:property value="resource"/>;
		if(document.getElementById("resource"+peid).checked == true)
			resource = 0;
		$.ajax({
			url: '<s:url action="Programme_editResource" />',
			type: 'POST',
			data: 'peid=' + peid + '&resource=' + resource,
			success: function() {
				if(res==1&&resource==0){
					document.getElementById('pe'+peid).style.display='none';
						if(document.getElementById('ppe'+pid).innerHTML.indexOf("block")==-1)
							document.getElementById('p'+pid).style.display='none';
				}
				if(res==0&&resource==1){
					document.getElementById('pe'+peid).style.display='none';
						if(document.getElementById('ppe'+pid).innerHTML.indexOf("block")==-1)
							document.getElementById('p'+pid).style.display='none';
				}
			}
		});
	}
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
      
        <h1>非高清视频</h1>
      </div>
      <br />
      <div class="select-bar">
		<s:form action="Programme_plain" validate="true" cssClass="form" theme="simple">
				<label> <s:textfield key="searchWord" cssClass="text" /> </label>
				<label>  </label>
				
				排序： 
				<select name="ob" id="ob" class="text">
				    <option value="0"></option>
				    <option value="1" <s:if test="ob==1">selected</s:if> >发行时间</option>
				    <option value="2" <s:if test="ob==2">selected</s:if> >搜索量</option>
				</select>
				
				类型：
				<select name="cate" id="cate" class="text">
				    <option value="0">所有</option>
				    <option value="1" <s:if test="cate==1">selected</s:if> >电视剧</option>
				    <option value="2" <s:if test="cate==2">selected</s:if> >电影</option>
				    <option value="3" <s:if test="cate==3">selected</s:if> >综艺</option>
				    <option value="5" <s:if test="cate==5">selected</s:if> >动漫</option>
				</select>
				
				高清资源：
				<select name="resource" id="resource" class="text">
					<option value="-1">所有</option>
					<option value="0" <s:if test="resource==0">selected</s:if> >无资源</option>
				    <option value="1" <s:if test="resource==1">selected</s:if> >未处理</option>
				</select>
				
				<input type="submit" name="Submit" class="ui-button ui-state-default ui-corner-all" value="Search" /> 
				
			</s:form> 
	</div>
      

    
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <table id="sorttable" class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="30%">剧名</th>
					<th align="center" width="10%">类型</th>
					
					<th align="center" width="60%">非高清集数</th>
					
				</tr>

				<s:iterator value="pageInfo.results" status="index" >
					<tr id="p<s:property value="programme.id"/>" <s:if test="#index.odd">class="bg"</s:if>>
						<td align="center">
							<a href="/VideoInfo_input.do?videoId=<s:property value="programme.id"/>"><s:property value="programme.name"/></a>
							
						</td>
						<td align="center">
						<s:property value="cateName"/>
						</td>
						<td align="left">
								
								<s:if test="programmeEpisodes.size > 0">
									<div class="site_toggle">					
										<div>
										<table id="ppe<s:property value="programme.id"/>" width="100%" cellpadding="0" cellspacing="0">
											<s:if test="#index.index==0">
												<tr id="pe0000000" style="display:block;"><td width="100px;">非高清剧集</td><td width="65px;">无高清资源</td></tr>
											</s:if>
											<s:iterator value="programmeEpisodes" >
												<tr id="pe<s:property value="id" />" style="display:block;">
													<td width="100px;" ondblclick="getEpisode(<s:property value="id" />, <s:property value="orderId" />, 14, <s:property value="programme.id"/>);">
														<s:if test="programme.cate == 1 || programme.cate == 5"><a target="_blank" title="<s:property value="title"/>" href="<s:property value="url"/>">第<s:property value="orderId"/>集</a></s:if>
														<s:if test="programme.cate == 2"><a target="_blank" title="<s:property value="title"/>" href="<s:property value="url"/>">播放</a></s:if>
														<s:if test="programme.cate == 3"><a target="_blank" title="<s:property value="title"/>" href="<s:property value="url"/>">第<s:property value="orderStage"/>期</a></s:if>
												   </td>
												   <td width="65px;">
												   		<input type="checkbox" id="resource<s:property value="id" />" <s:if test="resource==0">checked</s:if> onclick="editResource(<s:property value="id" />,<s:property value="programme.id"/>)"/>
												   </td>
												</tr>
											</s:iterator>
										</table>
										</div>
									</div>
									</s:if>	
						</td>
						
					</tr>
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td colspan="7"> <s:property
					value="pageInfo.totalRecords" /> ----
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="Programme_plain"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="searchWord" value="searchWord"/>	
								<s:param name="ob" value="ob"></s:param>
								<s:param name="cate" value="cate"></s:param>
								<s:param name="resource" value="resource"></s:param>
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="Programme_plain"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="searchWord" value="searchWord"/>	
								<s:param name="ob" value="ob"></s:param>
								<s:param name="cate" value="cate"></s:param>
								<s:param name="resource" value="resource"></s:param>
							</s:url>"><s:text
								name="nextpage"/></a>
						</s:if></td>
					</tr>
				</tfoot>
			</table>
       
				       <div class="select"><strong>Other Pages: </strong> <input
					type="hidden" id="pageurl"
					value="<s:url action="Programme_plain"> 
								<s:param name="searchWord" value="searchWord"/>	
								<s:param name="ob" value="ob"></s:param>
								<s:param name="cate" value="cate"></s:param>
								<s:param name="resource" value="resource"></s:param>
								<s:param name="pageNumber" value="0" />
							</s:url>" />
				<select id="pageselect">
					<s:bean name="org.apache.struts2.util.Counter" id="counter">
						<s:param name="first" value="1" />
						<s:param name="last" value="10" />
						<s:iterator status="count">
							<s:if test="(pageInfo.currentPageNumber - #count.index) > 0">
								<option><s:property
									value="pageInfo.currentPageNumber - #count.index" /></option>
							</s:if>
						</s:iterator>
					</s:bean>
				
					<s:bean name="org.apache.struts2.util.Counter" id="counter">
						<s:param name="first" value="1" />
						<s:param name="last" value="10" />
						<s:iterator status="count">
							<s:if
								test="(pageInfo.currentPageNumber + #count.index) < pageInfo.totalPageNumber">
								<option><s:property
									value="#count.index + pageInfo.currentPageNumber + 1" /></option>
							</s:if>
						</s:iterator>
					</s:bean>
				</select></div>
      </div>

      
    </div>

  </div>
  <div id="footer"></div>
  <div id="changeEpisode" title="修改剧集信息">
	 		<input type="hidden" id="episodeId" value="" />
	 		<input type="hidden" id="fkProgrammeSiteId" value="" />
	 		<input type="hidden" id="sourceSiteId" value="" />
	 		<input type="hidden" id="cateId" value="<s:property value="cate" />" />
	 		<table>
	 		<tr>
	 			<s:if test="cate != 3"><td>集数</td>
	 			<td><input id="orderId" value="" type="hidden"/>  <input id="orderStage" value="" type="text"/>	</td></s:if>
	 		</tr>
	 		<s:if test="cate == 3">
	 		<tr>
	 			<td>期数</td>
	 			<td><input id="orderId" value="" type="hidden"/> <input type="text" id="orderStage" value="" /> 格式：yyyymmdd<span id="orderStageTip" style="background:red"></span></td>
	 		</tr>
	 		</s:if>
	 		
	 		<tr>
	 			<td>播放链接</td>
	 			<td><input type="text" id="url" value="" onblur="getVideoDetail();"/>	</td>
	 		</tr>
	 		
	 		<tr>
	 			<td>图片</td>
	 			<td><img src='' id="logoImg" /> <br/>		
					<input type="hidden" id="logoUrl" value="" name="logo"/>
					<input id="changelogo<s:property value="orderId"/>" type="button" onclick='changeLogo();' 
												 				value="更改图片" />
				</td>
	 		</tr>
	 		<tr>
	 			<td>时长</td>
	 			<td><input id="seconds" name="seconds" type="text" value="" class="editing"  onchange="sethiddenseconds();"/>
					<input id="secondsH" type="hidden" value="" class="editing" />
				</td>
	 		</tr>
	 		<tr>
	 			<td>高清</td>
	 			<td><span class="HD" title="高清"></span>
					<input type="checkbox" id="hd" onchange="sethiddenhd()"/>
					<input type="hidden" id="hdH" value=""  name="hd"/>
				</td>
	 		</tr>
	 		</table>
									
									
	 </div>
  
   <div id="imagedialog" title="Upload Picture">

	<s:form action="FileUpload" validate="false" cssClass="form" enctype="multipart/form-data">
	图片路径： <input type="text"  id="picPath" name="imageUrl"/>
	<s:file name="upload" label="上传图片"/>	
	</s:form>
 </div>
</div>
</body>
</html>
