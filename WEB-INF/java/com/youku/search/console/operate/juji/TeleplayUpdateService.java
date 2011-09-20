package com.youku.search.console.operate.juji;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.Criteria;

import com.youku.search.console.pojo.PlayNamePeer;
import com.youku.search.console.vo.TeleUpdateVO;
import com.youku.soku.library.Utils;

public class TeleplayUpdateService {

	static Log logger = LogFactory.getLog(TeleplayUpdateService.class);

	private TeleplayUpdateService() {
	}

	private static TeleplayUpdateService instance = null;

	public static synchronized TeleplayUpdateService getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new TeleplayUpdateService();
			return instance;
		}
	}
	
	public void playNameDelete(int tid, int mainflag){
		Criteria criteria = new Criteria();
		criteria.add(PlayNamePeer.FK_TELEPLAY_ID, tid);
		if (mainflag == 1 || mainflag == 0)
			criteria.add(PlayNamePeer.IS_MAIN, mainflag);
		try {
			PlayNamePeer.doDelete(criteria);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public boolean[] updateTeleplay(TeleUpdateVO tuvo){
		if(StringUtils.isBlank(tuvo.getKeyword()))
			return new boolean[] { false, false };
		if (PlayNameMgt.nameIsOtherExist(tuvo.getKeyword(), tuvo.getTc().getSubcate(), tuvo.getPid()))
			return new boolean[] { false, false };
			//删除别名
			playNameDelete(tuvo.getTid(), 0);
		if(!StringUtils.isBlank(tuvo.getAliasStr())){
			PlayNameMgt.getInstance().aliasSave(tuvo.getTid(),Utils.parseStr2Set(tuvo.getAliasStr(), "\\|"),tuvo.getTc().getSubcate());
		}
		TeleplayMgt.getInstance().updateTeleplay(tuvo);
		if (tuvo.getTc().getSubcate() == 2078){
			if(!StringUtils.isBlank(tuvo.getExclude()))
				ExcludeMgt.getInstance().updateExclude(tuvo.getTid(),tuvo.getExclude());
		}
		PlayNameMgt.getInstance().executeSql(
				"update play_name set name='" + tuvo.getKeyword()
						+ "' where id=" + tuvo.getPid());
		return new boolean[] { true, true };
	}
	
//	public boolean[] updateTeleplay(TeleUpdateVO tuvo) throws SQLException,
//			TorqueException {
//
//		if (tuvo.getKeyword() == null || tuvo.getKeyword().trim().length() < 1)
//			return new boolean[] { false, false };
//
//		if (PlayNameMgt.nameIsOtherExist(tuvo.getKeyword(), tuvo.getTc()
//				.getSubcate(), tuvo.getPid()))
//			return new boolean[] { false, false };
//
//		if (tuvo.getAliasStr() == null || tuvo.getAliasStr().trim() == ""
//				|| tuvo.getAliasStr().trim().length() < 1) {
//			playNameDelete(tuvo.getTid(), 0);
//			TeleplayMgt.getInstance().executeSql(
//					"update teleplay set is_valid=" + tuvo.getIs_valid()
//							+ ",version_count=" + tuvo.getVersion_count()
//							+ ",cate=" + tuvo.getTc().getCate() + ",subcate="
//							+ tuvo.getTc().getSubcate() + " where id="
//							+ tuvo.getTid());
//			if (tuvo.getTc().getSubcate() == 2078)
//				ExcludeMgt.getInstance().updateExclude(tuvo.getTid(),
//						tuvo.getExclude());
//			PlayNameMgt.getInstance().executeSql(
//					"update play_name set name='" + tuvo.getKeyword()
//							+ "' where id=" + tuvo.getPid());
//			return new boolean[] { true, true };
//		} else {
//			tuvo.setAliasStr(tuvo.getAliasStr().replace("；", ";"));
//			String[] aliasstr = null;
//			if (tuvo.getAliasStr() != null && tuvo.getAliasStr().trim() != ""
//					&& tuvo.getAliasStr().trim().length() > 0) {
//				aliasstr = tuvo.getAliasStr().split(";");
//				if (null != aliasstr) {
//					if (PlayNameMgt.aliasIsExist(aliasstr, tuvo.getTc()
//							.getSubcate(), tuvo.getTid()))
//						return new boolean[] { false, false };
//				}
//			}
//			playNameDelete(tuvo.getTid(), 0);
//			TeleplayMgt.getInstance().executeSql(
//					"update teleplay set is_valid=" + tuvo.getIs_valid()
//							+ ",version_count=" + tuvo.getVersion_count()
//							+ ",cate=" + tuvo.getTc().getCate() + ",subcate="
//							+ tuvo.getTc().getSubcate() + " where id="
//							+ tuvo.getTid());
//			if (tuvo.getTc().getSubcate() == 2078)
//				ExcludeMgt.getInstance().updateExclude(tuvo.getTid(),
//						tuvo.getExclude());
//			PlayNameMgt.getInstance().executeSql(
//					"update play_name set name='" + tuvo.getKeyword()
//							+ "' where id=" + tuvo.getPid());
//			if (null != aliasstr) {
//				for (int i = 0; i < aliasstr.length; i++) {
//					if (null == aliasstr[i] || aliasstr[i].trim().length() < 1) {
//						continue;
//					}
//					if (PlayNameMgt.nameIsExist(aliasstr[i], null, tuvo.getTc()
//							.getSubcate()))
//						return new boolean[] { false, false };
//					PlayNameMgt.getInstance().executeSql(
//							"insert play_name values(null," + tuvo.getTid()
//									+ ",'" + aliasstr[i] + "',0)");
//				}
//			}
//		}
//		return new boolean[] { true, true };
//	}
}
