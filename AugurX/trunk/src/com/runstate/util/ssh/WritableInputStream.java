/**
 * WritableInputStream.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util.ssh;

import java.io.*;
import java.nio.*;

public class WritableInputStream extends InputStream
{
	public  WritableInputStream()
	{
	}
	
	public  int read(byte[] b,int off,int len)
	{
		int ptr=0;
//		System.out.println("readmany");
		if(buff.size()==0)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		
		synchronized(buff)
		{
			
			while(ptr<len && buff.size()>0)
			{
				b[ptr+off]=buff.getFront();
				ptr++;
			}
		}

//			if(buff.size()>0) System.out.println("Returning "+ptr+" with "+buff.size()+" left");
		
		return ptr;
	}
	
	public  int read() throws IOException
	{
		int c;
//		System.out.println("readon");
		while(buff.size()==0)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException ioe) {}
		}
		
		synchronized(buff)
		{
			c=(int)(buff.getFront()&0xFF);
		}
		
		return c;
	}
	
	public int available()
	{
		return buff.size();
	}
	
	public boolean markSupported() { return false; }
	
	ByteQueue buff=new ByteQueue(8192);
	
	public void write(byte b)
	{
		synchronized(buff)
		{
			buff.insert(b);
		}
	}
	
	public void write(String s)
	{
		byte[] tmp=s.getBytes();
		
		for(int i=0;i<tmp.length;i++) write(tmp[i]);
	}
	
}

