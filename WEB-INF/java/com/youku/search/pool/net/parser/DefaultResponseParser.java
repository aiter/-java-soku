package com.youku.search.pool.net.parser;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.index.entity.DefaultResponse;
import com.youku.search.index.entity.Result;
import com.youku.search.pool.net.ResultHolder;
import com.youku.search.util.Constant;

public class DefaultResponseParser extends ResponseParser<DefaultResponse> {

	private static final Log logger = LogFactory.getLog(DefaultResponseParser.class);

	@Override
	public ResultHolder<DefaultResponse> parseResponse(String responseBody)
			throws Exception {

		ResultHolder<DefaultResponse> resultHolder = new ResultHolder<DefaultResponse>();
		try {
			resultHolder.result = innerParse(responseBody);
		} catch (Exception e) {
			throw new ParserException("------------- Response解析错误，ResponseBody=\n"
					+ responseBody, e);
		}

		return resultHolder;
	}
	
	private Result<DefaultResponse> innerParse(String responseBody)
			throws Exception {
		JSONObject jsonResponse = new JSONObject(responseBody);
		
		int timecost = jsonResponse.getInt("usetime"); // C-Server检索耗时
		String cutStr = jsonResponse.getString("cutstr"); // 切词结果
		if (null == cutStr) {
			throw new JSONException("cutStr为空, jsonResponse=" + jsonResponse.toString(4));
		}
		
		int totalCount = jsonResponse.getInt("lastnum"); // 结果数量
		int preNum = jsonResponse.getInt("prenum");	// 预估总数
		
		JSONArray resultArray = jsonResponse.getJSONArray("results");
		if (null == resultArray || resultArray.length() != totalCount) {
			throw new JSONException("results为空或者results数组个数与lastnum不符");
		}
		
		Result<DefaultResponse> result = new Result<DefaultResponse>();
		result.timecost = (int) timecost;
		result.totalCount = preNum;
		result.results = new ArrayList<DefaultResponse>(totalCount);
		result.extra = cutStr;
		result.hasNext = ((totalCount < Constant.Socket.INDEX_PAGE_SIZE) ? false : true);
		
		JSONObject jsonResult;
		DefaultResponse response = null;
		int[] statCount = new int[]{0,0,0,0,0,0,0,0};
		for (int i = 0; i < resultArray.length(); i++) {
			jsonResult = (JSONObject)resultArray.get(i);
			
			// 老字段
			String docID = jsonResult.getString("docid").trim();
			float score =  Float.parseFloat(jsonResult.getString("power").trim());
			String finger = jsonResult.getString("finger").trim();
			
			// 新字段
			int type = jsonResult.getInt("type");
			int category = jsonResult.getInt("cate_id");
			long createdTime = Long.parseLong(jsonResult.getString("created_time").trim());
			int isBlurInt = jsonResult.getInt("is_blur");
			float matchDegree = Float.parseFloat(jsonResult.getString("match_degree").trim());
			float score15Days = jsonResult.getInt("power15");
			
			if (createdTime == 0L && score15Days == 0) {
				response = new DefaultResponse(docID, score, finger, type, category, createdTime, isBlurInt, matchDegree, score15Days, true, cutStr);
			} else {
				response = new DefaultResponse(docID, score, finger, type, category, createdTime, isBlurInt, matchDegree, score15Days, false, cutStr);
			}
			result.results.add(response);
			
			if (logger.isDebugEnabled()) {
				// 0-最新精确个数、1-最新总个数；2-最热精确个数、3-最热总个数；4-经典精确个数、5-经典总个数；6-type=0&&power15>2784个数、7-type=0总个数
				switch (response.type) {
				case NEW:
					if (!response.isBlur) {
						statCount[0]++;
					}
					statCount[1]++;
					break;
				case HOT:
					if (!response.isBlur) {
						statCount[2]++;
					}
					statCount[3]++;
					break;
				case CLASSICAL:
					if (!response.isBlur) {
						statCount[4]++;
					}
					statCount[5]++;
					break;
				case OTHER:
					if (response.score15Days > 2784) {
						statCount[6]++;
					}
					statCount[7]++;
					break;
				default:
					break;
				}
			}
		}
		
		if (result.results.size() != totalCount) {
			// lastnum
			throw new JSONException("lastnum个数与results数组个数不相等！lastnum="+totalCount+", results.size()="+result.results.size()+", cutStr=" + cutStr);
		}
		
		result.statCount = statCount;
		
//		JSONObject jsonResult = jsonResponse.getJSONObject("result");
//		String[] docIDArray = filterArray(
//				jsonResult.getString("docid").split("\t"), totalCount,
//				String.class);
//		Float[] scoreArray = filterArray(
//				jsonResult.getString("power").split("\t"), totalCount,
//				Float.TYPE);
//		String[] md5Array = filterArray(
//				jsonResult.getString("finger").split("\t"), totalCount,
//				String.class);
//		md5Array = filterMD5(md5Array);	//加上-反而不好比较其它Log，所以注释本行
//		String[] md5Array = MockResponseParser.mockMD5Array(docIDArray);
		
//		DefaultResponse response = null;
//		for (int i = 0; i < totalCount; i++) {
//			response = new DefaultResponse(docIDArray[i], scoreArray[i],
//					md5Array[i]);
//			result.results.set(i, response);
//		}
		
		return result;
	}
	
	/**
	 * 过滤jsonArray，返回前count个。 <br>
	 * 如果不足count个则抛出ParserException（C-Server的Response出错） <br>
	 * 如果大于count个则只返回前count个 <br>
	 * 
	 * @param array
	 * @param count
	 * @param type
	 *            Integer.TYPE 或 Float.TYPE 或String.class
	 * @return
	 * @throws ParserException
	 */
	private <T> T[] filterArray(String[] array, int count, Class<T> type)
			throws ParserException {
		if (array.length < count) {
			throw new ParserException("传入的数组个数=" + array.length + "，期望个数="
					+ count);
		}

		T[] result;
		if (type == Integer.TYPE) {
			Integer[] newArray = new Integer[count];
			for (int i = 0; i < count; i++) {
				newArray[i] = Integer.parseInt(array[i].trim());
				if (newArray[i] <= 0) {
					throw new ParserException("传入的数组[" + i + "]=" + newArray[i]);
				}
			}
			result = (T[]) newArray;
		} else if (type == Float.TYPE) {
			Float[] newArray = new Float[count];
			for (int i = 0; i < count; i++) {
				newArray[i] = Float.parseFloat(array[i].trim());
			}
			result = (T[]) newArray;
		} else if (type == String.class) {
			String[] newArray = new String[count];
			for (int i = 0; i < count; i++) {
				newArray[i] = array[i].trim();
				if (newArray[i].length() == 0) {
					throw new ParserException("传入的数组[" + i + "]没有值");
				}
			}
			result = (T[]) newArray;
		} else {
			throw new ParserException("不支持 " + type.getSimpleName()
					+ " 类型的数组转换");
		}

		return result;
	}

	@Override
	public void clear() {
	}

	public static void main(String[] args) {
		DefaultResponseParser parser = new DefaultResponseParser();
		try {
			ResultHolder<DefaultResponse> r = parser
					.parseResponse(MockResponseParser.mockResponseStr());
			System.out.println(r.result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
