package com.youku.soku.manage.questionnaire.bo;

import com.youku.soku.manage.questionnaire.orm.QuestionAnswer;
import com.youku.soku.manage.questionnaire.orm.User;

public class UserQuestionAnswerBo {
	
	private QuestionAnswer qa;
	
	private User user;

	public QuestionAnswer getQa() {
		return qa;
	}

	public void setQa(QuestionAnswer qa) {
		this.qa = qa;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	

}
