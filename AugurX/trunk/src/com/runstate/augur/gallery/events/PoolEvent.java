package com.runstate.augur.gallery.events;

import com.runstate.augur.gallery.Poolable;

public class PoolEvent
{	public final static int ADDED=1;
	public final static int UPDATED=2;
	public final static int DELETED=3;
	
	Poolable poolable;
	String key;
	String pool;
	
	int type;
	
	/**
	 * Constructor
	 *
     * @param type      type of pool change
     * @param poolable  changed item
	 */
	public PoolEvent(int type,Poolable poolable) {
		this.type=type;
		this.pool=poolable.getPool();
		this.key=poolable.getKey();
		this.poolable=poolable;
	}
	
	public PoolEvent(String pool,String key)
	{
		this.type=DELETED;
		this.key=key;
		this.pool=pool;
		this.poolable=null;
	}
	
	/**
	 * Sets Pool
	 *
	 * @param    pool                a  String
	 */
	public void setPool(String pool) {
		this.pool = pool;
	}
	
	/**
	 * Returns Pool
	 *
	 * @return    a  String
	 */
	public String getPool() {
		return pool;
	}
	
	/**
	 * Sets Path
	 *
	 * @param    key                  a  String
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * Returns Path
	 *
	 * @return    a  String
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Sets Type
	 *
	 * @param    type                an int
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * Returns Type
	 *
	 * @return    an int
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Sets Command
	 *
	 * @param    poolable      a poolable item
	 */
	public void setPoolable(Poolable poolable) {
		this.poolable = poolable;
	}
	
	/**
	 * Returns Command
	 *
	 * @return    a  Command
	 */
	public Poolable getPoolable() {
		return poolable;
	}

	@Override
    public String toString()
	{
		return "PoolEvent[type="+type+",key="+key+",poolable="+poolable+"]";
	}
}
