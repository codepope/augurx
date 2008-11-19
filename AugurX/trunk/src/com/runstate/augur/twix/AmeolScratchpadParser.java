/**
 * TwixScratchpadParser.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmeolScratchpadParser
{
	File sourcefile;
	BufferedReader source;
	ByteBuffer buffer;
	int estimatedCount;
	
//Twix confhdr_pat
//match1 = conference,    2 = topic,       3 = message number, 4 = Author, 
//     5 = message size,  6 = Full Date,   7 = Day of week,    8 = Month,
//     9 = day of month, 10 = Hour,       11 = Minute,        12 = Second,
//    13 = Year
	public static Pattern confhdr_pat = Pattern.compile("^([^\\/]+)/([^ ]+) #(\\d+), from ([^,]+), (\\d+) chars, (([A-Za-z][a-z][a-z]) ([A-Za-z][a-z][a-z]) +([0-9]|[0-9][0-9]) ([0-9]+):([0-9]+):([0-9]+) ([0-9]+))*$| \\**$| $|$");
	static final int S_STARTPARSE=0;
	static final int S_HEADERPARSE=1;
	static final int S_FIRSTLINEPARSE=2;
	static final int S_BODYPARSE1=3;
	static final int S_BODYPARSE2=4;
	static final int S_BODYPARSE3=5;
	static final int S_BODYPARSE4=6;
	static final int S_EXIT=99;
	
	String content;
	
	public AmeolScratchpadParser(File sourcefile) throws FileNotFoundException,IOException
	{
		this.sourcefile=sourcefile;
		Charset charset = Charset.forName("UTF-16");
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(sourcefile),charset));
		
		String s="";
		
		while((s=br.readLine())!=null)
		{
			if(s.startsWith("==="))
			{
				estimatedCount++;
			}
			
		}
		
		br.close();
		
		// Ok, trash em out and make new ones.
		this.source=new BufferedReader(new InputStreamReader(new FileInputStream(sourcefile),charset));
		
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
				
				//System.out.println(">"+String.valueOf( s));

				if(s==null)
				{
					done=true;
				}
				else
				{
					switch(state)
					{
						case S_STARTPARSE:
							if(s.startsWith("Checking for conference activity.")
							   || s.startsWith("Joining")
                                                           || s.startsWith("show scratchpad")
                                                           || s.startsWith("Main:")
                                                           || s.startsWith("M:")
							   || s.startsWith("No unread")
                                                           || s.startsWith("READ ONLY")
                                                           || s.startsWith("==========")
                                                           || s.startsWith("Not join")
                                                           || s.startsWith("Back at top of conferences")
                                                           || s.startsWith(" #####AUGURBREAK####")
                                                           || s.equals(""))
							{
								break;
							}
							
//MF Doesn't seem to exist for Twix			if(s.startsWith("!MF:"))
//							{
//								currmsg.append(s);
//								currmsg.append("\n");
//								skipmail=false;
//								break;
//							}
							
							if(!skipmail)
							{
//Memo Doesn't seem to exist on Twix    			if(s.startsWith("Memo #"))
//								{
//									skipmail=true;
//									currmsg.setLength(0);
//								}
//								else
								{
									// First line should be parsed for msg type
									Matcher m=confhdr_pat.matcher(s);
									
									if(m.matches())
									{
										// Confmsg
										state=S_BODYPARSE2;
										currmsg.append(s);
										currmsg.append("\n");
										msgsize=Integer.parseInt(m.group(5));
                                                                                msgsize=msgsize+s.length()+1;
										currsize=s.length()+1;
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
                                                case S_BODYPARSE2:
                                                        msgsize=msgsize+s.length();
							currsize=currsize+s.length()+1;
							currmsg.append(s);
							currmsg.append("\n");
                                                        state=S_BODYPARSE3;
                                                        break;
                                                  case S_BODYPARSE3:    
                                                        if(s.equals("----------"))
                                                        {
                                                            state=S_BODYPARSE4;
                                                            break;
                                                        }
                                                  case S_BODYPARSE4:    
							currsize=currsize+s.length()+1;
							currmsg.append(s);
							currmsg.append("\n");
                                                       
							if(currsize>=msgsize || msgsize>Controller.getProfile().getInt(Prefs.M_T_MSGSIZE,32767))
							{
								content=currmsg.toString();
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
	
	
}
