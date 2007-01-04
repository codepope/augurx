/**
 * DateUtils.java
 *
 * @author Dj
 */

package com.runstate.util;

import java.text.*;
import java.util.*;

public class DateUtils
{
	static SimpleDateFormat ISO8601UTC = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss,SSS'Z'");// 24 characters

	static {
		ISO8601UTC.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC == GMT
	}
	
	public static String getISO8601(Date d)
	{
		if(d==null) return "NA";
		return ISO8601UTC.format(d);
	}
}

