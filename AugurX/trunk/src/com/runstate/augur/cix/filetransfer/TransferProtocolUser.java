/**
 * TransferProtocolListener.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.filetransfer;
import java.io.OutputStream;



public interface TransferProtocolUser
{
	
	/**
	 * Method transferUploadStart
	 *
	 * @param    filename            a  String
	 * @param    filesize            an int
	 *
	 */
	public void transferUploadStart(String filename, int filesize);
	
	
	/**
	 * Method transferComplete
	 *
	 */
	public void transferComplete();
	
	
	/**
	 * Method transferMessage
	 *
	 * @param    p0                  a  String
	 *
	 */
	public void transferMessage(String p0);
	
	
	/**
	 * Method transferProgress
	 *
	 * @param    sizesofar           a  long
	 * @param    p1                  a  boolean
	 *
	 */
	public void transferProgress(long sizesofar, boolean p1);
	
	
	/**
	 * Method transferFileStart
	 *
	 * @param    currentfilename     a  String
	 * @param    currentfilesize     a  long
	 *
	 * @return   a  ByteArrayOutputStream
	 */
	public OutputStream transferFileStart(String currentfilename, long currentfilesize);
	
//	public abstract void statusEventRecieved(TransferProtocolEvent tse);
}

