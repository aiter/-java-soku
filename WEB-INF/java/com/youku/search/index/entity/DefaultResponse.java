package com.youku.search.index.entity;

import org.json.JSONException;

import com.youku.search.pool.net.parser.ResponseParser.ParserException;
import com.youku.search.util.StringUtil;

/**
 * C-Server返回的Response里的一条Doc信息
 * 
 * @author gaosong
 * 
 */
public class DefaultResponse {
	
	public static final int MIN_SCORE15 = 2784;
	
	public static enum TYPE_ENUM {
		NEW, HOT, CLASSICAL, OTHER,
	}
	
	public TYPE_ENUM type;
	
	public String docID;
	
	public float score;

	public String md5;
	
	public int category;
	
	public long createdTime;

	public boolean isBlur;

	public float matchDegree;

	public float score15Days;

	public boolean isUpdateServer;
	
	public DefaultResponse(String docID, float score, String finger, int typeInt,
			int category, long createdTime, int isBlurInt, float matchDegree,
			float score15Days, boolean isUpdateServer, String cutStr) throws JSONException, ParserException {
		this.docID = docID;
		this.score = score;
		this.md5 = StringUtil.convertFingerToMD5(finger);
		
		if (isUpdateServer) {
			typeInt = 1;
		}
		
		this.category = category;
		this.createdTime = createdTime;
		this.matchDegree = matchDegree;
		this.score15Days = score15Days;
		this.isUpdateServer = isUpdateServer;
		
		baseFieldValid(typeInt, cutStr);
		if (!isUpdateServer) {
			newFieldValid(isBlurInt);
		}
	}
	
	private void baseFieldValid(int typeInt, String cutStr) throws JSONException {
		if (docID.length() == 0) {
			throw new JSONException("docID为空！ cutStr=" + cutStr);
		}

		if (md5.length() == 0) {
			throw new JSONException("finger为空！docID=" + docID);
		}
		
		switch (typeInt) {
		case 1:
			this.type = TYPE_ENUM.NEW;
			break;
		case 2:
			this.type = TYPE_ENUM.HOT;
			break;
		case 3:
			this.type = TYPE_ENUM.CLASSICAL;
			break;
		case 0:
			this.type = TYPE_ENUM.OTHER;
			break;
		default:
			throw new JSONException("type=" + typeInt + "为非法值！ docID=" + docID);
		}
	}

	private void newFieldValid(int isBlurInt) throws JSONException {
		if (createdTime <= 0L) {
			throw new JSONException("created_time=" + createdTime
					+ "为非法值！docID=" + docID);
		}

		if (isBlurInt == 1) {
			isBlur = true;
		} else if (isBlurInt == 2) {
			isBlur = false;
		} else {
			throw new JSONException("is_blur=" + isBlurInt + "为非法值！ docID="
					+ docID);
		}
		
		// TAG检索会返回0，所以这里先暂时去掉
//		if (score15Days <= 0L) {
//			throw new JSONException("power15=" + score15Days + "为非法值！docID="
//					+ docID);
//		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('\n').append('\t');
		sb.append(" docID=").append(docID).append(',');
		sb.append(" score=").append(score).append(',');
		sb.append(" md5=").append(md5).append(',');
		sb.append(" type=").append(type.name()).append(',');
		sb.append(" category=").append(category).append(',');
		sb.append(" createdTime=").append(createdTime).append(',');
		sb.append(" isBlur=").append(isBlur).append(',');
		sb.append(" matchDegree=").append(matchDegree).append(',');
		sb.append(" score15Days=").append(score15Days);
		return sb.toString();
	}

}
