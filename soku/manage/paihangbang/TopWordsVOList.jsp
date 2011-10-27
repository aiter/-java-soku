<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="搜索词榜单" /></title>


<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
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

$(document).ready(function() {
	var options = { 
	        beforeSubmit:  showRequest,  // pre-submit callback 
	        success:       showResponse  // post-submit callback 
	    };
	    $('#FileUpload').ajaxForm(options); 
});

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
    $.ajax({
		url: "<s:url action="TopWords_update" />",
		type: "GET",
		data: "updateid=" + id + "&pic=" + o.filePath,
		success: function(data) {
			if("success" == data){
					alert("图片修改成功");
			}
			}
     	});
}

function chooseOne(cb) {
	var obj = document.getElementsByName("cbox");
	for( i= 0; i < obj.length; i++) {
		if(obj[i] != cb)
			obj[i].checked = false;
		else
			obj[i].checked = cb.checked;
	}
}

var editable = false;

function updateVisible(id){
	editable = true;
    newVisible = document.getElementsByName('new_visible'+id);
    i=0;
    for(;i<newVisible.length;i++){if(newVisible[i].checked){
			break;
    }};
	if(i==newVisible.length){alert("没有选择状态!")}
	else{
		var updateurl="TopWords_update.do";
		var visible = newVisible[i].value;
		$.ajax({
		    url: updateurl,
		    type: "POST",
		    data: ({"visible": visible, "updateid" : id}),
		    success: function(msg) {
			if("success" == msg){
				    alert("修改成功");
					$('#visibleRadio' + id).hide();
					var visible_text = "屏蔽";
					if(1==visible) 
						visible_text = "正常";
					$('#visibleShow' + id).html(visible_text);
					$('#visibleShow' + id).show();
			}else{
				 alert("修改失败");
				 $('#visibleRadio' + id).hide();
				 $('#visibleShow' + id).show();
			}
	    	}
		});
	}

	editable = false;
}

function editVisible(id){
    editable = false;
    $('#visibleRadio' + id).show();
	$('#visibleShow' + id).hide();
}

function changechannelname(id) {
	editable = false;
	$('#divchannel' + id).show();
	$('#channelName' + id).hide();
}

function cancelchangechannelname(id) {
	$('#divchannel' + id).hide();
	$('#channelName' + id).show();
	editable = true;
}

function submitchangechannelname(id) {
	
	$.ajax({
		url: '<s:url action="TopWords_update" />',
		type: 'GET',
		data: 'updateid=' + id + '&cate=' + $('#channelValue' +id).val(),
		
		success: function(msg) {
			if("success" == msg){
				alert("关键词类型修改成功");
				$('#channelName' + id).html($('#channelValue' +id + ' option:selected').text());
				$('#divchannel' + id).hide();
				$('#channelName' + id).show();
			}else{
				alert("关键词类型修改失败或已有相同分类");
				$('#divchannel' + id).show();
				$('#channelName' + id).hide();
			}
			
		}
	});
}

function changepic(id) {
	$('#dialog').dialog('open');
	$('#picPath').val("");
	var file = $("#FileUpload_upload");  
	file.after(file.clone().val(""));  
	file.remove();
	$("#operateId").val(id);
}

