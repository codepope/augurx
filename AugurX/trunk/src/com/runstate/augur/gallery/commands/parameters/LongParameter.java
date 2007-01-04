/**
 * LongParameter.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.commands.parameters;

import com.runstate.augur.gallery.commands.Parameter;

public class LongParameter extends Parameter
{
	long value;
	long max;
	long min;
	long step;
	
	public LongParameter(String name,String label,long value,long min,long max,long step)
	{
		super(name,label);
		this.value=value;
		this.max=max;
		this.min=min;
		this.step=step;
	}
	
	/**
	 * Sets Max
	 *
	 * @param    Max                 an int
	 */
	public void setMax(long max) {
		this.max = max;
	}
	
	/**
	 * Returns Max
	 *
	 * @return    an int
	 */
	public long getMax() {
		return max;
	}
	
	/**
	 * Sets Min
	 *
	 * @param    Min                 an int
	 */
	public void setMin(long min) {
		this.min = min;
	}
	
	/**
	 * Returns Min
	 *
	 * @return    an int
	 */
	public long getMin() {
		return min;
	}
	
	/**
	 * Sets Step
	 *
	 * @param    Step                an int
	 */
	public void setStep(long step) {
		this.step = step;
	}
	
	/**
	 * Returns Step
	 *
	 * @return    an int
	 */
	public long getStep() {
		return step;
	}
	
	public long getLongValue()
	{
		return value;
	}
	
	public void setLongValue(long value)
	{
		this.value=value;
	}
	
}

