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

	function getEpisode(episodeId, orderId, sourceSiteId) {  //借用ProgrammeEpisode中的ViewOrder字段传递sourceSiteId
	
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
		var programmeId = $('#programmeId').val();
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
				$("#hiddenId" + orderId).val(o.newId)
				if(o.status == 'success') {
					$("#lock" + orderId).attr("style", "");
					alert('保存成功');
					window.location.reload();
				}else if(o.status == 'unique'){
					alert('期数已经存在');
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
		var programmeId = $('#programmeId').val();
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
      
        <h1><s:property value="programmDetail" /></h1>
      </div>
      <br />
      <div class="select-bar">
       
      </div>
      
	  <s:hidden key="programmeId"></s:hidden>
	  <s:hidden key="maxOrder"></s:hidden>
	  <s:hidden key="maxOrderSite"></s:hidden>
    
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <table id="sorttable" class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="9%">集数</th>
					<th align="center" width="9%">优酷网</th>
					<th align="center" width="9%">土豆网</th>
					<th align="center" width="9%">56网</th>
					<th align="center" width="9%">新浪网</th>
					<th align="center" width="9%">搜狐</th>
					<th align="center" width="9%">CNTV</th>
					<th align="center" width="9%">激动网</th>
					<th align="center" width="9%">乐视网</th>
					<th align="center" width="9%">奇艺网</th>
					<th align="center" width="5%">QQ</th>
					<th align="center" width="5%">PPTV</th>
					
				</tr>

				<s:iterator value="pageInfo.results" status="index" >
				
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="left">
							<s:property value="orderStage"/>(<s:property value="orderId"/>)
						</td>
						
							<s:iterator value="siteEpisode" status="siteIndex">
										<input type="hidden" id="hstage<s:property value="orderId" />" value="<s:property value="orderStage" />" />
										<td <s:if test="url == null || url == ''">style="background: #B2DFEE;"</s:if>
										ondblclick="getEpisode(<s:property value="id" />, <s:property value="orderId" />, <s:property value="viewOrder" />);">
											<s:if test="url != null && url != ''"> 
												<a target="_blank" href="<s:property value="url" />">已收录 (<s:property value="orderStage"/>)</a>
											</s:if>
										</td>													
									</s:iterator>
					
						</tr>
					
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td><s:if test="canAddEpisodeSize == 1"><a href="#" onclick="addEpisodeSize(); return false;" >添加一集</a></s:if></td>
						<td colspan="10"> <s:property
					value="pageInfo.totalRecords" /> ----
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="EpisodeDetail_detail"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="programmeId" value="programmeId"/>	
							
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="EpisodeDetail_detail">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="programmeId" value="programmeId"/>
							</s:url>"><s:text
								name="nextpage"/></a>
						</s:if></td>
					</tr>
				</tfoot>
			</table>
       
				       <div class="select"><strong>Other Pages: </strong> <input
					type="hidden" id="pageurl"
					value="<s:url action="EpisodeDetail_detail">
												<s:param name="programmeId" value="programmeId"/>
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
