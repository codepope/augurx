/**
 * IntParameter.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.commands.parameters;
import com.runstate.augur.gallery.commands.*;

public class IntParameter extends Parameter
{
	int value;
	int max;
	int min;
	int step;
	
	public IntParameter(String name,String label,int value,int min,int max,int step)
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
	public void setMax(int max) {
		this.max = max;
	}
	
	/**
	 * Returns Max
	 *
	 * @return    an int
	 */
	public int getMax() {
		return max;
	}
	
	/**
	 * Sets Min
	 *
	 * @param    Min                 an int
	 */
	public void setMin(int min) {
		this.min = min;
	}
	
	/**
	 * Returns Min
	 *
	 * @return    an int
	 */
	public int getMin() {
		return min;
	}
	
	/**
	 * Sets Step
	 *
	 * @param    Step                an int
	 */
	public void setStep(int step) {
		this.step = step;
	}
	
	/**
	 * Returns Step
	 *
	 * @return    an int
	 */
	public int getStep() {
		return step;
	}
	
	public int getIntValue()
	{
		return value;
	}
	
	public void setIntValue(int value)
	{
		this.value=value;
	}
	
}

