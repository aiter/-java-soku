package com.youku.search.sort.servlet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.config.Config;
import com.youku.search.sort.util.bridge.BridgeMap;
import com.youku.search.util.StringUtil;

public class ChangeConfigServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    Log logger = LogFactory.getLog(getClass());

    @Override
    protected synchronized void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        ChangeParam param = parseParam(request);
        int group = Config.getGroupNumber();

        InetSocketAddress[] oldAddr = new InetSocketAddress[0];
        InetSocketAddress[] newAddr = new InetSocketAddress[0];

        if (group == param.group) {

            oldAddr = Config.getVideoIndexSocket();

            Config.configChanged(param);
            BridgeMap.configChanged(param);

            newAddr = Config.getVideoIndexSocket();
        }

        logger.info("old addr: " + Arrays.toString(oldAddr));
        logger.info("new addr: " + Arrays.toString(newAddr));

        //
        StringBuilder builder = new StringBuilder();
        builder.append("<p>current group: ").append(group).append("</p>");
        builder.append("<p>group param: ").append(param.group).append("</p>");
        builder.append("<p>change param: ").append(param).append("</p>");
        builder.append("<p>old addr: ").append(Arrays.toString(oldAddr))
                .append("</p>");
        builder.append("<p>new addr: ").append(Arrays.toString(newAddr))
                .append("</p>");

        response.getWriter().print(builder);
    }

    private ChangeParam parseParam(HttpServletRequest request) {

        ChangeParam param = new ChangeParam();

        String group_param = request.getParameter("group");
        String back_param = request.getParameter("back");

        int group = StringUtil.parseInt(group_param, ChangeParam.GROUP_NULL);
        int back = StringUtil.parseInt(back_param, ChangeParam.BACK_FALSE);

        param.group = group;
        param.back = back == ChangeParam.BACK_TRUE;

        return param;
    }

}