function searchNames() {
	var searchWord = $("#diaSearchWord").val();
	var subjectid = $("#operateSubjectId").val();
	if(searchWord == "") {
		alert('搜索关键字不能为空');
		return;
	}
	$.ajax({
		url: '<s:url action="TopWords_searchProgramme" />',
		type: 'POST',
		data: 'searchWord=' + searchWord +'&updateid=' + subjectid,
		success: function(data) {
			if(data == "[]") {
				var resStr = '所搜索的节目不存在';
				$("#diaResult").html(resStr);
				return;
			}
			var resultStr = "<table class='cbox'>";
			var jsonArr = eval('(' + data + ')');
			var i=0;
			for(var idx in jsonArr) {
				if(i==0){
					resultStr += '<tr ><td><input type="radio" name="cbox" value="' + 0 + '" onClick="chooseOne(this);"/>';
					resultStr += '<FONT color="green">取消对应节目</FONT></td></tr>';
					resultStr += '<input type="hidden" id="names0" value="" />';
					resultStr += '<tr><td></td></tr>';
					i+=1;
				}
				resultStr += '<tr ><td><input type="radio" name="cbox" value="' + jsonArr[idx].id + '" onClick="chooseOne(this);"/>';
				resultStr += '<a target="_blank" href=\"';
				if(jsonArr[idx].cate==3){
					resultStr += encodeURI("/VideoInfo_list.do?searchWord="+jsonArr[idx].name+"&accuratelyMatched=1&statusFilter=1&categoryFilter=3");
				}else{
					resultStr += encodeURI("/ProgrammeSite_list.do?programmeId="+jsonArr[idx].id);
				}
				resultStr += '\"a>';
				resultStr += jsonArr[idx].name + '</a></td></tr>';
				resultStr += '<input type="hidden" id="names' + jsonArr[idx].id + '" value="' + jsonArr[idx].name + '" />';
			}
			
			$("#diaResult").html(resultStr);
		}
	});
}

function changepname(id,name){
	$("#diaSearchWord").val(name);
	$("#operateSubjectId").val(id);
	$('#dialogNames').dialog('open');
}

$(function() {

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
	
	$("#topdate").datepicker({dateFormat: 'yy-mm-dd'});
	if($("#topdate").val() == "") {
		var myDate=new Date();
		myDate.setDate(myDate.getDate()-1);
		$("#topdate").datepicker("setDate", myDate);
	}
	
	$("#pageselect").change(function() {
		location.href = $("#pageurl").val() + $("#pageselect").val();
	});
	$("#list").click(function () {
		$("#topwordsForm").attr("action","TopWords_list.do");
		$("#topwordsForm").submit();
	});
	$("#createNew").click(function () {
		if(confirm("确认要重新生成？")){
			var date = $("#topdate").val();
			$.ajax({
				url: '<s:url action="TopWords_createMulu" />',
				type: 'POST',
				data: "topdate=" + date ,
				success: function(msg) {
					alert(msg);
				}
			});
		}
		
	});
	
	$("[name=cancelUpdateVisible]").click(function(event) {
		agent = jQuery.browser;
		if(agent.msie) {
			event.cancelBubble = true;
		} else {
			event.stopPropagation();
		}
		editable = false;
		$(this).parent().hide();
		$(this).parent().parent().find('.a').show();
		})

		$("#dialogNames").dialog({
			bgiframe: true,
			autoOpen: false,
			height: 250,
			modal: true,
			title : "对应节目",
			buttons: {
				'取消' : function() {
					$(this).dialog('close');
				},
				
					'确定': function() {
				var obj = document.getElementsByName("cbox");
				var proid ;
				var proname
				for( i= 0; i < obj.length; i++) {
					if(obj[i].checked) {
						proid = obj[i].value;
						proname = $("#names" + obj[i].value).val();
					}
				}
				if('undefined'==proid){
					$(this).dialog('close');
					return;
				}
				
				var id = $("#operateSubjectId").val();
				
				$.ajax({
					url: '<s:url action="TopWords_update" />',
					type: 'POST',
					data: "updateid=" + id + "&proid=" + proid,
					success: function(msg) {
						if("success" == msg){
						    alert("修改成功");
						    $("#pname" + id).html(proname);
						}
					}
				});
				
				$(this).dialog('close');
				}
				},
				close : function() {
					$("#diaResult").html("");
				}
		});
});
	
	function exportXls(){
		var num = document.getElementById("num").value;
		var cate = document.getElementById("topwordsForm_cate").value;
		var topdate = document.getElementById("topdate").value;
		if(num.length==0){
			alert("请输入导出的条数！");
			document.getElementById("num").focus();
			return;
		}
		window.location = 'TopWords_exportXls.do?num='+num+'&cate='+cate+'&topdate='+topdate;
	}
	
	function compareKeyword(){
		var num = document.getElementById("top").value;
		var cate = document.getElementById("topwordsForm_cate").value;
		if(num.length==0){
			alert("请输入导出的条数！");
			document.getElementById("top").focus();
			return;
		}
		window.open("TopWords_compareKeyWord.do?num="+num+"&cate="+cate,"_blank");
	}
	
	</script>
