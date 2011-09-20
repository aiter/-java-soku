package com.youku.soku.suggest.trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.youku.search.hanyupinyin.Converter;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.suggest.entity.EpisodeViewUrl;
import com.youku.soku.suggest.entity.NamesEntity;
import com.youku.soku.suggest.entity.PersonWork;
import com.youku.soku.suggest.entity.VideoCheckResult;
import com.youku.soku.suggest.orm.TrieWordsSuggest;
import com.youku.soku.suggest.parser.KeywordsParser;
import com.youku.soku.suggest.util.WordUtil;

public class TrieTree implements Serializable {

	private Logger log = Logger.getLogger(this.getClass());

	public static final int PROMPTLISTSIZE = 10;

	private TrieNode root = new TrieNode();

	public int count = 0; // 记录节点个数

	public static int clearUselessDataTime = 0;  //记录删除没用数据的次数，测试用
	
	public int episodeUrlCount = 0; //记录urlmap中播放链接的个数，测试用
	
	private Map<String, List<DirectEntity>> directEntityMap = new HashMap<String, List<DirectEntity>>();
	
	private Map<String, String> viewUrlsMap = new HashMap<String, String>();
	
	private Map<String, String> directPlayUrlsMap = new HashMap<String, String>();  //首页直接播放Map，因为只提示电视剧，根据提示词又不好判断，只能单独存放
	
	private static String OLD_DEFAULT_NO_IMG_LOGO = "http://res.mfs.ykimg.com/051000004DACF16A9792732B23061177.jpg";
	
	private static String SOKU_DEFAULT_NO_IMG_LOGO = "0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80";
	
	public Map<String, List<DirectEntity>> getDirectEntityMapp() {
		return this.directEntityMap;
	}
	
	public Map<String, String> getViewUrlsMap() {
		return this.viewUrlsMap;
	}
	
	public Map<String, String> getDirectPlayUrlsMap() {
		return this.directPlayUrlsMap;
	}
	
	public void addUrlToMap(String key, String value) {
		/*if(viewUrlsMap.get(key) != null) {
			this.viewUrlsMap.put(key, value);
		}*/
		this.viewUrlsMap.put(key, value);
	}
	
	public List<Entity> getTopWordEntity() {
		List<String> strList = Arrays.asList("单身男女", "火影忍者", "美人心计", "快乐大本营", "非诚勿扰", "周星驰", "天涯赤子心", "郭德纲", "回家的诱惑", "康熙来了");
		List<Entity> result = new ArrayList<Entity>();
		for(String s : strList) {
			Entity e = new Entity();
			e.setWord(s);
			result.add(e);
		}
		return result;
	}
	
	
	public void addDirectEntityToMap(String key, NamesEntity ne, String viewUrl) {
		if(ne == null || StringUtils.isBlank(viewUrl)) {
			return;
		}
		DirectEntity de = new DirectEntity();
		if(OLD_DEFAULT_NO_IMG_LOGO.equals(ne.getThumb())) {
			StringBuilder sb = new StringBuilder("http://g");
			sb.append(System.currentTimeMillis() % 4 + 1);
			sb.append(".ykimg.com/");
			sb.append(SOKU_DEFAULT_NO_IMG_LOGO);
			de.setLogo(sb.toString());
		} else {
			de.setLogo(ne.getThumb());
		}
		
		if(ne.getTotalEpisode() > 0) {
			de.setLiTip(ne.getTotalEpisode() + "集全 ， 高清电视剧");
			de.setDivTip(ne.getTotalEpisode() + "集电视剧 ");
		} else if(ne.getCate() == Constants.TELEPLAY_CATE_ID) {
			de.setLiTip("电视剧");
			de.setDivTip("电视剧 ");
		} else if(ne.getCate() == Constants.MOVIE_CATE_ID) {
			de.setLiTip("电影");
			de.setDivTip("电影 ");
		}
		de.setDivTipYear(WordUtil.parseYear(ne.getRealeaseTime()));
		de.setUrl(viewUrl);
		de.setPerformers(ne.getPerformers() == null ? "" : ne.getPerformers());
		de.setProgrammeName(ne.getNames());
		de.setProgrammeEncodeId(WordUtil.encodeProgrammeId(ne.getProgrammeId()));
		de.setShowIdStr(ne.getShowIdStr());
		List<DirectEntity> deList = directEntityMap.get(key);
		if(deList == null) {
			deList = new ArrayList<DirectEntity>();
			directEntityMap.put(key, deList);
		}
		if(!deList.contains(de)) {
			deList.add(de);
		}
		
	}
	
