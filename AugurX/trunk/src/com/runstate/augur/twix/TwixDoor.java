/**
 * TwixDoor.java
 *
 * @author Andy
 */

package com.runstate.augur.twix;

import com.runstate.augur.twix.authorinfo.TwixAuthorInfo;
import com.runstate.augur.twix.commands.TwixCommentCommand;
import com.runstate.augur.twix.commands.TwixGetCommand;
import com.runstate.augur.twix.commands.TwixPathInfoCommand;
import com.runstate.augur.twix.commands.TwixSayCommand;
import com.runstate.augur.twix.commands.TwixUserInfoCommand;
import com.runstate.augur.twix.missions.TwixRandomExportMission;
import com.runstate.augur.twix.missions.TwixSimpleExportMission;
import com.runstate.augur.twix.missions.TwixSimpleImportMission;
import com.runstate.augur.twix.pathinfo.TwixConfPathInfo;
import com.runstate.augur.twix.pathinfo.TwixPathInfo;
import com.runstate.augur.twix.pathinfo.TwixRootPathInfo;
import com.runstate.augur.twix.sync.TwixSync;
import com.runstate.augur.twix.ui.TwixDoorUI;
import com.runstate.augur.twix.ui.TwixSyncUI;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.controller.DoorUI;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.controller.SyncUI;
import com.runstate.augur.gallery.AuthorInfo;
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.gallery.PathInfo;
import com.runstate.augur.gallery.Sync;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.augur.gallery.commands.commands.CommentCommand;
import com.runstate.augur.gallery.commands.commands.GetCommand;
import com.runstate.augur.gallery.commands.commands.SayCommand;
import com.runstate.augur.gallery.missions.Mission;
import com.runstate.util.ImageCache;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import org.apache.commons.lang.StringEscapeUtils;


public class TwixDoor extends Door
{

	TwixSimpleImportMission scimpmission;
	TwixSimpleExportMission scexpmission;
	TwixRandomExportMission scrandmission;
	
	public TwixDoor()
	{
		super();
		scimpmission=new TwixSimpleImportMission(this);
		scexpmission=new TwixSimpleExportMission(this);
		scrandmission=new TwixRandomExportMission(this);
	}
	
	public Sync getSyncImpl()
	{
		return new TwixSync(this);
	}
	
	public SyncUI getSyncUIImpl()
	{
		return new TwixSyncUI();
	}
	
	public DoorUI getDoorUI()
	{
		return new TwixDoorUI(this);
	}
	
	
	
	public String getMyNativeUser()
	{
		return Controller.getProfile().get(Prefs.M_T_USERNAME);
	}
	
	public Mission[] getMiscMissions()
	{
		return new Mission[] {scimpmission,scexpmission,scrandmission};
	}
	
	public Command refresh(Object obj)
	{
		if(obj instanceof TwixPathInfo)
		{
			return new TwixPathInfoCommand(getDoorid(),((TwixPathInfo)obj).getBundleid());
		}
		else if(obj instanceof TwixAuthorInfo)
		{
			return new TwixUserInfoCommand(getDoorid(),((TwixAuthorInfo)obj).getAugurAddress());
		}
		
		return null;
	}
	
	/**
	 * Method createUserInfo
	 *
	 * @param    address             a  String
	 *
	 * @return   an UserInfo
	 *
	 */
	public AuthorInfo createUserInfo(String address)
	{
		return new TwixAuthorInfo(address);
	}
	
	public PathInfo createPathInfo(Long bundleid)
	{
		if(getNativePath(Controller.getController().getGallery().getBundleManager().idToName(bundleid))==null)
		{
			return new TwixRootPathInfo(getDoorid(),bundleid);
		}
		
		return new TwixConfPathInfo(getDoorid(),bundleid);
	}

	public ImageIcon getDoorIcon()
	{
		return ImageCache.get("cixdoor");
	}
	
	public boolean isMsgHot(Msg m)
	{
		if(m.getAuthor().equals(Controller.getProfile().get(Prefs.M_T_USERNAME))) return true;
		return false;
	}
	
	public GetCommand createGetCommand(Long bundleid, Long msgid, boolean all)
	{
		GetCommand gc=new TwixGetCommand(getDoorid(),bundleid,msgid);
		gc.setAll(all);
		return gc;
	}
	
	public SayCommand createSayCommand(Long bundleid,String text)
	{
		SayCommand sc=new TwixSayCommand(getDoorid(),bundleid,text);
		return sc;
	}
	
	public CommentCommand createCommentCommand(Long bundleid,Long knotid,String text)
	{
		CommentCommand cc=new TwixCommentCommand(getDoorid(),bundleid,knotid,text);
		return cc;
	}
	
