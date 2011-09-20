<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="topword.title.list" /></title>


<link type="text/css"
	href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
<link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>"
	rel="stylesheet" type="text/css" />

<script type="text/javascript"
	src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/jquery.tablednd_0_5.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/ui.datepicker.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
<script type="text/javascript"><!--

	$(document).ready(function() {
		var options = { 
		        //target:        '#output',   // target element(s) to be updated with server response 
		        beforeSubmit:  showRequest,  // pre-submit callback 
		        success:       showResponse  // post-submit callback 
		 
		      
		    }; 
		 
		    // bind form using 'ajaxForm' 
		    $('#FileUpload').ajaxForm(options); 
		
		 
		// pre-submit callback 
		function showRequest(formData, jqForm, options) { 
		    			 
		    return true; 
		} 
		 
		// post-submit callback 
		function showResponse(responseText, statusText)  { 
		    var o = eval('(' + responseText + ')');  		
		    var id = $("#operateId").val();
		    var pic = $('#pic' + id).val();		
	        var html = '<img class="person" src="' + o.filePath + '" />';
	        $('#pic' + id).html(html);
	       // $('#TopWord_save_topWord_picturePath').attr("disabled","disabled");
	        $.ajax({
				url: "<s:url action="DirectoryPerson_updatePicAjax" />",
				type: "POST",
				data: "directoryPersonInfoId=" + id + "&picPath=" + o.filePath  + "&startDate=" + $('#startDate').val(),
				success: function(data) {
						var jsonArr = eval('(' + data + ')');
						if(jsonArr.status == "success") {
							alert("图片修改成功");
						}
					}	
		     	});	         
		} 
		

		$("#dialog").dialog({
			bgiframe: true,
			autoOpen: false,
			height: 250,
			modal: true,
			buttons: {
			'取消' : function() {
				$(this).dialog('close');
			},
				'确定': function() {
					var bValid = true;
						
					if (bValid) {
						$('#FileUpload').submit();
						$(this).dialog('close');
					}
			}
			
		},
		close : function() {
		}
	});
	
	    // Initialise the table
		$("#sorttable").tableDnD({
			 onDragClass: "bg",	
			 dragHandle: "dragHandle",			
			 onDrop: function(table, row) {
				var rows = table.tBodies[0].rows;
	            var newOrder = "";
	            for (var i=0; i<rows.length; i++) {
	            	newOrder += rows[i].id+" ";
	            }
		     //   alert(debugStr);
	
			     $.ajax({
						url: "<s:url action="DirectoryPerson_updateOrder" />",
						type: "POST",
						data: "newOrder=" + newOrder + "&orginalOrder=" + $('#TopWord_orginalOrder').val() + "&startDate=" + $('#startDate').val(),
						success: function(data) {
								var jsonArr = eval('(' + data + ')');
								if(jsonArr.status == "success") {
									$('#TopWord_orginalOrder').val(jsonArr.newOrder)
								}
							}	
				     	});
           	 	
        	}
			
		});
	});


	var editing  = false;
	var newhtmlhead;
	var newhtmltail;
	var editobj;
	var noVersion;	

	function cancelupdateorder(){		    
		editing = false;
		neworder = $("#neworder").val();	    
	    editobj.html(neworder + newhtmltail);
	}
		
		$(function() {


			$("#DirectoryPerson_list_channelName").val("明星");
			$("#DirectoryPerson_list_channelName").change(function() {
				if($("#DirectoryPerson_list_channelName").val() != 10) {
					var url = '<s:url action="Directory_list" />';
					location.href = url + '?channelName=' + $("#DirectoryPerson_list_channelName").val() + '&startDate=' + $("#startDate").val();
					return;
				}
				$("#TopWord_list").submit();
			});

			

			$("#sorttable tr").hover(function() {
		          $(this.cells[0]).addClass('showDragHandle');
		    }, function() {
		          $(this.cells[0]).removeClass('showDragHandle');
		    });
					

			$("#activeTopDate").click(function () {
				if(!confirm("确认上线？")) {
					return;
				}
				var channelName = $("#DirectoryPerson_list_channelName").val();
				var startDate = $("#startDate").val();
				//$("#cn").val(channelName);
				//$("#sd").val(startDate);
				//$("#changetd").submit();
				
				$.ajax({					
					url: '<s:url action="DirectoryPerson_changeTopDate" />',
					type: 'GET',
					data: 'channelName=' + channelName + '&startDate=' + startDate,
					success: function(data) {
						alert("上线成功");
					}
				});



			});

			

			$('.editOrder, dragHandle').dblclick(function(event) {			
				if(editing) return;
				editobj = $(this);
				html = $(this).html();

				orderendidx = html.indexOf('<input');
				order = html.substring(0, orderendidx);

				idstartidx = html.indexOf('value="');
				idendidx = html.indexOf('"', idstartidx + 7);
				wordid = html.substring(idstartidx + 7, idendidx);

			

				newhtmltail = html.substring(orderendidx); 
		        $(this).html('<input id="neworder" type="text" value="' + order + '" /> <input type="button" id="ajaxupdate" value="更新" onclick="updateorder(' + wordid + ');"/> <input type="button" id="ajaxupdate" value="取消" onclick="cancelupdateorder();"/>');
		        editing = true;
		    });

			$("#startDate").datepicker({dateFormat: 'yy-mm-dd'});
			if($("#startDate").val() == "") {
				var myDate=new Date();
				myDate.setDate(myDate.getDate()-1);
				$("#startDate").datepicker("setDate", myDate);
			}
			
			$("#pageselect").change(function() {
				location.href = $("#pageurl").val() + $("#pageselect").val();
			});

			$("#TopWord_list_channelName").change(function() {
				$("#TopWord_list").submit()
			});

			$("a[name='blockLink']").click(function() {
				return confirm("确认屏蔽？");
			});

			$("a[name='deleteLink11']").click(function() {
				

				$.ajax({					
					url: '<s:url action="DirectoryPerson_deleteAjax" />',
					type: 'GET',
					data: 'directoryPersonInfoId=' + id,
					
					success: function(data) {
						alert("屏蔽成功");
					}
				});
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
				$("#TopWord_batchdelete").submit();
			 } else {
				 alert("没有数据要删除");
				 return false;
			 }
		 });

			$('#downloadFile').click(function () {
				
				//$("#cn").val(channelName);
			//$("#sd").val(startDate);
			//$("#changetd").submit();
			
			$("#DirectoryPerson_list").attr("action", "DirectoryPerson_exportFile.do");
			$("#DirectoryPerson_list").submit();
			$("#DirectoryPerson_list").attr("action", "DirectoryPerson_list.do");

		});
		});

		function deleteAjax(id) {

			if(!confirm("确认永久屏蔽？")) {
				return;
			}
			
			$.ajax({					
				url: '<s:url action="DirectoryPerson_deleteAjax" />',
				type: 'GET',
				data: 'directoryPersonInfoId=' + id + "&startDate=" + $('#startDate').val(),
				
				success: function(data) {
					//alert($("#block" + id).html());
					alert("屏蔽成功");
					$("#block" + id).html("屏蔽");
					$("#deleteLink" + id).attr("css", "listbutton, highlight");
					$("#readdLink" + id).attr("css", "listbutton");
					
				}
			});

		}

		function readdAjax(id) {

			if(!confirm("确认解除屏蔽？")) {
				return;
			}
			
			$.ajax({					
				url: '<s:url action="DirectoryPerson_reAddAjax" />',
				type: 'GET',
				data: 'directoryPersonInfoId=' + id + "&startDate=" + $('#startDate').val(),
				
				success: function(data) {
					//alert($("#block" + id).html());
					alert("解除屏蔽成功");
					$("#block" + id).html("显示");
					$("#deleteLink" + id).attr("css", "listbutton");
					$("#readdLink" + id).attr("css", "listbutton, highlight");
					
				}
			});

		}

		function deleteForEverAjax(id) {

			if(!confirm("确认永久删除？")) {
				return;
			}
			
			$.ajax({					
				url: '<s:url action="DirectoryPerson_deleteForEverAjax" />',
				type: 'GET',
				data: 'directoryPersonInfoId=' + id,
				
				success: function(data) {
					//alert($("#block" + id).html());
					alert("删除成功");
					$("#block" + id).html("删除");
				}
			});

		}

	

		function changepic(id) {
			$('#dialog').dialog('open');
			$('#picPath').val("");
			//var obj = document.getElementById('FileUpload_upload') ;
			var file = $("#FileUpload_upload");  
			file.after(file.clone().val(""));  
			file.remove();
			$("#operateId").val(id);
		}

		function changechannelname(id) {
			$('#channelNameS' + id).show();
			$('#channelNameL' + id).hide();
		}

		function submitchangechannelname(id) {
			
			$.ajax({					
				url: '<s:url action="DirectoryPerson_updateAjax" />',
				type: 'GET',
				data: 'directoryPersonInfoId=' + id + '&channelName=' + $('#channelNameValue' +id).val(),
				
				success: function(data) {
					//alert($("#block" + id).html());
					alert("修改成功");
					
					$('#channelNameS' + id).hide();
					$('#channelNameL' + id).html($('#channelNameValue' +id + ' option:selected').text());
					$('#channelNameL' + id).show();
				}
			});
		}

		function cancelchangechannelname(id) {
			
			$('#channelNameS' + id).hide();
			$('#channelNameL' + id).show();
		}
	--></script>
