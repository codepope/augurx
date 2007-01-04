/**
 * Resources.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.ImageIcon;
import java.io.IOException;

public class ResourceLoader
{
	static ClassLoader cl;
	
	static {
		try
		{
			cl=Class.forName("com.runstate.augur.support.ResourceAnchor").getClassLoader();
		}
		catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    System.exit(1);
                
                }
	}
	
	public static ImageIcon getIcon(String path)
	{
		return new ImageIcon(cl.getResource("com/runstate/augur/support/"+path));
	}
	
	public static String getText(String path)
	{
		InputStream is=cl.getResourceAsStream(path);
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		StringBuffer text=new StringBuffer();
		String line=null;
		
		try
		{
			while((line=br.readLine())!=null)
			{
				text.append(line);
			}
		}
		catch (IOException e) { return null; }
		
		return text.toString();
	}
	
	public static InputStream getInputStream(String path)
	{
		return cl.getResourceAsStream(path);
	}
}

