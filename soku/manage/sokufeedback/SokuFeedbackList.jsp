<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="soku反馈" /></title>  
<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
   <link type="text/css" href="/soku/manage/sokufeedback/css/ui-lightness/jquery-ui-1.7.2.custom.css" rel="stylesheet" />
	<script type="text/javascript" src="/soku/manage/sokufeedback/js/jquery-1.3.2.min.js"></script>
	<script type="text/javascript" src="/soku/manage/sokufeedback/js/jquery-ui-1.7.2.custom.min.js"></script>
	<script type="text/javascript" src="/soku/manage/sokufeedback/js/timepicker.js"></script>
<style type="text/css">  
            * {  
                margin: 0;  
                padding: 0;  
            }  
              
            /*所有内容都在这个DIV内*/  
            .all {  
                width: 100%;  
                border: 1px solid #000000;  
            }  
              
            /*表头在这个DIV内*/  
            .title {  
                width: 1000px;  /*这个宽度需要与下面的内容的DIV相等*/  
            }  
              
            /*表格样式*/  
            table {  
                width: 100%;  /*撑满上面定义的500像素*/  
                border: 1px solid #FF00FF;  
                border-collapse: collapse;  /*边线与旁边的合并*/  
                table-layout:fixed;  
            }  
            /*表头单元格(这里将表头单元格的样式设置成了和单元格一样,实际中可以改变)*/  
            table tr th {  
                border: 1px solid #FF00FF;  
                /*overflow: hidden;  /*超出长度的内容不显示*/  
                word-wrap: break-word;  /*内容将在边界内换行,这里显示省略号,所以不需要了*/  
                word-break: break-all;  /*字内断开*/  
                /*text-overflow: ellipsis;  /*当对象内文本溢出时显示省略标记（…），省略标记插入的位置是最后一个字符*/  
                white-space: nowrap;  
            }  
            /*单元格样式*/  
            table tr td {  
                border: 1px solid #FF00FF;  
                /*overflow: hidden;*/
                word-wrap: break-word;  /*内容将在边界内换行,这里显示省略号,所以不需要了*/  
                word-break: break-all;  
                /*text-overflow: ellipsis;
                white-space: nowrap; */  
            }  
              
            /*容纳表格内容的DIV,这个DIV上放置滚动条*/  
            .content {  
                width: 100%;  
                height: 100px;  /*定一下高度,要不然就撑出来没滚动条了*/  
                overflow: scroll;  /*总是显示滚动条*/  
            }  
            /*真正存放内容的DIV*/  
            .content div {  
                width: 1000px;  /*与表头的DIV宽度相同*/  
            }
            .select-bar select {
				width: 50px;
				margin: 0 2px;
			}
</style>	

<script type="text/javascript">

$(function() {
	
	$('#startTime').datepicker({
    	duration: '',
        showTime: true,
        constrainInput: false,
        dateFormat: 'yy-mm-dd'
     });
	
	$('#endTime').datepicker({
    	duration: '',
        showTime: true,
        constrainInput: false,
        dateFormat: 'yy-mm-dd'
     });
	
	
	$("#pageselect").change(function() {
		location.href = $("#pageurl").val() + $("#pageselect").val();
	});
	$("#list").click(function () {
		$("#sokufeedbackForm").attr("action","SokuFeedBack_list.do");
		$("#sokufeedbackForm").submit();
	});
	
	$("#export").click(function () {
		$("#sokufeedbackForm").attr("action","SokuFeedBack_export.do");
		$("#sokufeedbackForm").submit();
	});
	
});
	
	</script>
</head>
<body>
<div id="main">
<div id="middle"><s:include
	value="/soku/manage/module/leftnav.jsp"></s:include>

<div id="center-column">
<div class="top-bar">
<h1><s:text name="soku反馈" /></h1>
</div>
<br />
<div class="select-bar"><s:form id="sokufeedbackForm"
	action="SokuFeedBack_list" validate="false" cssClass="form"
	theme="simple">
	<label> 开始时间:<s:textfield id="startTime" name="startTime" /> </label>
	<label> 结束时间:<s:textfield id="endTime" name="endTime" /> </label>
	<label> source:<s:select key="source"
		list="#{-1:'所有', 1:'站内', 0:'站外'}" listKey="key" listValue="value" cssClass="aw"></s:select>
	</label>
	<label> state:<s:select key="state"
		list="#{-1:'所有', 1:'喜欢', 0:'不喜欢'}" listKey="key" listValue="value" cssClass="aw"></s:select>
	</label>
	<label> keyword:<s:textfield key="keyword" cssClass="text" /> </label>
	<label> url:<s:textfield key="url" cssClass="text" /> </label>
	<label> <input id="list" type="button"
		class="ui-button ui-state-default ui-corner-all" value="查询" /> </label>
	<label> <input id="export" type="button"
		class="ui-button ui-state-default ui-corner-all" value="导出为xls文件" /> </label>
