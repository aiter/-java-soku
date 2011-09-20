package com.youku.soku.manage.questionnaire.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.torque.util.Criteria;

import com.opensymphony.xwork2.ActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.questionnaire.AnswerMap;
import com.youku.soku.manage.questionnaire.bo.UserQuestionAnswerBo;
import com.youku.soku.manage.questionnaire.orm.QuestionAnswer;
import com.youku.soku.manage.questionnaire.orm.QuestionAnswerPeer;
import com.youku.soku.manage.questionnaire.orm.User;
import com.youku.soku.manage.questionnaire.orm.UserPeer;

public class SurveyAction extends ActionSupport {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private static final String SEPARATOR = "|";

	public String input() throws Exception {
		
		Cookie[] cookies = ServletActionContext.getRequest().getCookies();
		String submitFlag = "";
		if(cookies!=null){
			for(int i=0;i<cookies.length;i++){
				if(cookies[i].getName().equals("survey_repeat_check")){
					submitFlag = cookies[i].getValue();
					break;
				} 
			}
		}
		
		if("1".equals(submitFlag)) {
			return "repeat";
		}
		return INPUT;
	}
	
	public String list() throws Exception {
		List<UserQuestionAnswerBo> uqaList = new ArrayList<UserQuestionAnswerBo>();
		List<QuestionAnswer> qaList = QuestionAnswerPeer.doSelect(new Criteria());
	;
				
		for(QuestionAnswer qa : qaList) {
			User user = UserPeer.retrieveByPK(qa.getFkUserId());
			UserQuestionAnswerBo uqa = new UserQuestionAnswerBo();
			uqa.setQa(qa);
			uqa.setUser(user);
			uqaList.add(uqa);
			
		}
		setUqaList(uqaList);
		
		
		return Constants.LIST;
	}
	
