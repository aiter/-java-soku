package com.youku.search.util;

import java.util.ArrayList;
import java.util.List;

import com.youku.analyzer.danga.MemCached.MemCachedClient;
import com.youku.analyzer.danga.MemCached.SockIOPool;

public class WordUtil {
	public static final String NATURE_D_A  = "40000000"; //形容词 形语素
	public static final String NATURE_D_B  = "20000000"; //区别词 区别语素
	public static final String NATURE_D_C  = "10000000"; //连词 连语素
	public static final String NATURE_D_D  = "08000000"; //副词 副语素
	public static final String NATURE_D_E  = "04000000"; //产品词
	public static final String NATURE_D_F  = "02000000"; //方位词 方位语素
	public static final String NATURE_D_I  = "01000000"; //成语
	public static final String NATURE_D_L  = "00800000"; //习语
	public static final String NATURE_A_M  = "00400000"; //数词 数语素
	public static final String NATURE_D_MQ = "00200000"; //数量词
	public static final String NATURE_D_N  = "00100000"; //名词 名语素
	public static final String NATURE_D_O  = "00080000"; //拟声词
	public static final String NATURE_D_P  = "00040000"; //介词
	public static final String NATURE_A_Q  = "00020000"; //量词 量语素
	public static final String NATURE_D_R  = "00010000"; //代词 代语素
	public static final String NATURE_D_S  = "00008000"; //处所词
	public static final String NATURE_D_T  = "00004000"; //时间词
	public static final String NATURE_D_U  = "00002000"; //助词 助语素
	public static final String NATURE_D_V  = "00001000"; //动词 动语素
	public static final String NATURE_D_W  = "00000800"; //标点符号
	public static final String NATURE_D_X  = "00000400"; //非语素字
	public static final String NATURE_D_Y  = "00000200"; //语气词 语气语素
	public static final String NATURE_D_Z  = "00000100"; //状态词
	public static final String NATURE_A_NR = "00000080"; //人名
	public static final String NATURE_A_NS = "00000040"; //地名
	public static final String NATURE_A_NT = "00000020"; //机构团体
	public static final String NATURE_A_NX = "00000010"; //外文词
	public static final String NATURE_A_NZ = "00000008"; //其他专名
	public static final String NATURE_D_H  = "00000004"; //前接成分
	public static final String NATURE_D_K  = "00000002"; //后接成分
	public static final String encoding = "4"; //1 gbk，2,big5,4,utf-8

	public static List<Entity> getTokenizeResult(String word){
		MemCachedClient mc = new MemCachedClient();
		String result = (String)mc.keyword(word, encoding);
		String[] r = result.split(" ");
		if(r.length%3!=0){
			return null;
		}
		List<Entity> l = new ArrayList<Entity>();
		for(int i=0;i<r.length;i+=3){
			Entity e = new Entity(r[i],r[i+1],r[i+2]);
			l.add(e);
		}
		return l;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 		String[] serverlist     = { "10.101.6.55:12344" };

				SockIOPool pool = SockIOPool.getInstance();
				pool.setServers(serverlist);

				pool.setInitConn(5);
				pool.setMinConn(5);
				pool.setMaxConn(50);
				pool.setMaintSleep(30);

				pool.setNagle(false);
				pool.initialize();
		 		List<Entity> list = WordUtil.getTokenizeResult("我爱北京天安门天安门上红旗飘");
		 		for(Entity e:list){
		 			System.out.println(e.getWord());
		 			System.out.println(e.getPos());
		 			System.out.println(e.getWeight());
		 		}
	}

}
class Entity{
	public Entity(String word, String pos, String weight) {
		this.word = word;
		this.pos = pos;
		this.weight = weight;
		// TODO Auto-generated constructor stub
	}
	private String word;
	private String pos;
	private String weight;
	public String getWord() {
		return word;
	}
	public String getPos() {
		return pos;
	}
	public String getWeight() {
		return weight;
	}
}
