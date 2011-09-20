/**
 * 
 */
package com.youku.search.index.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Database;

/**
 * @author 1verge
 * 
 */
public class AffectManager {

	private static final String AFFECT_PATH = "/opt/affect/";
	private static final String AFFECT_FILE_NAME = "affect";

	private static final File directory = new File(AFFECT_PATH);

	static {
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	protected org.apache.log4j.Logger _log = org.apache.log4j.Logger
			.getLogger("INDEXLOG");
	public static final int DELETE = 0;
	public static final int UPDATE = 1;
	public static final int RENEW = 2;
	public static final int MOBILE_CODING_OVER = 3;
	public static final int PROCESSED = 100; // 索引管理机器已经处理过的

	public static final int MAX_OP_COUNT = 10000;// 最大操作数量，防止阻塞

	private AffectManager() {

	}

	private static AffectManager self = null;

	public synchronized static AffectManager getInstance() {

		if (self == null) {
			self = new AffectManager();
		}
		return self;
	}

	public enum Type {
		VIDEO, USER, PK, FOLDER
	}

	@SuppressWarnings("unchecked")
	public List<Affect> getAffects(Type type, int opType) {
		ArrayList<Affect> list = new ArrayList<Affect>();
		Connection conn = null;
		try {
			conn = Database.getAffectConnection();
			List<Record> rs = BasePeer
					.executeQuery("select * from t_affect_new where type='"
							+ type.name() + "' and optype=" + opType
							+ " order by pk_affect"
							+ " limit " + MAX_OP_COUNT ,
							false, conn);
			if (rs != null && rs.size() > 0) {
				for (Record record : rs) {
					Affect affect = new Affect();
					affect.setPk_affect(record.getValue("pk_affect").asInt());
					affect.setPk_id(record.getValue("id").asInt());
					affect.setType(record.getValue("type").asString());
					list.add(affect);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception e) {
			}
		}
		return list;

	}

	public void delete(int pk_affect) {
		Connection conn = null;
		try {
			conn = Database.getAffectConnection();
			String sql = "delete from t_affect_new where pk_affect="
					+ pk_affect;
			BasePeer.executeStatement(sql, conn);

		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception e) {
			}
		}
	}


	public void insert(int id, Type type, int optype) {
		try {
			BasePeer.executeStatement(
					"insert into t_affect_new (id,type,optype) values(" + id
							+ ",'" + type.name() + "'," + optype + ")",
					"affect");
		} catch (TorqueException e) {
			e.printStackTrace();
		}
	}

	private HashMap<Type,FileWriter> writerMap = new HashMap<Type,FileWriter>();
	
	/**
	 * 初始化
	 * 
	 * @param type
	 * @param create
	 */
	public synchronized void initWriter(Type type,boolean create) {
		
		FileWriter writer = writerMap.get(type);
		if (writer!= null){
			try {
				closeWriter(type);
			} catch (IOException e) {
				_log.error(e.getMessage(),e);
			}
		}
		
		String filePath = getAbsoluteFile(type);
		File affectFile = new File(filePath);
		if (create) {
			if (affectFile.exists()) {
				_log.info(affectFile.getAbsoluteFile() + " 存在");
				rename(affectFile);
			} else {
				_log.info(affectFile.getAbsoluteFile() + " 不存在");
			}
		}
		try {
			affectFile.createNewFile();
			writer = new FileWriter(affectFile);
			writerMap.put(type, writer);
		} catch (IOException e) {
			_log.error(e.getMessage(), e);
		}

	}

	/**
	 * 把多条记录保存到文件，并从数据库删除
	 * @param type
	 * @param affects
	 */
	public void saveToFile(Type type,List<Affect> affects) {
		FileWriter writer = writerMap.get(type);
		if (writer == null) {
			_log.error("error:writer没有被初始化!!");
			return;
		}
		if (affects != null) {
			for (Affect affect : affects) {
				saveToFile(type,affect);
			}
		}
	}

	/**
	 * 把单条记录保存到文件，并从数据库删除
	 * @param type
	 * @param affect
	 */
	public void saveToFile(Type type,Affect affect) {
		FileWriter writer = writerMap.get(type);
		if (writer == null) {
			_log.error("error:writer没有被初始化!!");
			return;
		}
		if (affect == null)
			return;

		synchronized(writer){
			try {
				writer.write(affect.getPk_id() + "\r\n");
				//删除数据库中的数据
				delete(affect.getPk_affect());
			} catch (IOException e) {
				_log.error(e.getMessage(), e);
			}
		}
	}

	public void closeWriter(Type type) throws IOException {
		FileWriter writer = writerMap.get(type);
		if (writer == null) {
			_log.error("error:affect writer is null!!");
			return;
		}

		writer.close();
		writer = null;
	}
	
	public List<Integer> getAllDeleteFromFile(Type type) {
		String[] files = directory.list();
		if (files != null) {
			HashSet<Integer> set = new HashSet<Integer>();
			for (String file : files) {
				if (file.equals(type.name()))
				{
					HashSet<Integer> one = readFromFile(type,file);
					set.addAll(one);
				}
			}
			List<Integer> result = new ArrayList<Integer>();
			result.addAll(set);
			return result;
		}
		return null;
	}

	/**
	 * 从文件中读取所有符合类型的id
	 * @param type
	 * @param filename
	 * @return
	 */
	public HashSet<Integer> readFromFile(Type type,String filename) {
		HashSet <Integer> list = new HashSet<Integer>();

		File file = new File(AFFECT_PATH + filename);
		if (file.exists()) {
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(
						new FileInputStream(file)));
				String line = null;

				while ((line = r.readLine()) != null) {
						list.add(DataFormat.parseInt(line));
				}
				r.close();
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}
		return list;
	}