	public String list1() throws Exception {
		List<UserQuestionAnswerBo> uqaList = new ArrayList<UserQuestionAnswerBo>();
		List<QuestionAnswer> qaList = QuestionAnswerPeer.doSelect(new Criteria());
		File file = new File("/opt/soku_data/survey_result.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		StringBuilder builder = new StringBuilder();
		builder.append("用户名").append("\t").append("性别").append("\t").append("年龄").append("\t").append("学历").append("\t").append("所在行业").append("\t")
				.append("联系电话").append("\t").append("邮箱").append("\t").append("所在地区").append("\t")
				.append("P1").append("\t").append("每天").append("\t").append("每周").append("\t").append("偶尔").append("\t").append("几乎不用").append("\t")
				.append("P2").append("\t").append("搜库").append("\t").append("百度").append("\t").append("优酷").append("\t").append("奇艺").append("\t").append("搜狗").append("\t").append("搜狐视频").append("\t").append("搜搜").append("\t").append("土豆").append("\t").append("Google").append("\t").append("其他").append("\t")
				.append("P3").append("\t").append("片名").append("\t").append("片名 + 关键词").append("\t").append("表示类别的词").append("\t")
				.append("P4").append("\t").append("快速浏览搜索结果").append("\t").append("先利用页面的筛选工具对结果进行筛选").append("\t")
				.append("P5").append("\t").append("视频时长").append("\t").append("视频来源网站").append("\t").append("发布者").append("\t").append("画质").append("\t").append("播放数").append("\t").append("网络速度").append("\t").append("截图").append("\t").append("发布时间").append("\t").append("分段观看").append("\t")
				.append("P6").append("\t").append("电视剧").append("\t").append("电影").append("\t").append("综艺").append("\t").append("动漫").append("\t").append("体育").append("\t").append("原创").append("\t").append("科技").append("\t").append("汽车").append("\t").append("音乐").append("\t").append("教育").append("\t").append("游戏").append("\t").append("其他").append("\t")
				.append("P7").append("\t").append("留意到了改版后变化较大").append("\t").append("留意到了改版后变化不大").append("\t").append("没有留意").append("\t")
				.append("P8").append("\t").append("公司").append("\t").append("学校").append("\t").append("家").append("\t").append("网吧").append("\t").append("公共场所").append("\t").append("其他").append("\t")
				.append("P9").append("\t").append("门户网站").append("\t").append("社交网站").append("\t").append("即时聊天工具").append("\t").append("论坛").append("\t").append("订阅信息").append("\t").append("邮件").append("\t").             append("博客和微博客").append("\t").append("搜索").append("\t").append("专业网站").append("\t").append("其他").append("\t")
				.append("P10").append("\t").append("网络新闻").append("\t").append("网络游戏").append("\t").append("网络购物").append("\t").append("网络音乐").append("\t").append("网络视频").append("\t").append("论坛").append("\t").append("搜索引擎").append("\t").append("电子邮件").append("\t").append("社交网站").append("\t").append("即时通信").  append("\t").append("博客和微博").append("\t").append("网络炒股").append("\t").append("其他").append("\t")
				.append("\n");
				
		for(QuestionAnswer qa : qaList) {
			User user = UserPeer.retrieveByPK(qa.getFkUserId());
			UserQuestionAnswerBo uqa = new UserQuestionAnswerBo();
			uqa.setQa(qa);
			uqa.setUser(user);
			uqaList.add(uqa);
			
			builder.append(user.getName()).append("\t").append(AnswerMap.getUserSex().get(user.getSex() + "")).append("\t").append(AnswerMap.getUserAge().get(user.getAge()+ "")).append("\t").
			append(AnswerMap.getUserEducation().get(user.getEducation()+ "")).append("\t").append(AnswerMap.gerUserProfession().get(user.getProfession()+ "")).append("\t")
			.append(user.getPhone()).append("\t").append(user.getEmail()).append("\t").append(AnswerMap.getUserArea().get(user.getArea() + "")).append("\t")
			.append(answerFormater(qa.getProblem1(), 4, false))
			.append(answerFormater(qa.getProblem2(), 10, true))
			.append(answerFormater(qa.getProblem3(), 3, false))
			.append(answerFormater(qa.getProblem4(), 2, false))
			.append(answerFormater(qa.getProblem5(), 9, true))
			.append(answerFormater(qa.getProblem6(), 12, true))
			.append(answerFormater(qa.getProblem7(), 3, false))
			.append(answerFormater(qa.getProblem8(), 6, true))
			.append(answerFormater(qa.getProblem9(), 10, true))
			.append(answerFormater(qa.getProblem10(), 13, true))
			.append("\n");
		}
		setUqaList(uqaList);
		writer.write(builder.toString());
		writer.flush();
		writer.close();
		
		return Constants.LIST;
	}

	
	private String answerFormater(String answer, int answerNumber, boolean hasOther) {
		String[] answerArr = answer.split("[" + SEPARATOR + "]");
		StringBuilder builder = new StringBuilder();
		builder.append(answer).append("\t");
		for(int i = 1; i <= answerNumber; i++) {
			String choosenAnswer = "";
			for(String a : answerArr) {
				int answerOrder = -1;
				try {
					answerOrder = Integer.valueOf(a);
				} catch (NumberFormatException e) {
					logger.error(e.getMessage(), e);
				}
				if(answerOrder == i) {
					choosenAnswer = a;
				} else if(i == answerNumber && answerOrder == -1 && hasOther) {
					choosenAnswer = a;
				}
			}
			builder.append(choosenAnswer).append("\t");
		}
		
		return builder.toString();
	}

	public String save() throws Exception {
		try {
			
			Cookie[] cookies = ServletActionContext.getRequest().getCookies();
			String submitFlag = "";
			if(cookies!=null){
				for(int i=0;i<cookies.length;i++){
					if(cookies[i].getName().equals("survey_repeat_check")){
						submitFlag = cookies[i].getValue();
						break;
					} 
				}
			}
			
			if("1".equals(submitFlag)) {
				return "repeat";
			}
			
			User user = new User();
			user.setName(getName());
			user.setAge(getAge());
			user.setArea(getArea());
			user.setEducation(getEducation());
			user.setEmail(getEmail());
			user.setPhone(getPhone());
			user.setSex(getSex());
			user.setProfession(getProfession());
			user.save();
			
			QuestionAnswer qa = new QuestionAnswer();
			qa.setProblem1(arrayToString(getProblem1()));
			qa.setProblem2(arrayToString(getProblem2()));
			qa.setProblem3(arrayToString(getProblem3()));
			qa.setProblem4(arrayToString(getProblem4()));
			qa.setProblem5(arrayToString(getProblem5()));
			qa.setProblem6(arrayToString(getProblem6()));
			qa.setProblem7(arrayToString(getProblem7()));
			qa.setProblem8(arrayToString(getProblem8()));
			qa.setProblem9(arrayToString(getProblem9()));
			qa.setProblem10(arrayToString(getProblem10()));
			qa.setFkUserId(user.getId());
			qa.save();
			
			HttpServletRequest requst = ServletActionContext.getRequest();
			HttpServletResponse response = ServletActionContext.getResponse();
			Cookie repeatcookie = new Cookie("survey_repeat_check","1");
			repeatcookie.setPath("/");// very important		
			repeatcookie.setMaxAge(5*60);
			repeatcookie.setDomain("soku.com");
			response.addCookie(repeatcookie);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return SUCCESS;

	}
	
	public String thanks() throws Exception {
		return "thanks";
	}
	
	private String arrayToString(String[] arrayStr) {
		StringBuilder builder = new StringBuilder();
		if(arrayStr != null) {
			for(String s : arrayStr) {
				if(builder.length() > 0) {
					builder.append(SEPARATOR);
				}
				String filterAnswer = s.replace(SEPARATOR, "");
				builder.append(filterAnswer);
			}
		}
		
		return builder.toString();
	}

	private String[] problem1;
	
	private String[] problem2;
	
	private String[] problem3;
	
	private String[] problem4;
	
	private String[] problem5;
	
	private String[] problem6;
	
	private String[] problem7;
	
	private String[] problem8;

	private String[] problem9;
	
	private String[] problem10;
	
	private String name;
	
	private int sex;
	
	private int age;
	
	private int education;
	
	private int area;
	
	private int profession;
	
	private String phone;
	
	private String email;
	
	private List<UserQuestionAnswerBo> uqaList;
	
	
	public List<UserQuestionAnswerBo> getUqaList() {
		return uqaList;
	}

	public void setUqaList(List<UserQuestionAnswerBo> uqaList) {
		this.uqaList = uqaList;
	}

	public String[] getProblem1() {
		return problem1;
	}

	public void setProblem1(String[] problem1) {
		this.problem1 = problem1;
	}

	public String[] getProblem2() {
		return problem2;
	}

	public void setProblem2(String[] problem2) {
		this.problem2 = problem2;
	}

	public String[] getProblem3() {
		return problem3;
	}

	public void setProblem3(String[] problem3) {
		this.problem3 = problem3;
	}

	public String[] getProblem4() {
		return problem4;
	}

	public void setProblem4(String[] problem4) {
		this.problem4 = problem4;
	}

	public String[] getProblem5() {
		return problem5;
	}

	public void setProblem5(String[] problem5) {
		this.problem5 = problem5;
	}

	public String[] getProblem6() {
		return problem6;
	}

	public void setProblem6(String[] problem6) {
		this.problem6 = problem6;
	}

	public String[] getProblem7() {
		return problem7;
	}

	public void setProblem7(String[] problem7) {
		this.problem7 = problem7;
	}

	public String[] getProblem8() {
		return problem8;
	}

	public void setProblem8(String[] problem8) {
		this.problem8 = problem8;
	}

	public String[] getProblem9() {
		return problem9;
	}

	public void setProblem9(String[] problem9) {
		this.problem9 = problem9;
	}

	public String[] getProblem10() {
		return problem10;
	}

	public void setProblem10(String[] problem10) {
		this.problem10 = problem10;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getEducation() {
		return education;
	}

	public void setEducation(int education) {
		this.education = education;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public int getProfession() {
		return profession;
	}

	public void setProfession(int profession) {
		this.profession = profession;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
