/**
 * CixUserInfo.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.authorinfo;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.AuthorInfo;
import java.util.Date;

public class CixAuthorInfo extends AuthorInfo
{
	static final long serialVersionUID = 5773689621780047483L;

	String resume;
	
	public CixAuthorInfo(String address)
	{
		super(address);
	}
	
	public void setResume(String resume) {
		this.resume = resume;
		setUpdateDate(new Date());
		setUpdatePending(false);
	}
	
	public String getResume() {
		return resume;
	}
	
	public String getHTML()
	{
		StringBuffer sb=new StringBuffer();
		
		if(getResume()==null)
		{
			sb.append("No resume information has been retrieved");
		}
		else
		{
			sb.append("<PRE>");
			sb.append(getResume());
		}
		
		return Controller.getController().wrapWithStyle(null,sb.toString());
	}
	
}

