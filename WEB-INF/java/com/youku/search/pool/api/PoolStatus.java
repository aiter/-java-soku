package com.youku.search.pool.api;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class PoolStatus {

	private TotalStatus total = new TotalStatus();
	private Map<InetSocketAddress, SessionStatus> detail = new HashMap<InetSocketAddress, SessionStatus>();

	public PoolStatus() {
	}

	public PoolStatus(TotalStatus total,
			Map<InetSocketAddress, SessionStatus> detail) {
		this.total = total;
		this.detail = detail;
	}

	public TotalStatus getTotal() {
		return total;
	}

	public void setTotal(TotalStatus total) {
		this.total = total;
	}

	public Map<InetSocketAddress, SessionStatus> getDetail() {
		return detail;
	}

	public void setDetail(Map<InetSocketAddress, SessionStatus> detail) {
		this.detail = detail;
	}

	public String toJsonString() {

		try {
			JSONObject totalObject = total.toJSONObject();

			JSONArray detailObject = new JSONArray();
			for (Iterator<InetSocketAddress> i = detail.keySet().iterator(); i
					.hasNext();) {
				InetSocketAddress address = i.next();
				SessionStatus status = detail.get(address);

				JSONObject o = new JSONObject();
				o.put("ip", address.getAddress().getHostAddress());
				o.put("port", address.getPort());
				o.put("status", status.toJSONObject());

				detailObject.put(o);
			}

			// OK!
			JSONObject statusObject = new JSONObject();
			statusObject.put("total", totalObject);
			statusObject.put("detail", detailObject);

			return statusObject.toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {

		TotalStatus total = new TotalStatus(200, 100, 90);

		Map<InetSocketAddress, SessionStatus> detail = new HashMap<InetSocketAddress, SessionStatus>();
		detail.put(new InetSocketAddress("127.0.0.1", 80), new SessionStatus(
				300, 100, 20));
		detail.put(new InetSocketAddress("127.0.0.2", 80), new SessionStatus(
				300, 100, 20));

		PoolStatus poolStatus = new PoolStatus(total, detail);

		System.out.println(poolStatus.toJsonString());
	}
}

class BaseStatus {
	private int active;
	private int idle;

	public BaseStatus() {
	}

	public BaseStatus(int active, int idle) {
		this.active = active;
		this.idle = idle;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getIdle() {
		return idle;
	}

	public void setIdle(int idle) {
		this.idle = idle;
	}

	public JSONObject toJSONObject() throws Exception {
		JSONObject object = new JSONObject();
		object.put("active", getActive());
		object.put("idle", getIdle());
		return object;
	}
}

class SessionStatus extends BaseStatus {

	private int max_active;

	public SessionStatus() {
	}

	public SessionStatus(int max_active, int active, int idle) {
		super(active, idle);
		this.max_active = max_active;
	}

	public int getMax_active() {
		return max_active;
	}

	public void setMax_active(int max_active) {
		this.max_active = max_active;
	}

	public JSONObject toJSONObject() throws Exception {
		JSONObject object = super.toJSONObject();
		object.put("max_active", getMax_active());
		return object;
	}
}

class TotalStatus extends BaseStatus {

	private int max_total;

	public int getMax_total() {
		return max_total;
	}

	public void setMax_total(int max_total) {
		this.max_total = max_total;
	}

	public TotalStatus() {
	}

	public TotalStatus(int max_total, int active, int idle) {
		super(active, idle);
		this.max_total = max_total;
	}

	public JSONObject toJSONObject() throws Exception {
		JSONObject object = super.toJSONObject();
		object.put("max_total", getMax_total());
		return object;
	}

}