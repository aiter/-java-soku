/**
 * 
 */
package com.youku.soku.library.load.autoSearch;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.youku.search.pool.net.util.Cost;
import com.youku.soku.library.load.dao.ProgrammeDao;
import com.youku.soku.library.load.dao.ProgrammeEpisodeDao;
import com.youku.soku.library.load.dao.ProgrammeSiteDao;
import com.youku.soku.library.load.form.ProgrammeBo;
import com.youku.soku.library.load.form.ProgrammeEpisodeBo;
import com.youku.soku.library.load.form.ProgrammeSiteBo;

/**
 * 计算整理的综合
 * @author liuyunjian
 * 2011-3-15
 */
public class Zonghe {
	private static final Logger log = Logger.getLogger(Zonghe.class
			.getName());
	
	public static void main(String[] args) {
		if(args.length<3){
			System.out.println("usage: log4j torque teleplay/anime/movie/variety");
			System.exit(0);
			return;
		}
		// logger
		String log4j = args[0];
		System.out.println("初始化log4j: " + log4j);
		DOMConfigurator.configure(log4j);

		// torque
		String torque = args[1];
		System.out.println("初始化torque: " + torque);
		try {
			Torque.init(torque);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		
		
		Zonghe  loader = new Zonghe();
		if(args.length>=3){
			if("siteid".equalsIgnoreCase(args[2])){/**单个站点ID*/
				System.out.println("zonghe-->site id:"+args[3]);
				loader.updateBySiteId(Integer.parseInt(args[3]));
			}else if("all".equalsIgnoreCase(args[2])){/**所有的*/
				System.out.println("zonghe --> all");
				loader.updateAll();
			}/*else { //Test
				ProgrammeSiteBo programmeSiteBo = new ProgrammeSiteBo();
				
				ProgrammeBo programmeBo = new ProgrammeBo();
				programmeBo.contentId=106128;
				programmeSiteBo.programmeBo = programmeBo;
				
				ProgrammeDao.getInstance().updateAudit(programmeSiteBo.programmeBo);
			}*/
		}
	}

	/**
	 * 计算所有的综合整理的结果
	 */
	public void updateAll(){
		//找出所有siteId，同一节目只需任一siteId
		List<Integer> siteIds = ProgrammeSiteDao.getInstance().getGroupSiteIds();
		log.info("计算综合，共："+(siteIds==null?0:siteIds.size()));
		
		Cost cost = new Cost();
		if(siteIds!=null && siteIds.size()>0){
			for (Integer siteId : siteIds) {
				updateBySiteId(siteId);
			}
		}
		cost.updateEnd();
		log.info("计算综合，共耗时："+cost.getCost());
	}
	
	/**
	 * 
	 * 1、获取一个站点的一个节目所有站点的该节目
	 * 2、通过一定的规则，选择最优、最全的一组节目视频，更新到综合站点中
	 * @param progSiteId
	 */
	public void updateBySiteId(int progSiteId){
		List<ProgrammeSiteBo> psList = ProgrammeSiteDao.getInstance().getProgrammeSites(progSiteId);
		log.info((psList==null?0:psList.size())+"个站点（含综合），有同一个节目");
		
		if(psList!=null){
			Map<Integer, List<ProgrammeEpisodeBo>> episodeMap = new HashMap<Integer, List<ProgrammeEpisodeBo>>(psList.size());
			List<ProgrammeEpisodeBo> tmp = null;
			for (ProgrammeSiteBo psBo : psList) {
				tmp = null;
				try {
					tmp =  ProgrammeEpisodeDao.getInstance().getList(psBo.id);//每个站点节目的视频列表
				} catch (TorqueException e) {
					log.error("get episodes error:"+psBo.id);
				}
				if(tmp!=null){
//					//优酷可能有两个视频列表,将原来的列表加入
//					List<ProgrammeEpisodeBo> old = episodeMap.get(psBo.id);
//					if(old!=null){
//						tmp.addAll(old);
//					}
					//优酷的2个列表都在tmp中,同一个站点，
					Collections.sort(tmp, new Comparator<ProgrammeEpisodeBo>() {
						@Override
						public int compare(ProgrammeEpisodeBo o1,
								ProgrammeEpisodeBo o2) {
							if(o1==null && o2==null){
								return 0;
							}
							if(o1==null){
								return -1;
							}
							if(o2==null){
								return 1;
							}
							
							//2011.6.16 如果有orderStage。orderStage优先
							if(o1.orderStage>0 && o2.orderStage>0){
								return  (o1.orderStage==o2.orderStage)?(o1.viewOrder<o2.viewOrder?-1:1):(o1.orderStage>o2.orderStage?1:-1);
							}
							/**
							 * 排序规则：orderid正排,相同的orderid时，使用vieworder小的，这样可以优先选择版权数据 
							 */
							return  (o1.orderId==o2.orderId)?(o1.viewOrder<o2.viewOrder?-1:1):(o1.orderId>o2.orderId?1:-1);
						}
						
					});
					episodeMap.put(psBo.sourceSite, tmp);
				}
			}
			
			List<ProgrammeEpisodeBo> zongheEpisodeBoList = getZongheEBo(episodeMap);
			
			//没有找到综合的视频，也插入一条记录
			//获取节目信息，主要是获取总集数
			ProgrammeBo programmeBo = ProgrammeDao.getInstance().getProgramme(psList.get(0).fkProgrammeId);
			//更新site表
			if(programmeBo!=null){
				boolean isNull = (zongheEpisodeBoList==null || zongheEpisodeBoList.size()==0);
				
				ProgrammeSiteBo zongheSiteBo = new ProgrammeSiteBo();
				zongheSiteBo.fkProgrammeId = psList.get(0).fkProgrammeId;
				zongheSiteBo.sourceSite = 100;
				zongheSiteBo.orderId = 5;
				zongheSiteBo.firstLogo = (isNull?"":zongheEpisodeBoList.get(0).logo);
				zongheSiteBo.completed = (isNull?0:((programmeBo.episodeTotal>0 && programmeBo.episodeTotal<=zongheEpisodeBoList.size())?1:0));//TODO
				zongheSiteBo.blocked = 0;
				zongheSiteBo.midEmpty = 0; //TODO
				zongheSiteBo.episodeCollected = (isNull?0:zongheEpisodeBoList.size());
				
				//插入综合site表
				ProgrammeSiteDao.getInstance().insert(zongheSiteBo);
				
				
				List<ProgrammeEpisodeBo> zongheOldList = null;
				try {
					zongheOldList = ProgrammeEpisodeDao.getInstance().getList(zongheSiteBo.id);
				} catch (TorqueException e) {
					log.equals("get episode list error:"+zongheSiteBo.id);
				}
				if(zongheOldList==null){
					zongheOldList = new ArrayList<ProgrammeEpisodeBo>();
				}
				
				if(zongheEpisodeBoList!=null && zongheEpisodeBoList.size()>0){
					//更新episode表
					boolean success = false;
					for (ProgrammeEpisodeBo programmeEpisodeBo : zongheEpisodeBoList) {
						programmeEpisodeBo.fkProgrammeSiteId = zongheSiteBo.id;
						programmeEpisodeBo.viewOrder = 1;
						
						success = ProgrammeEpisodeDao.getInstance().insert(programmeEpisodeBo);
						
						if(success && zongheOldList.size()>0){
							//如果是已有的，就是更新，把原有的，而且更新过的移除，未移除的，应该删除
							for (Iterator peBos = zongheOldList.iterator(); peBos.hasNext();) {
								ProgrammeEpisodeBo oldPeBo = (ProgrammeEpisodeBo) peBos
										.next();
								if(programmeEpisodeBo.fkProgrammeSiteId == oldPeBo.fkProgrammeSiteId && getOrderId(programmeEpisodeBo) == getOrderId(oldPeBo)){
										peBos.remove();
								}
							}
						}
						success = false;
					}
					
					//处理多余的视频
					int delCnt=0;
					for (ProgrammeEpisodeBo peBo : zongheOldList) {
						boolean s = ProgrammeEpisodeDao.getInstance().delete(peBo.id);
						if(s){
							delCnt++;
						}
					}
					if(zongheOldList!=null && zongheOldList.size()>0){
						log.info("[videos] delete zonghe videos success/total size:"+delCnt+"/"+zongheOldList.size()+" fk_p_site_id:"+zongheSiteBo.id);
					}
				}else {
					if(log.isDebugEnabled()){
						log.info(zongheSiteBo.id+"'s video list is null");
					}
				}
			}
		}
	}

	/**
	 * TODO 重点看选择算法
	 * 选择综合的一组数据
	 * @param episodeMap
	 * @return
	 */
	private List<ProgrammeEpisodeBo> getZongheEBo(
			Map<Integer, List<ProgrammeEpisodeBo>> episodeMap) {
		if(episodeMap==null || episodeMap.size()==0){
			return null;
		}
		
		int []beginEnd = getBeginEnd(episodeMap); 
		
		List<ProgrammeEpisodeBo> zongheList = new ArrayList<ProgrammeEpisodeBo>();
		List<ProgrammeEpisodeBo> tmp = null;
//		System.out.println("begin:"+beginEnd[0]+" end:"+beginEnd[1]);
		BitSet bitSet = new BitSet(beginEnd[1]);
		int[] siteIds = getSiteIds();
			//1、先从指定的站点选择。
			for (int siteId : siteIds) {
				
				tmp = null;
				tmp = episodeMap.get(new Integer(siteId));//一个站点的数据
				if(tmp!=null){
//					System.out.println("siteid:"+siteId+" size:"+tmp.size());
					for (ProgrammeEpisodeBo siteEpisode : tmp) {
						int orderId = getOrderId(siteEpisode); 
						if(!bitSet.get(orderId)){
							if(orderId<=beginEnd[1]){
								bitSet.set(orderId);
								zongheList.add(siteEpisode);
							}
						}
					}
				}
			}
			
			//2、从所有的数据中选
			for (List<ProgrammeEpisodeBo> list : episodeMap.values()) {
				for (ProgrammeEpisodeBo siteEpisode : list) {
					int orderId = getOrderId(siteEpisode); 
					if(!bitSet.get(orderId)){
						if(orderId<=beginEnd[1]){
							bitSet.set(orderId);
							zongheList.add(siteEpisode);
						}
					}
				}
			}
			
		return zongheList;
	}
	
	/**
	 * @param episodeMap
	 * @return
	 */
	private int[] getBeginEnd(Map<Integer, List<ProgrammeEpisodeBo>> episodeMap) {
		int [] tmp =new int[]{1,10};//默认1-10集
		for (List<ProgrammeEpisodeBo> list : episodeMap.values()) {
			for (ProgrammeEpisodeBo programmeEpisodeBo : list) {
				int orderId = getOrderId(programmeEpisodeBo); 
				
				if(orderId<tmp[0]){
					tmp[0]=orderId;
				}
				if(orderId>tmp[1]){
					tmp[1]=orderId;
				}
			}
		}
		
		return tmp;
	}

	/**
	 * @param programmeEpisodeBo
	 * @return
	 */
	private int getOrderId(ProgrammeEpisodeBo programmeEpisodeBo) {
		if(programmeEpisodeBo==null){
			return 0;
		}
		int orderId = programmeEpisodeBo.orderId;
		if(programmeEpisodeBo.orderStage>0 && programmeEpisodeBo.orderStage<2000){/** 避免综合中的20110405这样的orderStage数据*/
			orderId = programmeEpisodeBo.orderStage;
		}
		
		return orderId;
	}
	
	

	private int[] getSiteIds(){
		int [] siteIds = new int[]{14,1,2,19};
		return siteIds;
	}
	
}
