package com.youku.soku.newext.loader;


public class UpdateTask /**extends java.util.TimerTask*/ {/*
	// 初始调用时，将此值设置为soku_extinfo.bin修改日期向前推一天
	private static Date lastUpdate;
	private static Log logger=LogFactory.getLog(UpdateTask.class);

	@Override
	public void run() {
		try {
			logger.info("更新内存中的Map信息...(间隔:一个小时)");
			Cost cost = new Cost();
			if(lastUpdate==null){
//				如果lastUpdate没有值，则将其设置为（直达区文件的修改时间）
				String extFileName=System.getProperty("java.io.tmpdir")+"soku_extinfo.bin";
				File extFile=new File(extFileName);
				if(!extFile.exists()){
					logger.error("extFile 不存在，请查明原因！	");
					Calendar calendar=Calendar.getInstance();
					calendar.add(Calendar.DAY_OF_MONTH, -2);
					lastUpdate=calendar.getTime();
				}else{
					lastUpdate=new Date(extFile.lastModified());
				}
				
			}
//			更新lastUpdate为当前时间，然后开始更新
			Date startDate=lastUpdate;
			lastUpdate=new Date();
			
			new UpdateMovie().doUpdate(startDate,lastUpdate);
			
			cost.updateEnd();
			logger.info("更新extInfo信息完毕, cost: " + cost.getCost());

		} catch (Exception e) {
			logger.error("更新 extInfo信息发生异常", e);
		}
	}
*/}