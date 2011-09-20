package com.youku.search.monitor.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.console.util.Wget;
import com.youku.search.monitor.Result;

public class RecomendMonitor extends CMonitor {
	protected static Log logger = LogFactory.getLog(RecomendMonitor.class);

	protected final String[] addresses = new String[] { "http://10.103.8.219/",
			"http://10.103.8.220/" };

	protected static enum types {
		video, playlist, user, bar
	}

	private final String[] test_keys = new String[] { "h", "x" };

	@Override
	public Result check() {
		Result rs = null;
		for (String addr : addresses) {
			for (String test_key : test_keys) {
				rs = checkSingle(new StringBuffer(addr)
						.append("search_keys?k=").append(test_key).append(
								"&type=").append(types.video.name()).toString());
				if (null != rs)
					return rs;
				rs = checkSingle(new StringBuffer(addr)
						.append("search_keys?k=").append(test_key).append(
								"&type=").append(types.playlist.name())
						.toString());
				if (null != rs)
					return rs;
				rs = checkSingle(new StringBuffer(addr)
						.append("search_keys?k=").append(test_key).append(
								"&type=").append(types.user.name()).toString());
				if (null != rs)
					return rs;
				rs = checkSingle(new StringBuffer(addr)
						.append("search_keys?k=").append(test_key).append(
								"&type=").append(types.bar.name()).toString());
				if (null != rs)
					return rs;
			}
		}
		if (null != rs)
			return rs;
		else
			return new Result();
	}

	private Result checkSingle(String url) {
		byte[] bytes = null;
		Result rs = new Result();
		try {
			bytes = Wget.get(url);
			if (null == bytes) {
				logger.error("null returned. url:" + url);
				rs.setOk(false);
				rs.setMessage("null returned. url:" + url);
				return rs;
			}
			String res = new String(bytes, "utf-8");
			String subres = StringUtils.substringBetween(res, "showresult('(",
					")',false)");
			if (StringUtils.isBlank(subres)) {
				logger.error("null returned. url:" + url + ",response:" + res);
				rs.setOk(false);
				rs.setMessage("null returned. url:" + url + ",response:" + res);
				return rs;
			}
			// subres = "'"+subres+"'";
			// JSONObject json = new JSONObject(new
			// JSONTokener(subres).nextValue().toString());
			JSONObject json = new JSONObject(subres);
			if (null == json || json == JSONObject.NULL) {
				logger.error("null json returned. url:" + url + ",response:"
						+ res);
				rs.setOk(false);
				rs.setMessage("null json returned. url:" + url + ",response:"
						+ res);
				return rs;
			}
			JSONArray jsonarr = json.optJSONArray("result");
			if (null == jsonarr || 0 == jsonarr.length()) {
				logger.error("null result returned. url:" + url + ",response:"
						+ res);
				rs.setOk(false);
				rs.setMessage("null result returned. url:" + url + ",response:"
						+ res);
				return rs;
			}
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rs.setOk(false);
			rs.setMessage(e.getMessage());
			rs.setException(e);
			return rs;
		}
	}
}
