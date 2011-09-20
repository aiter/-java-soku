package com.youku.search.console.config;
import java.util.Iterator;
import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.youku.search.console.vo.LeftMenuVO;

public class AuthorityInterceptor extends AbstractInterceptor{
	protected static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("OPERATELOG");

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		ActionContext ctx = arg0.getInvocationContext();
		Map session = ctx.getSession();
		String userid = (String) session.get("user_id");
		String strac = arg0.getProxy().getActionName();
		if (userid != null) {
			if (strac.equalsIgnoreCase("useredit")
					|| strac.equalsIgnoreCase("userupdate")
					|| strac.equalsIgnoreCase("removeindex")
					|| isPrivilege(session,strac)) {
				return arg0.invoke();
			} else
				return "welcome";
		}
		ctx.put("message", Constants.SESERR);
		return Action.LOGIN;
	}
	
	
	private boolean isPrivilege(Map session,String src){
		if(null!=session.get("urlmap")){
		Map<Integer,LeftMenuVO> urlmap = (Map<Integer,LeftMenuVO>)session.get("urlmap");
		if(urlmap!=null&&urlmap.size()>0){
			Iterator<Integer> is=urlmap.keySet().iterator();
			String[] subis=null;
			while(is.hasNext()){
				subis=urlmap.get(is.next()).getModules().toArray(new String[]{});
				if(null!=subis){
				for(String s:subis){
//					System.out.println(s);
					if(src.startsWith(s))
						return true;
				}
			}
			}
		}
		}
		return false;
	}
}
