/**
 * 
 */
package com.youku.soku.library.load.form;

import java.util.Date;

/**
 * 节目站点    信息，对应中间层的show
 * @author liuyunjian
 * 2011-2-22
 */
public class ProgrammeSiteBo {
	public int id;//数据库ID

	public int fkProgrammeId;//节目的数据库ID

	public int sourceSite;//网站ID

	public int orderId = 10;//排序

	public String firstLogo;//第一个视频的logo-url

	public int completed = 0;//是否完成，中间层的completed==1,并且episode_total==episode_collected

	public int blocked = 0;//是否屏蔽

	public int midEmpty = 0;//中间是否有空集

	public int episodeCollected = 0;//已收录集数

	public String source = "000";//版权

	public int otherSiteCompleted = 0;//表示站外版权是否收录完成,1为收录完成

	public Date updateTime;

	public Date createTime;
	
	public boolean updated;//是否更新过，统计用
	
	public ProgrammeBo programmeBo;//节目的公共信息

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("[").append(programmeBo==null?"":programmeBo).append("]")
		.append(" id:").append(id)
		.append(" fkProgrammeId:").append(fkProgrammeId)
		.append(" source:").append(source)
		.append(" updateTime:").append(updateTime)
		.append(" createTime:").append(createTime)
		.append(" sourceSite:").append(sourceSite)
		.append(" blocked:").append(blocked)
		.append(" orderId:").append(orderId)
		.append(" completed:").append(completed)
		.append(" midEmpty:").append(midEmpty)
		.append(" otherSiteCompleted:").append(otherSiteCompleted)
		.append(" firstLogo:").append(firstLogo)
		.append(" episodeCollected:").append(episodeCollected);
		
		return sBuilder.toString();
	}
    
    
}
