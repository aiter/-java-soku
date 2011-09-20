package com.youku.soku.manage.index;

import java.util.Map;

import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;

public class WelcomeAction extends BaseActionSupport {

	public String execute() throws Exception{
		if (hasErrors()) {
			return ERROR;
		} else {
			
			//Torque.init("conf/torque.properties");
			Map session = getSession();
			if(session.get(Constants.SHIELD_USER_KEY) != null) {
				setShieldUser((Boolean) session.get(Constants.SHIELD_USER_KEY));
			}
			
			return SUCCESS;
		}
	}
	
	private boolean shieldUser;

	public boolean isShieldUser() {
		return shieldUser;
	}

	public void setShieldUser(boolean shieldUser) {
		this.shieldUser = shieldUser;
	}
}
