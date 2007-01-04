/**
 * TransferProtcol.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.filetransfer;

import java.io.IOException;

public interface TransferProtocol
{
	public void setDebug(boolean debug);
	public void startDownload() throws IOException;
	public boolean startUpload(FileSpec[] fs);
	public boolean startUpload(FileSpec fs);
	public void setTransferProtocolUser(TransferProtocolUser tse);
	public void clearTransferProtocolUser();
}

