package com.youku.search.drama.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.youku.search.drama.Drama;
import com.youku.search.drama.Episode;
import com.youku.search.drama.EpisodeVideo;
import com.youku.search.drama.Version;
import com.youku.search.drama.Drama.Type;

public class DramaLoader {

	static Log logger = LogFactory.getLog(DramaLoader.class);
	static final int episode_span_size = 30; // 剧集分割段

	public static final int SC_ZongYi = 2078; // subcate, 综艺数据的区分字段

	// drama
	static String sql_drama_ = "select id, subcate from teleplay where is_valid = 1 ";
	static String sql_dramaId_d = sql_drama_ + "and subcate != " + SC_ZongYi;// 过滤掉综艺
	static String sql_dramaId_z = sql_drama_ + "and subcate = " + SC_ZongYi;// 只要综艺
	static String sql_dramaId = sql_drama_;// 默认查询
	static String sql_dramaName = "select name, is_main from play_name where fk_teleplay_id = ?";

	// version
	static String sql_version = "select id, view_name, alias, cate, subcate, fixed, order_id, firstlogo, reverse from play_version where fk_teleplay_id = ? order by order_id";

	// episode
	static String sql_episode = "select id, name, order_id, source_name, video_id as vid, encode_video_id as vidEncode, logo, seconds, islock from episode where video_id != '' and fk_version_id = ? order by order_id";
	static String sql_episode_desc = sql_episode + " desc";

	static String sql_episodeVideo = "select * from episode_video where fk_episode_id = ? and shield = 0";

	//
	static JdbcTemplate jdbcTemplate = new JdbcTemplate(new SimpleDataSource());

	/**
	 * @param dramaIds
	 *            限制读取的drama id
	 * 
	 * @param loadEpisodeVideo
	 *            数据读取深度：到每一集电视剧所匹配的视频列表（true），还是只到每一集电视剧（false）
	 */
	public static List<Drama> loadDrama(List<Integer> dramaIds,
			boolean loadEpisodeVideo) {

		List<Drama> list = load(dramaIds, loadEpisodeVideo);

		// 过滤空记录
		filterEmpty(list, loadEpisodeVideo);

		// 调整属性
		adjustProperties(list);

		return list;
	}

	/**
	 * @param list
	 *            没有加载的EpisodeVideo信息的drama list
	 * 
	 * @return 加载的EpisodeVideo信息
	 */
	public static List<Drama> loadEpisodeVideo(List<Drama> list) {

		if (list == null || list.isEmpty()) {
			return list;
		}

		for (Drama drama : list) {
			List<Version> versions = drama.getVersions();
			if (versions == null || versions.isEmpty()) {
				continue;
			}

			for (Version version : versions) {
				List<Episode> episodes = version.getEpisodes();
				if (episodes == null || episodes.isEmpty()) {
					continue;
				}

				for (Episode episode : episodes) {
					List<EpisodeVideo> videos = queryEpisodeVideos(episode
							.getId());
					episode.setVideos(videos);
				}
			}
		}

		// 调整属性
		adjustProperties(list);

		return list;
	}

	private static List<EpisodeVideo> queryEpisodeVideos(int id) {

		Object[] e_args = { id };
		int[] e_argTypes = { Types.INTEGER };

		List<EpisodeVideo> videos = jdbcTemplate.query(sql_episodeVideo,
				e_args, e_argTypes, new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {

						EpisodeVideo ev = new EpisodeVideo();

						ev.setId(rs.getInt("id"));
						ev.setFk_episode_id(rs.getInt("fk_episode_id"));
						ev.setVideo_id(rs.getInt("video_id"));

						// extra info
						ev.setVidEncoded(rs.getString("encode_video_id"));
						ev.setSourceName(rs.getString("source_name"));
						ev.setLogo(rs.getString("logo"));
						ev.setSeconds(rs.getFloat("seconds"));

						return ev;
					}
				});