	public String getBody(Msg m)
	{
		if(m.isDeleted()) return "Deleted Message";
		if(m.getBodystart()==-1) return "Corrupted Message";

		return m.getText().substring(m.getBodystart());
	}
	

	public String getBodyHTML(Msg m)
	{
		StringBuilder htmlised=new StringBuilder();
		
//		htmlised.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
//
//		htmlised.append("<HTML>");
//		htmlised.append("<HEAD>");
//		htmlised.append(Augur.getController().getHTMLStyle());
//		htmlised.append("</HEAD>");
//		htmlised.append("<BODY>");
//
		boolean multiline=false;
		String collectedurl="";
		String buffline=null;
		
		StringTokenizer st=new StringTokenizer(getBody(m)," \n",true);
		
		int nlc=0;
		
		while(buffline!=null || st.hasMoreTokens())
		{
			String line=null;
			if(buffline!=null)
			{
				line=buffline;
				buffline=null;
			}
			else
			{
				line=st.nextToken();
			}
			
			
//			System.out.println("line:"+line); 
			
			if(line.equals("\n"))
			{
				nlc++;
				//htmlised.append("<P>");
			}
			else
			{
				if(nlc>0)
				{
					if(nlc==1)
					{
						htmlised.append("<BR>");
					}
					else if (nlc==2)
					{
						htmlised.append("<P>");
					}
					else
					{
						for(int i=0;i<nlc-1;i++)
						{
							htmlised.append("<BR>");
						}
						htmlised.append("<P>");
					}
					
					nlc=0;
				}
				
				if(multiline)
				{
					int endind=line.indexOf(">");
					
					if(endind!=-1)
					{
						multiline=false;
						collectedurl=collectedurl+line.substring(0,endind);
						
						htmlised.append("<A HREF='"+collectedurl+"'>"+StringEscapeUtils.escapeHtml(collectedurl)+"</A>");
						if(endind!=line.length())
						{
							buffline=line.substring(endind+1);
						}
					}
					else
					{
						if(!Character.isWhitespace(line.charAt(0)))
						{
							collectedurl=collectedurl+line;
						}
					}
				}
				else
				{
					if(line.startsWith("http:")
					   || line.startsWith("https:")
					   || line.startsWith("twix:")
					   || line.startsWith("mailto:"))
					{
						htmlised.append("<A HREF='"+line+"'>"+StringEscapeUtils.escapeHtml(line)+"</A>");
					}
					else if(line.startsWith("<http:")
							|| line.startsWith("<https:")
							|| line.startsWith("<twix:")
							|| line.startsWith("<mailto:")
							|| line.startsWith("<URL:http:")
							|| line.startsWith("<URL:twix:")
							|| line.startsWith("<URL:mailto:"))
					{
						int startind=line.startsWith("<URL:")?5:1;
						int endind=line.indexOf(">");
						
						if(endind!=-1)
						{
							String url=line.substring(startind,endind);
							htmlised.append("<A HREF='"+url+"'>"+StringEscapeUtils.escapeHtml(url)+"</A>");
							if(endind!=line.length())
							{
								buffline=line.substring(endind+1);
							}
						}
						else
						{
							multiline=true;
							collectedurl=line.substring(startind);
						}
					}
					else if(line.length()>3 && line.startsWith("*") && line.endsWith("*"))
					{
						htmlised.append("<B>");
						htmlised.append(StringEscapeUtils.escapeHtml(line.substring(1,line.length()-1)));
						htmlised.append("</B>");
					}
					else if(line.length()>3 && line.startsWith("/") && line.endsWith("/"))
					{
						htmlised.append("<I>");
						htmlised.append(StringEscapeUtils.escapeHtml(line.substring(1,line.length()-1)));
						htmlised.append("</I>");
					}
					else if(line.length()>3 && line.startsWith("_") && line.endsWith("_"))
					{
						htmlised.append("<U>");
						htmlised.append(StringEscapeUtils.escapeHtml(line.substring(1,line.length()-1)));
						htmlised.append("</U>");
					}
					else
					{
						htmlised.append(StringEscapeUtils.escapeHtml(line));
					}
				}
			}
			
		}
		
//		htmlised.append("</BODY>");
//		htmlised.append("</HTML>");
		
		return Controller.getController().wrapWithStyle(null,htmlised.toString());
	}
	
	
	private	static final int S_STARTPARSE=0;
	private static final int S_HEADERDONE=1;
	private static final int S_FIRSTLINEPARSE=2;
	private static final int S_BODYPARSE=3;
	private static final int S_EXIT=99;
	
	
	
	SimpleDateFormat sdf=new SimpleDateFormat("ddMMMyyyyHH:mm");
	
