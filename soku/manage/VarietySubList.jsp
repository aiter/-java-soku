<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="varietySub.title.list" /></title>

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

			$("#dialogSite").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				modal: true,
				
				close : function() {
					//allFields.val('').removeClass('ui-state-error');
				}
			});

			$('#changeSite').click(function() {
				$('#dialogSite').dialog('open');
			}).hover(function() {
				$(this).addClass("ui-state-hover");
			}, function() {
				$(this).removeClass("ui-state-hover");
			}).mousedown(function() {
				$(this).addClass("ui-state-active");
			}).mouseup(function() {
				$(this).removeClass("ui-state-active");
			});

			$("#imagedialog").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				width: 400,
				modal: true,
				buttons: {
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
			},
			'取消' : function() {
				$(this).dialog('close');
			}
			},
			close : function() {
				//allFields.val('').removeClass('ui-state-error');
			}
		});


			$('input[name="ymddate"]').each(function() {
				$(this).datepicker( {
							dateFormat: 'yy-mm-dd',
							beforeShow: function(input, inst) { 
								$(this).attr("class", "editing");
								$(this).attr("readonly", ""); 
							}
							}
										
						);
				
			});
			$("#pageselect").change(function() {
				location.href = $("#pageurl").val() + $("#pageselect").val();
			});

		

			$("a[name='deleteLink']").click(function() {
				return confirm("确认删除？");
			});

			$("#thcheck").click(function() {
				if($(this).attr("checked")) {
					$("[name=batchdeleteids]").each(function(){
						$(this).attr("checked", true);
					});
				} else {
					$("[name=batchdeleteids]").each(function() {
						$(this).attr("checked", false);
					});
				}
			});

		 $("#batchdelete").click(function() {
			 
			 haschecked = false;
			 $("[name=batchdeleteids]").each(function(){
					if($(this).attr("checked"))
						haschecked = true;
				});
			 if(haschecked) {
				if(!confirm("确认删除？"))
					 return false;
				$("#VarietySub_batchdelete").submit();
			 }  else {
				 alert("没有数据要删除");
				 return false;
			 }
			 return false;
		 });
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

		function changeLogo(orderId) {
			$("#currentOrderId").val(orderId);
			var id = $("#hiddenId" + orderId).val();
			$('#imagedialog').dialog('open');
			$("#FileUpload_upload").val("");
			$("#picPath").val("");
		}
		 
		// pre-submit callback 
		function showRequest(formData, jqForm, options) { 
		    			 
		    return true; 
		} 
		 
		// post-submit callback 
		function showResponse(responseText, statusText)  { 
			var orderId = $("#currentOrderId").val();
		    var o = eval('(' + responseText + ')');  				
	        $('#logoUrl' + orderId).val(o.filePath);	
	        $('#logoImg' + orderId).attr('src', o.filePath);
	       // $('#Video_save_video_picturePath').attr("disabled","disabled");
	        	         
		} 

		function showsiteversion(id) {
			window.location = $("#siteversionlink" + id).attr("href")
		}

		function deleteversion(id) {
			window.location = $("#deletelink" + id).attr("href")
		}

		function editableymd(id) {
			$("#ymd" + id).attr("class", "editing");
			$("#ymd" + id).attr("readonly", "");
		}

		function updateymd(id) {
			$("#ymd" + id).attr("class", "label");
		}

		function editablesubname(id) {
			$("#subname" + id).attr("class", "editing");
			$("#subname" + id).attr("readonly", "");
		}

		function updatesubname(id) {
			$("#subname" + id).attr("class", "label");
		}

		function sethiddenhd(id) {
			var hd = $("#hd" + id).attr("checked") ? 1 : 0;
			$("#hdH" + id).val(hd);
		}

		function save(id) {
			var subname = $("#subname" + id).val();
			var ymd = $("#ymd" + id).val();
			var playUrl = $("#playUrl" + id).val();
			var logoUrl = $("#logoUrl" + id).val();
			var seconds = $("#seconds" + id).val();
			var hd = $("#hdH" + id).val();
			var veid = $("#veid" + id).val();
			var orderId = $("#orderId" + id).val();
			var sourceSiteId = $('#sourcesite' + id).val();

			$.ajax({
				url: '<s:url action="VarietySub_saveAjax" />',
				type: 'POST',
				data: 'varietySubId=' + id + '&varietySub.subName=' + subname + '&varietySub.ymd=' + ymd + '&varietySub.ve.id=' + veid + '&varietySub.ve.url=' + playUrl +'&varietySub.ve.logo=' + logoUrl + '&varietySub.ve.seconds=' + seconds + '&varietySub.ve.hd=' + hd + '&sourceSite=' + sourceSiteId + '&varietySub.orderId=' + orderId,
				success: function(data) {
					var o = eval('(' + data + ')');					
					if(o.status == 'success') {
						$("#savesuccess"+ id).attr("style", "color:green");
					} else {
						$("#savefail" + id).attr("style", "color:red");
					}
				    
				}
			});
		}


		function getVideoDetail(id) {
			var url = $("#playUrl" + id).val();
			
			
			$.ajax({
				url: '<s:url action="VarietySub_searchVarietyDetail" />',
				type: 'POST',
				data: 'url=' + url,
				success: function(data) {
					var o = eval('(' + data + ')');	
					$("#logoUrl" + id).val(o.logo);
					$("#logoImg" + id).attr('src', o.logo);
					$("#seconds" + id).val(getSeconds(o.seconds));
				}
			});
		}

		function getSeconds(v) {
			if(1 == 1) {
				var h = v / 60;
				var s = v % 60;
				s = Math.floor(s);
				if(s < 10) {
					s = '0' + s;
				}
				return Math.floor(h) + ':' + s ;
			}
		}

		function play(id) {
			var url = $("#playUrl" + id).val();
			window.open(url);
		}

		
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
      	<s:if test="varietyId > 0">
      		<a href="<s:url action="VarietyEpisode_input">
			<s:param name="varietyId" value="varietyId" />
			<s:param name="varietyEpisodeId" value="-1" />
				</s:url>"  class="button"><s:text
            name="varietySub.add"/></a>
     	 </s:if>  
        <h1><s:text name="soku.varietySub.title" /></h1>
      </div>
      <br />
      <div class="select-bar">
         <s:form action="VarietySub_list" validate="false" cssClass="form" theme="simple">
	        <s:if test="varietySubFilter!=null">
			</s:if>
      		<label>
	        <s:textfield key="searchWord" cssClass="text"/>
	        </label>
	        <label>
	        <input type="submit" name="Submit" class="ui-button ui-state-default ui-corner-all" value="Search" />
	        </label>
      	</s:form> 
      </div>
      <input type="button" id="changeSite" class="ui-button ui-state-default ui-corner-all" value="更改站点"></input>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <s:form action="VarietySub_batchdelete" validate="false" cssClass="form" theme="simple">
        <s:hidden name="sourceSiteId"></s:hidden>
         <table class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" width="20%"><s:text name="varietySub.heading.names" /></th>					
					<th align="center" width="10%"><s:text name="varietySub.heading.ymd" /></th>					
					<th align="center" width="35%">播放地址</th>
					<th align="center" width="25%">详细</th>
					<th align="center" width="10%"><s:text name="varietySub.heading.operation" /></th>					
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						
						<td align="left">	
							<input type="text" id="subname<s:property value="id"/>" value="<s:property value="subName" />" 
								readonly="readonly"
								class="label" ondblclick="editablesubname(<s:property value="id"/>);" onblur="updatesubname(<s:property value="id"/>)"/>					
						</td>						
						<td align="left">
							<input type="text" id="ymd<s:property value="id"/>" value="<s:property value="ymd" />" 
								<s:if test="ve.orderIdType == 1"> name="ymddate" </s:if> readonly="readonly"
								class="label" ondblclick="editableymd(<s:property value="id"/>);" onblur="updateymd(<s:property value="id"/>)"/>
						</td>
						
						<td align="left"><input type="text" id="playUrl<s:property value="id"/>" value="<s:property value="ve.url" />" onblur="getVideoDetail(<s:property value="id"/>);"/>
								<input type="button" class="ui-button ui-state-default ui-corner-all" value="播放" onclick="play(<s:property value="id"/>)" />
						</td>
						<td align="center">
							<div>
								<img src='<s:property value="ve.logo" />' id="logoImg<s:property value="id"/>" /> <br/>								
							 	<input type="hidden" id="logoUrl<s:property value="id"/>" value="<s:property value="ve.logo" />" name="teleplayEpisodeList[<s:property value="orderId"/>].logo"/>
								<input id="changelogo<s:property value="id"/>" type="button" onclick='changeLogo(<s:property value="id"/> );' 
										 				value="更改图片" />	<br />
								时长：<input id="seconds<s:property value="id"/>" name="seconds" type="text" value="<s:property value="ve.seconds"/>" class="editing" /> <br/>
								<span class="HD" title="高清"></span>
								<input type="checkbox" id="hd<s:property value="id"/>" <s:if test="ve.hd == 1">checked="checked"</s:if> onchange="sethiddenhd(<s:property value="id"/>)"/> <br />
								<input id="orderId<s:property value="id"/>" name="orderId" type="text" value="<s:property value="orderId"/>" class="editing" />
								<input type="hidden" id="hdH<s:property value="id"/>" value="<s:property value="ve.hd"/>"  name="teleplayEpisodeList[<s:property value="id"/>].hd"/>
								<input type="hidden" id="veid<s:property value="id"/>" value="<s:property value="ve.id"/>"  />
								<input type="hidden" id="sourcesite<s:property value="id"/>" value="<s:property value="ve.sourceSite"/>"  />
								<p id="savesuccess<s:property value="id"/>" style="display:none">保存成功!</p>
							    <p id="savefail<s:property value="id"/>" style="display:none">保存失败!</p> 
							</div>
						</td>
						<td align="left">
							<input id="savebtn<s:property value="id"/>" type="button" onclick='save(<s:property value="id"/>);' value="保存"></input>
							<input type="button" value="站点版本" onclick='showsiteversion(<s:property value="id"/>);'></input>
							<input type="button" value="删除" onclick='deleteversion(<s:property value="id"/>);'></input>
							<a name="deleteLink" id="deletelink<s:property value="id"/>" class="listbutton"
								href="<s:url action="VarietySub_delete">
									<s:param name="varietySubId" value="id"/>
									<s:param name="varietyId" value="varietyId"/>
									<s:param name="searchWord" value="searchWord" />
									<s:param name="pageNumber" value="pageNumber" />
								</s:url>" style="display : none">
								<s:text name="varietySub.delete" /> </a> &nbsp;
							<a class="listbutton" id="siteversionlink<s:property value="id"/>" href="<s:url action="VarietyEpisode_list">
							<s:param name="varietySubId" value="id" />
									</s:url>" style="display : none"><s:text
            							name="varietyEpisodeList.list"/></a>
						</td>
					</tr>
				</s:iterator>
				<!-- 
							<a name="deleteLink" class="listbutton"
								href="<s:url action="VarietySub_delete">
									<s:param name="varietySubId" value="id"/>
									<s:param name="varietyId" value="varietyId"/>
									<s:param name="searchWord" value="searchWord" />
									<s:param name="pageNumber" value="pageNumber" />
								</s:url>">
								<s:text name="varietySub.delete" /> </a> &nbsp;
							 <a class="listbutton" href="<s:url action="VarietySub_input">
									<s:param name="varietySubId" value="id"/>
									<s:param name="varietyId" value="varietyId"/>
									<s:param name="searchWord" value="searchWord" />
									<s:param name="pageNumber" value="pageNumber" />
								</s:url>">
						<s:text name="varietySub.edit" /> </a>   &nbsp;  <a class="listbutton" href="<s:url action="VarietyEpisode_list">
							<s:param name="varietySubId" value="id" />
									</s:url>" ><s:text
            							name="varietyEpisodeList.list"/></a>
						 -->
				 <tfoot>
					<tr>
						<td colspan="8"> <s:property
					value="pageInfo.totalRecords" /> ----- 
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="VarietySub_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="varietyId" value="varietyId" />
								<s:param name="searchWord" value="searchWord" />
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="VarietySub_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="varietyId" value="varietyId" />
								<s:param name="searchWord" value="searchWord" />
							</s:url>"><s:text
								name="nextpage"/></a>
						</s:if></td>
					</tr>
				</tfoot>
			</table>
			<s:hidden name="varietyId" />
			<s:hidden name="pageNumber" />
   			<s:hidden name="searchWord" />
			</s:form>
        <div class="select"> <strong>Other Pages: </strong>
           <input type="hidden" id="pageurl" value="<s:url action="VarietySub_list">
								<s:param name="searchWord" value="searchWord" />
								<s:param name="varietyId" value="varietyId" />
								<s:param name="varietySubFilter" value="varietySubFilter" />
								<s:param name="pageNumber" value="0" />
							</s:url>" />
          <select id="pageselect">
          		<s:bean name="org.apache.struts2.util.Counter" id="counter">  
     			 <s:param name="first" value="1"/>  
     			 <s:param name="last" value="10"/>  
   				 	<s:iterator status="count">  
   				 		<s:if test="(pageInfo.currentPageNumber - #count.index) > 0">
     					 <option><s:property value="pageInfo.currentPageNumber - #count.index"/></option>
     					 </s:if> 
    				</s:iterator>  
   			 	</s:bean> 
          		 
          	   <s:bean name="org.apache.struts2.util.Counter" id="counter">  
     			 <s:param name="first" value="1"/>  
     			 <s:param name="last" value="10"/>  
   				 	<s:iterator status="count">  
   				 		<s:if test="(pageInfo.currentPageNumber + #count.index) < pageInfo.totalPageNumber">
     					 	<option><s:property value="#count.index + pageInfo.currentPageNumber + 1"/></option>
     					</s:if>  
    				</s:iterator>  
   			 	</s:bean>  
          </select>
        </div>
      </div>
      
    </div>

  </div>
  <div id="footer"></div>
</div>
<input id="currentOrderId" type="hidden" value="" />
 <div id="imagedialog" title="Upload Picture">

	<s:form action="FileUpload" validate="false" cssClass="form" enctype="multipart/form-data">
	图片路径： <input type="text"  id="picPath" name="imageUrl"/>
	<s:file name="upload" label="上传图片"/>	
	</s:form>
 </div>
 
<div id="dialogSite" title="选择站点">

	<s:form action="VarietySub_list" validate="false" cssClass="form" enctype="multipart/form-data" theme="simple">
		<s:select id="siteSelect" name="sourceSite" list="sitesFilterMap" listKey="key" listValue="value"></s:select>
		<s:hidden name="varietyId"></s:hidden>		
		<input type="submit" value="确认" />
	</s:form>
 </div>
</body>
</html>
