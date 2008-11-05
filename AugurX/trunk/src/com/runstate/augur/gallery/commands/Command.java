/**
 * Command.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.commands;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Bundle;
import com.runstate.util.RandomGUID;
import java.io.Serializable;
import java.util.Date;

public class Command implements Serializable
{
	static final long serialVersionUID = -3875375477194393738L;

	String id;
	Date created;
	Date whendone;
	Long doorid;
	Long bundleid;
	boolean hold=false;
	boolean keep=false;
	boolean done=false;
	
	transient Controller controller=null;
	
	public Command(Long doorid,Long bundleid)
	{
		id=new RandomGUID().toString();
		created=new Date();
		if(doorid==null)
		{
			if(bundleid==null)
			{
				throw new RuntimeException("Tried to create command with no door or bundleid");
			}
			
			this.doorid=Controller.getGallery().getDoorIdByBundleId(bundleid);
		}
		else
		{
			this.doorid=doorid;
		}
		
		this.bundleid=bundleid;
		
		hold=false;
		keep=false;
		done=false;
	}
	
	/**
	 * Sets Bundleid
	 *
	 * @param    Bundleid            a  long
	 */
	public void setBundleid(Long bundleid)
	{
		this.bundleid = bundleid;
		this.doorid=Controller.getGallery().getDoorIdByBundleId(bundleid);
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
		if(bundleid==null) return null;
		return Controller.getController().getGallery().getBundleManager().getBundle(bundleid).getBundlename();
	}

	/**
	 * Sets Door
	 *
	 * @param    Door                a  long
	 */
	public void setDoorid(Long door)
	{
		this.doorid = door;
	}
	
	/**
	 * Returns Door
	 *
	 * @return    a  long
	 */
	public Long getDoorid()
	{
		return doorid;
	}
	
	/**
	 * Sets Whendone
	 *
	 * @param    Whendone            a  Date
	 */
	public void setWhendone(Date whendone) {
		this.whendone = whendone;
	}
	
	/**
	 * Returns Whendone
	 *
	 * @return    a  Date
	 */
	public Date getWhendone() {
		return whendone;
	}
	
	/**
	 * Sets Done
	 *
	 * @param    Done                a  boolean
	 */
	public void setDone(boolean done) {
		this.done = done;
	}
	
	/**
	 * Returns Done
	 *
	 * @return    a  boolean
	 */
	public boolean isDone() {
		return done;
	}
	
	/**
	 * Sets Keep
	 *
	 * @param    Keep                a  boolean
	 */
	public void setKeep(boolean keep) {
		this.keep = keep;
	}
	
	/**
	 * Returns Keep
	 *
	 * @return    a  boolean
	 */
	public boolean isKeep() {
		return keep;
	}
	
	/**
	 * Sets Onhold
	 *
	 * @param    Onhold              a  boolean
	 */
	public void setHold(boolean onhold) {
		this.hold = onhold;
	}
	
	/**
	 * Returns Onhold
	 *
	 * @return    a  boolean
	 */
	public boolean isHold() {
		return hold;
	}
	
	/**
	 * Sets Id
	 *
	 * @param    Id                  a  String
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns Id
	 *
	 * @return    a  String
	 */
	public String getId() {
		return id;
	}
	
/**
	 * Sets Created
	 *
	 * @param    Created             a  Date
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	
	/**
	 * Returns Created
	 *
	 * @return    a  Date
	 */
	public Date getCreated() {
		return created;
	}
	
//	/**
//	 * Sets Path
//	 *
//	 * @param    Path                a  String
//	 */
//	public void setAugurPathRef(BundleRef path) {
//		this.augurPathRef = path;
//	}
//
//	/**
//	 * Returns Path
//	 *
//	 * @return    a  String
//	 */
//	public BundleRef getAugurPathRef() {
//		return augurPathRef;
//	}
	
	public String getCommandType()
	{
		return "Empty Command";
	}
	
	public boolean isEditable() { return false; }
	
	public Parameter[] getParameters()
	{
		return null;
	}
	
	public boolean setParameters(Parameter[] params)
	{
		return false;
	}
}

