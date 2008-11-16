/**
 * PathInfo.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.GalleryException;
import com.runstate.augur.gallery.Poolable;
import com.runstate.augur.gallery.commands.Command;
import java.io.Serializable;
import java.util.Date;

public class PathInfo extends Poolable implements Serializable {
		
	static final long serialVersionUID = -5744371142003726526L;
	
	public static final String REFRESH="@refresh";
	
	boolean refreshpending=false;
	
	/**
	 * Sets Refreshpending
	 *
	 * @param    refreshpending      a  boolean
	 */
	public void setRefreshpending(boolean refreshpending)
	{
		this.refreshpending = refreshpending;
	}
	
	/**
	 * Returns Refreshpending
	 *
	 * @return    a  boolean
	 */
	public boolean isRefreshpending()
	{
		return refreshpending;
	}
	
	/**
	 * Sets Doorid
	 *
	 * @param    doorid              a  long
	 */
	public void setDoorid(Long doorid)
	{
		this.doorid = doorid;
	}
	
	/**
	 * Returns Doorid
	 *
	 * @return    a  long
	 */
	public Long getDoorid()
	{
		return doorid;
	}
	
	public static String getPoolName()
	{
		return "pathinfo";
	}

	public PathInfo(Long doorid,Long bundleid)
	{
		this.doorid=doorid;
		this.bundleid=bundleid;
	}
	
	public String getKey() {
		return bundleid.toString();
	}
	
	/**
	 * Method getPool
	 *
	 * @return   a String
	 *
	 */
	public String getPool() {
		return "pathinfo";
	}
	
	Long bundleid=null;
	Long doorid=null;

	
	/**
	 * Sets Bundleid
	 *
	 * @param    bundleid            a  long
	 */
	public void setBundleid(Long bundleid)
	{
		this.bundleid = bundleid;
	}
	
	/**
	 * Returns Bundleid
	 *
	 * @return    a  long
	 */
	public Long getBundleid()
	{
		return bundleid;
	}
	
	public String getBundleName()
	{
		return Controller.getController().getGallery().getBundleManager().getBundle(bundleid).getBundlename();
	}


	public Command getRefreshCommand()
	{
		return null;
	}
	
	public String getReportText()
	{
		return "";
	}
	
	public String getUIClassName() { return null; }
	
	public boolean doCommand(String command)
	{
		if(REFRESH.equals(command))
		{
			try {
				Controller.getGallery().addCommand(getRefreshCommand());
				setRefreshpending(true);
				Controller.getGallery().updatePathInfo(this);
//				showMessage("Will refresh "+getBundleName()+" on next sync");
			} catch (GalleryException e) { e.printStackTrace(); }
			return true;
		}
		else if(command.startsWith("@join("))
		{
		}
		return false;
	}
	
	Date updateDate=null;
	
	public Date getUpdateDate()
	{
		return updateDate;
	}
	
	public void setUpdateDate(Date date)
	{
		updateDate=date;
	}
	
	String signature=null;
	
	public String getSignature()
	{
		return signature;
	}
	
	public void setSignature(String sig)
	{
		signature=sig;
	}
	
}

