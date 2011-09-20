/**
 * 
 */
package com.youku.search.index.entity;

import java.io.Serializable;

import com.youku.search.sort.core.entity.Page;

/**
 * @author 1verge
 *
 */
public class BasePageQuery implements Serializable,Cloneable{

	public int start; //数据开始行
	public int end;	//数据结束行
	
	public transient Page indexPage;
}
