package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.gallery.*;
import com.runstate.augur.ui.viewer.*;
import java.util.*;

public class VCChillStrand extends ViewerDBCommand {
	Long bundleid;
	Long msgid;
	
	public VCChillStrand(Long bundleid,Long msgid) {
		super();
		this.msgid=msgid;
		this.bundleid=bundleid;
	}
	
	public VCChillStrand(Msg msg,boolean setting) {
		this(msg.getBundleId(),msg.getRootKnotId());
	}
	
	public void execute(Browser viewer) {
		Bundle bundle=viewer.cmdGetBundle(bundleid);

		ArrayList<Long> al=bundle.getStrandIds(msgid);

		for(Long id:al)
		{
			if(bundle.isHot(id)) bundle.setMsgHot(id,false);
		}
		
	}
}

