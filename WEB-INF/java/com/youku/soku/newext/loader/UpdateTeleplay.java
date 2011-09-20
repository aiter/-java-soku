package com.youku.soku.newext.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.library.load.Programme;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.info.TeleplayInfo;
import com.youku.soku.newext.util.MiscUtil;
import com.youku.soku.newext.util.SortUtil;


public class UpdateTeleplay {
	private static Log logger = LogFactory.getLog(UpdateTeleplay.class);

	public static void doUpdate(Programme programme, TeleplayInfo teleplayInfo,
			PersonInfo personInfo) {
		logger.debug("更新teleplayInfo ...添加programme:" + programme.getName());

		Set<String> programmeNameSet = new HashSet<String>();
		programmeNameSet.add(StringUtils.trimToEmpty(programme.getName().toLowerCase()));
		if (programme.getAlias() != null
				&& StringUtils.trimToEmpty(programme.getAlias()).length() > 0) {
			String[] tmpArr = StringUtils.trimToEmpty(programme.getAlias())
					.split("\\|");
			if (tmpArr != null && tmpArr.length > 0) {
				for (String aliasName : tmpArr)
					programmeNameSet.add(StringUtils.trimToEmpty(aliasName.toLowerCase()));
			}
		}

		if (programmeNameSet == null || programmeNameSet.size() == 0)
			return;
		// 构造节目名/节目别名-->节目列表（列表中含一个或多个节目） 结构
		load(programme, teleplayInfo, programmeNameSet);

		// 人物头像 和 人物-->节目列表（列表中含一个或多个节目）
		personInfo.addPerson(programme, teleplayInfo);

		// 对name--programme_list的list排序
		for (String proName : programmeNameSet) {
			List<Programme> programmeList = teleplayInfo.name_programme
					.get(proName);
			if (programmeList == null || programmeList.size() <= 0)
				continue;
			SortUtil.sortProgrammeByDate(programmeList, teleplayInfo.middMap);
		}
		logger.debug("更新teleplayInfo。。。 添加programme:" + programme.getName()
				+ "  结束");

	}

	// 将某个programme 对应的List<ProgrammeSite>加入到 name_programme
	private static void load(Programme programme, TeleplayInfo info,
			Set<String> programmeNameSet) {
		if (programme == null || info == null)
			return;

		if (programmeNameSet == null || programmeNameSet.size() == 0)
			return;

		for (String proName : programmeNameSet) {
			MiscUtil.putIfAbsent(info.name_programme, StringUtils
					.trimToEmpty(proName), new ArrayList<Programme>());

			if (!info.name_programme.get(StringUtils.trimToEmpty(proName))
					.contains(programme)) {
				info.name_programme.get(StringUtils.trimToEmpty(proName)).add(
						programme);

				if (logger.isDebugEnabled()) {
					logger.info("name_programme 加载：" + proName);
				}
			}

		}

	}
}
