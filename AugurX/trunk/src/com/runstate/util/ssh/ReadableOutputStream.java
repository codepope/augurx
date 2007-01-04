/**
 * ReadableOutputStream.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util.ssh;

import java.io.*;

public class ReadableOutputStream extends OutputStream
{
	ByteQueue buff=new ByteQueue(8192);
	
	public  void write(int b) throws IOException
	{
		synchronized(buff)
		{
			buff.insert((byte)b);
		}
	}
	
	public void write(byte b)
	{
		synchronized(buff)
		{
			buff.insert(b);
		}
	}
	
	public void write(byte[] b,int off,int len)
	{
		synchronized(buff)
		{
			for(int i=0;i<len;i++) write(b[i+off]);
		}
	}
	
	public int read()
	{
		int i;
		
		while(buff.size()==0)
		{
			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException ioe) {}
		}
		
		synchronized(buff)
		{
			i=(int)(buff.getFront()&0XFF);
		}
		
		return i;
	}
	
	public int available()
	{
		return buff.size();
	}
}

