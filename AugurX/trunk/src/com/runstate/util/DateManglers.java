/**
 * DateManglers.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util;

import java.util.*;
import java.text.*;

public class DateManglers
{
	static Calendar d=Calendar.getInstance();
	static Calendar now=Calendar.getInstance();
	static SimpleDateFormat sdf=new SimpleDateFormat("HH:mm dd/MMM/yy");
	static SimpleDateFormat smf=new SimpleDateFormat("HH:mm dd/MMM");
	static SimpleDateFormat stf=new SimpleDateFormat("HH:mm ");
	
	public static String formatDate(long l)
	{
		now.setTimeInMillis(System.currentTimeMillis());
		d.setTimeInMillis(l);
		
		if( d.get(Calendar.YEAR)==now.get(Calendar.YEAR) &&
			   d.get(Calendar.DAY_OF_YEAR)==now.get(Calendar.DAY_OF_YEAR))
			
			{
				return stf.format(d.getTime())+" Today";
		}
		if(d.get(Calendar.YEAR)==now.get(Calendar.YEAR))
		{
			return smf.format(d.getTime());
		}
				
		return sdf.format(d.getTime());
	}
			
		
}