</s:form></div>

<div class="table"><img src="soku/manage/img/bg-th-left.gif" width="8"
	height="7" alt="" class="left" /> <img src="soku/manage/img/bg-th-right.gif"
	width="7" height="7" alt="" class="right" />
	<table class="listing" cellpadding="0" cellspacing="0">
		
		<tr>
		<th width="5%" align="center">
				来源
				</th>
				<th width="10%" align="center">
				状态
				</th>
				<th width="30%" align="center">
				url
				</th>
				<th width="20%" align="center">
				关键词
				</th>
				<th width="15%" align="center">
				messgae
				</th>
				<th width="10%" align="center">
				时间
				</th>
				<th width="10%" align="center">
				ip
				</th>
		</tr>
		
		<s:iterator value="pageInfo.results" status="index">
			<tr <s:if test="#index.odd">class="bg"</s:if>>
			<td align="center" width="5%">
				<s:if test="source==1">
					站内
				</s:if>
				<s:if test="source==0">
					站外
				</s:if>
				<s:if test="source!=0&&source!=1">
					未知
				</s:if>
				</td>
				<td align="center" width="10%">
				<s:if test="state==1">
					喜欢
				</s:if>
				<s:if test="state==0">
					不喜欢
				</s:if>
				<s:if test="state!=0&&state!=1">
					未知
				</s:if>
				</td>
				<td align="center" width="30%">
				<div style="overflow:hidden;word-break:break-all;text-align:center;word-warp:break-word;">
					<a target="_blank" href='<s:property value="url" />'><s:property value="url" /></a>
				</div>
				</td>
				<td align="center" width="20%" >
				<s:property value="keyword" />
				</td>
				<td align="center" width="15%">
				<div style="overflow:hidden;word-break:break-all;text-align:center;word-warp:break-word;">
				<s:property value="message"/>
				</div>
				</td>
				<td align="center" width="10%">
				<s:property value="%{getText('{0,date,yyyy-MM-dd HH:mm:ss}',{createTime})}"/>
				</td>
				<td align="center" width="10%" >
				<s:property value="ipHost" />
				</td>
			</tr>
		</s:iterator>

		<tfoot>
			<tr>
				<td colspan="7"><strong>Total: </strong><s:property
					value="pageInfo.totalRecords" /> <s:text name="totalpagenumber" />: <s:property
					value="pageInfo.totalPageNumber" /> <s:text
					name="currentpagenumber" />: <s:property
					value="pageInfo.currentPageNumber" /> <s:if
					test="pageInfo.hasPrevPage">
					<a
						href="<s:url action="SokuFeedBack_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="startTime" value="startTime" />
								<s:param name="endTime" value="endTime" />
								<s:param name="state" value="state" />
								<s:param name="keyword" value="keyword" />
								<s:param name="url" value="url" />
							</s:url>"><s:text
						name="prevpage" /></a>
				</s:if> <s:if test="pageInfo.hasNextPage">
					<a
						href="<s:url action="SokuFeedBack_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="startTime" value="startTime" />
								<s:param name="endTime" value="endTime" />
								<s:param name="state" value="state" />
								<s:param name="keyword" value="keyword" />
								<s:param name="url" value="url" />
							</s:url>"><s:text
						name="nextpage" /></a>
				</s:if></td>
			</tr>
		</tfoot>
	</table>
<div class="select"><strong>Other Pages: </strong> <input
	type="hidden" id="pageurl"
	value="<s:url action="SokuFeedBack_list">
								<s:param name="startTime" value="startTime" />
								<s:param name="endTime" value="endTime" />
								<s:param name="state" value="state" />
								<s:param name="keyword" value="keyword" />
								<s:param name="url" value="url" />
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
</div>
</body>
</html>
