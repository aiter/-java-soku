package com.youku.search.pool.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PoolSatusServlet extends HttpServlet {

    private static final long serialVersionUID = 2830045429266238065L;

    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        response.setCharacterEncoding("utf-8");
        response.setContentType("text/plain;charset=utf-8");

        response.getWriter().write(PoolSatusUtil.buildPoolStatusJsonString());
        response.getWriter().flush();
    }
}