/**
 * 
 */
package com.youku.soku.library.load.form;

import java.util.Date;

/**
 * 节目基本信息，对应中间层的show
 * @author liuyunjian
 * 2011-2-22
 */
public class ProgrammeBo {

    public int id;//数据库ID

    public int contentId;//中间层ID

    public String name;//名称
    
    public String alias;//别名，多个用|分隔

    public int cate;//分类

    public int episodeTotal = 0;//正片总集数

    public String source = "000";//信息来源，1优酷版权、2站外版权、3、自动发现

    public Date updateTime;//更新时间

    public Date createTime;//添加时间

    public String state;//节目状态：normal;limited,check;checking;blocked;deleted;delayed;paycheck

    public int blocked = 0;//是否被屏蔽,1为屏蔽
    
    public boolean updated;//是否更新过，统计用

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("id:").append(id)
		.append(" cate:").append(cate)
		.append(" source:").append(source)
		.append(" updateTime:").append(updateTime)
		.append(" createTime:").append(createTime)
		.append(" state:").append(state)
		.append(" blocked:").append(blocked)
		.append(" contentId:").append(contentId)
		.append(" episodeTotal:").append(episodeTotal)
		.append(" name:").append(name)
		.append(" alias:").append(alias);
		
		return sBuilder.toString();
	}
    
    
}
