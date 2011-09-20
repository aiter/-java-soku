<%@ taglib uri="/struts-tags" prefix="s" %>

<div id="left-column">
 <h3><s:text name="soku.title" /></h3>
 <ul class="nav">
<!--
 <s:if test="#session.user_permission.manage_feedback != null">
 <li><a href="<s:url action="Feedback_list"></s:url>"><s:text name="soku.feedback.manage" /></a></li>
 </s:if>
 -->
 <s:if test="#session.user_permission.manage_correction != null">
 <li><a href="<s:url action="Correction_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.correction.title" /></a></li>
 </s:if>
 <li><a href="<s:url action="ProtocolSite_list"></s:url>"><s:text name="soku.protocol.manage" /></a></li>
 <li><a href="<s:url action="SokuFeedBack_list"></s:url>"><s:text name="soku.newfeedback.manage" /></a></li>
 </ul>
      
      <h3><s:text name="sokulib.title" /></h3>
      <ul class="nav">     
      <!--
      	<li><a href="<s:url action="Category_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.category.manage" /></a></li>
      		<li><a href="<s:url action="Names_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.names.manage" /></a></li>
      		<li><a href="<s:url action="TeleplayVersion_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.teleplayversion.manage" /></a></li>
      		<li><a href="<s:url action="TeleplaySiteVersion_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.teleplaysiteversion.manage" /></a></li>
      		<li><a href="<s:url action="Movie_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.movie.manage" /></a></li>
      		<li><a href="<s:url action="AnimeVersion_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.animeversion.manage" /></a></li>
      		<li><a href="<s:url action="AnimeSiteVersion_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.animesiteversion.manage" /></a></li>
      		<li><a href="<s:url action="Variety_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.variety.manage" /></a></li>
      		<li><a href="<s:url action="VarietySub_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.varietySub.manage" /></a></li>
      		
      		
      --> 
      
      		<li><a href="<s:url action="VideoInfo_list"><s:param name="pageNumber" value="1" /><s:param name="latestUpdate" value="1" /></s:url>"><s:text name="soku.videoinfo.manage" /></a></li>
      		<li><a href="<s:url action="AuditEpisodeLog_list"><s:param name="pageNumber" value="1" /><s:param name="concernLevel" value="1" /></s:url>"><s:text name="soku.concern.audit.manage" /></a></li>
		<li><a href="<s:url action="AuditEpisodeLog_list"><s:param name="pageNumber" value="1" /><s:param name="concernLevel" value="0" /></s:url>"><s:text name="soku.audit.manage" /></a></li>
     		<li><a href="<s:url action="EpisodeLog_list"></s:url>"><s:text name="soku.log.manage" /></a></li>
     		<li><a href="<s:url action="KnowledgeColumn_list"></s:url>"><s:text name="soku.knowledge" /></a></li>
     		<li><a href="<s:url action="ProgrammeForwardWord_list"></s:url>"><s:text name="soku.forwardword" /></a></li>
     		<li><a href="<s:url action="NewProgramme_list"></s:url>"><s:text name="soku.newprogramme" /></a></li>
     		<li><a href="<s:url action="SokuIndexPic_list"></s:url>"><s:text name="soku.index.pic.manage" /></a></li>
     		<li><a href="<s:url action="DeadLinkReport_list"></s:url>"><s:text name="soku.deadlink.report" /></a></li>
     		<li><a href="<s:url action="Programme_plain"></s:url>"><s:text name="soku.programme.plain" /></a></li>
      </ul>  

      <h3><s:text name="sokutop.title" /></h3>
      <ul class="nav">
     <!-- 
      <li><a href="<s:url action="TopWord_list"><s:param name="pageNumber" value="1" /><s:param name="channelName">topconcern</s:param></s:url>"><s:text name="soku.topword.manage" /></a></li>
      <li><a href="<s:url action="Directory_list"><s:param name="pageNumber" value="1" /><s:param name="channelName">1</s:param></s:url>"><s:text name="soku.directory.title" /></a></li>
      <li><a href="/DirectoryPerson_list.do?channelName=10&startDate=2010-10-07"><s:text name="soku.person.manage" /></a></li>
      <li><a href="<s:url action="TypeWord_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.typeword.manage" /></a></li>
      <li><a href="<s:url action="TopDate_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.topdate.manage" /></a></li>
      <li><a href="<s:url action="ChannelNavigation_list"><s:param name="pageNumber" value="1" /></s:url>"><s:text name="soku.navigation.manage" /></a></li>
      -->
      <li><a href="<s:url action="TopWords_list"><s:param name="pageNumber" value="1" /><s:param name="cate" value="0" /></s:url>"><s:text name="soku.topwords.searchkeys" /></a></li>
      </ul>
      
      <a href="<s:url action="User_changepwd"><s:param name="userId" value="curUserId" /></s:url>" class="link"><s:text name="soku.changePwd" /></a>
      <a href="<s:url action="Logout"></s:url>" class="link"><s:text name="soku.logout" /></a>
</div>
