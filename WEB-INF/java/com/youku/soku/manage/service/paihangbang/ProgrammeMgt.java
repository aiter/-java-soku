package com.youku.soku.manage.service.paihangbang;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.soku.manage.bo.paihangbang.ProgrammeVO;
import com.youku.top.util.TopWordType.WordType;

public class ProgrammeMgt {

	private static ProgrammeMgt instance = null;

	private ProgrammeMgt() {
		super();
	}

	public synchronized static ProgrammeMgt getInstance() {
		if (null == instance)
			instance = new ProgrammeMgt();
		return instance;
	}

	public String getProgrammeNameById(int id, int cate) {
		
			return getProgrammeNoVarietyNameById(id);
	}

	private String getProgrammeNoVarietyNameById(int id) {
		try {
			List<Record> records = BasePeer
					.executeQuery("select name from programme where id=" + id,
							"soku_library");
			if (null != records && records.size() > 0) {
				return records.get(0).getValue("name").asString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getSeriesNameById(int id) {
		try {
			List<Record> records = BasePeer.executeQuery(
					"select name from series where id=" + id, "soku_library");
			if (null != records && records.size() > 0) {
				return records.get(0).getValue("name").asString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<ProgrammeVO> getProgramme(String name, int cate) {
		List<ProgrammeVO> programmes = new ArrayList<ProgrammeVO>();
		ProgrammeVO pvo = null;
		try {
			String wheresql = " where blocked=0 and state='normal' and (name like '%"
					+ name + "%' or alias like '%" + name + "%')";
			if (cate > 0)
				wheresql += " and cate = " + cate;
			List<Record> records = BasePeer.executeQuery(
					"select programme.id as id,programme.name as name from programme "
							+ wheresql, "soku_library");
			if (null != records && records.size() > 0) {
				for (Record r : records) {
					pvo = new ProgrammeVO();
					programmes.add(pvo);
					pvo.setCate(cate);
					pvo.setId(r.getValue("id").asInt());
					pvo.setName(r.getValue("name").asString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return programmes;
	}

	public List<ProgrammeVO> getSeries(String name, int cate) {
		List<ProgrammeVO> programmes = new ArrayList<ProgrammeVO>();
		ProgrammeVO pvo = null;
		try {
			String wheresql = " where (name like '%" + name
					+ "%' or alias like '%" + name + "%')";
			if (cate > 0)
				wheresql += " and cate = " + cate;
			List<Record> records = BasePeer.executeQuery(
					"select series.id as id,series.name as name from series "
							+ wheresql, "soku_library");
			if (null != records && records.size() > 0) {
				for (Record r : records) {
					pvo = new ProgrammeVO();
					programmes.add(pvo);
					pvo.setCate(cate);
					pvo.setId(r.getValue("id").asInt());
					pvo.setName(r.getValue("name").asString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return programmes;
	}
}
