package com.youku.soku.manage.admin.paihangbang;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.util.DataFormat;
import com.youku.soku.manage.bo.paihangbang.CateVO;
import com.youku.soku.manage.bo.paihangbang.ChannelStopWordsVO;
import com.youku.soku.manage.bo.paihangbang.ProgrammeVO;
import com.youku.soku.manage.bo.paihangbang.TopWordsVO;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.service.paihangbang.CateMgt;
import com.youku.soku.manage.service.paihangbang.ChannelStopWordsMgt;
import com.youku.soku.manage.service.paihangbang.ProgrammeMgt;
import com.youku.soku.manage.service.paihangbang.TopDateService;
import com.youku.soku.manage.service.paihangbang.TopWordsService;
import com.youku.soku.manage.service.paihangbang.TypeWordsService;
import com.youku.soku.manage.service.paihangbang.ZhidaquTopWordsBuilder;
import com.youku.soku.top.mapping.TopWords;
import com.youku.soku.top.mapping.TypeWords;
import com.youku.top.paihangbang.RankinfoMgt;
import com.youku.top.paihangbang.TopDateMgt;
import com.youku.top.util.TopDateType;
import com.youku.top.util.TopWordType;
import com.youku.top.util.TopWordType.WordType;

public class TopWordsAction extends BaseActionSupport {

	private static Logger logger = Logger.getLogger(TopWordsAction.class);
	private List<TopWordsVO> topwordsvo;
	private int pageNumber;
	private PageInfo pageInfo;
	private int updateid;
	private String words;
	private int cate = -1;
	private int visible = -1;
	private String topdate;
	private List<CateVO> catevos;
	private List<CateVO> updatecatevos;
	private String pic = "";
	private String searchWord;
	private int proid = -1;
	private static boolean isRun = false;

	private String muludateMsg;
	private String bangdandateMsg;
	private String fundateMsg;
	private Map<String, String> topdatemap = TopDateService.getInstance()
			.getTopDate();

	private TypeWords typewords;

