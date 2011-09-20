/**
 * 
 */
package com.youku.search.index;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;

import com.youku.search.util.Constant;

/**
 * @author william
 * 
 */
public class CmdManager {
	
	protected static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	
	public static final String command = "/bin/sh";
	public static final String pathPrefix = "/opt/sh/";
	public static final String rsyncIndexFile = "rsync_index.sh";
	public static final String moveIndexFile = "mv_index.sh";

	/**
	 * 同步所有索引
	 * 
	 * @param type
	 * @return
	 */
	public static int rsyncIndex(int type) {
		return rsyncIndex(type, null, 0);
	}

	/**
	 * 同步索引
	 * 
	 * @param type
	 * @param ip
	 * @param order
	 * @return
	 */
	public static int rsyncIndex(int type, String ip, int order) {
		
		int value = 0;
		
		String[] args = new String[]{pathPrefix+rsyncIndexFile , Constant.QueryField.getNameString(type) , order+"" , ip}; 
		value = Exec(command,args);
		
		_log.info("rsyncIndex " + type + " " + order + ":" + value);
		return value;
	}

	/**
	 * 将索引移动到online文件夹下
	 * 
	 * @param type
	 * @param order
	 * @return
	 */

	public static int moveIndex(int type, int order) {
		int value = 0;

		String[] args = new String[]{pathPrefix+moveIndexFile , Constant.QueryField.getNameString(type) , order+""}; 
		
		value = Exec(command ,args);

		_log.info("moveIndex " + type + " " + order + ":" + value);
		return value;
	}

	public static int Exec(String cmd , String... args)
	{
		_log.info(cmd);
		
		int value = 0;
		LogOutputStream log = new LogOutputStream(){
			protected void processLine(String arg0, int arg1) {
				_log.info(arg0);
				
			}
		};
		PumpStreamHandler handler = new PumpStreamHandler(log);
		
		Executor exec = new DefaultExecutor();
		exec.setStreamHandler(handler);
		
		CommandLine cl = new CommandLine(cmd);
		for (String arg:args){
			if (arg != null && arg.length() >0)
				cl.addArgument(arg);
		}
		 try {
			 value = exec.execute(cl);
		} catch (Exception e1) {
			_log.error(e1.getMessage(),e1);
		} 

		return value;
	}
	
	
	public static void main(String[] args) {
	}

}
