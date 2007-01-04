/**
 * Poolable.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery;

import java.io.Serializable;

public abstract class Poolable implements Serializable
{
	static final long serialVersionUID = -587998483364749687L;

	public static String getPoolName()
	{
		return null;
	}
	
	public abstract String getKey();
	
	public abstract String getPool();
	
}

