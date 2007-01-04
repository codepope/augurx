/**
 * SSHConnectionException.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util.ssh;

public class SSHConnectionException extends Exception
{
	public SSHConnectionException(String msg,Throwable t)
	{
		super(msg,t);
	}
}

