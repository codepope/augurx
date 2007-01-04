/**
 * FileSpec.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.filetransfer;

import java.io.ByteArrayInputStream;
import java.io.*;

public class FileSpec
{
	String filename;
	byte[] filecontent;

	boolean bytesource=false;
	
	public FileSpec(String filename, byte[] filecontent)
	{
		this.filename = filename;
		this.filecontent = filecontent;
		this.bytesource=true;
	}
		
	public FileSpec(String filename)
	{
		this.filename = filename;
	}
	
	public void setFilename(String filename)
	{
		if(!bytesource) this.filename = filename;
	}
	
	public String getFilename()
	{
		if(bytesource)
		{
			return "Internal";
		}
		else
		{
			return filename;
		}
	}
	
	public void setFilecontent(byte[] filecontent)
	{
		this.filecontent = filecontent;
	}
	
	public byte[] getFilecontent()
	{
		return filecontent;
	}
	
	public long getContentSize()
	{
		if(bytesource)
		{
			return filecontent.length;
		}
		else
		{
			File f=new File(filename);
			return f.length();
		}
	}
	
	public DataInputStream getStream() throws IOException
	{
		if(bytesource)
		{
			return new DataInputStream(new ByteArrayInputStream(filecontent));
		}
		else
		{
			return new DataInputStream(new FileInputStream(filename));
		}
	}
}