	/**
	 * 删除一天前的文件
	 */
	public void cleanOldFile(Type type,int hour) {
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				//删除一天前的文件
				if (file.getName().startsWith(type.name()))
				{
					if (System.currentTimeMillis() - file.lastModified() > (hour * 60 * 60 * 1000)){
						_log.info("删除文件："+ file.getName());
						file.delete();
					}
				}
			}
		}
	}

	private String getAbsoluteFile(Type type) {
		return AFFECT_PATH + type.name()+ "_" + AFFECT_FILE_NAME;
	}

	private void rename(File file) {
		SimpleDateFormat fmtDate = new SimpleDateFormat ( ) ;
		fmtDate.applyPattern ( "yyyy-MM-dd_HH_mm" ) ;
		String subfix = fmtDate.format ( new Date() );

		file.renameTo(new File(file.getAbsoluteFile() + "." + subfix));

		_log.info("rename to :" + (file.getAbsoluteFile() + "." + subfix));
	}

	public class Affect {
		private int pk_affect;
		private int pk_id;
		private String type;

		public int getPk_affect() {
			return pk_affect;
		}

		public void setPk_affect(int pk_affect) {
			this.pk_affect = pk_affect;
		}

		public int getPk_id() {
			return pk_id;
		}

		public void setPk_id(int pk_id) {
			this.pk_id = pk_id;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}
	
	public static void main(String[] args){
//		AffectManager.getInstance().initWriter(AffectManager.Type.VIDEO,true);
//		Affect affect = new AffectManager().new Affect();
//		affect.setPk_affect(1);
//		affect.setPk_id(1);
//		affect.setType("VIDEO");
//		AffectManager.getInstance().saveToFile(Type.VIDEO, affect);
//		try {
//			AffectManager.getInstance().closeWriter(Type.VIDEO);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		List<Integer> ids = AffectManager.getInstance().getAllDeleteFromFile(Type.VIDEO);
//		if (ids != null){
//			for (int id:ids){
//				System.out.println(id);
//			}
//		}
		
		AffectManager.getInstance().cleanOldFile(Type.VIDEO,23);
	}
}