</head>
<body>
<div id="main">
<div id="middle"><s:include
	value="/soku/manage/module/leftnav.jsp"></s:include>

<div id="center-column">
<div class="top-bar">
<h1><s:text name="搜索词排行" /></h1>
</div>
<br />
<div>
<s:property value="bangdandateMsg"/>
<s:property value="fundateMsg"/>
<s:property value="muludateMsg"/>
</div>
<div class="select-bar"><s:form id="topwordsForm"
	action="TopWords_list" validate="false" cssClass="form"
	theme="simple">
	<label> <s:textfield id="topdate" name="topdate" /> </label>
	<label> <s:select key="cate"
		list="catevos" listKey="catevalue" listValue="catename"></s:select>
	</label>
	<label> <s:select key="visible"
		list="#{-1:'所有', 0:'屏蔽', 1:'正常'}" listKey="key" listValue="value"></s:select>
	</label>
	<label> <s:textfield key="words" cssClass="text" /> </label>
	<label> <input id="list" type="button"
		class="ui-button ui-state-default ui-corner-all" value="查询" /> </label>&nbsp;&nbsp;&nbsp;&nbsp;
	<label> <input id="createNew" type="button"
		class="ui-button ui-state-default ui-corner-all" value="生成新的目录" /> </label>
</s:form>
	<label> <s:textfield id="num" name="num" cssClass="text" value="200"/> </label>
	<label> <input id="list" type="button" onclick="exportXls()" 
		class="ui-button ui-state-default ui-corner-all" value="导出搜索排行" /> </label>
	
	<label> <s:textfield id="top" name="top" cssClass="text" value="50"/> </label>
	<label> <input id="list" type="button" onclick="compareKeyword()" 
		class="ui-button ui-state-default ui-corner-all" value="关键词比较" /> </label>
</div>

