<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
	</head>
	<body>
	<table cellspacing="1" cellpadding="3" width="100%" bgcolor="#999999"
			align="center" title="统计站外视频展示和点击次数">
      <tr align="center" bgcolor="#A4D6FF">
        <td align="center" rowspan="2" width="6%"> 日期 </td>
        <td colspan="2" align="center"> 查询次</td>
        <td colspan="2" align="center"> 显示次 </td>
        <td colspan="2" align="center"> 点击次 </td>
        <td align="center" colspan="20"  rowspan="2"> 跳转至<br>
		站点（总跳转数，无内容查询引起的跳转，无版权查询引起的跳转）
		</td>
      </tr>
      <tr align="center" bgcolor="#A4D6FF">
        <td align="center" width="5%">无结果 </td>
        <td align="center" width="5%">无版权</td>
        <td align="center">无结果</td>
        <td align="center">无版权</td>
        <td align="center">无结果</td>
        <td align="center">无版权</td>
      </tr>
      <s:iterator value="unionslist">
        <tr align="center" bgcolor="#FFFFFF"
					onMouseOver="this.style.background='#FFCC00' "
					onMouseOut="this.style.background='#F3F3F3'">
          <td rowspan="2" width="8%"><s:property value="uniondate"/>
          </td>
          <td colspan="2"><s:property value="search_nums" />
          </td>
          <td colspan="2"><s:property value="show_nums" />
          </td>
          <td colspan="2"><s:property value="click_nums" />
          </td>
		  <td align="left" width="56%" rowspan="2">
		   <s:iterator value="sites"  id="index">
		   		<s:property value="#index.key" /> (  <s:property value="#index.value.num1+#index.value.num2" /> , <s:property value="#index.value.num1" /> , <s:property value="#index.value.num2" />  )<br>
		   </s:iterator>
		  </td>
        </tr>
        <tr align="center" bgcolor="#FFFFFF"
					onMouseOver="this.style.background='#FFCC00' "
					onMouseOut="this.style.background='#F3F3F3'">
          <td width="6%"><s:property value="search_nums1" /></td>
          <td width="6%"><s:property value="search_nums2" /></td>
          <td width="6%"><s:property value="show_nums1" /></td>
          <td width="6%"><s:property value="show_nums2" /></td>
          <td width="6%"><s:property value="click_nums1" /></td>
          <td width="6%"><s:property value="click_nums2" /></td>
        </tr>
      </s:iterator>
    </table>
	</body>
</html>