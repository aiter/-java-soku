<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="typeword.title.list" /></title>


	<link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"   type="text/css"/>
    <link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
	
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.tablednd_0_5.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.datepicker.js"/>"></script>
		<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	<script type="text/javascript">




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
		        var html = '<img src="' + o.filePath + '" />';
		        $('#pic' + id).html(html);
		       // $('#TopWord_save_topWord_picturePath').attr("disabled","disabled");
		        $.ajax({
					url: "<s:url action="TypeWord_updatePicAjax" />",
					type: "POST",
					data: "typeWordId=" + id + "&picPath=" + o.filePath,
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

			$('.editOrder').dblclick(function(event) {			
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
				$("#startDate").datepicker("setDate", new Date());
			}
			
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

		 $("#deleteBatch").click(function() {			 
			 haschecked = false;
			 $("[name=batchdeleteids]").each(function(){
					if($(this).attr("checked"))
						haschecked = true;
				});
			 if(haschecked) {
				 if(!confirm("确认删除？"))
					 return false;
				$("#TypeWord_batchdelete").submit();
			 } else {
				 alert("没有数据要删除");
				 return false;
			 }
		 });
		});

		function changepic(id) {
			$('#dialog').dialog('open');
			$("#operateId").val(id);
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
      	<a href="<s:url action="TypeWord_input">
			<s:param name="typeWordId" value="-1" />
		</s:url>"  class="button"><s:text
            name="typeWord.add"/></a>
        <h1><s:text name="soku.typeword.title" /></h1>
      </div>
      <br />
      <div class="select-bar">
         <s:form action="TypeWord_list" validate="false" cssClass="form" theme="simple">
      		 <s:textfield id="keyword" name="keyword" />
      		 
      		 <s:select list="channelList" listKey="name" listValue="label" key="cate"></s:select>
	        <label>
	        <input type="submit" name="Submit" class="ui-button ui-state-default ui-corner-all" value="Search" />
	        </label>
      	</s:form> 
      	
      	<input type="hidden" id="operateId" />
      </div>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <s:form action="TypeWord_batchdelete" validate="false" cssClass="form" theme="simple">
        <s:hidden name="orginalOrder"></s:hidden>
        <input type="hidden" name="cate" value="<s:property value="cate" />" />
        <input type="hidden" name="pageNumber" value="<s:property value="pageNumber" />"/>
        <table id="sorttable" class="listing" cellpadding="0" cellspacing="0">
			<tbody>
				<tr>
					<th align="center" width="5%"><input id="thcheck"
						type="checkbox" /></th>
					<th align="center" width="15%">关键字</th>
					<th align="center" width="10%">类型</th>
					<th align="center" width="10%">图片</th>
					
					<th align="center" width="10%">操作</th>
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="center">	<input type="checkbox" name="batchdeleteids"
						value="<s:property value="id" />" /></td>
						<td align="left"><s:property value="keyword"/></td>
						<td align="center"><s:property value="labels"/></td>
						<td align="center" id="pic<s:property value="id" />" ondblclick="changepic('<s:property value="id" />');"><img src='<s:property value="pic" />' /></td>
						
						<td align="center"><a name="deleteLink" class="listbutton"
							href="<s:url action="TypeWord_delete">
								<s:param name="typeWordId" value="id"/>
								<s:param name="channelName" value="channelName" />
								<s:param name="startDate" value="startDate" />
								<s:param name="pageNumber" value="pageNumber" />
								<s:param name="cate" value="cate" />
							</s:url>">
						<s:text name="typeword.delete" /> </a> &nbsp; <a class="listbutton"
							href="<s:url action="TypeWord_input">
								<s:param name="typeWordId" value="id"/>
								<s:param name="channelName" value="channelName" />
								<s:param name="startDate" value="startDate" />
								<s:param name="pageNumber" value="pageNumber" />
								<s:param name="cate" value="cate" />
							</s:url>">
						<s:text name="typeword.edit" /> </a></td>
					</tr>
				</s:iterator>
			</tbody>
				 <tfoot>
					<tr>
						<td><input type="button" id="deleteBatch"
						class="ui-button ui-state-default ui-corner-all" value="批量删除"></input></td>
						<td colspan="9"><s:property	value="pageInfo.totalRecords" /> -----
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="TypeWord_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="channelName" value="channelName" />
								<s:param name="startDate" value="startDate" />
								<s:param name="cate" value="cate" />
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="TypeWord_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="channelName" value="channelName" />
								<s:param name="startDate" value="startDate" />
								<s:param name="cate" value="cate" />
							</s:url>"><s:text
								name="nextpage"/></a>
						</s:if></td>
					</tr>
				</tfoot>
			</table>
			</s:form>
        <div class="select"> <strong>Other Pages: </strong>
           <input type="hidden" id="pageurl" value="<s:url action="TypeWord_list">          						
								<s:param name="channelName" value="channelName" />
								<s:param name="startDate" value="startDate" />
								<s:param name="cate" value="cate" />
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
  
   <div id="dialog" title="Upload Picture"><s:form
	action="FileUpload" validate="false" cssClass="form"
	enctype="multipart/form-data">
	图片路径： <input type="text" id="picPath" name="imageUrl" />
	<s:file name="upload" label="上传图片" />
</s:form></div>
  
</div>
</body>
</html>