<input type="hidden" id="operateId" />
<input type="hidden" id="operateSubjectId" />
<div class="table"><img src="soku/manage/img/bg-th-left.gif" width="8"
	height="7" alt="" class="left" /> <img src="soku/manage/img/bg-th-right.gif"
	width="7" height="7" alt="" class="right" />
	<table class="listing" cellpadding="0" cellspacing="0">
		
		<tr>
		<td align="center" width="20%">搜索词</td>
		<td>
		<table width="100%" cellpadding="0" cellspacing="0" width="100%">
		<tr>
				<td width="10%" align="center">
				类型
				</td>
				<td width="10%" align="center">
				搜索量(或播放量)
				</td>
				<!-- 
				<td width="30%" align="center">
				图片
				</td>
				-->
				<td width="30%" align="center">
				对应节目(或系列)
				</td>
				<td width="20%" align="center">
				状态
				</td>
		</tr>
		</table>
		</td>
		</tr>
		
		<s:iterator value="pageInfo.results" status="index">
			<tr <s:if test="#index.odd">class="bg"</s:if>>
				<td align="center" width="20%">
				<s:property value="keyword" />
				</td>
				<td align="center" width="80%">
				<table width="100%" cellpadding="0" cellspacing="0" width="100%">
				<s:iterator value="tops" >
				<tr>
				<td width="10%" align="center" id="tdchannel<s:property value="id" />" ondblclick="changechannelname('<s:property value="id" />');">
					<div id="divchannel<s:property value="id" />" style="display:none">
							<select id="channelValue<s:property value="id" />">
								<s:iterator value="updatecatevos" >
									<s:if test="cate==catevalue">selected="selected"</s:if>
									<option <s:if test="cate==catevalue">selected="selected"</s:if> value="<s:property value="catevalue"/>"><s:property value="catename"/></option>
								</s:iterator>
							</select>
								<input type="button" value="更新" onclick="submitchangechannelname('<s:property value="id" />');"/>
								<input type="button" value="取消" onclick="cancelchangechannelname('<s:property value="id" />');"/>
							</div>
							<div id="channelName<s:property value="id" />"><s:property value="cname" /></div>
				</td>
				<td align="center" width="10%">
					<s:property value="queryCount" />
				</td>
				<!-- 
				<td width="30%" align="center" id="pic<s:property value="id" />" ondblclick="changepic('<s:property value="id" />');"><img src='<s:property value="pic" />' /></td>
				 -->
				<td width="30%" align="center" id="pname<s:property value="id" />" ondblclick="changepname('<s:property value="id" />','<s:property value="programmeName" />');">
					<s:if test="isMulu==1"><Font color="GREEN"/><s:property value="programmeName" /></s:if>
					<s:else><s:property value="programmeName" /></s:else>
				</td>
				<td width="20%" align="center" ondblclick="editVisible('<s:property value="id" />')" >
				<div id="visibleRadio<s:property value="id" />"
					style="display: none"> <input type="radio"
					name="new_visible<s:property value="id" />" value="0"
					id="new_visible<s:property value="id" />"
					<s:if test="visible==0">checked="checked"</s:if>>屏蔽</input> <input
					type="radio" name="new_visible<s:property value="id" />" value="1"
					id="new_visible<s:property value="id" />"
					<s:if test="visible==1">checked="checked"</s:if>>正常</input> <input
					type="button" value="确定"
					onclick="updateVisible(<s:property value="id" />);" /> <input
					name="cancelUpdateVisible" type="button" value="取消" /> </div> 
					<div
					id="visibleShow<s:property value="id" />" class="a">
				<s:if test="visible==0">
					屏蔽
				</s:if> <s:else>
					正常
				</s:else>
				</div>
				</td>
				</tr>
				</s:iterator>
				</table>
				
				</td>
			</tr>
		</s:iterator>

		<tfoot>
			<tr>
				<td colspan="4"><s:text name="totalpagenumber" />: <s:property
					value="pageInfo.totalPageNumber" /> <s:text
					name="currentpagenumber" />: <s:property
					value="pageInfo.currentPageNumber" /> <s:if
					test="pageInfo.hasPrevPage">
					<a
						href="<s:url action="TopWords_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="topdate" value="topdate" />
								<s:param name="cate" value="cate" />
								<s:param name="visible" value="visible" />
								<s:param name="words" value="words" />
							</s:url>"><s:text
						name="prevpage" /></a>
				</s:if> <s:if test="pageInfo.hasNextPage">
					<a
						href="<s:url action="TopWords_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="topdate" value="topdate" />
								<s:param name="cate" value="cate" />
								<s:param name="visible" value="visible" />
								<s:param name="words" value="words" />
							</s:url>"><s:text
						name="nextpage" /></a>
				</s:if></td>
			</tr>
		</tfoot>
	</table>
<div class="select"><strong>Other Pages: </strong> <input
	type="hidden" id="pageurl"
	value="<s:url action="TopWords_list">
								<s:param name="topdate" value="topdate" />
								<s:param name="cate" value="cate" />
								<s:param name="visible" value="visible" />
								<s:param name="words" value="words" />
								<s:param name="pageNumber" value="0" />
							</s:url>" />
<select id="pageselect">
	<s:bean name="org.apache.struts2.util.Counter" id="counter">
		<s:param name="first" value="1" />
		<s:param name="last" value="10" />
		<s:iterator status="count">
			<s:if
				test="(pageInfo.currentPageNumber - #count.index) >0">
				<option><s:property
					value="pageInfo.currentPageNumber - #count.index " /></option>
			</s:if>
		</s:iterator>
	</s:bean>
	<s:bean name="org.apache.struts2.util.Counter" id="counter">
		<s:param name="first" value="1" />
		<s:param name="last" value="10" />
		<s:iterator status="count">
			<s:if
				test="(pageInfo.currentPageNumber + #count.index) <= pageInfo.totalPageNumber">
				<option><s:property
					value="#count.index + pageInfo.currentPageNumber" /></option>
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
	图片路径： <input type="text" id="picPath" name="imageUrl" /><br/>
	上传图片：<s:file name="upload" />
</s:form></div>

<div id="dialogNames" title="Search the ProjectName"><input type="text"
	name="searchWord" id="diaSearchWord" /><input type="button"
	value="search" onclick="searchNames()" />
<div id="diaResult"></div>
</div>

</div>
</body>
</html>
