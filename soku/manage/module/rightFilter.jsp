<%@ taglib uri="/struts-tags" prefix="s" %>
<div class="box">
    	以所属栏目
    	<ul class="nav">
    		<li>
	    		<a href="<s:url action="Video_list"><s:param name="pageNumber" value="1" /></s:url>">
	    			<s:if test="itemFilter==null">
						<strong>
					</s:if>
        			全部
        			<s:if test="itemFilter==null">
						</strong>
					</s:if>
	    		</a>
    		</li>
      		<s:iterator value="itemList" status="index">					
        	<li>
        		<a href="<s:url action="Video_list"><s:param name="itemFilter"><s:property value="itemId"/></s:param></s:url>">
        			<s:if test="itemFilter==itemId">
						<strong>
					</s:if>
        			<s:property value="name"/>
        			<s:if test="itemFilter==itemId">
						</strong>
					</s:if>
        		</a>
        	</li>
       		</s:iterator>
       </ul>
</div>