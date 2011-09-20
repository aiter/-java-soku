package com.youku.search.index.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.torque.TorqueException;

import com.youku.search.index.db.LastVvManager;
import com.youku.search.index.db.NewAssembleDoc;
import com.youku.search.index.db.NewVideoManager;
import com.youku.search.index.db.VideoManager;
import com.youku.search.index.manager.AffectManager.Affect;
import com.youku.search.store.ContainerFactory;
import com.youku.search.store.ObjectType;
import com.youku.search.util.Constant;

public class NewVideoIndexManager {

	
	protected String indexPath;
	private static final int ONCE_CREATE_COUNT = 1000; //一次创建N条
	private static final int ONE_FILE_COUNT = 100000;  //每个文件存储索引多少条
	
	Writer writer = null;
	IndexFileNameManager fileNameManager = null;
	
	protected org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	
	
	public NewVideoIndexManager(String indexPath)
	{
		this.indexPath = indexPath;
	}
	
	public void initWriter(String fileName) throws Exception{
		
		File file = new File(indexPath + "/file/",fileName);
		if (file.exists()){
			throw new IllegalArgumentException("文件已经存在");
		}
		writer = new OutputStreamWriter(new FileOutputStream(file),"GBK");
		
		fileNameManager = new IndexFileNameManager(indexPath + "/name/");
	}
	public void closeWriter () throws IOException {
		if (writer == null)
		{
			_log.error("error:boost writer is null!!");
			return;
		}
		
		writer.close();
		writer = null;
	}
	
