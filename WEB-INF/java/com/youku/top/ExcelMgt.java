package com.youku.top;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.youku.search.util.DataFormat;
import com.youku.top.quick.QueryTopWordsMerger;
import com.youku.top.topn.util.KeywordUtil;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelMgt {
	
	public static void main(String[] args) {
//		write();
		System.out.println(KeywordUtil.wordFilterTopword("就是爱玩美 2009"));
	}
	
	public static void write() {
		
		try {
			Workbook wb = Workbook.getWorkbook(new File("/opt/a.xls"));
			WritableWorkbook wwb = Workbook.createWorkbook(new File("/opt/b.xls"));
			WritableSheet s = wwb.createSheet("officeTopInfo", 0);
			Sheet sheet = wb.getSheet(0);
			System.out.println(sheet.getColumns());
			System.out.println(sheet.getRows());
			
//			WritableCell c = null;
			Cell c = null;
			String date = null;
			String src_keyword = null;
			String search_key = null;
			Map<Integer,Map<String,Integer>> map = new HashMap<Integer, Map<String,Integer>>();
			Map<String,Integer> datemap = null;
			Map<String,Integer> totalmap = new HashMap<String, Integer>();
			WritableFont writefontkeyword = new WritableFont(WritableFont.ARIAL, 11,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat writecellfontkeyword = new WritableCellFormat(writefontkeyword);
			for(int i=0;i<sheet.getRows();i++){
				for(int j=0;j<sheet.getColumns();j++){
					c = sheet.getCell(j, i);
					src_keyword = c.getContents();
					if(StringUtils.isBlank(src_keyword))
						src_keyword = "";
					else src_keyword = src_keyword.trim();
					if(i==0){
						s.addCell(new jxl.write.Label(j,i,src_keyword));
					}else{
						if(j==0)
							s.addCell(new jxl.write.Label(j,i,src_keyword));
						if(j==1)
							s.addCell(new jxl.write.Number(j,i,DataFormat.parseInt(src_keyword),writecellfontkeyword));
						if(j==2){
							s.addCell(new jxl.write.Label(j,i,src_keyword));
							search_key = KeywordUtil.wordFilterTopword(src_keyword);
							s.addCell(new jxl.write.Label(j+1,i,search_key));
						}
						if(j==4){
							if(null==totalmap||totalmap.size()<20000)
								totalmap = getTotalMap();
							s.addCell(new jxl.write.Number(j,i,DataFormat.parseInt(totalmap.get(search_key))));
						}
						if(j>4){
							if(null==map.get(j)){
								date = getDate(j-5);
								datemap = QueryTopWordsMerger.getInstance().getQueryMap(date);
								while(null==datemap||datemap.size()<1){
									Thread.sleep(20000);
									System.out.println(date);
									datemap = QueryTopWordsMerger.getInstance().getQueryMap(date);
								}
								map.put(j, datemap);
							}
							s.addCell(new jxl.write.Number(j,i,DataFormat.parseInt(map.get(j).get(search_key))));
						}
					}
					
				}
			}
			
			wwb.write();
			wwb.close();
			wb.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static String getDate(int j){
		Date date= DataFormat.parseUtilDate("2011-01-01", DataFormat.FMT_DATE_YYYYMMDD);
		return DataFormat.formatDate(DataFormat.getNextDate(date, j), DataFormat.FMT_DATE_YYYY_MM_DD);
	}
	
	public static String getAllDate(int j){
		Date date= DataFormat.parseUtilDate("2010-08-01", DataFormat.FMT_DATE_YYYYMMDD);
		return DataFormat.formatDate(DataFormat.getNextDate(date, j), DataFormat.FMT_DATE_YYYY_MM_DD);
	}
	
	public static Map<String,Integer> getTotalMap(){
		int i=0;
		Map<String,Integer> datemap = null;
		Map<String,Integer> map = new HashMap<String, Integer>();
		while(true){
			String date = getAllDate(i);
			i=i+1;
			datemap = QueryTopWordsMerger.getInstance().getQueryMap(date);
			int block=0;
			while(null==datemap||datemap.size()<1){
				if(block>3) break;
				try {
					Thread.sleep(20000);
				} catch (Exception e) {
				}
				System.out.println(date);
				datemap = QueryTopWordsMerger.getInstance().getQueryMap(date);
				block=block+1;
			}
			if(null!=datemap){
				for(Entry<String, Integer> entry:datemap.entrySet()){
					map.put(entry.getKey(), DataFormat.parseInt(map.get(entry.getKey()))+entry.getValue());
				}
			}
			if(date.equalsIgnoreCase("2011_03_28"))
				break;
		}
		return map;
	}
}
