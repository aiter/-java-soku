<%@ taglib uri="/struts-tags" prefix="s" %>

<div id="left-column">
      <h3><s:text name="shield.title" /></h3>
      <ul class="nav">      
        <s:if test="#session.user_permission.manage_shielding_system_keyword != null">
      		<li><a href="<s:url action="ShieldWord_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.shieldword.manage" /></a></li>
      	</s:if>
      	
      	
      	<s:if test="#session.user_permission.manage_shielding_system_keyword != null">
      		<li><a href="<s:url action="ShieldCategory_list"><s:param name="pageNumber" value="1" /><s:param name="type" value="2" /></s:url>"><s:text name="soku.shieldcategory.manage" /></a></li>
      	</s:if>
      	<s:if test="#session.user_permission.manage_shielding_system_site != null">
      		<li><a href="<s:url action="ShieldSite_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.shieldsite.manage" /></a></li>
      	</s:if>
      	      		<li><a href="<s:url action="User_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.user.manage" /></a></li>
      
      	<!--  
      	<s:if test="#session.user_permission.manage_shielding_system_keyword != null">
      		<li><a href="<s:url action="ShieldWord_list"><s:param name="pageNumber" value="1" /><s:param name="searchParameter.type" value="2" /></s:url>"><s:text name="soku.determinerword.manage" /></a></li>
      	</s:if>
      	<s:if test="#session.user_permission.manage_shielding_system_keyword != null">
      		<li><a href="<s:url action="ShieldChannel_list"><s:param name="pageNumber" value="1" /><s:param name="type" value="2" /></s:url>"><s:text name="soku.shieldchannel.manage" /></a></li>
      	</s:if>
      	<s:if test="#session.user_permission.manage_shielding_system_mail != null">
      		<li><a href="<s:url action="ShieldWord_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.shieldmail.manage" /></a></li>
      	</s:if>
      	<s:if test="#session.user_permission.manage_shielding_system != null">
      		<li><a href="<s:url action="ShieldWord_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.user.manage" /></a></li>
      	</s:if>-->
      	
  </ul>     
      
    
      <a href="<s:url action="Logout"></s:url>" class="link"><s:text name="soku.logout" /></a>
</div>