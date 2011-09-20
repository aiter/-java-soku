/**
 * 
 */
package com.youku.soku.library.load.form;

import java.util.Date;

/**
 * 节目-视频剧集    信息，对应中间层的show
 * @author liuyunjian
 * 2011-2-23
 */
public class ProgrammeEpisodeBo {



	public int id;

	public int fkProgrammeSiteId;

	public String title;

	public int orderId;
	
	public int orderStage;

	public int viewOrder;

	public String logo;

	public double seconds;

	public int hd = 0;

	public String url;

	public String source = "000";

	public Date updateTime;

	public Date createTime;
	
	public boolean updated;//是否更新过，统计用

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("id:").append(id)
		.append(" fkProgrammeSiteId:").append(fkProgrammeSiteId)
		.append(" source:").append(source)
		.append(" updateTime:").append(updateTime)
		.append(" createTime:").append(createTime)
		.append(" title:").append(title)
		.append(" viewOrder:").append(viewOrder)
		.append(" orderId:").append(orderId)
		.append(" orderStage:").append(orderStage)
		.append(" seconds:").append(seconds)
		.append(" hd:").append(hd)
		.append(" logo:").append(logo)
		.append(" url:").append(url);
		
		return sBuilder.toString();
	}
    
    
}