	public TrieNode getRoot() {
		return root;
	}

	public int getCount() {
		return count;
	}

	public int getClearUselessDataTime() {
		return clearUselessDataTime;
	}

	public void setClearUselessDataTime(int clearUselessDataTime) {
		this.clearUselessDataTime = clearUselessDataTime;
	}

	public String getAllKeys() {
		if (root.getChild() == null) {
			return null;
		} else {
			return Arrays.toString(root.getChild().keySet().toArray());
		}
	}

	public Collection<Character> getKeysCollection() {
		if (root.getChild() == null) {
			return null;
		} else {
			return root.getChild().keySet();
		}
	}

	public void insert(Entity e) {
		TrieNode pnode = root;
		/*
		 * for(int i=0;i<e.getWord().length();i++){ char c = e.getWord().charAt(i); TrieNode cnode = pnode.getChild(c); if(cnode == null){ cnode = new
		 * TrieNode(); cnode.setKey(c); pnode.addChild(cnode); } cnode.addData(e); pnode = cnode; }
		 */
		addNode(e.getWord(), e, pnode, true);
		addNode(WordUtil.getPYFirstLetter(e.getWord()), e, pnode, false);
		Set<String> wordPinyinSet = WordUtil.getPinyin(e.getWord());
		if(wordPinyinSet != null) {
			for(String pinyin : wordPinyinSet) {
				addNode(pinyin, e, pnode, false);
			}
		}
		
	}

