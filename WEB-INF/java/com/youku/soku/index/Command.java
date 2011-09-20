/**
 * 
 */
package com.youku.soku.index;

import com.youku.search.index.CmdManager;

/**
 * @author 1verge
 *
 */
public class Command extends CmdManager{
	public static int rsyncIndex(int group,String ip) {
		return CmdManager.Exec(command ,new String[]{pathPrefix + "rsync_index.sh", group+"",ip});
	}
	public static int rsyncWord(int group,String ip) {
		System.out.println(command + " " + pathPrefix + "rsync_word.sh "+ group + " "+ip);
		return CmdManager.Exec(command ,new String[]{pathPrefix + "rsync_word.sh", group+"",ip});
	}
}
