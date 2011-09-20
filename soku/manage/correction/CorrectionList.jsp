<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="soku.correction.title.list" /></title>


<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />

<script type="text/javascript"
	src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>

<script type="text/javascript">
var editable = false;
var editobj;
var newhtmltail;
function updateContent(id, keyword,status,type) {
	var updateurl="Correction_update.do";
	$.ajax({
	    url: updateurl,
	    type: "POST",
	    data: ({"keyword" : keyword, "status": status, "updateid" : id}),
	    success: function(msg) {
			if(type == "keyword") {
				editobj.html(keyword+ newhtmltail);	
			} else if(type == "status"){
				$('#statusRadio' + id).hide();
				status_text = "无效";
				if(1==status) 
					status_text = "有效";
				$('#statusShow' + id).html(status_text);
				$('#statusShow' + id).show();
			}
    	}
	});
}

function updateKeyword(id){	
    editable = false;
    newkeyword = $("#new_keyword").val();
   
    updateContent(id, newkeyword, -1, "keyword");
}

function updateStatus(id){
	editable = true;
    newStatus = document.getElementsByName('new_status'+id);
    i=0;
    for(;i<newStatus.length;i++){if(newStatus[i].checked){
			break;
    }};
	if(i==newStatus.length){alert("没有选择状态!")}
	else{updateContent(id, "", newStatus[i].value,"status");}

	editable = false;
}

function editStatus(id){
    editable = false;
    $('#statusRadio' + id).show();
	$('#statusShow' + id).hide();
}

