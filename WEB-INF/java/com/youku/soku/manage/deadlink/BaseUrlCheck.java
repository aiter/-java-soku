package com.youku.soku.manage.deadlink;

import org.apache.torque.TorqueException;

import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;

public abstract class BaseUrlCheck {
	
	public abstract void checkAll();
	
	protected void markDeadLinkEpisode(ProgrammeEpisode pe) throws TorqueException {
		/*pe.setUrl("");
		pe.setLogo("");
		pe.setSeconds(0);
		pe.setHd(0);
		pe.setTitle("");*/
		ProgrammeEpisodePeer.doDelete(pe.getPrimaryKey());
	}
}
