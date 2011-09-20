package com.youku.search.console.operate;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.youku.soku.library.Utils;

public class RegexpBuilder {
	
	public static String build(Set<String> teleplayNames,Set<String> versionNames,int order){
		if(null==teleplayNames||teleplayNames.size()<1) return "";
		if(order==0) return "";
		StringBuilder regexp = new StringBuilder();
		regexp.append("(");
		int i = 0;
		for(String tn:teleplayNames){
			if(i!=0)
				regexp.append("|");
			regexp.append(tn);
			i++;
		}
		regexp.append(")");
		regexp.append("[ ]*");
		if(null!=versionNames&&versionNames.size()>0){
			regexp.append("(");
			i = 0;
			for(String vn:versionNames){
				if(i!=0)
					regexp.append("|");
				regexp.append(vn);
				i++;
			}
			regexp.append(")");
		}
		regexp.append("[ ]*");
		regexp.append("[第]*");
		regexp.append("[0 ]*");
		regexp.append(order);
		regexp.append("[ ]*");
		regexp.append("[集期话]*");
		return regexp.toString();
	}
	
	public static String build(Set<String> varietyNames,int order){
		if(null==varietyNames||varietyNames.size()<1) return "";
		if(order==0) return "";
		StringBuilder regexp = new StringBuilder();
		StringBuilder name = new StringBuilder();
		name.append("(");
		int i = 0;
		for(String tn:varietyNames){
			if(i!=0)
				name.append("|");
			name.append(tn);
			i++;
		}
		name.append(")");
		StringBuilder orderdate = new StringBuilder();
		if((""+order).length()==8){
			orderdate.append("(");
			orderdate.append("(");
			orderdate.append("[0 ]*");
			orderdate.append(StringUtils.substring(""+order, 3,4));
			orderdate.append("|");
			orderdate.append(order/10000);
			orderdate.append(")");
			orderdate.append("[-,_,/,年, ]*");
			orderdate.append("[0 ]*");
			orderdate.append((order/100)%100);
			orderdate.append("[-,_,/,月, ]*");
			orderdate.append("[0 ]*");
			orderdate.append(order%100);
			orderdate.append(")");
		}else return null;
		
		regexp.append("(").append(name).append("[ ]*").append(orderdate).append(")").append("|").append("(").append(orderdate).append("[ ]*").append(name).append(")");
		
		return regexp.toString();
	}
	
	public static boolean allMatch(String title,String regexp){
		if (title.matches(regexp))
			return true;
		if (title.toLowerCase().matches(regexp.toLowerCase()))
			return true;
		if (Utils.analyzerForSearch(title).matches(regexp))
			return true;
		if (Utils.analyzerForSearch(regexp).matches(title))
			return true;
		if (Utils.analyzerForSearch(title).matches(Utils.analyzerForSearch(regexp)))
			return true;
		String t = Utils.formatTeleplayName(title);
		if(StringUtils.isBlank(t)) return false;
		t = Utils.stopWordsFilter(t);
		if(StringUtils.isBlank(t)) return false;
		if (t.matches(regexp))
			return true;
		if (t.toLowerCase().matches(regexp.toLowerCase()))
			return true;
		if (Utils.analyzerForSearch(t).matches(regexp))
			return true;
		if (Utils.analyzerForSearch(regexp).matches(t))
			return true;
		if (Utils.analyzerForSearch(t).matches(Utils.analyzerForSearch(regexp)))
			return true;
		return false;
	}
	
	public static boolean varietyAllMatch(String title,String regexp,Collection<String> exclude){
		if(null!=exclude){
			StringBuilder bf = new StringBuilder();
			bf.append("(");
			int i = 0;
			for(String a:exclude){
				if(i!=0)
					bf.append(a);
				i++;
			}
			bf.append(")");
			if(Pattern.compile(bf.toString()).matcher(title).find())
				return false;
		}
		
		boolean f = allMatch(title, regexp);
		if(!f){
			String sub_t = title.replaceAll(regexp, "");
			if(StringUtils.isBlank(sub_t))
				f = true;
			if(!Pattern.compile("\\d{1,}").matcher(sub_t).find())
				f = true;
		}
		return f;
	}
}