</head>
<body>
<div id="main">
<div id="header"><a href="#" class="logo"><img
	src="img/logo.gif" width="101" height="29" alt="" /></a></div>
<div id="middle"><s:include value="module/leftnav.jsp"></s:include>
<div id="center-column">
<div class="top-bar"><a
	href="<s:url action="TopWord_input">
			<s:param name="directoryPersonInfoId" value="-1" />
			<s:param name="startDate" value="startDate" />
			<s:param name="channelName" value="channelName" />
		</s:url>"
	class="button"><s:text name="topWord.add" /></a>
<h1><s:text name="soku.topword.title" /> <s:if test='today'>
	<span class="highlight">线上数据</span>
</s:if></h1>
</div>
<br />
<div class="select-bar"><s:form action="DirectoryPerson_list"
	validate="false" cssClass="form" theme="simple">
	<s:select list="channelList" listKey="id" listValue="label"
		key="channelName"></s:select>
	<s:textfield id="startDate" name="startDate" />
	<s:select key="visible" list="#{-1:'所有', 1:'屏蔽', 0:'正常'}" listKey="key"
		listValue="value"></s:select>
	<label> <input type="submit" name="Submit"
		class="ui-button ui-state-default ui-corner-all" value="Search" /> </label>
	<s:if test="#session.user_permission.manage_top_word != null">
		<input type="button" id="activeTopDate"
			class="ui-button ui-state-default ui-corner-all" value="上线" />
	</s:if>

	<input type="button" id="downloadFile"
		class="ui-button ui-state-default ui-corner-all" value="导出数据" />
