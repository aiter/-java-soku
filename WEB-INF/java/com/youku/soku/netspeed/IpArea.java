package com.youku.soku.netspeed;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class IpArea {

	private HashMap<String, HashMap<Integer, HashMap<Integer, Region>>> segMap = new HashMap<String, HashMap<Integer, HashMap<Integer, Region>>>();
	private static IpArea self;
	public synchronized static void init(String file){
		if(self == null){
			self = new IpArea(file);
		}
	}
	
	public static IpArea getInstance(){
		return self;
	}
	
	private IpArea(String ipdata) {
		loadIP(ipdata);
	}


	private void loadIP(String ipdata) {
		InputStream is =  null;
		BufferedReader br = null;
		try {
			FileInputStream fis = new FileInputStream(ipdata);
			br=new BufferedReader(new InputStreamReader(fis));

			loadIP(br);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("ipdata file:[" + ipdata
					+ "] not found");
		}finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void loadIP(Reader reader) {
		try {
			List<Region> regionList = new ArrayList<Region>();
			BufferedReader br = null;
			br = new BufferedReader(reader);
			String line = null;
			while ((line = br.readLine()) != null) {
				StringTokenizer itr = new StringTokenizer(line, "/,");
				if (itr.countTokens() == 4) {
					String f_ip = itr.nextToken();
					String mask = itr.nextToken();
					String netseg = itr.nextToken();
					String city_id = itr.nextToken();
					Region region = new Region(city_id,city_id.substring(0,2),f_ip,Integer.parseInt(mask),netseg);
					regionList.add(region);
				}
			}

			for (Region region : regionList) {
				HashMap<Integer, HashMap<Integer, Region>> maskMap = null;
				String[] ipsegs = region.getF_ip().split("\\.");
				String topSeg = ipsegs[0];
				if (segMap.containsKey(topSeg)) {
					maskMap = segMap.get(topSeg);
				} else {
					maskMap = new HashMap<Integer, HashMap<Integer, Region>>();
				}
				HashMap<Integer, Region> ipMap = null;
				if (maskMap.containsKey(region.getMask())) {
					ipMap = maskMap.get(region.getMask());
				} else {
					ipMap = new HashMap<Integer, Region>();
				}
				ipMap.put(ipToInt(ipsegs), region);
				maskMap.put(region.getMask(), ipMap);
				segMap.put(topSeg, maskMap);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Region getRegion(String ip) {
		String[] strs = ip.split("\\.");
		if (strs.length != 4) {
			return null;
		}
		int ipInt = this.ipToInt(strs);
		HashMap<Integer, HashMap<Integer, Region>> maskMap = segMap
				.get(strs[0]);
		if (maskMap != null) {
			for (int mask : maskMap.keySet()) {
				int net = (ipInt >> (32 - mask)) << (32 - mask);
				HashMap<Integer, Region> ipMap = maskMap.get(mask);

				if (ipMap.containsKey(net)) {
					return ipMap.get(net);
				}
			}
		}
		return null;
	}

	private int ipToInt(String[] strs) {
		int ipInt = (parseInt(strs[0]) << 24) | (parseInt(strs[1]) << 16)
				| (parseInt(strs[2]) << 8) | (parseInt(strs[3]));
		return ipInt;
	}

	public int parseInt(Object strData) {
		if (strData == null || strData.toString().length() == 0) {
			return 0;
		} else {
			try {
				return Integer.parseInt(strData.toString());
			} catch (Exception e) {
				return 0;
			}
		}
	}

	public static class Region {
		private static final String[] ZHI_XiA_SHI_IDS = { "11", "31", "12",
				"50" };
		private static final int SUBURB_CODE_POS_ZXS = 3;
		private static final int SUBURB_CODE_POS_OTHER = 4;
		private static final String EMPTY_STR = "";


		public Region(String city_id, String province_id, String f_ip,
				int mask, String netSegCode) {
			setCity_id(city_id);
			setF_ip(f_ip);
			setMask(mask);
			setNetSegCode(netSegCode);
		}

		private String city_id;
		private String province_id;
		private int mask;
		private String f_ip;
		private char suburb;
		private String netSegCode;

		public String getCity_id() {
			return city_id;
		}

		/**
		 * 得到所属省份的代码，例如：11代表北京，37代表山东
		 * 
		 * @return
		 */
		public String getProvince_id() {
			return province_id;
		}

		public int getMask() {
			return mask;
		}

		public String getF_ip() {
			return f_ip;
		}

		/**
		 * 获取是否是市区还是郊区
		 * 
		 * @return 0：未知，1：市区，2：郊区
		 */
		public char getSuburb() {
			return suburb;
		}

		/**
		 * @return 得到所属网段的编码，据此可以计算教育网等网段
		 */
		public String getNetSegCode() {
			return netSegCode;
		}

		public boolean isZhiXiaShi() {
			for (String zxs : ZHI_XiA_SHI_IDS) {
				if (zxs.equals(this.province_id))
					return true;
			}
			return false;
		}

		public void setCity_id(String id) {
			String provinceId = id.substring(0, 2);
			this.setProvince_id(provinceId);
			if (this.isZhiXiaShi()) {
				this.suburb = id.charAt(SUBURB_CODE_POS_ZXS);
				char[] s = id.toCharArray();
				s[SUBURB_CODE_POS_ZXS] = '0';
				this.city_id = String.copyValueOf(s);
			} else {
				this.suburb = id.charAt(SUBURB_CODE_POS_OTHER);
				char[] s = id.toCharArray();
				s[SUBURB_CODE_POS_OTHER] = '0';
				this.city_id = String.copyValueOf(s);
			}
		}

		public void setProvince_id(String id) {
			this.province_id = id;
		}

		public void setMask(int mask2) {
			this.mask = mask2;
		}

		public void setF_ip(String f_ip2) {
			this.f_ip = f_ip2;
		}

		public void setNetSegCode(String netSegCode) {
			this.netSegCode = netSegCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Region) {
				Region r = (Region) obj;
				return r.city_id.equals(this.city_id)
						&& r.f_ip.equals(this.f_ip) && r.mask == this.mask;
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			char sep = ';';
			sb.append("province:").append(this.province_id).append(sep);
			sb.append("city:").append(this.city_id).append(sep);
			sb.append("ip:").append(this.f_ip).append(sep);
			sb.append("mask:").append(this.mask).append(sep);
			sb.append("suburb:").append(this.suburb).append(sep);
			sb.append("ZhiXiaShi:").append(this.isZhiXiaShi());
			sb.append("segmentCode:").append(this.getNetSegCode());

			return sb.toString();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		String testData = "110100\t11,47.153.128.0,18\n"
//				+ "110100,11,47.154.0.0,16\n" + "520110,52,58.16.0.0,20\n"
//				+ "520110,52,58.16.16.0,21\n" + "520110,52,58.16.24.0,23\n"
//				+ "520110,52,58.16.26.0,24\n" + "520310,52,58.16.27.0,24\n"
//				+ "520110,52,58.16.28.0,22\n" + "520110,52,58.16.32.0,19\n"
//				+ "520110,52,58.16.64.0,19\n" + "520110,52,58.16.96.0,20\n"
//				+ "522720,52,58.16.112.0,20\n" + "520110,52,58.16.128.0,20\n"
//				+ "520110,52,58.16.144.0,22\n" + "520110,52,58.16.148.0,24";
//		StringReader sr = new StringReader(testData);

		IpArea.init("E:/work/youku/search/src/WEB-INF/java/com/youku/soku/netspeed/resource/ip.csv");
		IpArea ia = IpArea.getInstance();
		System.out.println(ia.getRegion("47.153.128.1"));
		System.out.println(ia.getRegion("58.16.0.1"));
		System.out.println(ia.getRegion("58.16.112.1"));

	}

}
