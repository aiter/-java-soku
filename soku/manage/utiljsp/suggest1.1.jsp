<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page buffer="1kb" %>
<%@ page import="com.youku.soku.suggest.trie.*, 
com.youku.soku.suggest.parser.*,
com.youku.soku.suggest.data.*,
com.youku.soku.suggest.entity.*,
com.youku.soku.suggest.trie.*,
java.util.List,
com.youku.soku.web.util.WebUtil,
java.util.Collection,
com.youku.soku.manage.shield.ShieldInfo,
com.youku.soku.shield.Filter,
com.youku.soku.shield.Filter.Source,
com.youku.soku.suggest.parser.WordProcessor"%>
<%
response.resetBuffer();
%><%
//soku站外版  主要区别在详情页
long startTime = System.currentTimeMillis();
	if(request.getParameter("query") != null){
		String keyword = (String) request.getParameter("query");
		
		ShieldInfo shieldInfo = Filter.getInstance().isShieldWord(keyword, Source.soku);
		boolean isShieldWord = false;
		if (shieldInfo != null && shieldInfo.isMatched()) {
			isShieldWord = true;
		}
		StringBuilder builder = new StringBuilder();
		String directPlayUrl = null;
		builder.append("aa.Ja({q:'" + WebUtil.encodingHtmlTag(keyword) + "',r:[");
		
		keyword = keyword.replace("[\n\\s]", "");
		keyword = keyword.toLowerCase();
		if(WordProcessor.getStringLength(keyword) < 14 && !isShieldWord) {
			try {
				TrieTree tree = TrieTreeHolder.getCurrentThreadLocal();	
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
									builder.append("{o:'").append(i).append("',k:'").append(de.getUrl())
										   .append("',d:'").append("/detail/show/X").append(de.getProgrammeEncodeId()).append("?keyword=").append(de.getProgrammeName())
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
		out.print(builder.toString());
		
	}
	long endTime = System.currentTimeMillis();
%>
