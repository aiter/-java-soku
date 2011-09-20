/**
 * 
 */
package com.youku.soku.servlet;


import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.torque.TorqueException;

import com.youku.soku.manage.torque.ProtocolSite;

/**
 * @author 1verge
 *
 */
public class AddVideoMap extends HttpServlet {
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException
	{
		String site = request.getParameter("site");
		String url = request.getParameter("url");
		String description = request.getParameter("description");
		
		if (description != null && description.trim().length()>0)
		{
			ProtocolSite p = new ProtocolSite();
			p.setName(site);
			p.setUrl(url);
			p.setRemark(description);
			p.setCreatetime(new Date());
			try {
				com.youku.soku.manage.service.ProtocolSiteService.saveProtocolSite(p);
			} catch (TorqueException e) {
				e.printStackTrace();
			}
		}
		response.sendRedirect("/service/protocol.html");
		return ;
	}
}
