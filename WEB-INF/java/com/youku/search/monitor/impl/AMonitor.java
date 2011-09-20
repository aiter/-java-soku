/**
 * 
 */
package com.youku.search.monitor.impl;

import com.youku.search.monitor.Monitor;
import com.youku.search.monitor.Person;

/**
 * @author 1verge
 *
 */
public abstract class AMonitor extends Monitor{
	/**
	 * @param mail
	 */
	public AMonitor() {
		super(Person.luwei);
	}
}
