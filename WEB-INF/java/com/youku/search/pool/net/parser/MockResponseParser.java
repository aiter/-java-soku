package com.youku.search.pool.net.parser;

import org.json.JSONException;

import com.youku.search.console.util.MD5;
import com.youku.search.index.entity.Result;
import com.youku.search.index.entity.Video;
import com.youku.search.pool.net.ResultHolder;
import com.youku.search.util.StringUtil;

public class MockResponseParser extends ResponseParser<Video> {

	@Override
	public ResultHolder<Video> parseResponse(String responseBody)
			throws Exception {
		ResultHolder<Video> resultHolder = new ResultHolder<Video>();
		resultHolder.result = parse(responseBody);

		return resultHolder;
	}

	@Override
	public void clear() {
	}

	private Result<Video> parse(String responseBody) throws JSONException {
		Result r = new Result();
		r.totalCount = 1;
		r.results = null;
		r.hasNext = false;

		return r;
	}

	public static String mockResponseStr(){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"workstate\":\"0000000000101111\",");
		sb.append("\"usetime\":0,");
		sb.append("\"servertype\":\"0000000000101001\",");
		sb.append("\"pageno\":1,");
		sb.append("\"request\":\"张杰演唱会2010演唱会全集\",");
		sb.append("\"cutstr\":\"张杰 演唱会 2010 演唱会 全集[张杰 ]\",");
		sb.append("\"lastnum\":48,");
		sb.append("\"prenum\":2466,");
		sb.append("\"result\": {");
		sb.append("\"docid\":\"36163735	51846015	39330655	35634395	35635885	35634075	36215220	40339185	35770015	55575960	50388380	43820965	42282825	15829860	39771220	53057035	46493780	51957640	49480060	45528045	39724805	35681615	45398380	36562050	49479030	39131095	41264735	39784730	49088250	34948575	34950735	37131460	37132460	49505255	50988375	45139400	40748740	45850645	46045905	44639025	47938075	35668565	45367315	36693930	35690880	44424020	36401220	51095535	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	\",");
		sb.append("\"pageid\":\"3156896	628090	2550513	3083399	3083591	3083361	3163625	2162887	3102153	264899	943520	2122983	1907925	5877292	2614123	791098	1434808	643556	1340016	1826705	2607394	3089956	1808605	3210983	1339871	2521127	2296124	2616072	1280598	2990884	2991140	2765716	2765854	1343866	1030414	1771919	2221531	1872956	1370452	1701595	1112308	3088067	1804255	2703187	3091308	1670582	3188995	1045046	0	0	\",");
		sb.append("\"power\":\"0.872214	4.153725	0.342830	1.311851	0.342677	1.180946	0.000000	1.002224	0.589349	1.062964	0.981653	0.440275	0.439410	0.561374	1.014273	1.701652	1.000220	0.979429	0.380345	0.840271	0.377121	0.186555	0.722233	0.935423	0.362177	1.240499	0.172319	0.171808	0.645679	0.790602	0.735960	0.332027	0.332027	0.921389	0.325842	0.829648	0.713708	0.321988	0.417366	0.753776	0.753776	0.202902	0.309168	0.857391	0.590322	0.302798	0.302483	0.301067	0	0	\",");
		
		// md5现在还没有，先用docid来Mock
		sb.append("\"finger\":\"36163735	51846015	39330655	35634395	35635885	35634075	36215220	40339185	35770015	55575960	50388380	43820965	42282825	15829860	39771220	53057035	46493780	51957640	49480060	45528045	39724805	35681615	45398380	36562050	49479030	39131095	41264735	39784730	49088250	34948575	34950735	37131460	37132460	49505255	50988375	45139400	40748740	45850645	46045905	44639025	47938075	35668565	45367315	36693930	35690880	44424020	36401220	51095535	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	\",");
		sb.append("}");
		sb.append("}");
		
		return sb.toString();
	}
	
	public static String[] mockMD5Array(String[] array){
		if (null == array || array.length == 0) {
			return new String[0];
		}
		
		String[] md5Array = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			md5Array[i] = MD5.hash(array[i]);
		}
		
		return md5Array;
	}

}
