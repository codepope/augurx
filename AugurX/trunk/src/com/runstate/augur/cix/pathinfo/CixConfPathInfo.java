/**
 * CixConfPathInfo.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.pathinfo;

import com.runstate.augur.AugurX;
import com.runstate.augur.cix.commands.CixPathInfoCommand;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.commands.Command;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CixConfPathInfo extends CixPathInfo {
	
	static final long serialVersionUID = -3630628217784116055L;
	
	String conftext;
	String mycixusername;
	
	transient Controller prefs=Controller.getController();
	
	public CixConfPathInfo(Long doorid,Long bundleid) {
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
	 * @param    confnote            a  String
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
	 * @param    created             a  String
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
	 * @param    moderators          an ArrayList<String>
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
	 * @param    topics              an ArrayList<Topic>
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
	 * @param    participants        an ArrayList<Part>
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
		return new CixPathInfoCommand(getDoorid(),getBundleid());
	}
	
	public String getUIClassName() {
		return "com.runstate.augur.cix.ui.CixConfPathInfoUI";
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
		if(moderators.contains(Controller.getProfile().get(Prefs.M_C_USERNAME))) return true;
		return false;
	}
	
	private boolean isMyUser(String s) {
		if(s.equals(Controller.getProfile().get(Prefs.M_C_USERNAME))) return true;
		return false;
	}
	
	private void parseText( String conflist) {
		
		moderators=new ArrayList<String>();
		participants=new ArrayList<Part>();
		topics=new ArrayList<Topic>();
		confnote="";
		created="";
		
		int mode=0; // 0: Moderator line, 1:created, 2: dash line, 3: topics, 4: confnote, 5: partlist
		
		String patterntStr="(^$|^.*\\S+.*$)+";
		
		//	StringTokenizer st=new StringTokenizer(conftext,"\n",true);
		
		Pattern p=Pattern.compile(patterntStr,Pattern.MULTILINE);
		Matcher matcher=p.matcher(conflist);
		
		while(matcher.find()) {
			String line=matcher.group();
			//	System.out.println("line ["+line+"]");
			switch(mode) {
				case 0:
					// Parse mods
					String mods=line.substring(15);
					String[] modnames=mods.split("[,| |\\.]+");
					for(int i=0;i<modnames.length;i++) {
						moderators.add(modnames[i]);
					}
					mode=1;
					break;
				case 1:
					// Parse created
					created=line.substring(15);
					mode=2;
					break;
				case 2:
					// Skip --- line
					mode=3;
					break;
				case 3:
				    // parse topic
					
					if(line.equals("")) {
						mode=4;
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
				case 4:
					if(line.equals(" #####AUGURBREAK#####")) {
						mode=5;
					}
					else {
						confnote=confnote+line+"\n";
					}
					break;
				case 5:
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

