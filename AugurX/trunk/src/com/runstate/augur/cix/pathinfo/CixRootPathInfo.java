/**
 * CixRootPathInfo.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.pathinfo;

import com.runstate.augur.AugurX;
import com.runstate.augur.cix.commands.CixPathInfoCommand;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.commands.Command;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CixRootPathInfo extends CixPathInfo implements Serializable {
	
    static final long serialVersionUID = -6708348571815538038L;
	
	String conflist;
	ArrayList<String> sections=new ArrayList<String>();
	ArrayList<ConfEntry> confentries=new ArrayList<ConfEntry>();
	
	transient boolean showall=false;
	
	public CixRootPathInfo(Long doorid,Long bundleid) {
		super(doorid,bundleid);
	}
	
	/**
	 * Sets Sections
	 *
	 * @param    sections            an ArrayList<String>
	 */
	public void setSections(ArrayList<String> sections) {
		this.sections = sections;
	}
	
	/**
	 * Returns Sections
	 *
	 * @return    an ArrayList<String>
	 */
	public ArrayList<String> getSections() {
		return sections;
	}
	
	/**
	 * Sets Confentries
	 *
	 * @param    confentries         an ArrayList<ConfEntry>
	 */
	public void setConfentries(ArrayList<ConfEntry> confentries) {
		this.confentries = confentries;
	}
	
	/**
	 * Returns Confentries
	 *
	 * @return    an ArrayList<ConfEntry>
	 */
	public ArrayList<ConfEntry> getConfentries() {
		return confentries;
	}
	
	public String getUIClassName() {
		return "com.runstate.augur.cix.ui.CixRootPathInfoUI";
	}
	
	private void parseRootPathInfo(String conflist) {
		if(sections!=null) sections.clear(); else sections=new ArrayList<String>();
		if(confentries!=null) confentries.clear(); else confentries=new ArrayList<ConfEntry>();
		
		String[] lines=conflist.split("\n");
		Pattern confp=Pattern.compile("([oc]) ([^ ]+)[ ]*([^$]+)");
		Pattern subp=Pattern.compile("Conference: ([^$]+)");
		StringBuffer confnote=new StringBuffer();
		int sectionid=-1;
		boolean sublist=true;
		HashSet<String> subs=new HashSet<String>();
		
		for(String line:lines) {
			if(sublist) {
				if(line.equals(" #####AUGURBREAK#####")) {
					sublist=false;
				}
				else {
					Matcher m=subp.matcher(line);
					if(m.matches()) {
						subs.add(m.group(1));
					}
				}
				
			}
			else {
				Matcher m=confp.matcher(line);
				if(m.matches()) {
					String oc=m.group(1);
					String name=m.group(2);
					String desc="";
					
					if(m.groupCount()==3) desc=m.group(3);
					boolean exists=Controller.getController().getGallery().getBundleManager().getBundleExists("/cix/"+name);
					boolean joined=subs.contains(name);
					
					ConfEntry ce=new ConfEntry(oc,sectionid,name,desc,exists,joined);
					
					confentries.add(ce);
				} else if(!line.startsWith("--")) {
					if(!line.equals("-")) {
						if(line.startsWith("-")) {
							confnote.append(line);
							confnote.append("\n");
						}
						else {
							if(!line.equals("") && !line.equals("(o=open, c=closed)")) {
								sections.add(line);
								sectionid++;
							}
						}
					}
					
					
				}
			}
		}
	}
	
	public void setConflist(String conflist) {
		this.conflist = conflist;
		parseRootPathInfo(conflist);
		this.conflist = null;
	}
	
	public String getConflist() {
		return conflist;
	}
	
	public Command getRefreshCommand() {
		return new CixPathInfoCommand(getDoorid(),getBundleid());
	}
	
	public String getReportText() {
		return "";
	}
	
	public boolean doCommand(String command) {
		return	false;
	}
	
	public	class ConfEntry implements Serializable {
		
		static final long serialVersionUID = -6708348571815538098L;
		
		public String type;
		public int id;
		public String name;
		public String description;
		public boolean existslocally;
		public boolean joined;
		
		public ConfEntry(String type, int id, String name, String description,boolean existslocally,boolean joined) {
			this.type = type;
			this.id = id;
			this.name = name;
			this.description = description;
			this.existslocally = existslocally;
			this.joined = joined;
		}
		
		public boolean isOpen() {
			return type.equals("o");
		}
	}
}

