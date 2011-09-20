<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="soku反馈统计" /></title>  
<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
	<link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
    <link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
    <script type="text/javascript" src="<s:url value="/soku/manage/js/ui.datepicker.js"/>"></script>
    <script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="/soku/manage/sokufeedback/js/highcharts.js"></script>
<style type="text/css">
.select-bar select {
				width: 50px;
				margin: 0 2px;
}
.ba {
	background:#DDDD00
}
</style>

<script type="text/javascript">
			var chart;
			$(document).ready(function() {
			
				chart = new Highcharts.Chart({
					chart: {
						renderTo: 'container',
						defaultSeriesType: 'spline'
					},
					title: {
						text: 'feedback （<s:property value="startTime" />  -  <s:property value="endTime" />）'
					},
					subtitle: {
						text: '站外反馈 : <s:property value="sokutotal" /> , 站外喜欢 : <s:property value="sokugoodtotal" /> , 站外不喜欢 : <s:property value="sokubadtotal" /> , 站内反馈 : <s:property value="youkutotal" /> , 站内喜欢 : <s:property value="youkugoodtotal" /> , 站内不喜欢 : <s:property value="youkubadtotal" />'
					},
					xAxis: {
						categories: <s:property value="feeddate_str" />,
						labels: {
							rotation: -45,
							align: 'right',
							style: {
								 font: 'normal 13px Verdana, sans-serif'
							}
						}
					},
					yAxis: {
						title: {
							text: 'feedback times'
						}
					},
					tooltip: {
						formatter: function() {
						
						if(this.series.name != '站外不喜欢/站外所有反馈'&&this.series.name != '站内不喜欢/站内所有反馈'){
							return  this.series.name +'<br/>'+
								this.x+'<br/>' + this.y + ' times' +'<br/>'
						}else{
						return this.series.name +'<br/>'+
								this.x+'<br/>' + this.y;
								}
						}
					},
					plotOptions: {
						spline: {
							dataLabels: {
								enabled: true
							},
							enableMouseTracking: true
						}
					},
					series: [{
						name: '站外所有反馈',
						data: <s:property value="sokuall_str" />
				
					},{
						name: '站外喜欢',
						data: <s:property value="sokugood_str" />
				
					},{
						name: '站外不喜欢',
						data: <s:property value="sokubad_str" />
				
					},{
						name: '站外不喜欢/站外所有反馈',
						data: <s:property value="sokubadrate_str" />
				
					},{
						name: '站内所有反馈',
						data: <s:property value="youkuall_str" />
				
					},{
						name: '站内喜欢',
						data: <s:property value="youkugood_str" />
				
					},{
						name: '站内不喜欢',
						data: <s:property value="youkubad_str" />
				
					},{
						name: '站内不喜欢/站内所有反馈',
						data: <s:property value="youkubadrate_str" />
				
					}],
					navigation: {
						menuItemStyle: {
							fontSize: '10px'
						}
					}
				});
			
			});
			
			$(function() {
				$("#startTime").datepicker({dateFormat: 'yy-mm-dd'});
				$("#endTime").datepicker({dateFormat: 'yy-mm-dd'});
				
				$("#list").click(function () {
				$("#sokufeedbackForm").attr("action","SokuFeedbackUnion_list.do");
				$("#sokufeedbackForm").submit();
					});
				
				$("#hourlist").click(function () {
				$("#sokufeedbackForm").attr("action","SokuFeedbackUnion_hourlist.do");
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
<h1><s:text name="soku反馈统计" /></h1>
</div>

<div class="table" id="container" style="width: 1000px; height: 400px; margin: 0 auto"></div>

<br />
<div class="select-bar"><s:form id="sokufeedbackForm"
	action="SokuFeedbackUnion_list" validate="false" cssClass="form"
	theme="simple">
	<label> 开始时间:<s:textfield id="startTime" name="startTime" /> </label>
	<label> <s:select key="starthour"
		list="#{23:'23',22:'22', 21:'21', 20:'20', 19:'19', 18:'18', 17:'17', 16:'16', 15:'15', 14:'14', 13:'13', 12:'12', 11:'11', 10:'10', 9:'9', 8:'8', 7:'7', 6:'6', 5:'5', 4:'4', 3:'3', 2:'2', 1:'1', 0:'0'}" listKey="key" listValue="value"></s:select>
	</label>
	<label> 结束时间:<s:textfield id="endTime" name="endTime" /> </label>
	<label> <s:select key="endhour"
		list="#{23:'23',22:'22', 21:'21', 20:'20', 19:'19', 18:'18', 17:'17', 16:'16', 15:'15', 14:'14', 13:'13', 12:'12', 11:'11', 10:'10', 9:'9', 8:'8', 7:'7', 6:'6', 5:'5', 4:'4', 3:'3', 2:'2', 1:'1', 0:'0'}" listKey="key" listValue="value"></s:select>
	</label>
	<label> <input id="list" type="button"
		class="ui-button ui-state-default ui-corner-all" value="按天查询" /> </label>
	<label> <input id="hourlist" type="button"
		class="ui-button ui-state-default ui-corner-all" value="按小时查询" /> </label>
	
</s:form></div>
<div class="table"><img src="soku/manage/img/bg-th-left.gif" width="8"
	height="7" alt="" class="left" /> <img src="soku/manage/img/bg-th-right.gif"
	width="7" height="7" alt="" class="right" />
	<table class="listing" cellpadding="0" cellspacing="0">
		
		<tr>
		<th width="20%" align="center">
				时间
		</th>
		<th width="10%" align="center">
				来源
		</th>
		<th width="10%" align="center">
				状态
		</th>
		<th width="40%" align="center">
				关键词
		</th>
		<th width="20%" align="center">
				次数
		</th>
		</tr>
		
		<s:iterator value="sfs" status="index">
			<tr <s:if test="source==0">class="bg"</s:if>>
			<td align="center" width="20%">
				<s:property value="feeduniondate"/>
			</td>
			<td align="center" width="10%">
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
				<td align="center" width="40%" >
				<s:property value="keyword" />
				</td>
				<td align="center" width="20%" >
				<s:property value="count" />
				</td>
			</tr>
		</s:iterator>
	</table>
</div>
</div>


<div id="footer"></div>
</div>
</body>
</html>
