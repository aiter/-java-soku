package com.youku.soku.suggest;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.youku.soku.manage.shield.ShieldInfo;
import com.youku.soku.shield.Filter;
import com.youku.soku.shield.Filter.Source;
import com.youku.soku.suggest.data.TrieTreeHolder;
import com.youku.soku.suggest.parser.WordProcessor;
import com.youku.soku.suggest.trie.DirectEntity;
import com.youku.soku.suggest.trie.Entity;
import com.youku.soku.web.util.WebUtil;

public class JsRecomendServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	  @Override
	    protected void service(HttpServletRequest request,
	            HttpServletResponse response) throws ServletException, IOException {
		  
	        response.setCharacterEncoding("utf-8");
	        response.setContentType("text/plain;charset=utf-8");
	        if(request.getParameter("query") != null){
	    		String keyword = request.getParameter("query");
	    		String siteIdStr =  request.getParameter("site");  //标识站内还是站外引用， 默认站外，不为空就是站内
	    		
	    		
	    		ShieldInfo shieldInfo = Filter.getInstance().isShieldWord(keyword, Source.soku);
	    		boolean isShieldWord = false;
	    		if (shieldInfo != null && shieldInfo.isMatched()) {
	    			isShieldWord = true;
	    		}
	    		StringBuilder builder = new StringBuilder();
	    		String directPlayUrl = null;
	    		builder.append("aa.suggestUpdate({q:'" + WebUtil.encodingHtmlTag(keyword) + "',r:[");
	    		keyword = keyword.replace("[\n\\s]", "");
	    		keyword = keyword.toLowerCase();
	    		if(WordProcessor.getStringLength(keyword) < 14 && !isShieldWord) {
	    			try {
	    				com.youku.soku.suggest.trie.TrieTree tree = TrieTreeHolder.getCurrentThreadLocal();	
	    				boolean showRank = false;
	    				
	    				if(tree != null) {
	    					List<Entity> result = null;
	    					if(keyword.isEmpty()) {
	    						result = tree.getTopWordEntity();
	    						showRank = true;
	    					} else {
	    						result = tree.search(keyword.trim());
	    					}
	    					
	    					long endTime = System.currentTimeMillis();	
	    					
	    					//String head = "";
	    					String processedWord = WordProcessor.analyzerPrepare(keyword);
	    					if(processedWord != null) {
	    						processedWord = processedWord.trim();
	    						processedWord = processedWord.replace(" ", "");
	    					}
	    					directPlayUrl = tree.getDirectPlayUrlsMap().get(processedWord);

	    					if(result != null) {
	    						for(int j =0; j < result.size(); j++) {
	    							Entity e = result.get(j);
	    							if(j > 0) {
	    								builder.append(",");
	    							}
	    							String displayWord = WebUtil.encodingHtmlTag(e.getWord());
	    							if(showRank) {
	    								displayWord = (j + 1) + ". " + displayWord;
	    							}
	    							builder.append("{c:'" + displayWord + "'");
	    							List<DirectEntity> deList = tree.getDirectEntityMapp().get(e.getWord());	
	    							String viewUrl = tree.getViewUrlsMap().get(e.getWord());
	    							//|| EntityFlagUtil.checkFlag(e.getFlag(), EntityFlagUtil.DIRECTFLAG
	    							if(deList != null && deList.size() > 0) {
	    								builder.append(", d:1, u:[");
	    								for(int i = 0; i < deList.size(); i++) {
	    									DirectEntity de = deList.get(i);
	    									if(i > 0) {
	    										builder.append(", ");
	    									}
	    									if(i > 2) {
	    										break;
	    									}
	    									String detailUrl = "/detail/show/X" + de.getProgrammeEncodeId();
	    									if(siteIdStr != null && !StringUtils.isBlank(de.getShowIdStr())) {
	    										detailUrl = "http://www.youku.com/show_page/id_z" + de.getShowIdStr() + ".html";
	    									}
	    									builder.append("{o:'").append(i).append("',k:'").append(de.getUrl())
	    										   .append("',d:'").append(detailUrl).append("?keyword=").append(URLEncoder.encode(de.getProgrammeName(), "utf-8"))
	    										   .append("',j:'").append(de.getProgrammeName())
	    										   .append("',m:'").append(de.getDivTip())
	    										   .append("',y:'").append(de.getDivTipYear())
	    										   .append("',n:'").append(de.getLiTip())
	    										   .append("',b:'").append(de.getLogo())
	    										   .append("',p:'").append(de.getPerformers()).append("'}") ;
	    								}
	    								builder.append("]");
	    							} else if(viewUrl != null) {
	    								builder.append(", l:'" + viewUrl + "', t:1");
	    							}
	    						
	    							
	    							builder.append("}");
	    						}
	    						
	    					}
	    					
	    				} 
	    				
	    				
	    			} catch(Exception e) {
	    				e.printStackTrace();
	    			} finally {
	    				TrieTreeHolder.removeCurrentTrheadLocal();
	    			}
	    		}
	    		
	    		builder.append("]");
	    		if(directPlayUrl != null) {
	    			builder.append(", p:'").append(directPlayUrl).append("'");
	    		}
	    		builder.append("})");
	    		response.getWriter().print(builder.toString());
	    		
	    	}
	        response.getWriter().flush();
	    }
	 
}
