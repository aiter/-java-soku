<%@ page contentType="text/html;charset=utf-8" language="java"
	import="java.util.Map.Entry,com.youku.search.console.operate.log.*,com.youku.search.util.*,com.youku.search.console.vo.*,java.util.*"%>

<%@page import="com.youku.search.console.util.KeywordUtil"%><html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<title>搜索TOP500关键词统计</title>
		<style type="text/css">
<!--
.STYLE1 {
	color: #CC3399
}

.STYLE2 {
	color: #0000FF
}
-->
</style>
		<script language="javascript" type="text/javascript">
	function show(k){
	var d = document.getElementById(k);
	if(null==d)return;
	var s = d.style.display;
	if(s=='')
		s='none';
	else s='';
		 d.style.display=s;
	}
</script>

		<%
			String uniondate = request.getParameter("uniondate");
			String week = request.getParameter("week");
			List<KeywordQueryVO> keywordList = null;
			if("1".equalsIgnoreCase(week)){
				KeywordWeekPropare kp = new KeywordWeekPropare();
				keywordList = kp.getTopKeywords(uniondate);
			}else{
				if(null==uniondate||uniondate.trim().length()<1)
					uniondate = DataFormat.formatDate(DataFormat.getNextDate(
						new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);
				KeywordPropare kp = new KeywordPropare();
				keywordList = kp.getTopKeywords(uniondate);
			}
			int len = keywordList.size();
			int i=0;
		%>
	</head>

	<body>
		<p>
			b.站内结果(估):站内加权平均结果,（词的搜索量*结果数）/组下所有词搜索量之和
			<br />
			e.搜索比：站外搜索数/站内搜索数,即 c/a
			<br />
			i.点击率：点击数/站外搜索数,即 h/c
		</p>
		<p align="center" class="STYLE2">
			搜索TOP500关键词统计(<%=uniondate%>)
		</p>
		<table cellSpacing="1" cellPadding="3" width="95%" bgcolor="#999999"
			align="center">
			<tr align="left" bgcolor="#A4D6FF">
				<td colspan=13>
					排序字段:max(a,c)
				</td>
			</tr>
			<tr bgcolor="#A4D6FF">
				<td width="4%">
					序号
				</td>
				<td width="15%">
					关键词
				</td>
				<td width="8%">
					站内搜索数(a)
				</td>
				<td width="8%">
					站内结果(估)(b)
				</td>
				<td width="8%">
					站外搜索数(c)
				</td>
				<td width="7%">
					搜索比(e)
				</td>
				<td width="7%">
					点击数(h)
				</td>
				<td width="7%">
					点击率(i)
				</td>
			</tr>
			<%
				for (; i < len; i++) {
					KeywordQueryVO kqo = keywordList.get(i);
			%>
			<tr align="left" bgcolor="#FFFFFF"
				onmouseover="this.style.background='#FFCC00'"
				onmouseout="this.style.background='#F3F3F3'" onclick="show(<%=i%>)">
				<td>
					<%=i + 1%>
				</td>
				<td>
					<%=kqo.getKeyword()%>
				</td>
				<td bgcolor="#99CC99">
					<%=kqo.getInsearchs()%>
				</td>
				<td bgcolor="#FF9966">
					<%=kqo.getInavg()%>
				</td>
				<td bgcolor="#99CC99">
					<%=kqo.getOutsearchs()%>
				</td>
				<!-- 
				<td bgcolor="#FF9966">
					<%=kqo.getOutavg()%>
				</td>
				 -->
				<td bgcolor="#99FF66">
					<%=KeywordUtil.formatString(kqo.getOsrate())%>%
				</td>
				<!-- 
				<td>
					<%=KeywordUtil.formatString(kqo.getOsrate1())%>%
				</td>
				<td>
					<%=KeywordUtil.formatString(kqo.getOsrate2())%>%
				</td>
				 -->
				<td bgcolor="#FFCCCC">
					<%=kqo.getOutclicks()%>
				</td>
				<td bgcolor="#FFCCCC">
					<%=KeywordUtil.formatString(kqo.getOcrate())%>%
				</td>
				<!-- 
				<td>
					<%=KeywordUtil.formatString(kqo.getOcrate1())%>%
				</td>
				<td>
					<%=KeywordUtil.formatString(kqo.getOcrate2())%>%
				</td>
				 -->
			</tr>
			<%
				if (kqo.getKeywords().size() > 1) {
			%>
			<tr id="<%=i%>" style="display: none">
				<td colspan=13>
					<table width="100%" bgcolor="#66FF99" align="center">
						<tr>
							<td width="4%">

							</td>
							<td width="15%">
								组合
							</td>
							<td width="8%">
								站内搜索比(l)
							</td>
							<td width="8%">
								站内结果(估)(m)
							</td>
							<td width="8%">
								站外搜索比(n)
							</td>
							<td width="7%">
							</td>
							<td width="7%">

							</td>
							<td width="7%">
								跳转比例(r)
							</td>
						</tr>
						<%
							int j=0;
							String temp="a"+i+"_"+j;
							for (Entry<Integer, KeywordComVO> entry : kqo.getKeywords()
											.entrySet()) {
										KeywordComVO kqvo = entry.getValue();
										
						%>
						<tr  align="left" bgcolor="#FFFFFF"
				onmouseover="this.style.background='#FFCC00'"
				onmouseout="this.style.background='#F3F3F3'" onclick="show('<%=temp%>')">
							<td>

							</td>
							<td>
								<%=kqvo.getKeyword()%>
							</td>
							<td>
								<%=KeywordUtil.formatString(kqvo.getIsrate())%>%
							</td>
							<td>
								<%=kqvo.getInavg()%>
							</td>
							<td>
								<%=KeywordUtil.formatString(kqvo.getOsrate())%>%
							</td>
							<!-- 
							<td>
								<%=kqvo.getOutavg()%>
							</td>
							-->
							<td>
							</td>
							<!-- 
							<td>
								<%=KeywordUtil.formatString(kqvo.getOsrate1())%>%
							</td>
							<td>
								<%=KeywordUtil.formatString(kqvo.getOsrate2())%>%
							</td>
							-->
							<td>

							</td>
							<td>
								<%=KeywordUtil.formatString(kqvo.getOcrate())%>%
							</td>
							<!-- 
							<td>
								<%=KeywordUtil.formatString(kqvo.getOcrate1())%>%
							</td>
							<td>
								<%=KeywordUtil.formatString(kqvo.getOcrate2())%>%
							</td>
							-->
						</tr>
						<%
						if(kqvo.getKeywords().size()>0){
						%>
						<tr id="<%=temp%>" style="display: none">
						<td colspan=13>
						<table width="100%" bgcolor="#66FF99" align="center">
						<%
							for(KeywordComVO kcvo:kqvo.getKeywords()){
						%>
							<tr>
							<td width="4%">

							</td>
							<td width="15%">
								<%=kcvo.getKeyword()%>
							</td>
							<td width="8%">
								<%=KeywordUtil.formatString(kcvo.getIsrate())%>%
							</td>
							<td  width="8%">
								<%=kcvo.getInresults()%>
							</td>
							<td width="8%">
								<%=KeywordUtil.formatString(kcvo.getOsrate())%>%
							</td>
							<!-- 
							<td  width="8%">
								<%=kcvo.getOutresults()%>
							</td>
							-->
							<td width="7%">
							</td>
							<!-- 
							<td width="7%">
								<%=KeywordUtil.formatString(kcvo.getOsrate1())%>%
							</td>
							<td width="7%">
								<%=KeywordUtil.formatString(kcvo.getOsrate2())%>%
							</td>
							-->
							<td width="7%">
								<%=kcvo.getOutclicks()%>
							</td>
							<td width="7%">
								<%=KeywordUtil.formatString(kcvo.getOcrate())%>%
							</td>
							<!-- 
							<td width="7%">
								<%=KeywordUtil.formatString(kcvo.getOcrate1())%>%
							</td>
							<td width="7%">
								<%=KeywordUtil.formatString(kcvo.getOcrate2())%>%
							</td>
							-->
						</tr>	
						<%
							}
						%>
						</table>
					</td>
				</tr>
						<%
						}
							j=j+1;
							temp="a"+i+"_"+j;
							}
						%>
					</table>
				</td>
			</tr>
			<%
				}
				}
			%>
		</table>
	</body>
</html>