	/**
	 * 
	 * @param word
	 * @param e
	 * @param pnode
	 * @param removeCompleteMatch
	 */
	public void addNode(String word, Entity e, TrieNode pnode, boolean removeCompleteMatch) {
		if (word == null) {
			return;
		}
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			TrieNode cnode = pnode.getChild(c);

			if (cnode == null) {
				cnode = new TrieNode();
				cnode.setKey(c);
				pnode.addChild(cnode);
			}

			if (removeCompleteMatch) {
				if (i != word.length() - 1) { // 提示相同词, 完全匹配改词
					cnode.addData(e);
				}
			} else {
				cnode.addData(e);
			}

			pnode = cnode;
			count++;
		}
	}

	public void handlePersonName(Set<String> personList) {
		for (String person : personList) {
			List<PersonWork> personWorks = WordUtil.getPersonRelatedEpisode(person);
			List<Entity> entityList = search(person);
			int suggestSize = 0;

			if (personWorks != null && entityList != null) {
				int entitySize = entityList.size();
				Random random = new Random();
				int addPosition = entitySize;
				for (PersonWork s : personWorks) {
					if (suggestSize >= 3) {
						break;
					}
					
					String workName = s.getWorkName();
					int workCate = s.getCate();
					String viewUrl = "";

					List<NamesEntity> works = KeywordsParser.parse(workName);
					List<NamesEntity> worksClone = null;
					if(works != null) {
						worksClone = new ArrayList<NamesEntity>(works);
						//去除掉类型对不上的数据
						for(Iterator<NamesEntity> it = worksClone.iterator(); it.hasNext(); ) {
							NamesEntity ne = it.next();
							//if(ne.getCate() != workCate || (ne.getPerformers().indexOf(person) == -1 && ne.getDirectors().indexOf(person) == -1 && ne.getHosts().indexOf(person) == -1)) {
							if(ne.getCate() != workCate) {	
								it.remove();
							}
						}
					}
					
					if (worksClone != null && !worksClone.isEmpty()) {
						
						NamesEntity newestWork = worksClone.get(0);
						if (worksClone.size() > 1) {
							for (int i = 1; i < worksClone.size(); i++) {
								NamesEntity ne = worksClone.get(i);
								//log.info("work name: " + workName + ne.getRealeaseTime());
								if (ne.getRealeaseTime() != null && newestWork.getRealeaseTime() != null
										&& ne.getRealeaseTime().after(newestWork.getRealeaseTime())) {
									newestWork = ne;
								}
							}
						}
						
						//log.info("newestWork" + newestWork);

						if (newestWork.getCate() == Constants.TELEPLAY_CATE_ID) {
							// 取得人物相关的电视剧信息
							try {
								List<EpisodeViewUrl> viewUrls = WordUtil.getTeleplayViewUrls(workName, false);
								if (viewUrls != null && !viewUrls.isEmpty()) {
									viewUrl = viewUrls.get(0).getViewUrl();
								}
								NamesEntity ne = WordUtil.getProgrammeEntity(workName, Constants.TELEPLAY_CATE_ID);
								if(!StringUtils.isBlank(viewUrl)) {
									addDirectEntityToMap(person, ne, viewUrl);
								}
								/*for (EpisodeViewUrl ev : viewUrls) {  显示所有集数
									addUrlToMap(person + s.getWorkName(), ev.getViewUrl());
								}*/
							} catch (Exception e) {
								log.error(e.getMessage(), e);
							}
						} else if (newestWork.getCate() == Constants.MOVIE_CATE_ID) {
							// 再取人物相关的电影信息
							// VideoCheckResult vcr =
							// WordUtil.isVideo(workName);
							viewUrl = KeywordsParser.getViewUrl(newestWork, 1);
							
							NamesEntity ne = WordUtil.getProgrammeEntity(workName, Constants.MOVIE_CATE_ID);
							if(!StringUtils.isBlank(viewUrl)) {
								addDirectEntityToMap(person, ne, viewUrl);
							}
						} else {
							log.info("Other type works(not movie or teleplay)" + workName);
						}
						
					} else {
						//log.info("Miss URL person works: " + workName);
					}

					if (!StringUtils.isBlank(viewUrl)) {

						if (entitySize > 4) {
							int stepSize = 0;
							while (stepSize < 100) {
								stepSize++;
								int newAddPosition = random.nextInt(entitySize - 4);
								if (Math.abs(newAddPosition - addPosition) > 1) {
									addPosition = newAddPosition;
									break;
								}
							}

						}

						Entity pe = new Entity();
						
						pe.setWord(person + " " + s.getWorkName());
						if (entityList.contains(pe)) {
							continue;
						}
						pe.setWord(person + s.getWorkName());
						if (entityList.contains(pe)) {
							continue;
						}
						
						//pe.setViewUrl(viewUrl);
						
						// pe.setPersonWorksFlag(true);
						pe.setFlag(EntityFlagUtil.DIRECTFLAG);
						// pe.setPyAbbreviation(NamesFirstChar.convertFirstLetter(s.getWorkName(),
						// true));
						// this.insert(pe);
						if (s.getWorkName().length() <= 10) {   //节目名不能大于10。避免“刘德华《建党伟业》之《践行者》”这样的数据，造成折行
							entityList.add(addPosition, pe);
						}
						
						suggestSize++;
					}

				}
			}
			
			//0426 人物搜索完全命中一个人名是，显示这个人物
			Entity directEntity = new Entity();
			directEntity.setWord(person);
			directEntity.setFlag(EntityFlagUtil.DIRECTFLAG);
			entityList.add(0, directEntity);
			

			fixSpecialEntityList(entityList);

			/*
			 * if (!StringUtils.isBlank(directAreaUlr)) { Entity directEntity = new Entity(); directEntity.setWord(directAreaWorkName);
			 * directEntity.setDirect(true); directEntity.setViewUrl(directAreaUlr); directEntity.setPersonWorksFlag(true); entityList.add(directEntity); }
			 */
		}

	}

	public void handleTeleplayName(Set<String> teleplayList) {
		for (String teleplay : teleplayList) {
			try {
				List<EpisodeViewUrl> viewUrls = WordUtil.getTeleplayViewUrls(teleplay, false);
				
				List<Entity> entityList = search(teleplay);
			
				if (entityList == null) {
					continue;
				}
				if (viewUrls != null) {
					int entitySize = entityList.size();
					Random random = new Random();
					int addPosition = entitySize;
					for (EpisodeViewUrl ev : viewUrls) {
						if (entitySize > 4) {
							int stepSize = 0;
							while (stepSize < 100) {
								stepSize++;
								int newAddPosition = random.nextInt(entitySize - 4);
								if (Math.abs(newAddPosition - addPosition) > 1) {
									addPosition = newAddPosition;
									break;
								}
							}

						}
						if (!StringUtils.isEmpty(ev.getViewUrl())) {
							Entity e = new Entity();
							e.setWord(teleplay + ev.getOrderNumber());
							//e.setViewUrl(ev.getViewUrl());
							e.setFlag(EntityFlagUtil.TELEPLAYORDERFLAG);
							viewUrlsMap.put(e.getWord(), ev.getViewUrl());
							entityList.add(addPosition, e);
						}
					}
				}
				

				viewUrls = WordUtil.getTeleplayViewUrls(teleplay, true);
				if (viewUrls != null && !viewUrls.isEmpty()) {
					
					Entity directEntity = new Entity();
					directEntity.setWord(teleplay);
					//directEntity.setDirect(true);
					//directEntity.setViewUrl(viewUrls.get(0).getViewUrl());
					directEntity.setFlag(EntityFlagUtil.DIRECTFLAG);
					if(!entityList.contains(directEntity)) {
						entityList.add(0, directEntity);
					}					
					
					/*for (EpisodeViewUrl ev : viewUrls) {
						addUrlToMap(directEntity.getWord(), ev.getViewUrl());
					}*/
					//addThumbToMap(directEntity.getWord(), WordUtil.getProgrammeThumb(teleplay));
					NamesEntity ne = WordUtil.getProgrammeEntity(teleplay, Constants.TELEPLAY_CATE_ID);
					addDirectEntityToMap(teleplay, ne, viewUrls.get(0).getViewUrl());
					
					for(EpisodeViewUrl v : viewUrls) {
						addUrlToMap(teleplay + v.getOrderNumber(), v.getViewUrl());
						directPlayUrlsMap.put(teleplay + v.getOrderNumber(), v.getViewUrl());
					}
				}
				fixSpecialEntityList(entityList);

				List<Entity> entityPYList = search(Converter.convert(teleplay));
				if (entityPYList != null) {
					entityPYList.clear();
				}
				if (entityPYList != null && entityList != null) {

					try {
						//TODO ConcurrentModificationException 
						entityPYList.addAll(entityList);
					} catch (Exception e) {
						log.info("电视剧拼音加入播放链接: " + teleplay);
						log.error(e.getMessage(), e);
					}

				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public void handleMovieName(Set<String> movieList) {
		for (String movie : movieList) {
			List<Entity> entityList = search(movie);
			
					
			if (entityList != null) {
				VideoCheckResult vcr = WordUtil.isVideo(movie);
				
				NamesEntity ne = WordUtil.getProgrammeEntity(movie, Constants.MOVIE_CATE_ID);
				addDirectEntityToMap(movie, ne, vcr.getViewUrl());
				
				if (vcr.getViewUrl() != null) {
					Entity directEntity = new Entity();
					directEntity.setWord(movie);
					directEntity.setFlag(EntityFlagUtil.DIRECTFLAG);
					//directEntity.setViewUrl(vcr.getViewUrl());
				//	addThumbToMap(directEntity.getWord(), WordUtil.getProgrammeThumb(movie));
				//	addUrlToMap(directEntity.getWord(), vcr.getViewUrl());
					
					if(!entityList.contains(directEntity)) {
						entityList.add(0, directEntity);
					}
				}
			}
			
			fixSpecialEntityList(entityList);
		}
	}
	
	public void handleAnime(Set<String> animeList) {
		for (String anime : animeList) {
			List<Entity> entityList = search(anime);
			
			if (entityList != null) {
				try {
					List<EpisodeViewUrl> viewUrls = WordUtil.getAnimeViewUrls(anime, false);  //动漫一般取最新一集的播放链接
					if (viewUrls != null && !viewUrls.isEmpty()) {
						Entity directEntity = new Entity();
						directEntity.setWord(anime);
						directEntity.setFlag(EntityFlagUtil.DIRECTFLAG);
						//directEntity.setViewUrl(vcr.getViewUrl());
					//	addThumbToMap(directEntity.getWord(), WordUtil.getProgrammeThumb(movie));
					//	addUrlToMap(directEntity.getWord(), vcr.getViewUrl());
						NamesEntity ne = WordUtil.getProgrammeEntity(anime, Constants.ANIME_CATE_ID);
						//log.info("anime ne: " + ne);						
						addDirectEntityToMap(anime, ne, viewUrls.get(0).getViewUrl());						
						//log.info("add anime to result: " + anime);
						if(!entityList.contains(directEntity)) {
							entityList.add(0, directEntity);
						}
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
			
			fixSpecialEntityList(entityList);
		}
	}

	public void handleNewestVariety() {

		Map<String, List<String[]>> newestVariety = KeywordsParser.getNewestVarietyMap();
		
		if(newestVariety == null) {
			return;
		}
		for (String variety : newestVariety.keySet()) {
			List<Entity> entityList = search(variety);
			if (entityList != null) {
				Random random = new Random();
				int entitySize = entityList.size();
				int addPosition = entitySize;
				List<String[]> newestVarietyArr = newestVariety.get(variety);
				NamesEntity ne = WordUtil.getProgrammeEntity(variety + "2011", Constants.VARIETY_CATE_ID);
				if(newestVarietyArr.size() == 0) {
					continue;
				}
				addDirectEntityToMap(variety, ne, newestVarietyArr.get(0)[0]);
				for (String[] episodeUrl : newestVariety.get(variety)) {
					boolean episodeExist = false;
					for (Entity e : entityList) {
						if (e.getWord().equals(episodeUrl[1])) {
							episodeExist = true;
							viewUrlsMap.put(e.getWord(), episodeUrl[0]);
							break;
						}
					}
					if (!episodeExist) {
						if (entitySize > 4) {
							int stepSize = 0;
							while (stepSize < 100) {
								stepSize++;
								int newAddPosition = random.nextInt(entitySize - 4);
								if (Math.abs(newAddPosition - addPosition) > 1) {
									addPosition = newAddPosition;
									break;
								}
							}

						}

						Entity e = new Entity();
						e.setWord(episodeUrl[1]);
						//e.setViewUrl(episodeUrl[0]);
						entityList.add(addPosition, e);
						viewUrlsMap.put(episodeUrl[1], episodeUrl[0]);
					}

				}
			}

			fixSpecialEntityList(entityList);
		}

	}

	public void handleInterventionWords() {
		List<String> interventionWords = WordUtil.getInterventionWords();
		for (String s : interventionWords) {
			List<Entity> entityList = search(s);
			List<TrieWordsSuggest> interventionSuggestList = WordUtil.getInterventionSuggest(s);
			for (TrieWordsSuggest suggest : interventionSuggestList) {
				Entity suggestEntity = new Entity();
				suggestEntity.setWord(s);
				//suggestEntity.setViewUrl(suggest.getViewUrl());
				entityList.set(suggest.getSort(), suggestEntity);
			}
		}
	}

	/**
	 * 添加播放链接后，去除多余的记录
	 * 
	 * @param entityList
	 * @param keyword
	 */
	private void fixSpecialEntityList(List<Entity> entityList) {

		if (entityList == null) {
			return;
		}
		try {
			int i = 0;
			for (Iterator<Entity> it = entityList.iterator(); it.hasNext();) {
				i++;
				/*
				 * //Entity e = it.next(); if (i <= PROMPTLISTSIZE) { if (e.getWord().length() > keyword.length()) { e.setCutPostion(keyword.length()); } } else
				 * { it.remove(); }
				 */
				Entity e = it.next();
				if (i > PROMPTLISTSIZE) {
					it.remove();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	public List<Entity> search(String keyword) {

		if (keyword == null || keyword.length() < 1) {
			return null;
		} else {
			TrieNode node = root;
			for (char c : keyword.toCharArray()) {
				node = node.getChild(c);

				if (node == null) {
					break;
				}
			}

			List<Entity> entityList = null;

			//if (node != null && node.getData() != null && node.getData().size() >= PROMPTLISTSIZE) {
			if (node != null && node.getData() != null) {
				entityList = node.getData();
			} else {
				/*
				 * update 2011-01-27 如果一个汉字返回结果为空，或者少于10个，则按拼音去查找。 例如：胸毛返回的结果少于10个，尝试按拼音找，则会返回熊猫的相关提示
				 */
				if (node != null) {
					entityList = node.getData();
				}

				String keywordPinyin = Converter.convert(keyword);

				node = root;
				for (char c : keywordPinyin.toCharArray()) {
					node = node.getChild(c);
					if (node == null) {
						break;
					}
				}
				if (entityList == null) {
					if (node != null) {
						entityList = node.getData();
					}
				} else {
					Set<Entity> nodeDataSet = new HashSet<Entity>(entityList);  //如果结果少于10个，直接按拼音查找，会造成结果重复。此处转换为Set, 去重。
					if (node != null && node.getData() != null) {
						nodeDataSet.addAll(node.getData());
					}
					entityList = new ArrayList<Entity>(nodeDataSet);
				}

				if (entityList != null && entityList.size() > PROMPTLISTSIZE) {
					entityList = entityList.subList(0, PROMPTLISTSIZE - 1);
				}

			}

			return entityList;
		}

		// if (keyword == null || keyword.length() < 1) {
		// return null;
		// } else {
		// TrieNode node = root;
		// for (char c : keyword.toCharArray()) {
		// node = node.getChild(c);
		// if (node == null) {
		// return null;
		// }
		// }
		//
		// List<Entity> entityList = node.getData();
		//		
		// return entityList;
		// }
	}

	public static void main(String[] args) {

		TrieTree tree = new TrieTree();
		Entity entity = new Entity();
		entity.setWord("欲望都市111欲望都市111欲望都市111欲望都市111");
		entity.setQueryCount(3);
		entity.setId(1);
		tree.insert(entity);

		Entity entity2 = new Entity();
		entity2.setWord("欲望都市1");
		entity2.setQueryCount(2);
		entity.setId(2);
		tree.insert(entity2);
		
		Entity entity3 = new Entity();
		entity3.setWord("欲望都市1");
		entity3.setQueryCount(2);
		entity.setId(3);
		tree.insert(entity3);

		System.out.println(tree.search("y"));
//		List<Entity> list = new ArrayList<Entity>();
//		Random r = new Random();
//		for (int i = 0; i < 10; i++) {
//			Entity e = new Entity();
//			e.setWord(r.nextInt() + "");
//			list.add(e);
//		}
//
//		System.out.println(tree.getAllKeys());
//		System.out.println(tree.search("w"));
//
//		tree.clear();
//		System.out.println(Converter.convert("舞蹈"));
//
//		Cost cost = new Cost();
//		cost.updateStart();
//		for (int i = 0; i < 10000; i++) {
//			Collections.sort(list, new Comparator<Entity>() {
//
//				@Override
//				public int compare(Entity o1, Entity o2) {
//					return o1.getWord().compareTo(o2.getWord());
//				}
//			});
//		}
//		cost.updateEnd();
//		System.out.println("Merge sort" + cost.getCost());
//
//		cost.updateStart();
//		for (int i = 0; i < 10000; i++) {
//			SelectionSort.sort(list, list.size());
//		}
//		cost.updateEnd();
//		System.out.println("Selection sort" + cost.getCost());

	}

	public void clear() {
		clear(root);
	}

	public void clearUselessData() {
		clearUselessData(root);
	}

	private void clear(TrieNode node) {

		if (node.getChild() != null) {
			if (node.getChild().size() > 0) {
				for (char c : node.getChild().keySet()) {
					TrieNode childNode = node.getChild(c);
					if (childNode != null) {
						clear(childNode);
						childNode.clearMap();
					}
				}
			} else {
				node.clear();
				node = null;
				return;
			}
		}

	}

	private void clearUselessData(TrieNode node) {
		// log.info("clear node: " + node.getKey());
		if (node.getChild() != null) {
			if (node.getChild().size() > 0) {
				for (char c : node.getChild().keySet()) {
					TrieNode childNode = node.getChild(c);
					if (childNode != null) {
						clearUselessData(childNode);
						childNode.clearNodeUseLessData();
					}
				}
			} else {
				node.clearNodeUseLessData();
				return;
			}
		}

	}

	public static class TrieNode implements Serializable {

		private char key;

		private Map<Character, TrieNode> child;

		private List<Entity> data;

		public TrieNode getChild(char c) {
			if (child == null) {
				return null;
			} else {
				return child.get(c);
			}
		}

		public void addData(Entity e) {
			if (data == null) {
				data = new ArrayList<Entity>();
			}

			int eidx = data.indexOf(e);
			
			if (eidx > -1) {
				Entity ee = data.get(eidx);
				if (!ee.isIdAdded(e.getId()) && ee.getId() != e.getId()) {
					ee.setQueryCount(ee.getQueryCount() + e.getQueryCount());
					ee.addQueryCount(e.getId());
				}
				// ee.setQueryCount(ee.getQueryCount() + e.getQueryCount());
			} else {
				data.add(e);
			}
			data = SelectionSort.sort(data, TrieTree.PROMPTLISTSIZE);

			/*
			 * cost.updateStart(); if (data.size() > 20) { data = data.subList(0, 20); } cost.updateEnd(); System.out.println("subList data :" +
			 * cost.getCost()); data.add(e);
			 */

		}

		public void removeData(Entity e) {
			if (data == null) {
				return;
			}
			data.remove(e.getWord());
		}

		public char getKey() {
			return key;
		}

		public void setKey(char key) {
			this.key = key;
		}

		public List<Entity> getData() {
			return data;
		}

		public void addChild(TrieNode node) {
			if (child == null) {
				child = new HashMap<Character, TrieNode>();
			}
			child.put(node.getKey(), node);

		}

		public Map<Character, TrieNode> getChild() {
			return child;
		}

		public void clear() {

			if (data != null) {
				data.clear();
				data = null;
			}
		}

		public void clearMap() {
			if (child != null) {
				child.clear();
				child = null;
			}
		}

		public void clearNodeUseLessData() { //

			if (data != null) {
				for (Entity e : data) {
					List<Integer> addedIds = e.getAddedIds();
					if (addedIds != null) {
						clearUselessDataTime += addedIds.size();
						addedIds.clear();
						addedIds = null;
					}
				}
			}
		}

	}
}