		return videos;
	}

	private static List<Drama> load(List<Integer> dramaIds,
			boolean loadAllEpisodeVideo) {

		// Drama: id, type
		List<Drama> list = getDramaIdAndTypeList(dramaIds);
		if (list == null || list.isEmpty()) {
			return list;
		}
		// Drama: name, alias
		for (final Drama drama : list) {
			if (drama.getAlias() == null) {
				drama.setAlias(new LinkedList<String>());
			}

			Object[] args = { drama.getId() };
			int[] argTypes = { Types.INTEGER };

			jdbcTemplate.query(sql_dramaName, args, argTypes, new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					String name = rs.getString("name");
					int is_main = rs.getInt("is_main");

					if (is_main == 1) {
						drama.setName(name);
					} else {
						drama.getAlias().add(name);
					}

					return drama;
				}
			});

			// versions
			List<Version> versions = jdbcTemplate.query(sql_version, args,
					argTypes, new RowMapper() {
						public Object mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							Version version = new Version();

							version.setDrama(drama);

							version.setId(rs.getString("id"));
							version.setName(rs.getString("view_name"));
							version.setAlias(rs.getString("alias"));
							version.setCate(rs.getInt("cate"));
							version.setSubcate(rs.getInt("subcate"));
							version.setFixed(rs.getInt("fixed"));
							version.setOrder(rs.getInt("order_id"));
							version.setLogo(rs.getString("firstlogo"));
							version.setReverse(rs.getInt("reverse") == 1);
							return version;
						}
					});

			if (versions == null || versions.isEmpty()) {
				continue;
			}
			drama.setRealVersionCount(versions.size());

			// episodes
			for (final Version version : versions) {

				Object[] v_args = { version.getId() };
				int[] v_argTypes = { Types.INTEGER };

				final String this_sql_episode = (!loadAllEpisodeVideo && version
						.isReverse()) ? sql_episode_desc : sql_episode;

				List<Episode> episodes = jdbcTemplate.query(this_sql_episode,
						v_args, v_argTypes, new RowMapper() {
							public Object mapRow(ResultSet rs, int rowNum)
									throws SQLException {

								Episode e = new Episode();

								e.setVersion(version);

								e.setId(rs.getInt("id"));
								e.setName(rs.getString("name"));
								e.setOrder(rs.getInt("order_id"));
								e.setSourceName(rs.getString("source_name"));
								e.setVid(rs.getInt("vid"));
								e.setVidEncoded(rs.getString("vidEncode"));
								e.setLogo(rs.getString("logo"));
								e.setSeconds(rs.getFloat("seconds"));
								e.setIslock(rs.getInt("islock"));

								return e;
							}
						});

				version.setEpisodes(episodes);

				// 读取所有匹配每一集的视频
				if (loadAllEpisodeVideo && episodes != null) {
					for (Episode episode : episodes) {
						List<EpisodeVideo> videos = queryEpisodeVideos(episode
								.getId());
						episode.setVideos(videos);
					}
				}
			}

			// 如果是剧集，只有1个版本，并且剧集数量大于30，对其进行分割
			if (drama.getType() == Drama.Type.DRAMA && versions.size() == 1
					&& versions.get(0) != null
					&& versions.get(0).getEpisodeCount() > episode_span_size) {

				final Version old_version = versions.get(0);
				final List<Episode> old_episodes = old_version.getEpisodes();
				final List<Version> new_versions = new ArrayList<Version>();

				int episodeCount = old_episodes.size();
				int versionCount = (episodeCount - 1) / episode_span_size + 1;

				for (int i = 1; i <= versionCount; i++) {

					int start = ((i - 1) * episode_span_size) + 1;
					int end = i * episode_span_size > episodeCount ? episodeCount
							: i * episode_span_size;

					Version new_version = new Version();

					new_version.setDrama(drama);

					new_version.setId(old_version.getId() + "-" + i);
					new_version.setName("第"
							+ old_episodes.get(start - 1).getOrder() + "-"
							+ old_episodes.get(end - 1).getOrder() + "集"); // 赋予新名字,不能直接用start和end，是因为可能中间会过滤剧集
					new_version.setAlias(old_version.getAlias());
					new_version.setCate(old_version.getCate());
					new_version.setSubcate(old_version.getSubcate());
					new_version.setFixed(old_version.getFixed());
					new_version.setOrder(i);

					final List<Episode> new_episodes = new ArrayList<Episode>();
					new_episodes.addAll(old_episodes.subList(start - 1, end)); // sublist返回不是序列化的，要重新装载一下

					for (Episode new_episode : new_episodes) {
						new_episode.setVersion(new_version);
					}

					new_version.setLogo(new_episodes.get(0).getLogo());
					new_version.setEpisodes(new_episodes);

					new_versions.add(new_version);
				}

				versions = new_versions;
			}

			drama.setVersions(versions);
		}

		return list;
	}

	/**
	 * 返回一系列drama，其中ids限制了有效的drama id。返回的drama只设置了id、type2个属性。
	 */
	private static List<Drama> getDramaIdAndTypeList(List<Integer> ids) {

		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Drama drama = new Drama();
				drama.setId(rs.getInt("id"));

				int subcate = rs.getInt("subcate");

				if (subcate == SC_ZongYi) {
					drama.setType(Type.ZONGYI);
				} else {
					drama.setType(Type.DRAMA);
				}
				return drama;
			}
		};

		return jdbcTemplate.query(buildDramaIdSql(ids), mapper);
	}

	private static String buildDramaIdSql(List<Integer> ids) {

		if (ids == null || ids.isEmpty()) {
			return sql_dramaId;
		}

		StringBuilder builder = new StringBuilder();
		builder.append(sql_dramaId);
		builder.append(" and id in (");

		for (int i = 0; i < ids.size() - 1; i++) {
			builder.append(ids.get(i));
			builder.append(", ");
		}
		builder.append(ids.get(ids.size() - 1));

		builder.append(")");

		return builder.toString();
	}

	private static Connection getConnection() {
		try {
			return Torque.getConnection("searchteleplay");
		} catch (TorqueException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 调整一下属性。把时长为0的视频设为平均值。
	 */
	public static void adjustProperties(List<Drama> list) {
		if (list == null || list.isEmpty()) {
			return;
		}

		Random random = new Random();

		for (Drama drama : list) {
			if (drama.getVersions() == null) {
				continue;
			}

			for (Version version : drama.getVersions()) {
				if (version.getEpisodes() == null) {
					continue;
				}

				// 1 统计平均时长
				int count = 0;// 时长大于0的视频数目
				float seconds = 0;// 时长大于0的视频的总时长

				List<Episode> episodes = version.getEpisodes();
				List<Integer> zeroIndex = new LinkedList<Integer>();// 时长为0的index
				for (int eIndex = 0; eIndex < episodes.size(); eIndex++) {
					Episode episode = episodes.get(eIndex);
					if (episode.getSeconds() > 0) {
						seconds += episode.getSeconds();
						count++;
					} else {
						zeroIndex.add(eIndex);
					}
				}

				float avgSeconds;
				if (count == 0) {// 所有视频时长都等于0
					avgSeconds = (random.nextInt(10) + 35) * 60;// 35～45分钟
				} else {// 所有或者有部分视频时长大于0
					avgSeconds = seconds / count;
				}

				// 2 设置0时长的剧集为平均时长
				for (Integer zero : zeroIndex) {
					episodes.get(zero).setSeconds(
							avgSeconds + random.nextInt(100));// 再微调100秒
				}

				// 3 设置0时长的剧集视频为平均时长
				for (Episode episode : episodes) {
					List<EpisodeVideo> videos = episode.getVideos();
					if (videos == null) {
						continue;
					}
					for (EpisodeVideo video : videos) {
						if (!(video.getSeconds() > 0)) {
							video.setSeconds(avgSeconds + random.nextInt(100));// 再微调100秒
						}
					}
				}
			}
		}
	}

	/**
	 * 删除空的version，删除空的drama
	 * 
	 * @param checkEpisodeVideo
	 *            如果为true，那么，如果episode没有关联的video，或者关联的video中没有找到该episode，就把自己加进去
	 */
	public static void filterEmpty(List<Drama> list, boolean checkEpisodeVideo) {

		if (list == null || list.isEmpty()) {
			return;
		}

		for (Iterator<Drama> iterator = list.iterator(); iterator.hasNext();) {
			Drama drama = iterator.next();

			List<Version> versions = drama.getVersions();
			if (versions == null || versions.isEmpty()) {
				iterator.remove();
				continue;
			}

			for (Iterator<Version> versionIterator = versions.iterator(); versionIterator
					.hasNext();) {
				Version version = versionIterator.next();

				List<Episode> episodes = version.getEpisodes();
				if (episodes == null || episodes.isEmpty()) {
					versionIterator.remove();
					drama.decRealVersionCount();
					continue;
				}

				if (checkEpisodeVideo) {
					checkAndMockEpisodeVideos(episodes);
				}
			}

			if (versions == null || versions.isEmpty()) {
				iterator.remove();
				continue;
			}
		}
	}

	private static void checkAndMockEpisodeVideos(List<Episode> episodes) {
		if (episodes == null) {
			return;
		}

		for (Episode episode : episodes) {
			boolean found = false;

			List<EpisodeVideo> videos = episode.getVideos();
			if (videos == null) {
				videos = new LinkedList<EpisodeVideo>();
				episode.setVideos(videos);
			} else {
				for (EpisodeVideo video : videos) {
					if (video.getVideo_id() == episode.getVid()) {
						found = true;
						break;
					}
				}
			}

			if (!found) {
				videos.add(EpisodeVideo.mock(episode));
			}
		}
	}

	static class SimpleDataSource implements DataSource {

		public Connection getConnection() throws SQLException {
			return DramaLoader.getConnection();
		}

		public Connection getConnection(String username, String password)
				throws SQLException {
			return getConnection();
		}

		public PrintWriter getLogWriter() throws SQLException {
			// TODO Auto-generated method stub
			return null;
		}

		public int getLoginTimeout() throws SQLException {
			// TODO Auto-generated method stub
			return 0;
		}

		public void setLogWriter(PrintWriter out) throws SQLException {
			// TODO Auto-generated method stub

		}

		public void setLoginTimeout(int seconds) throws SQLException {
			// TODO Auto-generated method stub

		}

		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			// TODO Auto-generated method stub
			return false;
		}

		public <T> T unwrap(Class<T> iface) throws SQLException {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static void main(String[] args) {
		List<Integer> list = null;
		System.out.println(buildDramaIdSql(list));

		list = new ArrayList<Integer>();
		System.out.println(buildDramaIdSql(list));

		list.add(1);
		System.out.println(buildDramaIdSql(list));

		list.add(2);
		System.out.println(buildDramaIdSql(list));

		list.add(99);
		System.out.println(buildDramaIdSql(list));

		System.out.println(0.0 == 0L);
	}
}