	public static Pattern memohdr_pat=Pattern.compile("Memo #([0-9]+) \\(([0-9]+)\\)");
	public static Pattern newshdr_pat=Pattern.compile("#! rnews ([0-9]*)");
	public static Pattern confhdr_pat = Pattern.compile("^([^\\/]+)/([^ ]+) #(\\d+), from ([^,]+), (\\d+) chars, (([A-Za-z][a-z][a-z]) ([A-Za-z][a-z][a-z]) +([0-9]|[0-9][0-9]) ([0-9]+):([0-9]+):([0-9]+) ([0-9]+))*$| \\**$| $|$");
        public static Pattern cmmthdr_pat = Pattern.compile("^Comment to (\\d+).*$");
        public static Pattern sayhdr_pat = Pattern.compile("^TITLE: (.*)$");
	public void parseReader(Msg m,BufferedReader br)
	{
		int currpos=0;
		int currsize=0;
		int msgsize=0;
		boolean done=false;
		int state=S_STARTPARSE;
		
		try
		{
			while(br.ready() && !done)
			{
				String l=br.readLine();
				if(l!=null)
				{
					switch(state)
					{
						case S_STARTPARSE:
							if(l.startsWith("Checking for conference activity") ||
							   l.startsWith("Joining ") ||
							   l.startsWith("No unread messages"))
							{
								break;
							}
							else if(l.startsWith("!MF:"))
							{
								currpos=currpos+l.length()+1;
								break;
							}
							
							
							// First line should be parsed for msg type
							Matcher ch_matcher=confhdr_pat.matcher(l);
							if(ch_matcher.matches())
							{
								String twixpath=ch_matcher.group(1)+"/"+ch_matcher.group(2);
								m.setTextpath("/"+getDoorname()+"/"+twixpath); 
								m.setBundleId(null); 
								Long lid=Long.decode(ch_matcher.group(3));
								m.setKnotId(lid);
								m.setSourceid(lid.toString());
								m.setAuthor(ch_matcher.group(4));
								try
								{
                                                                     //Twix settings
//        match1 = conference
//        match2 = topic
//             3 = message number        
//             4 = Author        
//             5 = message size      
//               6 = Full Date
//               7 = Day of week
//               8 = Month
//               9 = day of month
//               10  Hour
//               11  Minute
//               12  Second
//               13  Year
                                                                    //ddMMMyyyyHH:mm
m.setMsgDate(new Timestamp(sdf.parse(ch_matcher.group(9)+ch_matcher.group(8)+ch_matcher.group(13)+ch_matcher.group(10)+":"+ch_matcher.group(11)).getTime()));  //TODO Check this
								}
								catch (ParseException e)
								{
									m.setMsgDate(new Timestamp(0));
								}
																
								msgsize=Integer.parseInt(ch_matcher.group(5));
								currsize=0;
                                                                currpos=currpos+l.length()+1;
                                                                state = S_HEADERDONE;
                                                                break;
							}
                 					System.out.println("MsgParse: Header Did not match ["+l+"]");
                                                        break;
                                            case S_HEADERDONE:
                                                        Matcher cm_matcher=cmmthdr_pat.matcher(l);
                                                        Matcher say_matcher=sayhdr_pat.matcher(l);
                                                        
							if (cm_matcher.matches())
							{
								// Comment to a message
								state=S_FIRSTLINEPARSE;
								if(cm_matcher.group(1)!=null)
								{
									m.setCommentto(Long.decode(cm_matcher.group(1))); 
								}
                                                                currpos=currpos+l.length()+1;
								m.setBodystart(currpos);
                                                                break;
                                                        }
							if (!say_matcher.matches())
                                                        {
                                                            currpos=currpos+l.length()+1;
                                                            System.out.println("MsgParse: Comment Did not match ["+l+"]");
                                                            break;
                                                        }
                                                        else  
							{
								// New thread
								state=S_FIRSTLINEPARSE;
//								if(cm_matcher.group(1)!=null)
//								{
//									m.setCommentto(Long.decode(cm_matcher.group(1))); 
//								}
                                                                //currpos=currpos+l.length();
                                                                currpos=currpos+7; //+7 to remove TITLE: 
								m.setBodystart(currpos);
                                                                l=say_matcher.group(1);
                                                                //fallthrough

                                                        }
						case S_FIRSTLINEPARSE:
							if(l.length()!=0)
							{
								m.setSubject(l);
								state=S_BODYPARSE;
							}
							// Fall through to bodyparse
						case S_BODYPARSE:
							currsize=currsize+l.length()+1;
							currpos=currpos+l.length()+1;
							if(currsize>=msgsize)
							{
								state=S_EXIT;
								done=true;
							}
							break;
							
					}
					
				}
				else
				{
					done=true;
				}
			}
			
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
}
