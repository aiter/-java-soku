<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="deleteVideo.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="deleteVideo.title.edit"/></title>
    </s:if>
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
          type="text/css"/>
	<link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
	
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>


	
	<script type="text/javascript">
		$(function() {


		

			$("#urlSize").val(2);

			$('#addUrl').click(function() {
				addRow(1, 2, 3);
			}).hover(function() {
				$(this).addClass("ui-state-hover");
			}, function() {
				$(this).removeClass("ui-state-hover");
			}).mousedown(function() {
				$(this).addClass("ui-state-active");
			}).mouseup(function() {
				$(this).removeClass("ui-state-active");
			});

			$('#delete').click(function() {
				$('#DeleteVideo_delete').submit();
				//deleteURL();
			}).hover(function() {
				$(this).addClass("ui-state-hover");
			}, function() {
				$(this).removeClass("ui-state-hover");
			}).mousedown(function() {
				$(this).addClass("ui-state-active");
			}).mouseup(function() {
				$(this).removeClass("ui-state-active");
			});
		});
				
				
			var name = $("#name"),				
				allFields = $([]).add(name),
				tips = $("#validateTips");
	
			function updateTips(t) {
				tips.text(t);
			}
	
			function checkLength(o,n,min,max) {
	
				if ( o.val().length > max || o.val().length < min ) {
					o.addClass('ui-state-error');
					updateTips(n + " 的长度应该在 " + min + " 和 "+max+"之间.");
					return false;
				} else {
					return true;
				}
	
			}
	
			function checkRegexp(o,regexp,n) {
	
				if ( !( regexp.test( o.val() ) ) ) {
					o.addClass('ui-state-error');
					updateTips(n);
					return false;
				} else {
					return true;
				}
	
			}


		function addRow(personRoleId, personRoleName, personName) {
			var rowNumber = $("#urlSize").val();
			if(rowNumber == "") {
				rowNumber = 6;
			}

			var tdhtml = '<input type="text" class="text" value="" name="urlList">';
			$("table.form tr:last").after("<tr><td>URL " + rowNumber+ ": </td><td>" + tdhtml + "</td></tr>");

			rowNumber++;
			$("#urlSize").val(rowNumber);
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

		function chooseOne(cb) {
			var obj = document.getElementsByName("cboxperson");
			for( i= 0; i < obj.length; i++) {
				if(obj[i] != cb)
					obj[i].checked = false;
				else
					obj[i].checked = cb.checked;
			}
		}

		function deleteURL() {
			var searchWord = "";
			var obj = document.getElementsByName("urlList");
			for( i= 0; i < obj.length; i++) {
				searchWord += obj[i].value;
				if(i < (obj.length -1)) {
					//searchWord += "||";
				}
			}
			alert(searchWord);
			$.ajax({
				url: 'http://10.102.23.61/index/delete_byurl_server.jsp',
				type: 'POST',
				data: 'url=' + searchWord,
				success: function(data) {
					if(data == "[]") {
						alert("删除成功");
						return;
					} else {
						alert("删除失败");
					}
										
				
				    
				}
			});
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
        <h1>删除视频</h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="DeleteVideo_delete" validate="false" cssClass="form" >

    <s:token />
    <input type="hidden" id="urlSize" value="6" class="text"/>
    <s:if test='#status == "ok"'>删除成功</s:if>
    <s:if test='#status == "fail"'>删除失败</s:if>
    <tr>
    	<td>URL 1:</td>
		<td>
			<input type="text" name="urlList" class="text" />					
		</td>
	</tr>
	<tr>
    	<td>URL 2:</td>
		<td>
			<input type="text" name="urlList" class="text" />					
		</td>
	</tr>
	<tr>
    	<td>URL 3:</td>
		<td>
			<input type="text" name="urlList" class="text" />					
		</td>
	</tr>
	<tr>
    	<td>URL 4:</td>
		<td>
			<input type="text" name="urlList" class="text" />					
		</td>
	</tr>
	<tr>
    	<td>URL 5:</td>
		<td>
			<input type="text" name="urlList" class="text" />					
		</td>
	</tr>
	
	
</s:form>
        <input type="button" id="addUrl" class="ui-button ui-state-default ui-corner-all" value="添加URL" />
			<input type="button" id="delete" class="ui-button ui-state-default ui-corner-all" value="删除" />	
	
      </div>
    </div>
    
  </div>
  

  <div id="footer"></div>
</div>

</body>
</html>