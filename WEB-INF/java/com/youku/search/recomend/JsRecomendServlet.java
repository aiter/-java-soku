package com.youku.search.recomend;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import com.youku.search.hanyupinyin.Converter;
import com.youku.search.util.Constant;

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
	        String _keyword=request.getParameter("k");
	        String type=request.getParameter("type");
	    	String keyword=filterKeyword(_keyword);
	    	
	    	
	    	if(StringUtils.isBlank(keyword)) {
	    		response.getWriter().write(buildNullReturns(keyword, type));
	    		response.getWriter().flush();
	    		return;
	    	}
	    	
	    	if(StringUtils.isBlank(type)) {
	    		response.getWriter().write(buildNullReturns(keyword, type));
	    		response.getWriter().flush();
	    		return;
	    	}
	    	
	    	if(FilterUtils.isFilter(keyword)) {
	    		response.getWriter().write(buildNullReturns(keyword, type));
	    		response.getWriter().flush();
	    		return;
	    	}
	    	
	    	if(null==Constance.videoTree) {
	    		response.getWriter().write(buildNullReturns(keyword, type));
	    		response.getWriter().flush();
	    		return;
	    	}
	        String resstr;
			try {
				resstr = buildSearchRecomendJsonString(keyword, type);
			} catch (JSONException e) {
				resstr = buildNullReturns(keyword, type);
				e.printStackTrace();
			}
	        response.getWriter().write(resstr);
	        response.getWriter().flush();
	    }
	  /**
	   * 视频:1,专辑:2,会员:3,看吧:4,pk擂台:5
	   * @param type
	   * @param keyword
	   * @return
	 * @throws JSONException 
	   */
	  public String buildSearchRecomendJsonString(String keyword,String type) throws JSONException{
		  long cost = System.currentTimeMillis();
		  TrieTree t;
		  TrieTree ch_t;
		  if(type.equalsIgnoreCase(Constance.VIDEO)){
			  t=Constance.videoTree;ch_t=Constance.ch_videoTree;
		  }else if(type.equalsIgnoreCase(Constance.FRONT_FOLDER)){
			  t=Constance.folderTree;ch_t=Constance.ch_folderTree;
		  }else if(type.equalsIgnoreCase(Constance.USER)){
			  t=Constance.userTree;ch_t=Constance.ch_userTree;
		  }else if(type.equalsIgnoreCase(Constance.BAR)){
			  t=Constance.barTree;ch_t=Constance.ch_barTree;
		  }else if(type.equalsIgnoreCase(Constance.PK)){
			  t=Constance.pkTree;ch_t=Constance.ch_pkTree;
		  }else return buildNullReturns(keyword, type);
		  if(null==t)return buildNullReturns(keyword, type);
		  List<Entity> st=t.search(keyword);
		  List<Entity> ch_st=null;
		  if(null==st||st.size()<SelectionSort.RETURNSIZE)
			  ch_st=ch_t.search(Converter.convert(keyword));
		  Result rs = new Result();
		  rs.keyword = keyword;
		  rs.type = type;
		  Result.Item item = null;
		  if(null!=st){
		  for(int i=0;i<st.size();i++){
			  item = rs.newItem();
			  item.keyword = st.get(i).getKeyword();
			  item.count = st.get(i).getValueNum();
			  rs.items.add(item);
		  }
		  }
		  if(null!=ch_st){
			  int len=SelectionSort.RETURNSIZE;
			  if(null!=st) len=SelectionSort.RETURNSIZE-st.size();
		  for(int i=0;i<ch_st.size()&&(i<len);i++){
			  if(null!=st){
				  boolean flag=false;
				  for(int j=0;j<st.size();j++){
					  if(st.get(j).getKeyword().equalsIgnoreCase(ch_st.get(i).getKeyword_py())){
						  flag=true;
						  break;
					  }
				  }
				  if(true==flag)continue;
			  }
			  item = rs.newItem();
			  item.keyword = ch_st.get(i).getKeyword_py();
			  item.count = ch_st.get(i).getValueNum();
			  rs.items.add(item);
		  }
		  }
		  rs.cost = System.currentTimeMillis()-cost;
		  return "showresult('("+ResultConverter.convert(rs).toString()+")',false)";
	  }
	  
	  
	  private String filterKeyword(String keyword) {
		  if(null==keyword||keyword.length()<1) return null;
		    Set<Character> set = Constant.StopWords.getStopSet();
		    StringBuilder builder = new StringBuilder();
		    char[] chars = keyword.toCharArray();
		    for (char c : chars) {
		        if (set.contains(c)) {
		            builder.append(" ");
		        } else {
		            builder.append(c);
		        }
		    }

		    return builder.toString().trim().toLowerCase();
	}
	
	public String buildNullReturns(String keyword,String type){
		long cost = System.currentTimeMillis();
		 Result rs = new Result();
		 rs.keyword = keyword;
		 rs.type = type;
		 rs.cost = System.currentTimeMillis()-cost;
		 try {
			return "showresult('("+ResultConverter.convert(rs).toString()+")',false)";
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