function cancelUpdateKeyword(keyword){
    
    editable = false;
    editobj.html(keyword + newhtmltail);
}
		$(function() {
			$("#pageselect").change(function() {
				location.href = $("#pageurl").val() + $("#pageselect").val();
			});

			$("a[name='deleteLink']").click(function() {
				return confirm("确认删除？");
			});

			$("#thcheck").click(function() {
				if($(this).attr("checked")) {
					$("[name=batchids]").each(function(){
						$(this).attr("checked", true);
					});
				} else {
					$("[name=batchids]").each(function() {
						$(this).attr("checked", false);
					});
				}
			});

		 $("#batchdelete").click(function() {
			 
			 haschecked = false;
			 $("[name=batchids]").each(function(){
					if($(this).attr("checked"))
						haschecked = true;
				});
			 if(haschecked) {
				if(!confirm("确认删除？"))
					 return false;
				$("#CorrectBatchdeleteForm").attr("action","Correction_batchdelete.do");
				$("#CorrectBatchdeleteForm").submit();
			 } else {
				 alert("没有数据要删除");
				 return false;
			 }
			 return false;
		 });
			$("#list").click(function () {
				$("#correctionForm").attr("action","Correction_list.do");
				$("#correctionForm").submit();
			});

			$('.editKeyword').dblclick(function(event) {
				if(editable) return;
				editobj = $(this);
				html = $(this).html();
				html = html.toLowerCase();
				keywordidx = html.indexOf('<input');
				keyword = html.substr(0, keywordidx);
				agent = jQuery.browser;
				if(agent.msie){
				idstartidx = html.indexOf('value=');
				idendidx = html.indexOf(' ', idstartidx + 6);
				wordid = html.substring(idstartidx + 6, idendidx);
				}else{
					idstartidx = html.indexOf('value="');
					idendidx = html.indexOf('"', idstartidx + 7);
					wordid = html.substring(idstartidx + 7, idendidx);
				}
				newhtmltail = html.substring(keywordidx);
		        $(this).html('<textarea id="new_keyword" size="80%" cols="160" rows="10">'+keyword+'</textarea> <input type="button" value="确定" onclick="updateKeyword(' + wordid + ');"/> <input type="button" value="取消" onclick="cancelUpdateKeyword(\''+ keyword +'\');"/>');
		        editable = true;
		    });

			
			
			$("[name=cancelUpdateStatus]").click(function(event) {
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
		});
	
	</script>
</head>
<body>
<div id="main">
<div id="middle"><s:include
	value="/soku/manage/module/leftnav.jsp"></s:include>

<div id="center-column">
<div class="top-bar"><a
	href="<s:url action="Correction_input">
			<s:param name="correctionId" value="-1" />
		</s:url>"
	class="button"><s:text name="soku.correction.add" /></a>
	<a
	href="<s:url action="Correction_volumeAdd">
			<s:param name="correctionId" value="-1" />
		</s:url>"
	class="button"><s:text name="soku.correction.volumeAdd" /></a>
<h1><s:text name="soku.correction.title" /></h1>
</div>
<br />
<div class="select-bar"><s:form id="correctionForm"
	action="Correction_list" validate="false" cssClass="form"
	theme="simple">
	<s:if test="itemFilter!=null">
	</s:if>
	<label> <s:textfield key="searchWord" cssClass="text" /> </label>
	<label> <s:select key="status"
		list="#{-1:'所有', 0:'无效', 1:'有效'}" listKey="key" listValue="value"></s:select>
	</label>
	<label> <input id="list" type="button"
		class="ui-button ui-state-default ui-corner-all" value="查询" /> </label>
</s:form></div>
<div class="table"><img src="soku/manage/img/bg-th-left.gif" width="8"
	height="7" alt="" class="left" /> <img src="soku/manage/img/bg-th-right.gif"
	width="7" height="7" alt="" class="right" /> <s:form id="CorrectBatchdeleteForm"
	action="Correction_batchdelete" validate="false" cssClass="form" theme="simple">
	<table class="listing" cellpadding="0" cellspacing="0">
		<tr>
			<th align="center" width="5%"><input id="thcheck"
				type="checkbox" /></th>
			<th align="center" width="15%"><s:text
				name="soku.correction.correct_keyword" /></th>
			<th align="center" width="70%"><s:text
				name="soku.correction.keyword" /></th>
			<th align="center" width="10%"><s:text
				name="soku.correction.status" /></th>
		</tr>

		<s:iterator value="pageInfo.results" status="index">
			<tr <s:if test="#index.odd">class="bg"</s:if>>
				<td align="center"><input type="checkbox" name="batchids"
					value="<s:property value="id" />" /></td>
				<td align="center"><s:property
					value="correctKeyword" /></td>
				<td align="center" class="editKeyword"><s:property value="keyword" /><s:hidden name="id" /></td>
				<td align="center" onclick="editStatus(<s:property value="id" />)">
				<div id="statusRadio<s:property value="id" />"
					style="display: none"> <input type="radio"
					name="new_status<s:property value="id" />" value="0"
					id="new_status<s:property value="id" />"
					<s:if test="status==0">checked="checked"</s:if>>无效</input> <input
					type="radio" name="new_status<s:property value="id" />" value="1"
					id="new_status<s:property value="id" />"
					<s:if test="status==1">checked="checked"</s:if>>有效</input> <input
					type="button" value="确定"
					onclick="updateStatus(<s:property value="id" />);" /> <input
					name="cancelUpdateStatus" type="button" value="取消" /> </div> <div
					id="statusShow<s:property value="id" />" class="a"> <s:if
					test="status==0">
				无效
				</s:if> <s:else>
				有效
				</s:else> </div></td>
			</tr>
		</s:iterator>

		<tfoot>
			<tr>
				<td><input type="button" id="batchdelete" value="删除"/></td>
				<td colspan="4"><s:text name="totalpagenumber" />: <s:property
					value="pageInfo.totalPageNumber" /> <s:text
					name="currentpagenumber" />: <s:property
					value="pageInfo.currentPageNumber" /> <s:if
					test="pageInfo.hasPrevPage">
					<a
						href="<s:url action="Correction_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="status" value="status" />
								<s:param name="searchWord" value="searchWord" />
							</s:url>"><s:text
						name="prevpage" /></a>
				</s:if> <s:if test="pageInfo.hasNextPage">
					<a
						href="<s:url action="Correction_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="searchWord" value="searchWord" />
								<s:param name="status" value="status" />
							</s:url>"><s:text
						name="nextpage" /></a>
				</s:if></td>
			</tr>
		</tfoot>
	</table>
</s:form>
<div class="select"><strong>Other Pages: </strong> <input
	type="hidden" id="pageurl"
	value="<s:url action="Correction_list">
								<s:param name="searchWord" value="searchWord" />
								<s:param name="status" value="status" />
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
</div>
</body>
</html>
