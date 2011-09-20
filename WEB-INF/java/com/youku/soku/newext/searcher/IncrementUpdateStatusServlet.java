package com.youku.soku.newext.searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IncrementUpdateStatusServlet extends HttpServlet {

	private static Log logger = LogFactory.getLog(TestServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String result = "works well";
		try {
			File file = new File("/opt/update_error");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = reader.readLine()) != null) {
				result = line;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
}
