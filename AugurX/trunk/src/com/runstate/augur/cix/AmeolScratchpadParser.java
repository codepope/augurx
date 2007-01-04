/**
 * AmeolScratchpadParser.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix;


import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmeolScratchpadParser
{
	File sourcefile;
//	FileInputStream sourcefis;
	BufferedReader source;
	ByteBuffer buffer;
	int estimatedCount;
	
//	public static Pattern confhdr_pat=Pattern.compile("^>>>([A-Za-z0-9_\\-\\.\\+\\!]+)/([^ ]+) (\\d+) ([^\\(]+)\\((\\d+)\\)(\\d+[A-Za-z][a-z][a-z][0-9][0-9]) ([0-9]+:[0-9][0-9])( c([0-9]+)[\\*|\\* |\\*m|m| ]*$| \\**$| $|$)");
	public static Pattern confhdr_pat=Pattern.compile("^>>>([^\\/]+)/([^ ]+) (\\d+) ([^\\(]+)\\((\\d+)\\)(\\d+[A-Za-z][a-z][a-z][0-9][0-9]) ([0-9]+:[0-9][0-9])( c([0-9]+)[\\*|\\* |\\*m|m| ]*$| \\**$| $|$)");
	public static Pattern ameolhdr=Pattern.compile("!MF:(.*)");
	static final int S_STARTPARSE=0;
	static final int S_HEADERPARSE=1;
	static final int S_FIRSTLINEPARSE=2;
	static final int S_BODYPARSE=3;
	static final int S_EXIT=99;
	
	String content;
	
	public AmeolScratchpadParser(File sourcefile) throws FileNotFoundException,IOException
	{
		this.sourcefile=sourcefile;
		//	this.source=sourcefis.getChannel();
		
		BufferedReader br=new BufferedReader(new FileReader(sourcefile));
		
		String s="";
		
		while((s=br.readLine())!=null)
		{
			if(s.startsWith(">>>"))
			{
				estimatedCount++;
			}
			
		}
		
		br.close();
		
		// Ok, trash em out and make new ones.
		this.source=new BufferedReader(new FileReader(sourcefile));
		
	}
	
	public int getEstimatedCount()
	{
		return estimatedCount;
	}
	
	public void close()
	{
		try
		{
			if(source!=null) source.close();
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
//
//	public AmeolScratchpadParser(ReadableByteChannel source) {
//		this.source=source;
//	}
	
	public String next()
	{
		String returnable=null;
		
		if(content!=null)
		{
			returnable=content;
			content=null;
		}
		else
		{
			if(hasNext())
			{
				returnable=content;
				content=null;
			}
			else
			{
				return null;
			}
		}
		
		return returnable;
		
		
	}
	
	public boolean hasNext()
	{
		if(content!=null) return true;
		
		boolean done=false;
		int state=S_STARTPARSE;
		int msgsize=0;
		int currsize=0;
		boolean skipmail=false;
		
		StringBuffer currmsg=new StringBuffer();
		
		try
		{
			while(!done)
			{
				String s;
				
				s=source.readLine();
				
//				System.out.println(">"+s);

				if(s==null)
				{
					done=true;
				}
				else
				{
					switch(state)
					{
						case S_STARTPARSE:
							if(s.startsWith("Checking for conference activity")
							   || s.startsWith("Joining ")
							   || s.startsWith("No unread messages")
							   || s.startsWith("READ ONLY"))
							{
								break;
							}
							
							if(s.startsWith("!MF:"))
							{
								currmsg.append(s);
								currmsg.append("\n");
								skipmail=false;
								break;
							}
							
							if(!skipmail)
							{
								if(s.startsWith("Memo #"))
								{
									skipmail=true;
									currmsg.setLength(0);
								}
								else
								{
									// First line should be parsed for msg type
									Matcher m=confhdr_pat.matcher(s);
									
									if(m.matches())
									{
										// Confmsg
										state=S_BODYPARSE;
										currmsg.append(s);
										currmsg.append("\n");
										msgsize=Integer.parseInt(m.group(5));
										currsize=0;
										break;
									}
									else
									{
										if(s.length()>0)
										{
											System.out.println("ASP:No match on ["+s+"]");
									
										}
									}
								}
							}
							
							
							break;
						case S_BODYPARSE:
							currsize=currsize+s.length()+1;
							currmsg.append(s);
							currmsg.append("\n");
							if(currsize>=msgsize || msgsize>Controller.getProfile().getInt(Prefs.M_C_MSGSIZE,32767))
							{
								content=currmsg.toString();
				//				System.out.println("Got Msg "+content);
								return true;
							}
							
							break;
							
					}
				}
				
			}
		}
		catch (IOException ioe) { ioe.printStackTrace(); }
		
		return false;
		
	}
	
	
//	ByteBuffer buf=null;
//	ByteBuffer line=ByteBuffer.allocate(65535);
//	int linechars=0;
//	CharsetDecoder decoder=null;
//
//	private String readline(ReadableByteChannel b) throws IOException {
//		if(buf==null) {
//			buf=ByteBuffer.allocateDirect(1);
//			buf.rewind();
//		}
//
//		if(decoder==null) {
//			Charset charset=Charset.forName("Cp1252");
//
//			decoder=charset.newDecoder();
//
//			decoder.onMalformedInput(CodingErrorAction.IGNORE);
//			decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
//		}
//
//		line.clear();
//		linechars=0;
//
//		boolean done=false;
//		boolean eof=false;
//
//		while(!done && !eof) {
//			if(!buf.hasRemaining()) {
//				buf.rewind();
//				int nr=b.read(buf);
//				if(nr==-1) {
//					eof=true;
//				}
//				buf.rewind();
//			}
//
//			while(buf.hasRemaining() && !done) {
//				byte bc=buf.get();
//				if(bc==0x0a) {
//					done=true;
//				}
//				else if(bc!=0x0d && bc!=0x00) {
//					line.put(bc);
//					linechars++;
//				}
//
//			}
//
//		}
//
//		if(eof) return null;
//		line.limit(linechars);
//		line.rewind();
//
//		decoder.reset();
//
//		CharBuffer cbuf=decoder.decode(line);
//
//		return cbuf.toString();
//	}
	
//	private String readline(ByteBuffer buf) throws IOException {
//		if(decoder==null) {
//			Charset charset=Charset.forName("Cp1252");
//
//			decoder=charset.newDecoder();
//
//			decoder.onMalformedInput(CodingErrorAction.IGNORE);
//			decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
//		}
//
//		line.clear();
//		linechars=0;
//
//		boolean done=false;
//		boolean eof=false;
//		boolean seen0d=false;
//
//		while(!done && !eof) {
//			while(buf.hasRemaining() && !done) {
//				byte bc=buf.get();
//
//				if(bc==0x0a) {
//					done=true;
//				}
//				else if(bc!=0x0d && bc!=0x00) {
//					line.put(bc);
//					linechars++;
//				}
//			}
//
//			if(!buf.hasRemaining()) {
//				eof=true;
//			}
//		}
//
//		if(eof) return null;
//		line.limit(linechars);
//		line.rewind();
//
//		decoder.reset();
//
//		CharBuffer cbuf=decoder.decode(line);
//
//		return cbuf.toString();
//	}
//
//
}
