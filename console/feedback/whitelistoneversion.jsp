<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script language="javascript" type="text/javascript">
		
		function CheckAll(form)
		  {
		  	for (var i=0;i<form.elements.length;i++)
		    {
		   		 var e = form.elements[i];   
		       	 e.checked = form.chkall.checked;
		    }
		  }
		 function check(form)
		  {
		  	for (var i=0;i<form.elements.length;i++)
		    {
		   		 var e = form.elements[i];
		   		 if(e.checked){
		       	 	form.chkall.checked=false;
		       	 break;
		       	 }
		    }
		  }
			function submitInfo(form)
			{	
			 var isSelect = false;
				for (var i=0;i<form.elements.length;i++)
			    {
			    	if( form.elements[i].type=="checkbox" && form.elements[i].checked == true)  
					isSelect = true;
			    }
				if(isSelect==false){
					alert ("请选择一项后在操作！！");
				    return;
				}
				else{
					if(confirm("确认删除选定的剧集反馈白名单信息？")){
					form.action="whitedeletebyid.html";
					form.submit();
					}
				}
			}
			
			function update(id,endTime,k)
			{
				var reg="^((((1[6-9]|[2-9]\d)\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\d|3[01]))|(((1[6-9]|[2-9]\d)\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\d|30))|(((1[6-9]|[2-9]\d)\d{2})-0?2-(0?[1-9]|1\d|2[0-8]))|(((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$";
				if(!checkDate(reg,endTime)){
					alert("日期格式不对");return false;
				}else{
					if(compareDate(endTime)){
						realupdate(id,endTime,k);
					}else{
						if(confirm("您输入的日期早于或等于当前时间,确认继续？")){
							realupdate(id,endTime,k);
						}else return false;
					}
				}
			}
			
			function realupdate(id,endTime,k){
				document.getElementById("update"+k).disabled=true;
				var cate=document.getElementById("cate"+k).value;
				var xmlReq =initXMLHttpRequest();
				if(!xmlReq){
					document.getElementById("msg"+k).innerHTML="修改失败!";
					document.getElementById("msg"+k).style.color="red";
					document.getElementById("update"+k).disabled=false;
				}
				xmlReq.onreadystatechange = function(){
					if(xmlReq.readyState == 4){
						if (200==xmlReq.status){
							var da=xmlReq.responseText;
							if(da==1){
								document.getElementById("msg"+k).innerHTML="修改成功!";
								document.getElementById("msg"+k).style.color="green";
								document.getElementById("update"+k).disabled=false;
							}else{
							document.getElementById("msg"+k).innerHTML="修改失败!";
							document.getElementById("msg"+k).style.color="red";
							document.getElementById("update"+k).disabled=false;
							}
						}
					}
				}
				xmlReq.open("post", "whitelistupdatebyid.html", true);
				xmlReq.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
				xmlReq.send("wid="+id+"&end_time="+endTime+"&cate="+cate);
			}
			
			function initXMLHttpRequest(){
				if(window.XMLHttpRequest){
					return new XMLHttpRequest();
				} else if(window.ActiveXObject) {
					return new ActiveXObject('Microsoft.XMLHTTP');
				}
			}
		
			function checkDate(reg, str){
				if(reg.test(str)){
					return true;
				}
				return false;
			}
			
			function compareDate(str)
			{
		        var dayObj=new Date();
		        str = str.replace("-","/");
		        str = str.replace("-","/");
		        var day2=new Date(str);
		        if(Date.parse(day2) - dayObj<=0)
		    	{
		      		return false;
		    	}else{
		      		return true;
   			 	}
			}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="whitelistbyid.html">
	<s:property value="message"/>
	<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
	<tr bgcolor="#A4D6FF">
	<td colspan=10>剧集反馈白名单列表</td>
	</tr>
	<tr align="center" bgcolor="#A4D6FF">
	<td></td>
	<td colspan=4>电视剧</td>
	<td>版本</td>
	<td>分类</td>
	<td colspan=2>最快到期(yyyy-MM-dd)</td>
	<td></td>
	</tr>
	<%int k=0; %>
	<s:iterator value="wl">
	<tr align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td>
		<input type="checkbox" name="ids"
			value='<s:property value="id"/>' onclick="check(this.form)">
	</td>
	<td colspan=4 align="left">
	<s:property value="tName"/>
	</td>
	<td align="left"><s:property value="pName"/></td>
	<td align="left"><s:select theme="simple" id="cate<%=k %>" name="cate" list="#{'0':'热播剧','1':'站内无合适资源'}"></s:select></td>
	<td colspan=2 align="center"><s:property value="endTime"/></td>
	<td align="center"><input id="update<%=k %>" type="button" value="修改" onclick="update(<s:property value="id"/>,<s:property value="endTime"/>,<%=k %>)"/><div id="msg<%=k %>"></div></td>
	</tr>
	<%k=k+1; %>
	</s:iterator>
	<tr bgcolor="#A4D6FF">
	<td colspan=5><s:hidden name="maxpage"/>第<s:property value="page"/>页，总共<s:property value="maxpage"/>页,共<s:property value="totalSize"/>个剧集反馈白名单</td>
	<td><s:if test="page>1"><a href='whitelistbyid.html?page=1&fk_version_id=<s:property value="versionid"/>'>首页</a></s:if></td>
	<td><s:if test="page>1"><a href='whitelistbyid.html?page=<s:property value="page-1"/>&fk_version_id=<s:property value="versionid"/>'>上一页</a></s:if></td>
	<td><s:if test="page!=maxpage&&maxpage>0"><a href='whitelistbyid.html?page=<s:property value="page+1"/>&fk_version_id=<s:property value="versionid"/>'>下一页</a></s:if></td>
	<td><s:if test="page!=maxpage&&maxpage>0"><a href='whitelistbyid.html?page=<s:property value="maxpage"/>&fk_version_id=<s:property value="versionid"/>'>尾页</a></s:if></td>
	<td>页码:<select id="cuurpage" name="cuurpage" onchange="location.href='whitelistbyid.html?fk_version_id=<s:property value="versionid"/>&page=' + this.value;">
			<%
			int startpage=1;
			int maxpage=1;
			String check="";
			try{
			startpage=Integer.parseInt(""+request.getAttribute("cuurpage"));
			maxpage=Integer.parseInt(""+request.getAttribute("cuurmaxpage"));
			}catch(Exception e){startpage=1;}
			for(int i=1;i<=maxpage;i++){
			if(i==startpage)check="selected";
			else check="";
			%>
			<option value="<%=i %>" <%=check %>>第<%=i %>页</option>
			<% }%>
		</select>
	</td>
	</tr>
	<s:if test="wl.size!=0">
	<tr bgcolor="#A4D6FF">
		<td colspan="10">
			<input type="checkbox" name="chkall" onclick="CheckAll(this.form)">
					全部选中
		</td>
	</tr>
	<tr bgcolor="#A4D6FF">
		<td colspan="10">
			<input type="button" value="删  除" onclick="submitInfo(this.form)" />
		</td>
	</tr>
	</s:if>
	</table>
	</s:form>
	</body>
</html>