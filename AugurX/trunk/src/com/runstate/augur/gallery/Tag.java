/**
 * Tag.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery;

public class Tag
{
	Long tagid;
	String name;
	String description;
	
	/**
	 * Constructor
	 *
	 * @param    tagid               a  Long
	 * @param    name                a  String
	 * @param    description         a  String
	 */
	public Tag(Long tagid, String name, String description) {
		this.tagid = tagid;
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Sets Tagid
	 *
	 * @param    tagid               a  Long
	 */
	public void setTagid(Long tagid) {
		this.tagid = tagid;
	}
	
	/**
	 * Returns Tagid
	 *
	 * @return    a  Long
	 */
	public Long getTagid() {
		return tagid;
	}
	
	/**
	 * Sets Name
	 *
	 * @param    name                a  String
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns Name
	 *
	 * @return    a  String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets Description
	 *
	 * @param    description         a  String
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Returns Description
	 *
	 * @return    a  String
	 */
	public String getDescription() {
		return description;
	}
	
	}

