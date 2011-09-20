/**
 * 
 */
package com.youku.soku.library.load.autoSearch.handle;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.youku.soku.library.Utils;
import com.youku.soku.library.load.form.ProgrammeEpisodeBo;
import com.youku.soku.library.load.form.ProgrammeSiteBo;

/**
 * @author liuyunjian
 * 2011-3-2
 */
public class MovieHandler extends Handler {

	public MovieHandler(ProgrammeSiteBo psBo) {
		super(psBo);
	}

	/* (non-Javadoc)
	 * 电影的自动发现的总集数。小于0的，只自动发现1集；大于5的只自动发现5集。
	 * @see com.youku.soku.library.load.autoSearch.handle.Handler#getEpisodeTotal()
	 */
	@Override
	protected int getEpisodeTotal() {
		int total = super.getEpisodeTotal();
		if(total==0){
			total = 1;
		}else if (total>5) {
			total = 5;
		}
		return total;
	}
	
	@Override
	protected void removeExistId(List<Integer> nPeList) {
		if(peBoList!=null){
			for (ProgrammeEpisodeBo pe : peBoList) {
				nPeList.remove(new Integer(pe.orderId));
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.youku.soku.library.load.autoSearch.handle.Handler#matchName(java.lang.String, int, java.lang.String)
	 */
	@Override
	protected boolean matchName(String title, int order, String version) {
		String s = Utils.parse2Str(searchKeys);
		// String v = Utils.parse2Str(p.versions);
		StringBuilder keyword = new StringBuilder();

		if (!StringUtils.isBlank(s)) {
			keyword.append("(").append(s).append(")");
		} else {
			return false;
		}

		keyword.append("[ ]*");

		if (!StringUtils.isBlank(version)) {
			keyword.append("(").append(version).append("|").append(Utils.analyzer(version)).append(")");
		}
		
		String t = Utils.formatTeleplayName(title);
		
		if(StringUtils.isBlank(t)) return false;
		t = Utils.stopWordsFilter(t);
		if(StringUtils.isBlank(t)) return false;
		
		if(t.matches(keyword.toString()))
			return true;
		if(Utils.analyzer(t).matches(keyword.toString()))
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see com.youku.soku.library.load.autoSearch.handle.Handler#getStageOrder(int)
	 */
	@Override
	protected String getStageOrder(int orderId,boolean haveYear) {
		return "";
	}
}