	// 根据搜索条件列出榜单数据
	public String list() {

		String words = getWords();
		int cate = getCate();
		int visible = getVisible();
		String date = getTopdate();

		System.out.println("words:" + words + ",cate:" + cate + ",date:" + date
				+ ",visible" + visible);

		if (StringUtils.isBlank(date) || date.trim().equalsIgnoreCase("null"))
			date = DataFormat.formatDate(
					DataFormat.getNextDate(new Date(), -1),
					DataFormat.FMT_DATE_YYYYMMDD);
		if (cate < 0)
			cate = 0;
		try {
			if (null == pageInfo)
				pageInfo = new PageInfo();
			pageInfo.setPageSize(30);
			if (getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			TopWordsService.getInstance().findTopWordsPagination(pageInfo,
					words, cate, visible, date);
			pageInfo.setCurrentPageNumber(getPageNumber());
			setPageInfo(pageInfo);
		} catch (Exception e) {
			logger.error(e);
		}
		return Constants.LIST;
	}

	public String createMulu() {
		if (isRun) {
			try {
				HttpServletResponse response = ServletActionContext
						.getResponse();
				response.setContentType("text/html;charset=utf-8");
				PrintWriter out = response.getWriter();
				out.print("上一次创建尚未结束");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		String date = getTopdate();
		if (StringUtils.isBlank(date) || date.trim().equalsIgnoreCase("null"))
			date = DataFormat.formatDate(
					DataFormat.getNextDate(new Date(), -1),
					DataFormat.FMT_DATE_YYYYMMDD);
		isRun = true;
		List<TopWords> topwords = TopWordsService.getInstance().getTopWords(
				date);
		int count = 0;
		logger.debug("==== topwords:"+topwords.size()+" =====");
		if (null != topwords && topwords.size() > 1000) {
			try {
				logger.info("size:" + topwords.size());
				date = date.replaceAll("-", "_");
				RankinfoMgt.getInstance().rankinfoTableCreate(date);
				int max_version_no = RankinfoMgt.getInstance()
						.rankinfoTableVersionGetter(date);
				max_version_no += 1;
				System.out.println("max_version_no:" + max_version_no);
				count = ZhidaquTopWordsBuilder.getInstance().build(topwords,
						max_version_no);
				logger.info("保存电视剧电影榜单数据,size:" + count + ",date:" + date
						+ ",version_no:" + max_version_no + ",Operator:"
						+ getUserName());
				System.out.println("count:" + count);
				if (count > 500) {
					int c = TopDateMgt.getInstance().topDateSave(date,
							max_version_no, getUserName(),
							TopDateType.TopDate.zhidaqu.name());
					logger.info("跟新电视剧电影线上版本号和日期,date:" + date + ",version_no:"
							+ max_version_no + ",成功标志:"
							+ (c > -1 ? true : false) + ",Operator:"
							+ getUserName());
					System.out.println("c:" + c);
					if (c < 0)
						count = -1;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				topwords.clear();
				isRun = false;
			}
		} else {
			topwords.clear();
			isRun = false;
		}
		String msg = "创建失败，请联系技术人员";
		if (count > 500)
			msg = "创建成功结束";
		else if (count == -1)
			msg = "创建成功但更新版本失败，请联系技术人员";
		writeMsg(msg);
		return null;
	}

	public String update() {
		int cate = getCate();
		int visible = getVisible();
		int id = getUpdateid();
		String pic = getPic();
		int proid = getProid();
		// System.out.println("1======id:"+id+",cate:"+cate+",pic:"+pic+",visible"+visible);
		if (id > 0) {
			TopWords topwords = TopWordsService.getInstance().getTopWordsById(
					id);
			if (null != topwords) {
				logger.info("update,Operator " + getUserName());
				// logger.info(topwords.toString());
				TypeWords typewords = TypeWordsService.getInstance()
						.getTypeWordsByKeywordAndCate(
								topwords.getKeyword().trim(),
								topwords.getCate());
				if (-1 != cate) {
					int row = TopWordsService.getInstance().executeSql(
							"update top_words set cate = " + cate
									+ " where id=" + id);
					if (row > 0) {
						logger.info("更新top_words分类成功,keyword:"
								+ topwords.getKeyword() + ",srccate:"
								+ topwords.getCate() + ",desccate:" + cate
								+ ",id:" + id + ",Operator:" + getUserName());
						row = 0;
						if (null != typewords) {
							if (cate > 0)
								row = TypeWordsService.getInstance()
										.executeSql(
												"update type_words set state='normal',cate="
														+ cate + ",checker='"
														+ getUserName()
														+ "' where id="
														+ typewords.getId());
							else
								row = TypeWordsService.getInstance()
										.executeSql(
												"delete from type_words where id="
														+ typewords.getId());
						} else {
							if (cate > 0) {
								String date = DataFormat.formatDate(new Date(),
										DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
								row = TypeWordsService
										.getInstance()
										.executeSql(
												"insert ignore into type_words(keyword,cate,state,checker,create_date) values ('"
														+ topwords.getKeyword()
														+ "',"
														+ cate
														+ ",'normal','"
														+ getUserName()
														+ "','"
														+ date + "')");
							}
						}
						if (row > 0) {
							logger.info("更新或删除type_words分类成功,keyword:"
									+ topwords.getKeyword() + ",srccate:"
									+ topwords.getCate() + ",desccate:" + cate
									+ ",Operator:" + getUserName());
						}

						writeMsg("success");
					} else
						writeMsg("faild");
					return null;
				}
				if (-1 != visible) {
					int row = TopWordsService.getInstance().executeSql(
							"update top_words set visible = " + visible
									+ " where id=" + id);
					ChannelStopWordsVO cst = ChannelStopWordsMgt.getInstance()
							.getStopWordsByCate(topwords.getCate());
					if (row > 0) {
						logger.info("更新top_words状态成功,keyword:"
								+ topwords.getKeyword() + ",srcvisible:"
								+ topwords.getVisible() + ",descvisible:"
								+ visible + ",id:" + id + ",Operator:"
								+ getUserName());
						row = 0;
						if (null != cst) {
							if (visible == 0)
								cst.getBlocked_words().add(
										topwords.getKeyword());
							else
								cst.getBlocked_words().remove(
										topwords.getKeyword());
							if (cst.getBlocked_words().size() > 0)
								row = ChannelStopWordsMgt.getInstance()
										.updateStopWords(cst.getId(),
												"blocked_words",
												cst.getBlocked_words());
							else
								row = ChannelStopWordsMgt.getInstance()
										.deleteById(cst.getId());
						} else {
							if (visible == 0) {
								Set<String> set = new HashSet<String>();
								set.add(topwords.getKeyword());
								row = ChannelStopWordsMgt.getInstance()
										.insertStopWords(topwords.getCate(),
												"blocked_words", set);
							}
						}
						if (row > 0)
							logger.info("更新channel_stop_words成功,keyword:"
									+ topwords.getKeyword() + ",srcvisible:"
									+ topwords.getVisible() + ",descvisible:"
									+ visible + ",cate:" + topwords.getCate()
									+ ",Operator:" + getUserName());
						writeMsg("success");
					} else
						writeMsg("faild");
					return null;
				}
				if (!StringUtils.isBlank(pic)) {
					if (pic.startsWith("http"))
						pic = StringUtils
								.substringAfterLast(pic, ".ykimg.com/");
					if (!StringUtils.isBlank(pic)) {
						int row = TopWordsService.getInstance().executeSql(
								"update top_words set pic = '" + pic
										+ "' where id=" + id);
						if (row > 0) {
							logger.info("更新top_words图片成功,keyword:"
									+ topwords.getKeyword() + ",srcpic:"
									+ topwords.getPic() + ",descpic:" + pic
									+ ",id:" + id + ",Operator:"
									+ getUserName());
							row = 0;
							if (null != typewords) {
								row = TypeWordsService.getInstance()
										.executeSql(
												"update type_words set pic='"
														+ pic + "',checker='"
														+ getUserName()
														+ "' where id="
														+ typewords.getId());
							} else {
								String date = DataFormat.formatDate(new Date(),
										DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
								row = TypeWordsService
										.getInstance()
										.executeSql(
												"insert ignore into type_words(keyword,cate,pic,state,checker,create_date) values ('"
														+ topwords.getKeyword()
														+ "',"
														+ topwords.getCate()
														+ ",'"
														+ pic
														+ "','normal','"
														+ getUserName()
														+ "','"
														+ date + "')");
							}
							if (row > 0)
								logger.info("更新type_words图片成功,keyword:"
										+ topwords.getKeyword() + ",srcpic:"
										+ topwords.getPic() + ",descpic:" + pic
										+ ",Operator:" + getUserName());
						}
					}
				}
				if (proid > -1) {
					int row = TopWordsService.getInstance().executeSql(
							"update top_words set programme_id = " + proid
									+ " where id=" + id);
					if (row > 0) {
						logger.info("更新top_words节目id成功,keyword:"
								+ topwords.getKeyword() + ",srcprogramme_id:"
								+ topwords.getProgrammeId()
								+ ",descprogramme_id:" + proid + ",id:" + id
								+ ",Operator:" + getUserName());
						row = 0;
						if (null != typewords) {
							row = TypeWordsService.getInstance().executeSql(
									"update type_words set state='normal',programme_id="
											+ proid + ",checker='"
											+ getUserName() + "' where id="
											+ typewords.getId());
						} else {
							String date = DataFormat.formatDate(new Date(),
									DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
							row = TypeWordsService
									.getInstance()
									.executeSql(
											"insert ignore into type_words(keyword,cate,programme_id,state,checker,create_date) values ('"
													+ topwords.getKeyword()
													+ "',"
													+ topwords.getCate()
													+ ","
													+ proid
													+ ",'normal','"
													+ getUserName()
													+ "','"
													+ date + "')");
						}
						if (row > 0) {
							logger.info("更新type_words节目id成功,keyword:"
									+ topwords.getKeyword()
									+ ",srcprogramme_id:"
									+ topwords.getProgrammeId()
									+ ",descprogramme_id:" + proid
									+ ",Operator:" + getUserName());
						}
					}
				}
				writeMsg("success");
			}
		}

		return null;
	}

	public String addType() {
		int row = 0;
		if (typewords.getCate() > 0
				&& StringUtils.isBlank(typewords.getKeyword())) {
			typewords.setChecker(getUserName());
			String pic = null;
			if (typewords.getPic().startsWith("http"))
				pic = StringUtils.substringAfterLast(typewords.getPic(),
						".ykimg.com/");
			if (!StringUtils.isBlank(pic))
				pic = "'" + pic + "'";
			String date = DataFormat.formatDate(new Date(),
					DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
			row = TypeWordsService
					.getInstance()
					.executeSql(
							"insert ignore into type_words (keyword,programme_id,state,cate,pic,checker,create_date) values('"
									+ typewords.getKeyword()
									+ "',"
									+ typewords.getProgrammeId()
									+ ",'normal',"
									+ typewords.getCate()
									+ ","
									+ pic
									+ ",'"
									+ typewords.getChecker()
									+ "','"
									+ date
									+ "')");
		}
		if (row > 0) {
			writeMsg("success");
			cate = 0;
			return list();
		} else {
			writeMsg("failed");
			return INPUT;
		}
	}

	public String toAddType() {
		typewords = new TypeWords();
		return "toadd";
	}

	private void writeMsg(String msg) {
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String searchProgramme() {
		String searchwords = getSearchWord();
		int id = getUpdateid();
		TopWords topwords = TopWordsService.getInstance().getTopWordsById(id);
		JSONArray jarr = new JSONArray();
		if (null != topwords) {
			if (!StringUtils.isBlank(searchwords)) {
				List<ProgrammeVO> programmes = null;
				if (topwords.getCate() == WordType.综艺.getValue())
					programmes = ProgrammeMgt.getInstance().getSeries(
							searchwords.trim(), topwords.getCate());
				else
					programmes = ProgrammeMgt.getInstance().getProgramme(
							searchwords.trim(), topwords.getCate());
				if (null != programmes) {
					JSONObject json = null;
					for (ProgrammeVO pvo : programmes) {
						json = new JSONObject();
						try {
							json.put("id", pvo.getId());
							json.put("name", pvo.getName());
							json.put("cate", pvo.getCate());
							jarr.put(json);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}
			}
		}
		writeMsg(jarr.toString());

		return null;
	}
	
	private int num = 0;
	//查询指定数量的热门搜索 生成xls并下载
	public String exportXls(){
		logger.debug("********* start export action*********");
		String cateName = TopWordsService.getCateNameForXls(cate);
		String fileName;
		try {
			fileName = cateName + topdate + ".xls";
			HttpServletResponse response=ServletActionContext.getResponse();
			response.reset();
			response.setContentType("application/x-msdownload");  
		    response.setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes(),"iso8859-1"));  
		    ServletOutputStream sos = response.getOutputStream(); 
		    
		    jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(sos);
		    wwb = TopWordsService.exportXls(num, cate,cateName,topdate, wwb);
			logger.debug("********* end export action :"+wwb.getNumberOfSheets()+"*********");
		    wwb.write();
		    wwb.close();
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public TypeWords getTypewords() {
		return typewords;
	}

	public void setTypewords(TypeWords typewords) {
		this.typewords = typewords;
	}

	public int getProid() {
		return proid;
	}

	public void setProid(int proid) {
		this.proid = proid;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public void setCatevos(List<CateVO> catevos) {
		this.catevos = catevos;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public List<CateVO> getCatevos() {
		List<CateVO> cvs= CateMgt.getCateVOs();
		cvs.add(new CateVO("无类型",100));
		return cvs;
	}

	public List<CateVO> getUpdatecatevos() {
		return CateMgt.getUpdateCateVOs();
	}

	public void setUpdatecatevos(List<CateVO> updatecatevos) {
		this.updatecatevos = updatecatevos;
	}

	public List<TopWordsVO> getTopwordsvo() {
		return topwordsvo;
	}

	public void setTopwordsvo(List<TopWordsVO> topwordsvo) {
		this.topwordsvo = topwordsvo;
	}

	public String getTopdate() {
		return topdate;
	}

	public void setTopdate(String topdate) {
		this.topdate = topdate;
	}

	public String getMuludateMsg() {
		String muludate = topdatemap.get(TopDateType.TopDate.zhidaqu.name());
		if (null != muludate) {
			return "目录线上时间:" + muludate;
		}
		return "";
	}

	public void setMuludateMsg(String muludateMsg) {
		this.muludateMsg = muludateMsg;
	}

	public String getBangdandateMsg() {
		String bangdandate = topdatemap.get(TopDateType.TopDate.top.name());
		if (null != bangdandate) {
			return "榜单线上时间:" + bangdandate;
		}
		return "";
	}

	public void setBangdandateMsg(String bangdandateMsg) {
		this.bangdandateMsg = bangdandateMsg;
	}

	public String getFundateMsg() {
		String gaoxiaodate = topdatemap.get(TopDateType.TopDate.fun.name());
		if (null != gaoxiaodate) {
			return "搞笑榜单线上时间:" + gaoxiaodate;
		}
		return "";
	}

	public void setFundateMsg(String fundateMsg) {
		this.fundateMsg = fundateMsg;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public int getUpdateid() {
		return updateid;
	}

	public void setUpdateid(int updateid) {
		this.updateid = updateid;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

	public int getCate() {
		return cate;
	}

	public void setCate(int cate) {
		this.cate = cate;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}
}