	public int createIndex(int start,int end)
	{
		return createIndex(start,end,IndexFileType.create);
	}
	
	
	/**
	 * 创建索引
	 * @param start 索引起始位置
	 * @param end  索引结束位置
	 */
	public int createIndex(int start,int end,IndexFileType type)
	{
		int total = 0;
		_log.info("create index start=" + start + " end=" +end);
		
		String fileName = FileNameManager.generateFileName(type);
		
		_log.info(fileName);
		
		try {
			initWriter(fileName);
		} catch (Exception e1) {
			_log.error(e1.getMessage(),e1);
			return 0;
		}
		
		if(type == IndexFileType.create){
			//初始化vv表名
			String table = LastVvManager.getInstance().initTable();
			_log.info("vv表："+table);
		}
		int max = 0;
		int currentFileCount = 0;
		try {
			max = VideoManager.getInstance().getMaxId();
			
			end = Math.min(max, end);
			
			_log.info("update create index start=" + start + " end=" +end);
			
			int starttimes = (start + ONCE_CREATE_COUNT-1)/ONCE_CREATE_COUNT;
			int totaltimes = (end+ONCE_CREATE_COUNT-1)/ONCE_CREATE_COUNT;
			_log.info("starttimes="+starttimes + "  totaltimes="+totaltimes);
			for (int i = starttimes ;i <= totaltimes; i++)
			{
				int _start;
				int _end;
				
				if (i == starttimes){
					_start = start;
					_end = Math.min(i*ONCE_CREATE_COUNT, end);;
				}
				else if (i == totaltimes-1){
					_start = (i-1)*ONCE_CREATE_COUNT;
					_end = end;
				}
				else{
					_start = (i-1)*ONCE_CREATE_COUNT;
					_end = i*ONCE_CREATE_COUNT;
				}
				
				int count = 0;
				if (type == IndexFileType.create){
					count = createValidFile(_start,_end);
				}
				else if (type == IndexFileType.alldelete){
					count = createInvalidFile(_start,_end);
				}
				
				if (count == -1)
				{
					//数据库异常
					total = -1;
					break;
				}
				else
				{
					total += count;
					currentFileCount += count;
					
					if (currentFileCount >= ONE_FILE_COUNT ){  
						//关闭原来writer
						this.closeWriter();
						//记录该文件创建完毕
						fileNameManager.touch(fileName);
						
						//生成新文件名
						fileName = FileNameManager.generateFileName(type);
						//生成新文件writer
						initWriter(fileName);
						
						//currentFileCount清零
						currentFileCount = 0;
					}
				}
			}
			fileNameManager.touch(fileName);
		}
		catch (Exception e) {
			_log.error(e.getMessage(),e);
			return 0;
		}
		finally{
			try {
				closeWriter();
			} catch (IOException e) {
				_log.error(e.getMessage(),e);
			}
		}
		
		_log.info("end create index, total = "+ total);
		return total;
	}
	
	
	/**
	 * 创建索引
	 * @param start 索引起始位置
	 * @param end  索引结束位置
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public int createValidFile(int start,int end) 
	{
		long time1 =System.currentTimeMillis();
		
		List<NewAssembleDoc> list;
		try {
			list = NewVideoManager.getInstance().getVideos(start,end);
		} catch (TorqueException e1) {
			return -1;
		}
		try
		{
			if (list != null && list.size() > 0)
			{
				StringBuilder builder = new StringBuilder();
				
				for (NewAssembleDoc doc:list)
				{
					if (doc != null && doc.getIndex() != null && doc.getStore() != null){
						
						//索引
						builder.append(doc.getIndex()).append("\n");
						
						//存储
						String key = ObjectType.YOUKU_VIDEO.getKey(doc.getKey());
						ContainerFactory.getContainer().push(key, doc.getStore());
					}
				}
				
				if (builder.length() > 0)
					writer.write(builder.toString());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			_log.error(e.getMessage(),e);
		}
		_log.info("start=" + start + ",end="+end + "  cost:" + (System.currentTimeMillis() - time1));
		return list!=null?list.size():0;
	}
	
	/**
	 * 创建所有删除视频的docid
	 * @param start
	 * @param end
	 * @return
	 */
	public int createInvalidFile(int start,int end) 
	{
		long time1 =System.currentTimeMillis();
		
		List<Integer> list = NewVideoManager.getInstance().getInvalidVideoList(start,end);
		
		try
		{
			if (list != null && list.size() > 0)
			{
				StringBuilder builder = new StringBuilder();
				
				for (int id:list)
				{
					//索引
					builder.append(id).append("\n");
				}
				
				if (builder.length() > 0)
					writer.write(builder.toString());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			_log.error(e.getMessage(),e);
		}
		_log.info("create all delete start=" + start + ",end="+end + "  cost:" + (System.currentTimeMillis() - time1));
		return list!=null?list.size():0;
	}
	
	public int addIndex(){
		Date lastUpdateTime = LastUpdateContainer.get(com.youku.search.util.Constant.QueryField.VIDEO);
		int rows = 0;
		Date endTime = new Date();
		
		try
		{
			_log.info("video start=" + lastUpdateTime + ",end = " + endTime);
			List<NewAssembleDoc> list = NewVideoManager.getInstance().getVideos(lastUpdateTime,endTime);
			
			_log.info("video新增索引：" + list.size());
			if (list != null && list.size() > 0)
			{
				String fileName = FileNameManager.generateFileName(IndexFileType.add);
				
				try {
					initWriter(fileName);
				} catch (Exception e1) {
					_log.error(e1.getMessage(),e1);
					return 0;
				}
				
				StringBuilder builder = new StringBuilder();
				
				for (NewAssembleDoc doc:list)
				{
					if (doc != null && doc.getIndex() != null && doc.getStore() != null){
						
						//索引
						builder.append(doc.getIndex()).append("\n");
						
						//存储
						String key = ObjectType.YOUKU_VIDEO.getKey(doc.getKey());
						ContainerFactory.getContainer().push(key, doc.getStore());
					}
				}
				
				if (builder.length() > 0)
					writer.write(builder.toString());
				
				fileNameManager.touch(fileName);
				
				LastUpdateContainer.put(Constant.QueryField.VIDEO,endTime);
			}
		}catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
		finally
		{
			try {
				closeWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return rows;
	} 
	
	public int deleteIndex(){
		int row = 0;

		List<Affect> deleteAffects = AffectManager.getInstance().getAffects(AffectManager.Type.VIDEO,AffectManager.DELETE);
		
		if (deleteAffects != null && deleteAffects.size() > 0){
			String fileName = FileNameManager.generateFileName(IndexFileType.delete);
			
			try {
				initWriter(fileName);
				
				for (Affect affect:deleteAffects){
					writer.append(String.valueOf(affect.getPk_affect())).append("\n");
//					AffectManager.getInstance().delete(affect.getPk_affect());
					row ++;
				}
				
				fileNameManager.touch(fileName);
				
			} catch (Exception e1) {
				_log.error(e1.getMessage(),e1);
				return 0;
			}
			finally{
				try {
					closeWriter();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return row;
	}
	
	public int updateIndex(){
		int row = 0;
		
		List<Affect> vids_renew = AffectManager.getInstance().getAffects(AffectManager.Type.VIDEO,AffectManager.RENEW);
		List<Affect> vids_mobile_complete = AffectManager.getInstance().getAffects(AffectManager.Type.VIDEO,AffectManager.MOBILE_CODING_OVER);
		List<Affect> vids_update = AffectManager.getInstance().getAffects(AffectManager.Type.VIDEO,AffectManager.UPDATE);
		
		
		_log.info("取消屏蔽数量：" + vids_renew.size());
		_log.info("移动格式转码完成更新数量：" + vids_mobile_complete.size());
		_log.info("视频更新数量：" + vids_update.size());
		
		String fileName = FileNameManager.generateFileName(IndexFileType.update);
		
		try {
			initWriter(fileName);
			
			updateVideos(vids_renew);
			updateVideos(vids_mobile_complete);
			updateVideos(vids_update);
			
			fileNameManager.touch(fileName);
			
		} catch (Exception e1) {
			_log.error(e1.getMessage(),e1);
			return 0;
		}
		finally{
			try {
				closeWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return row;
	}
	
	
	private int updateVideos(List<Affect> vids) throws CorruptIndexException, IOException
	{
		int row = 0;
		if (vids !=null && vids.size()>0){
			_log.info("更新数量：" + vids.size());
			StringBuilder builder = new StringBuilder();
			
			for (Affect affect:vids){
				
				NewAssembleDoc doc =  NewVideoManager.getInstance().getVideoAsDocument(affect.getPk_id());
					
				if (doc != null && doc.getIndex() != null && doc.getStore() != null){
					
					_log.info("update video id=" + affect.getPk_id());
					
					builder.append(doc.getIndex()).append("\n");
					
					//存储
					String key = ObjectType.YOUKU_VIDEO.getKey(doc.getKey());
					ContainerFactory.getContainer().push(key, doc.getStore());
					
					//删除源
//					AffectManager.getInstance().delete(affect.getPk_affect());
					
					row++;
				}
				
			}
			
			if (builder.length() > 0)
				writer.write(builder.toString());
		}
		return row;
	}
	
}

class IndexFileNameManager{
	String path = null;
	IndexFileNameManager (String path){
		this.path = path;
	}
	public  void touch(String fileName){
		File file = new File(path + fileName);
		
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
