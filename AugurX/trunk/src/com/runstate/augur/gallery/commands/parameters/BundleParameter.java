/**
 * PathParameter.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.commands.parameters;
import com.runstate.augur.gallery.commands.Parameter;

public class BundleParameter extends Parameter
{
	String bundleName;
	
	public BundleParameter(String name,String label,String bundleref)
	{
		super(name,label);
		this.bundleName=bundleref;
	}
	
	/**
	 * Sets Path
	 *
	 * @param    Path                a  String
	 */
	public void setBundleName(String path) {
		this.bundleName = path;
	}
	
	/**
	 * Returns Path
	 *
	 * @return    a  String
	 */
	public String getBundleName() {
		return bundleName;
	}
}

