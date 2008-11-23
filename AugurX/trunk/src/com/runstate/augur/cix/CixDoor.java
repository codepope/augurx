/**
 * CixDoor.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix;

import com.runstate.augur.AugurX;
import com.runstate.augur.cix.authorinfo.CixAuthorInfo;
import com.runstate.augur.cix.commands.CixCommentCommand;
import com.runstate.augur.cix.commands.CixGetCommand;
import com.runstate.augur.cix.commands.CixPathInfoCommand;
import com.runstate.augur.cix.commands.CixSayCommand;
import com.runstate.augur.cix.commands.CixUserInfoCommand;
import com.runstate.augur.cix.missions.CixRandomExportMission;
import com.runstate.augur.cix.missions.CixSimpleExportMission;
import com.runstate.augur.cix.missions.CixSimpleImportMission;
import com.runstate.augur.cix.pathinfo.CixConfPathInfo;
import com.runstate.augur.cix.pathinfo.CixPathInfo;
import com.runstate.augur.cix.pathinfo.CixRootPathInfo;
import com.runstate.augur.cix.sync.CixSync;
import com.runstate.augur.cix.ui.CixDoorUI;
import com.runstate.augur.cix.ui.CixSyncUI;
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


public class CixDoor extends Door
{

	CixSimpleImportMission scimpmission;
	CixSimpleExportMission scexpmission;
	CixRandomExportMission scrandmission;
	
	public CixDoor()
	{
		super();
		scimpmission=new CixSimpleImportMission(this);
		scexpmission=new CixSimpleExportMission(this);
		scrandmission=new CixRandomExportMission(this);
	}
	
	public Sync getSyncImpl()
	{
		return new CixSync(this);
	}
	
	public SyncUI getSyncUIImpl()
	{
		return new CixSyncUI();
	}
	
	public DoorUI getDoorUI()
	{
		return new CixDoorUI(this);
	}
	
	
	
	public String getMyNativeUser()
	{
		return Controller.getProfile().get(Prefs.M_C_USERNAME);
	}
	
	public Mission[] getMiscMissions()
	{
		return new Mission[] {scimpmission,scexpmission,scrandmission};
	}
	
	public Command refresh(Object obj)
	{
		if(obj instanceof CixPathInfo)
		{
			return new CixPathInfoCommand(getDoorid(),((CixPathInfo)obj).getBundleid());
		}
		else if(obj instanceof CixAuthorInfo)
		{
			return new CixUserInfoCommand(getDoorid(),((CixAuthorInfo)obj).getAugurAddress());
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
		return new CixAuthorInfo(address);
	}

	public PathInfo createPathInfo(Long bundleid)
	{
		if(getNativePath(Controller.getController().getGallery().getBundleManager().idToName(bundleid))==null)
		{
			return new CixRootPathInfo(getDoorid(),bundleid);
		}
		
		return new CixConfPathInfo(getDoorid(),bundleid);
	}

	public ImageIcon getDoorIcon()
	{
		return ImageCache.get("cixdoor");
	}
	
	public boolean isMsgHot(Msg m)
	{
		if(m.getAuthor().equals(Controller.getProfile().get(Prefs.M_C_USERNAME))) return true;
		return false;
	}
	
	public GetCommand createGetCommand(Long bundleid, Long msgid, boolean all)
	{
		GetCommand gc=new CixGetCommand(getDoorid(),bundleid,msgid);
		gc.setAll(all);
		return gc;
	}
	
	public SayCommand createSayCommand(Long bundleid,String text)
	{
		SayCommand sc=new CixSayCommand(getDoorid(),bundleid,text);
		return sc;
	}
	
	public CommentCommand createCommentCommand(Long bundleid,Long knotid,String text)
	{
		CommentCommand cc=new CixCommentCommand(getDoorid(),bundleid,knotid,text);
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
        return processText(getBody(m));
	}

    public String processText(String s)
	{
		StringBuilder htmlised=new StringBuilder();

		boolean multiline=false;
		String collectedurl="";
		String buffline=null;

		StringTokenizer st=new StringTokenizer(s," \n",true);

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
					   || line.startsWith("cix:")
					   || line.startsWith("mailto:"))
					{
						htmlised.append("<A HREF='"+line+"'>"+StringEscapeUtils.escapeHtml(line)+"</A>");
					}
					else if(line.startsWith("<http:")
							|| line.startsWith("<https:")
							|| line.startsWith("<cix:")
							|| line.startsWith("<mailto:")
							|| line.startsWith("<URL:http:")
							|| line.startsWith("<URL:cix:")
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

		return Controller.getController().wrapWithStyle(null,htmlised.toString());
	}

	
	private	static final int S_STARTPARSE=0;
//	private static final int S_HEADERPARSE=1;
	private static final int S_FIRSTLINEPARSE=2;
	private static final int S_BODYPARSE=3;
	private static final int S_EXIT=99;
	
	
	
	SimpleDateFormat sdf=new SimpleDateFormat("ddMMMyyHH:mm");
	
	public static Pattern memohdr_pat=Pattern.compile("Memo #([0-9]+) \\(([0-9]+)\\)");
	public static Pattern newshdr_pat=Pattern.compile("#! rnews ([0-9]*)");
	public static Pattern confhdr_pat=Pattern.compile("^>>>([^\\/]+)/([^ ]+) (\\d+) ([^\\(]+)\\((\\d+)\\)(\\d+[A-Za-z][a-z][a-z][0-9][0-9]) ([0-9]+:[0-9][0-9])( c([0-9]+)[\\*|\\* |\\*m|m| ]*$| \\**$| $|$)");
	
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
								// Confmsg
								state=S_FIRSTLINEPARSE;
								//	msgtype=CixMsg.T_CONF;
								
								String cixpath=ch_matcher.group(1)+"/"+ch_matcher.group(2);
								//Bundle bundle=Augur.getBundleManager().getBundle("/"+getDoorname()+"/"+cixpath,true,false); // Get bundle and create if necessary
								m.setTextpath("/"+getDoorname()+"/"+cixpath);
								m.setBundleId(null);
								Long lid=Long.decode(ch_matcher.group(3));
								m.setKnotId(lid);
								m.setSourceid(lid.toString());
								m.setAuthor(ch_matcher.group(4));
								try
								{
									m.setMsgDate(new Timestamp(sdf.parse(ch_matcher.group(6)+ch_matcher.group(7)).getTime()));
								}
								catch (ParseException e)
								{
									m.setMsgDate(new Timestamp(0));
								}
								if(ch_matcher.group(9)!=null)
								{
									m.setCommentto(Long.decode(ch_matcher.group(9)));
								}
								
								msgsize=Integer.parseInt(ch_matcher.group(5));
								currsize=0;
								currpos=currpos+l.length()+1;
								m.setBodystart(currpos);
								break;
							}
							
							System.out.println("MsgParse: Did not match ["+l+"]");
							
							break;
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
