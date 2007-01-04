/**
 * UserInfo.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery;


import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.Poolable;
import java.io.Serializable;
import java.util.Date;

public class AuthorInfo extends Poolable implements Serializable,Comparable {
	
	 static final long serialVersionUID = -4194708532913228600L;

	String emailaddress;
	String augurAddress;
	
	


	/**
	 * Sets Emailaddress
	 *
	 * @param    Emailaddress        a  String
	 */
	public void setEmailaddress(String emailaddress)
	{
		this.emailaddress = emailaddress;
	}
	
	/**
	 * Returns Emailaddress
	 *
	 * @return    a  String
	 */
	public String getEmailaddress()
	{
		return emailaddress;
	}
	

	public void setAugurAddress(String address) {
		this.augurAddress = address;
	}

	public int compareTo(Object o) {
		return getAugurAddress().compareTo(((AuthorInfo)o).getAugurAddress());
	}
	

	public String getAugurAddress() {
		return augurAddress;
	}

	public static String getPoolName()
	{
		return "userinfo";
	}

	public String getKey() {
		return this.augurAddress;
	}
	
	public String getPool() {
		return "userinfo";
	}
	
	
	public AuthorInfo(String address)
	{
		this.augurAddress=address;
	}
	
	Date updateDate;
	boolean updatePending=false;
	
	public Date getUpdateDate()
	{
		return updateDate;
	}
	
	public void setUpdateDate(Date date)
	{
		updateDate=date;
	}

		/**
	 * Sets UpdatePending
	 *
	 * @param    UpdatePending       a  boolean
	 */
	public void setUpdatePending(boolean updatePending)
	{
		this.updatePending = updatePending;
	}
	
	/**
	 * Returns UpdatePending
	 *
	 * @return    a  boolean
	 */
	public boolean isUpdatePending()
	{
		return updatePending;
	}
	

	public String toString()
	{
		return getAugurAddress();
	}
	
	public String getFormattedDate(Date d)
	{
		if(d==null) return "Never";
		else return Controller.getController().getDateFormat().format(d);
	}
	
	public String getHTMLHeading()
	{
		StringBuffer sb=new StringBuffer();
		sb.append("<HTML><HEAD>"+Controller.getController().getHTMLStyle()+"</HEAD><BODY>");
		sb.append(augurAddress);
		sb.append(" Updated:"+getFormattedDate(updateDate));
		
		return sb.toString();
	}
	
	public String getHTML() {
		StringBuffer sb=new StringBuffer();
		sb.append("<HTML><HEAD>"+Controller.getController().getHTMLStyle()+"</HEAD><BODY>");
		
		sb.append(augurAddress+" Unimplemented getHTML method in "+this.getClass().getName());
	
		return sb.toString();
	}
}

