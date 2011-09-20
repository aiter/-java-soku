package com.youku.soku.manage.service.paihangbang;

import java.util.ArrayList;
import java.util.List;

import com.youku.soku.manage.bo.paihangbang.CateVO;
import com.youku.top.util.TopWordType;
import com.youku.top.util.TopWordType.WordType;

public class CateMgt {
	
	public static List<CateVO> getCateVOs(){
		List<CateVO> catevos = new ArrayList<CateVO>();
		for(TopWordType.WordType wt:TopWordType.WordType.values()){
			catevos.add(new CateVO(wt.name(),wt.getValue()));
		}
		return catevos;
	}
	
	public static List<CateVO> getUpdateCateVOs(){
		List<CateVO> catevos = new ArrayList<CateVO>();
		for(TopWordType.WordType wt:TopWordType.WordType.values()){
			if(!wt.name().equalsIgnoreCase(WordType.搞笑.name()))
				catevos.add(new CateVO(wt.name(),wt.getValue()));
		}
		return catevos;
	}
	
	public static void main(String[] args){
		List<CateVO> catevos = getCateVOs();
		for(CateVO cv:catevos){
			System.out.println(cv.getCatename()+"\t"+cv.getCatevalue());
		}
	}
}
