/**
 * TwixConfPathInfo.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.pathinfo;

import com.runstate.augur.AugurX;
import com.runstate.augur.twix.commands.TwixPathInfoCommand;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.commands.Command;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwixConfPathInfo extends TwixPathInfo {
	
	static final long serialVersionUID = -3630628217784116055L;
	
	String conftext;
	String mytwixusername;
	
	transient Controller prefs=Controller.getController();
	
	public TwixConfPathInfo(Long doorid,Long bundleid) {
		super(doorid,bundleid);
		ArrayList<Bundle> subpaths=Controller.getController().getGallery().getSubPaths(getBundleid());
		
		for(Bundle b:subpaths) {
			Topic t=new Topic(b.getBundlename(),b.getBasename(),"");
			t.indb=true;
			topics.add(t);
		}
	}
	
	/**
	 * Sets Confnote
	 *
	 * @param    Confnote            a  String
	 */
	public void setConfnote(String confnote) {
		this.confnote = confnote;
	}
	
	/**
	 * Returns Confnote
	 *
	 * @return    a  String
	 */
	public String getConfnote() {
		return confnote;
	}
	
	/**
	 * Sets Created
	 *
	 * @param    Created             a  String
	 */
	public void setCreated(String created) {
		this.created = created;
	}
	
	/**
	 * Returns Created
	 *
	 * @return    a  String
	 */
	public String getCreated() {
		return created;
	}
	
	/**
	 * Sets Moderators
	 *
	 * @param    Moderators          an ArrayList<String>
	 */
	public void setModerators(ArrayList<String> moderators) {
		this.moderators = moderators;
	}
	
	/**
	 * Returns Moderators
	 *
	 * @return    an ArrayList<String>
	 */
	public ArrayList<String> getModerators() {
		return moderators;
	}
	
	/**
	 * Sets Topics
	 *
	 * @param    Topics              an ArrayList<Topic>
	 */
	public void setTopics(ArrayList<Topic> topics) {
		this.topics = topics;
	}
	
	/**
	 * Returns Topics
	 *
	 * @return    an ArrayList<Topic>
	 */
	public ArrayList<Topic> getTopics() {
		return topics;
	}
	
	/**
	 * Sets Participants
	 *
	 * @param    Participants        an ArrayList<Part>
	 */
	public void setParticipants(ArrayList<Part> participants) {
		this.participants = participants;
	}
	
	/**
	 * Returns Participants
	 *
	 * @return    an ArrayList<Part>
	 */
	public ArrayList<Part> getParticipants() {
		return participants;
	}
	
	
	public void setConftext(String conflist) {
		parseText(conflist);
		conftext=null;
	}
	
	
	public Command getRefreshCommand() {
		return new TwixPathInfoCommand(getDoorid(),getBundleid());
	}
	
	public String getUIClassName() {
		return "com.runstate.augur.twix.ui.TwixConfPathInfoUI";
	}
	
	
	
	public String getReportText() {
		return "";
	}
	
	ArrayList<String> moderators=new ArrayList<String>();
	ArrayList<Topic> topics=new ArrayList<Topic>();
	ArrayList<Part> participants=new ArrayList<Part>();
	
	String confnote="";
	String created=null;
	
	
	private boolean isModerator() {
		if(getUpdateDate()==null) return false;
		if(moderators.contains(Controller.getProfile().get(Prefs.M_T_USERNAME))) return true;
		return false;
	}
	
	private boolean isMyUser(String s) {
		if(s.equals(Controller.getProfile().get(Prefs.M_T_USERNAME))) return true;
		return false;
	}
	
	private void parseText( String conflist) {
		
		moderators=new ArrayList<String>();
		participants=new ArrayList<Part>();
		topics=new ArrayList<Topic>();
		confnote="";
		created="";
		
//		int mode=0; // 0: Moderator line, 1:created, 2: dash line, 3: topics, 4: confnote, 5: partlist
		int mode=1; // 0: show scratchpad 1:Moderator line, 2:created, 3: dash line, 4: topics, 5: confnote, 5: partlist
		//Show scratchpad doesn't exist any more, as I've told it not to echo
		String patterntStr="(^$|^.*\\S+.*$)+";
		
		//	StringTokenizer st=new StringTokenizer(conftext,"\n",true);
		
		Pattern p=Pattern.compile(patterntStr,Pattern.MULTILINE);
		Matcher matcher=p.matcher(conflist);
		
		while(matcher.find()) {
			String line=matcher.group();
			//	System.out.println("line ["+line+"]");
			switch(mode) {
                                case 0:
                                        // Ignore it - it;'s the show scratchpad line
                                        if (line.equalsIgnoreCase("show scratchpad"))
                                        {
                                            mode=1;
                                            break;
                                        }
                                        else
                                        {
                                            System.out.println("Error parsing parlist - aborting parlist parse");
                                            System.out.println("The line was: ");
                                            System.out.println(line);
                                            mode=7;
                                            break;
                                        }
				case 1:
					// Parse mods
					String mods=line.substring(14,line.length()); //15 for cix - 14 for twix
					String[] modnames=mods.split("[,| |\\.]+");
					for(int i=0;i<modnames.length;i++) {
						moderators.add(modnames[i]);
					}
					mode=2;
					break;
				case 2:
					// Parse created
					created=line.substring(15);
					mode=3;
					break;
				case 3:
					// Skip --- line
					mode=4;
					break;
				case 4:
				    // parse topic
					
					if((line.equals(" #####AUGURBREAK#####")) || (line.equals(" #####AUGURBREAK2#####")) )
                                        {
						mode=6;
                                                break;
					}
                                        if (line.equals(""))
                                        {
                                            mode=5;
                                            break;
                                        }
					else {
						String name=line.substring(0,line.length()>15?15:line.length()).trim();
						String desc="";
						if(line.length()>=16) {
							desc=line.substring(16);
						}
						
						Topic t=new Topic(getBundleName(),name,desc);
						t.indb=Controller.getGallery().getBundleManager().getBundleExists(t.path);
						topics.add(t);
					}
					// 16 chars
					break;
				case 5:
					if(line.equals(" #####AUGURBREAK#####")) {
						mode=6;
					}
					else {
						confnote=confnote+line+"\n";
					}
					break;
				case 6:
                                    if (line.equals("M:")){break;}
                                    if (line.equals(" #####AUGURBREAK2#####")){break;}
                                        line = line.trim();
					Part part=new Part(line,moderators.contains(line));
					
					participants.add(part);
					
					break;
			}
		}
	}
	
	public class Topic implements Serializable {
		public String name;
		public String desc;
		public String path;
		public boolean indb;
		
		static final long serialVersionUID = -3630628217784886055L;

		Topic(String p,String n,String d) {
			name=n;
			desc=d;
			path=p;
		}
		
		String getFullPath() {
			return path+"/"+name;
		}
	}
	
	public class Part implements Serializable {
		public String name;
		public boolean mod;
		
		static final long serialVersionUID = -3630628217784996055L;
		
		Part(String n,boolean m) {
			name=n;
			mod=m;
		}
	}
	
	public boolean doCommand(String command) {
		return super.doCommand(command);
	}
}

