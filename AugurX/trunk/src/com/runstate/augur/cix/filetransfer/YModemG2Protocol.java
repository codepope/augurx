/**
 * YGProtocol.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.filetransfer;


import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import com.runstate.util.ssh.SSHConnection;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class YModemG2Protocol implements TransferProtocol
{
	
		TransferProtocolUser tpu=null;
	
	
	public void setTransferProtocolUser(TransferProtocolUser tse)
	{
		tpu=tse;
	}
	
	public void clearTransferProtocolUser()
	{
		tpu=null;
	}
	

	boolean debug=Controller.getProfile().getBool(Prefs.DEBUG_YMODEM,false);
	
	
	public void setDebug(boolean b)
	{
		debug=b;
	}
	
	private void debugMsg(String s)
	{
		if(debug) System.out.println("Ymodem:"+s);
	}
	
	String transfertype;
	
	public String getTransferType()
	{
		return transfertype;
	}
	
	public void setTransferType(String s)
	{
		transfertype=s;
	}
	
	static final byte SOH=0x01;
	static final byte STX=0x02;
	static final byte EOT=0x04;
	static final byte ACK=0x06;
	static final byte NAK=0x15;
	static final byte CAN=0x18;
	static final byte G=0x47;
	static final byte C=0x43;
	
	final int S_START=1;
	final int S_BLK=2;
	final int S_BLK2=3;
	final int S_DATA=4;
	final int S_CHKSUM=5;
	final int S_CRC1=6;
	final int S_CRC2=7;
	
	SSHConnection sshconnection;
	
	public YModemG2Protocol(TransferProtocolUser tpu,SSHConnection sshconnection)
	{
		this.tpu=tpu;
		this.sshconnection=sshconnection;
	}

	int cnt;
	
	int blkcnt=1;
	
	OutputStream buff;
	
	private long timer;
	
	private void setClock(int secs)
	{
		timer=System.currentTimeMillis()+(secs*1000);
	}
	
	private boolean hasClockExpired()
	{
		if(System.currentTimeMillis()>timer)
		{
			return true;
		}
		return false;
	}
	
	boolean starting=false;
	boolean gettingheader=false;
	boolean sendingheader=false;
	String currentfilename;
	long currentfilesize;
	long sizesofar=0;

	int readercnt=0;
	
	class Block
	{
		static final int X_NOP=1;
		static final int X_ACK=2;
		static final int X_NAK=3;
		static final int X_CAN=4;
		static final int X_END=5;
		
		int blocknum;
		int compblocknum;
		
		int crcbyte1;
		int crcbyte2;
		
		int[] bytes;
		int state;
		int blocksize;
		int ptr;
		CRC16 crc=new CRC16();
		
		Block(int size)
		{
			blocksize=size;
			bytes=new int[blocksize];
			ptr=0;
			state=-1;
		}
		
		boolean add(int b)
		{
			bytes[ptr++]=b;
			updatecrc((b&0xFF));
			return !((ptr-1)<(blocksize-1));
		}
		
		void setBlocknum(int b)
		{
			blocknum=b;
//			updatecrc(b);
		}
		
		void setCompBlocknum(int b)
		{
			compblocknum=b;
//			updatecrc(b);
		}
		
		boolean isBlockNumValid()
		{
			return (byte)(blocknum&0xFF) == (byte)(~compblocknum&0xFF);
		}
		
		void setCrcbyte1(int b)
		{
			crcbyte1=b;
		}
		
		void setCrcbyte2(int b)
		{
			crcbyte2=b;
		}
		
		void dump()
		{
			switch(state)
			{
				case X_NOP:
					System.out.println("Type:X_NOP");
					break;
				case X_ACK:
					System.out.println("Type:X_ACK");
					break;
				case X_NAK:
					System.out.println("Type:X_NAK");
					break;
				case X_END:
					System.out.println("Type:X_END");
					break;
				case X_CAN:
					System.out.println("Type:X_CAN");
					break;
				default:
					System.out.println("Type:"+state);
					break;
			}
			System.out.println("Size:"+blocksize);
			System.out.println("Ptr:"+ptr);
			System.out.println("Bytes:"+readableBytes(bytes,ptr));
			
		}
		private String readableBytes(int[] buff,int bufptr)
		{
			StringBuffer sb=new StringBuffer();
			
			for(int i=0;i<bufptr;i++)
			{
				int b=buff[i];
				char c=(char)b;
				if(c>0x1F)
				{
					sb.append(c);
					sb.append(" ");
				}
				else
				{
					sb.append(Integer.toString(b));
					sb.append(" ");
				}
			}
			
			return sb.toString();
		}
		
		long getCRC()
		{
			return crc.getValue();
		}
		
		public void updatecrc(int b)
		{
			crc.update(b);
		}
	}
	
	public void startDownload() throws IOException
	{
		setTransferType("YModemG Download");
		
		boolean done=false;
		starting=true;
		gettingheader=true;
		
		setClock(3);
		
//		sshconnection.waitFor("Sending in Batch Mode");
		sshconnection.flushInput();
		
		debugMsg("Starting");
		debugMsg("Writing C");
		sshconnection.write(G);
		
		while(!done)
		{
			int bytesavail=sshconnection.available();
			if(bytesavail==0)
			{
				try
				{
					Thread.sleep(10);
				}
				catch(InterruptedException ie){}
				
				if(hasClockExpired())
				{
					if(!starting)
					{
						debugMsg("Writing NAK");
						sshconnection.write(NAK);
						setClock(10);
					}
					else
					{
						debugMsg("Writing C");
						sshconnection.write(G);
						setClock(3);
						starting=false;
					}
				}
			}
			else
			{
				int res=0;
				
//				debugMsg("Read "+bufptr+" total "+totread);
//
				boolean consumed=false;
				
				while(!consumed)
				{
					Block b=process(sshconnection);
					
//					b.dump();
					
					res=b.state;
					
					switch(res)
					{
						case Block.X_ACK:
//							debugMsg("Processing X_ACK");
							if(gettingheader)
							{
								int p=0;
								while(p<b.blocksize && b.bytes[p]!=0x00) p++;
								if(p==0)
								{
									debugMsg("Writing ACK and exiting");
//									sshconnection.write(ACK);
									done=true;
									consumed=true;
									break;
								}
								
								StringBuffer sb=new StringBuffer();
								for(int q=0;q<p;q++)
								{
									sb.append((char)b.bytes[q]);
								}
								
								currentfilename=sb.toString();
								currentfilesize=0;
								p++;
								if(b.bytes[p]!=0x00)
								{
									while(p<b.blocksize && b.bytes[p]!=0x20)
									{
										debugMsg("filesize byte@"+p+" val="+Integer.toString(b.bytes[p])+" char="+(char)b.bytes[p]);
										currentfilesize=currentfilesize*10+((byte)(b.bytes[p]&0x7F)-0x30);
										p++;
									}
								}
								else
								{
									currentfilesize=-1;
								}
								blkcnt=0;
								sizesofar=0;
								//		buff.reset();
								debugMsg("Writing ACK");
								sshconnection.write(ACK);
								debugMsg("Writing C");
								sshconnection.write(G);
								debugMsg("Starting on file "+currentfilename+" "+currentfilesize);
								buff=tpu.transferFileStart(currentfilename,currentfilesize);
								gettingheader=false;
							}
							else
							{
								
								if((sizesofar+b.blocksize)>currentfilesize)
								{
									for(int q=0;q<(currentfilesize-sizesofar);q++)
									{
										buff.write((char)b.bytes[q]);
									}
								}
								else
								{
									sizesofar=sizesofar+b.blocksize;
									for(int q=0;q<b.blocksize;q++)
									{
										buff.write((char)b.bytes[q]);
									}
									blkcnt++;
								}
								
								
								tpu.transferProgress(sizesofar,false);
								//sizesofar,blkcnt,false);
//								sshconnection.write(ACK);
//								state=S_START;
								setClock(10);
							}
							break;
						case Block.X_NAK:
							debugMsg("Got NAK");
							tpu.transferMessage("Sending NAK");
							//,currentfilename,currentfilesize,sizesofar,blkcnt,true);
//					debugMsg("Sending NAK back and flushing");
//					sshconnection.write(NAK);
//					sshconnection.flushInput();
//							state=S_START;
							setClock(10);
							break;
						case Block.X_CAN:
							debugMsg("Got CAN cancelling");
							done=true;
							break;
						case Block.X_END:
							debugMsg("Got X_END");
							//fireStatusEvent("Complete",currentfilename,currentfilesize,currentfilesize,blkcnt,true);
							tpu.transferProgress(sizesofar,true);
							tpu.transferComplete();
							debugMsg("Sending ACK C back");
							sshconnection.write(ACK);
							sshconnection.write(G);
							gettingheader=true;
							debugMsg("Waiting for file name packet");
//							state=S_START;
							setClock(3);
							break;
						default:
							debugMsg("*** Bad result from process ");
							System.err.println("Bad ymodem process result ("+res+")");
							b.dump();
					}
				}
			}
		}
		
	}
	
	
	
	public Block process(SSHConnection sshcxn)
	{
		int state=S_START;
		Block pb=null;
		
		while(true)
		{
			if(sshcxn.available()==0)
			{
				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e) {}
			}
			else
			{
				
				int cb;
				
				
				cb=sshcxn.read();
				
				switch(state)
				{
					case S_START:
//						System.out.println("Start");
//						System.out.println("Char is "+Integer.toHexString((int)cb)+" "+(char)cb);
						if(cb==SOH)
						{
							pb=new Block(128);
							state=S_BLK;
							break;
						}
						else if(cb==STX)
						{
							pb=new Block(1024);
							state=S_BLK;
							break;
						}
						else if(cb==CAN)
						{
							Block canb=new Block(128);
							canb.state=Block.X_CAN;
							return canb;
						}
						else if(cb==EOT)
						{
							Block endb=new Block(128);
							endb.state=Block.X_END;
							return endb;
						}
						break;
					case S_BLK:
//						System.out.println("BLK");
						
						pb.setBlocknum(cb);
						state=S_BLK2;
						break;
					case S_BLK2:
//						System.out.println("BLK2");
						pb.setCompBlocknum(cb);
						if(!pb.isBlockNumValid())
						{
							Block r=pb;
							state=S_START;
							pb=null;
							r.state=Block.X_NAK;
							return r;
						}
						state=S_DATA;
						cnt=0;
						break;
					case S_DATA:
//						System.out.print(".");
						if(pb.add(cb))
						{
							state=S_CRC1;
						}
						break;
					case S_CRC1:
//						System.out.println("CRC1");
						pb.setCrcbyte1(cb);
						state=S_CRC2;
						break;
					case S_CRC2:
//						System.out.println("CRC2");
						pb.setCrcbyte2(cb);
						
						
						pb.updatecrc(0);
						pb.updatecrc(0);
							
						long c=pb.getCRC();
						
						//	short rxc=(short)((pb.crcbyte1<<8)&0xFFFF)|(pb.crcbyte2&0xFF);
						
						long rxc=((pb.crcbyte1<<8)&0xFFFF);
						rxc|=pb.crcbyte2&0xFF;
						
						if(rxc!=c)
						{
							debugMsg("CRC check failed "+rxc+" "+c);
							pb.state=Block.X_NAK;
						}
						else
						{
							pb.state=Block.X_ACK;
							state=S_START;
						}
						Block r=pb;
						pb=null;
						return r;
						
				}
				
			}
		}
	}
	
	
	public boolean startUpload(FileSpec[] filespecs)
	{
		setTransferType("YModem Upload");
		
		try
		{
			int state=0;
			int fileentry=0;
			
			while(fileentry<filespecs.length)
			{
				FileSpec fs=filespecs[fileentry];
				
				boolean b=txYmodem(fs,true,true);
				
				fileentry++;
			}
			
			txYmodem(new FileSpec(null),true,true);
		}
		catch (IOException e) { e.printStackTrace(); return false; }
		
		return true;
	}
	
	public boolean startUpload(FileSpec fs)
	{
		try
		{
			debugMsg("sending file "+fs);
			boolean s=txYmodem(fs,true,true);
			if(s)
			{
				boolean t=txYmodem(new FileSpec(null),true,true);
				if(t)
				{
					return true;
				}
			}
			return false;
		}
		catch (IOException e) { return false; }
	}
	
	private boolean txYmodem(FileSpec fs,boolean oneFlag, boolean batchFlag) throws IOException
	{
		int blocksize=128;
		int num128=0;
		int num1k=0;
		int filesize=0;
		int ncgByte=NAK;
		boolean emptyFlag=false;
		boolean eotFlag=false;
		DataInputStream ds=null;
		int firstpacket=0;
		int remainingBytes=0;
		int readsize=0;
		byte[] buff=new byte[1024];
		
		if(batchFlag)
		{
			if(fs.getFilename()==null)
			{
				emptyFlag=true;
			}
		}
		
		if(!emptyFlag)
		{
			try
			{
				ds=fs.getStream();
			}
			catch (IOException e) { e.printStackTrace(); }
		}
		
		if(emptyFlag)
		{
			num128=0;
			num1k=0;
		}
		else
		{
			filesize=(int)fs.getContentSize();
			if(oneFlag)
			{
				num1k=filesize/1024;
			}
			else
			{
				num1k=0;
			}
			
			num128=(filesize-1024*num1k)/128;
			
			if(128*num128+1024*num1k<filesize)
			{
				num128++;
			}
			
			remainingBytes=filesize;
		}
		
		
		if((ncgByte=txstartup())==-1)
		{
			debugMsg("Startup came back with -1");
			return false;
		}
		else
		{
			
			if(batchFlag) { firstpacket=0; }
			else { firstpacket=1; }
			
			
			
			for(int packet=firstpacket;packet<=(num1k+num128);packet++)
			{
				if(packet==0)
				{
					if(emptyFlag)
					{
						blocksize=128;
						for(int i=0;i<blocksize;i++) { buff[i]=0x00; }
					}
					else
					{
						blocksize=128;
						int k=0;
						for(int i=0;i<blocksize;i++) buff[i]=0x00;
						for(int j=0;j<fs.getFilename().length();j++) buff[k++]=(byte)fs.getFilename().charAt(j);
						buff[k++]=(byte)0x00;
						String fsize=Integer.toString(filesize);
						for(int j=0;j<fsize.length();j++) buff[k++]=(byte)fsize.charAt(j);
						while(k<blocksize) buff[k++]=(byte)0x00;
					}
				}
				else
				{
					if(batchFlag && (packet<=num1k))	blocksize=1024; else blocksize=128;
					if(remainingBytes<blocksize) readsize=remainingBytes; else readsize=blocksize;
					int k=0;
					for(int i=0;i<blocksize;i++) buff[i]=0x00;
					for(int q=0;q<readsize;q++)	{ byte b=ds.readByte();	buff[k++]=b; remainingBytes--; }
					if(readsize<blocksize) for(int q=readsize;q<blocksize;q++) { buff[k++]=(byte)0x1A;	}
				}
				
				if(!writePacket(packet,blocksize,buff,ncgByte))	{ debugMsg("writepacket failed"); return false;	}
				
				if(packet==0)
				{
					tpu.transferUploadStart(fs.getFilename(),filesize);
					//,0,packet,true);
				}
				else
				{
					tpu.transferProgress(filesize-remainingBytes,false);
					//,packet,false);
				}
				
				if(!emptyFlag && packet==0)	{ txstartup(); }
				
			}
			tpu.transferProgress(filesize-remainingBytes,true);
			
			tpu.transferMessage("Sent "+(fs.getFilename()==null?"Internal":fs.getFilename()));
			tpu.transferComplete();
			//(new TransferProtocolDone(currentfilename,filesize,null));
			//,filesize,filesize-remainingBytes,0,true);
			
			if(emptyFlag)
			{
				return true;
			}
			else
			{
				if(!txeot())
				{
					return false;
				}
				
				return true;
			}
		}
	}
	
	boolean txeot()
	{
		for(int i=0;i<10;i++)
		{
			sshconnection.write(EOT);
			int resp=sshconnection.read();
			
			if(resp==ACK)
			{
				return true;
			}
			
		}
		
		return false;
	}
	
	boolean writePacket(int serial,int packsize,byte[] buff,int ncgByte) throws IOException
	{
		int packettype;
		int maxattempts=5;
		CRC16 crc16=new CRC16();
		
		if(packsize==128)
		{
			packettype=SOH;
		}
		else if (packsize==1024)
		{
			packettype=STX;
		}
		else
		{
			System.err.println("Bad packet size");
			return false;
		}
		
		int packnum=serial & 0XFF;
		
		for(int attempt=1; attempt<maxattempts;attempt++)
		{
			debugMsg("Writing "+serial+" "+packsize);
			sshconnection.write((byte)packettype);
			
			sshconnection.write((byte)packnum);
			
			sshconnection.write((byte)(255-packnum));
			
			long checksum=0;
			
			crc16.reset();
			
			for(int i=0;i<packsize;i++)
			{
				sshconnection.write((byte)buff[i]);
				
				if(ncgByte!=NAK)
				{
					crc16.update(buff[i]);
				}
				else
				{
					checksum=checksum+buff[i];
				}
			}
			
			if(ncgByte!=NAK)
			{
				
				crc16.update(0);
				crc16.update(0);
				checksum=crc16.getValue();
				
				sshconnection.write((byte)((checksum&0xFF00)>>8));
				sshconnection.write((byte)(checksum&0xFF));
			}
			else
			{
				sshconnection.write((byte)(checksum&0XFF));
			}
			
			sshconnection.flushOutput();
			
			int resp=sshconnection.read();
			
			if(resp==CAN)
			{
				tpu.transferMessage("Cancelled"); //,"",0,0,0,true);
				return false;
			}
			else if(resp==ACK)
			{
				debugMsg("ACK");
				return true;
			}
			else if(resp==C)
			{
				debugMsg("C");
				return true;
			}
			else if(resp!=NAK)
			{
				debugMsg("Not NAK! "+resp);
				return false;
			}
			
			debugMsg("NAK");
			//,"",0,0,0,true);
		}
		
		tpu.transferMessage("Retry exceeded");
		//,"",0,0,0,true);
		
		return false;
	}
	
	private int txstartup()
	{
		int limit=1000;
		int b=-1;
		
		while(limit>0)
		{
			limit--;
			
			b=sshconnection.read();
			
			if(b==NAK)
			{
				return b;
			}
			else if(b==C)
			{
				return b;
			}
			
		}
		
		return -1;
		
	}
	
	
}