</s:form> <s:form action="TopWord_changeTopDate" id="changetd">
	<input type="hidden" id="sd" name="startDate" />
	<input type="hidden" id="cn" name="channelName" />

</s:form> <input type="hidden" id="operateId" /></div>
<div class="table"><img src="img/bg-th-left.gif" width="8"
	height="7" alt="" class="left" /> <img src="img/bg-th-right.gif"
	width="7" height="7" alt="" class="right" /> <s:form action="TopWord"
	validate="false" cssClass="form" theme="simple">
	<s:hidden name="orginalOrder"></s:hidden>
	<table id="sorttable" class="listing" cellpadding="0" cellspacing="0">
		<tbody>
			<tr>

				<th align="center" width="10%">顺序</th>
				<th align="center" width="10%">关键字</th>
				<th align="center" width="15%">图片</th>
				<th align="center" width="5%">屏蔽</th>
				<th align="center" width="10%">搜索量</th>
				<th align="center" width="10%">操作</th>
			</tr>

			<s:iterator value="pageInfo.results" status="index">
				<tr id="<s:property value="id" />">
					<td align="center" class="editOrder, dragHandle"><s:property
						value="order_number" /> <s:hidden name="id" /></td>


					<td align="left"><s:property value="name" /></td>
					<td align="center" id="pic<s:property value="id" />"
						ondblclick="changepic('<s:property value="id" />');"><s:if
						test="logo == null || logo.length() == 0">
						<img class="person"
							src='http://g2.ykimg.com/0900641F464A6318D600000000000000000000-0000-0000-0000-000042145BB0' />
					</s:if> <s:else>
						<img class="person" src='<s:property value="logo" />' />
					</s:else></td>

					<td align="left"><span id="block<s:property value="id" />">
					<s:if test='visible == 1'>
								显示
							</s:if> <s:if test='visible == 0'>
								屏蔽
							</s:if><span></td>

					<td align="center"><s:property value="search_nums" /></td>
				
					<td align="center"><s:if
						test="#session.user_permission.block_top_word != null">
						<a id="deleteLink<s:property value="id" />"
							onclick="deleteAjax(<s:property value="id" />);return false"
							href="#"
							class="listbutton<s:if test='visible == 0'>, highlight</s:if>">
						永久屏蔽 </a> &nbsp;<br />


						<a id="readdLink<s:property value="id" />"
							onclick="readdAjax(<s:property value="id" />);return false"
							href="#" class="listbutton<s:if test='visible == 1'></s:if>">
						解除屏蔽 </a> &nbsp;<br />

						<a
							href="<s:url action="DirectoryPerson_input"> 
								<s:param name="directoryPersonInfoId" value="id" />
								<s:param name="startDate" value="startDate" />
							</s:url>">修改</a>
					</s:if></td>
				</tr>
			</s:iterator>
		</tbody>
		<tfoot>
			<tr>
				<td></td>
				<td colspan="8"><s:property value="pageInfo.totalRecords" />
				----- <s:text name="totalpagenumber" />: <s:property
					value="pageInfo.totalPageNumber" /> <s:text
					name="currentpagenumber" />: <s:property
					value="pageInfo.currentPageNumber" /> <s:if
					test="pageInfo.hasPrevPage">
					<a
						href="<s:url action="DirectoryPerson_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="channelName" value="channelName" />
								<s:param name="startDate" value="startDate" />
								<s:param name="counter" value="counter" />
							</s:url>"><s:text
						name="prevpage" /></a>
				</s:if> <s:if test="pageInfo.hasNextPage">
					<a
						href="<s:url action="DirectoryPerson_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="channelName" value="channelName" />
								<s:param name="startDate" value="startDate" />
								<s:param name="counter" value="counter" />
							</s:url>"><s:text
						name="nextpage" /></a>
				</s:if></td>
			</tr>
		</tfoot>
	</table>
</s:form>
<div class="select"><strong>Other Pages: </strong> <input
	type="hidden" id="pageurl"
	value="<s:url action="DirectoryPerson_list">          						
								<s:param name="channelName" value="channelName" />
								<s:param name="startDate" value="startDate" />
								<s:param name="counter" value="counter" />
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


<div id="dialog" title="Upload Picture"><s:form
	action="FileUpload" validate="false" cssClass="form"
	enctype="multipart/form-data">
	<input type="hidden" name="personImage" value="person_image" />
	图片路径： <input type="text" id="picPath" name="imageUrl" />
	<s:file name="upload" label="上传图片" />
</s:form></div>
</div>
</body>
</html>
