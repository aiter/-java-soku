package com.youku.search.console.operate.juji;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;

import com.youku.search.console.config.Constants;
import com.youku.search.console.operate.DataConn;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.PlayName;
import com.youku.search.console.pojo.Teleplay;
import com.youku.search.console.vo.TeleCate;
import com.youku.search.console.vo.TelePage;
import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.Utils;

public class JujiService {

	static Log logger = LogFactory.getLog(JujiService.class);

	private JujiService() {
	}

	private static JujiService instance = null;

	public static synchronized JujiService getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new JujiService();
			return instance;
		}
	}

	public boolean[] saveTeleplay(String name, String alias,
			String excludes, TeleCate tc) throws Exception {
		boolean flag = true;
		boolean flag1 = true;
		Teleplay t = new Teleplay();
		PlayName p = new PlayName();
		int subcate = tc.getSubcate();
		Set<String> aliasSet = null;
		if(!StringUtils.isBlank(alias)){
			aliasSet = Utils.parseStr2Set(alias, "\\|");
		}
		
		//是否存在
		if (PlayNameMgt.nameIsExist(name, aliasSet, subcate))
			return new boolean[] { false, false };

		//save
		TeleplayMgt.getInstance().teleplaySave(t, 1, 0, tc.getCate(), subcate);
		
		Set<String> excludeSet = null;
		
		if (subcate == 2078) {
			if(!StringUtils.isBlank(excludes)){
				excludeSet = Utils.parseStr2Set(excludes, "\\|");
				ExcludeMgt.getInstance().saveExclude(t.getId(), excludes);
			}
		}

		//save名称
		PlayNameMgt.getInstance().playNameSave(p, t.getId(), name, aliasSet,
				subcate);

		Set<String> teleplays = new HashSet<String>();
		teleplays.add(name);
		if (null != aliasSet) {
			teleplays.addAll(aliasSet);
		}

		if (subcate != 2078) {
			//保存非综艺
			try {
				VersionEpisodeService.getInstance().saveAllVersionAndEpisode(
						teleplays, t.getId(), tc);
			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			//保存综艺
			VersionEpisodeService.getInstance().saveAllVersionAndVariety(
					teleplays, excludeSet, t.getId(), tc);
		}
		return new boolean[] { flag, flag1 };
	}

	public boolean deleteTeleplay(int id) throws TorqueException {

		TeleplayMgt.getInstance().deleteById(id);

		ExcludeMgt.getInstance().deleteExclude(id);

		PlayNameMgt.getInstance().deleteByTeleplayId(id);

		BlacklistMgt
				.getInstance()
				.executeSql(
						"delete from blacklist where fk_version_id in (select id from play_version where fk_teleplay_id="
								+ id + ")");

		BasePeer
				.executeStatement(
						"delete from feedback where status=0 and fk_version_id in (select id from play_version where fk_teleplay_id="
								+ id + ")", "searchteleplay");

		PlayVersionMgt.getInstance().deletePlayVersionByTeleplayID(id);

		List<Episode> episodes = EpisodeMgt.getInstance().getEpisodes(id, -1,
				-1);
		if (null != episodes) {
			EpisodeVideoMgt evmgt = EpisodeVideoMgt.getInstance();
			for (Episode episode : episodes) {
				evmgt.deleteEpisodeVideo(episode.getId());
			}
		}

		EpisodeLogMgt
				.getInstance()
				.executeSql(
						"delete from episode_log where fk_episode_id in (select id from episode where fk_teleplay_id="
								+ id + ")");

		EpisodeMgt.getInstance().deleteEpisodeByTeleplayId(id);

		return true;
	}

	/**
	 * 查询电视剧
	 * 
	 * @param name
	 * @param status
	 * @param telpage
	 * @param conn
	 * @return
	 */
	public TelePage searchTeleplay(String name, int status, int isPrecise,
			TelePage telpage) {
		Connection conn = null;
		try {
			conn = DataConn.getTeleplayConn();
			if (null == telpage)
				telpage = new TelePage();
			TeleplayService ts = TeleplayService.getInstance();
			telpage.setTeleplaySize(ts.searchTeleplayMaxSizeByNameAndStatus(
					name, status, isPrecise, conn));
			if (telpage.getTeleplaySize() % Constants.PAGESIZE == 0)
				telpage.setMaxpage(telpage.getTeleplaySize()
						/ Constants.PAGESIZE);
			else
				telpage.setMaxpage(telpage.getTeleplaySize()
						/ Constants.PAGESIZE + 1);
			if (telpage.getPage() < 1)
				telpage.setPage(1);
			if (telpage.getMaxpage() < 1)
				telpage.setMaxpage(1);
			telpage.setSl(ts.searchTeleplayByNameAndStatus(name, status,
					isPrecise, telpage.getPage(), Constants.PAGESIZE, conn));
		} catch (Exception e) {
			logger.error(e);
		} finally {
			JdbcUtil.close(conn);
		}
		return telpage;
	}
	
	public TelePage searchTeleplay(TelePage telpage){
		Connection conn = null;
		try {
			conn = DataConn.getTeleplayConn();
		if(null==telpage) telpage=new TelePage();
		telpage.setTeleplaySize(TeleplayMgt.getInstance().searchAllTeleplaySize());
		if(telpage.getTeleplaySize()%Constants.PAGESIZE==0)
			telpage.setMaxpage(telpage.getTeleplaySize()/Constants.PAGESIZE);
		else telpage.setMaxpage(telpage.getTeleplaySize()/Constants.PAGESIZE+1);
		if(telpage.getPage()<1)telpage.setPage(1);
		if(telpage.getMaxpage()<1)telpage.setMaxpage(1);
		telpage.setSl(TeleplayService.getInstance().searchAllTeleplay(telpage.getPage(), Constants.PAGESIZE, conn));
		} catch (Exception e) {
			logger.error(e);
		} finally {
			JdbcUtil.close(conn);
		}
		return telpage;
	}
}
