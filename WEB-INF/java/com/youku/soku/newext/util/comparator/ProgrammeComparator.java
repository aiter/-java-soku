package com.youku.soku.newext.util.comparator;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;

/**
 * 按releasedate倒序排序
 */
public class ProgrammeComparator implements Comparator<Programme>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Programme o1, Programme o2) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			if(sdf.parse(o1.getReleaseDate()).before(sdf.parse(o2.getReleaseDate()))){
				return 1;
			}else if(sdf.parse(o1.getReleaseDate()).after(sdf.parse(o2.getReleaseDate()))){
				return -1;
			}else{
				return 0;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
		
	}
}
