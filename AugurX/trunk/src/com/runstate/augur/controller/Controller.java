/**
 * Controller.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.controller;

import java.sql.SQLException;
import javax.swing.*;

import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.GalleryException;
import com.runstate.augur.gallery.events.GalleryEvent;
import com.runstate.augur.gallery.listeners.GalleryEventListener;
import com.runstate.augur.ui.augurpanel.AugurPanel;
import com.runstate.augur.ui.augurpanel.AugurPanelManger;
import com.runstate.augur.ui.augurpanel.AugurPanelManagerListener;
import com.runstate.augur.ui.preferences.PreferencesUI;
import com.runstate.augur.ui.viewer.Browser;
import com.runstate.util.ResourceLoader;
import com.runstate.util.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.Preferences;

public class Controller implements GalleryEventListener,AugurPanelManagerListener
{
//	private static Log log=LogFactory.getLog(Controller.class);
	

	static Controller myController;
	
	public static Controller getController()
	{
		if(myController==null)
		{
			myController=new Controller();
		}
		
		return myController;
	}
	
        Profile profile=null;
	Gallery gallery=null;
	
	public static Gallery getGallery()
	{
		if(myController==null) return null;
		
		return myController.getCurrentGallery();
	}
	
	private Gallery getCurrentGallery()
	{
		return gallery;
	}
	
        public static Profile getProfile()
        {
            return myController.getCurrentProfile();
        }
	
        private Profile getCurrentProfile()
        {
            return profile;
        }
        
	Browser browser;
	
	public void restart()
	{
	}
	
	/**
	 * Method shutdown
	 *
	 */
	public void shutdown()
	{
		mode=SHUTDOWN;
	}
	
	private JFrame createBusy(String message,JFrame parent)
	{
		final JFrame jf=new JFrame();
		
		jf.setUndecorated(true);
		
		ImageIcon i=ResourceLoader.getIcon("artwork/augur-badge-A-64.jpg");
		
		JLabel busymessage=new JLabel();
		JProgressBar prog=new JProgressBar();
		prog.setIndeterminate(true);
		busymessage.setFont(busymessage.getFont().deriveFont(20.0f));
		busymessage.setBackground(Color.WHITE);
		busymessage.setForeground(Color.BLACK);
		busymessage.setIcon(i);
		busymessage.setText(message);
		
		busymessage.setBorder(BorderFactory.createEtchedBorder());
		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add(BorderLayout.SOUTH,prog);
		jf.getContentPane().add(BorderLayout.CENTER,busymessage);
		jf.setIconImage(i.getImage());
		
		jf.pack();
		jf.setLocationRelativeTo(parent);
		jf.setVisible(true);
		
		return jf;
	}
	
	int mode=0;
	
	static final int INERT=0;
	static final int STARTGALLERY=1; // Initialise Gallery
	static final int STARTUI=2; // Initialise 'default' user interface
	static final int RUNNING=3; // Default running mode
	static final int SHUTDOWN=4; // Shutting down
	static final int SHOWPREFS=5; // Fallback mode for gallery or UI error
	static final int WAITINGPREFS=6;
	
	JFrame preferencesframe;
	
	public void status(Object o)
	{
		System.out.println(o);
	}
	
	public void debug(Object o)
	{
		System.out.println(o);
	}
	
	public void run()
	{
		JFrame starting=null;
		
		mode=STARTGALLERY;
		
		AugurPanelManger.createManager();
		AugurPanelManger.getManager().addAugurPanelManagerListener(this);
		
		while(true)
		{
			
			switch(mode)
			{
				case STARTGALLERY:
					starting=createBusy("Augur Starting Up",null);
					starting.toFront();
					
					status("Starting gallery");
					
					try
					{
						gallery=new Gallery();
						gallery.addGalleryEventListener(this);
						gallery.startGallery();
					}
					catch (GalleryException ge)
					{
                                            Throwable e=ge.getCause();
                                            
                                            if(e instanceof SQLException)
                                            {
                                                if(e.getMessage().startsWith("The database is already in use by another process"))
                                                {
                                                    System.err.println("The database is already in use");
                                                    System.err.println("The exception is "+e);
                                                    System.exit(1);
                                                }
                                            }
                                            
                                            ge.printStackTrace();
                                            status("Gallery exception:");
                                            status(ge);
                                            mode=SHOWPREFS;
                                            break;
					}
					mode=STARTUI;
					break;
					
				case STARTUI:
					
					starting.toFront();
					
					status("Starting Views");
					
					mode=RUNNING;
					
					try
                                        {
                                            browser=new Browser();
                                        }
					catch(Exception e)
                                        {
                                            e.printStackTrace();
                                            mode=SHUTDOWN;
                                            break;
                                        }
                                        
					AugurPanelManger.getManager().addToDesktop(browser);
					
					browser.requestFocus();
					
					status("Running");
					starting.setVisible(false);
					starting=null;
					break;
					
				case SHOWPREFS:
					AugurPanelManger.getManager().addToDesktop(new PreferencesUI());
					starting=null;
					mode=WAITINGPREFS;
					break;
				case SHUTDOWN:
					closedown();
					break;
				case RUNNING:
				case WAITINGPREFS:
					
					//			System.out.println("Free Memory  "+(int)Runtime.getRuntime().freeMemory()/1024+"/"+(int)Runtime.getRuntime().totalMemory()/1024+"/"+Runtime.getRuntime().maxMemory());
					
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e) {}
					break;
			}
			
		}
	}
	
	
	public void closedown()
	{
		JFrame starting=null;
                
                int pending=0;
                
                try
                {
                    if (gallery != null) pending=gallery.getPendingCommandsCount();
                }
                catch(GalleryException ge) {}
                
		int rv=0;
                if (pending > 0)
                {
                    String msg="There are " + pending+ " pending messages - really quit?";
                    rv = JOptionPane.showConfirmDialog(null, msg,"Confirm Exit",JOptionPane.YES_NO_OPTION);
                }

                if (rv!=0)
                {
                    status("Shutdown Aborted");
                    mode=RUNNING;
                    if (windowcount == 0)
                    {
                        AugurPanelManger.getManager().addToDesktop(browser);
                    }
                    return;
                }
	
		status("Shutdown started");
		
		starting=createBusy("Augur Shutting Down",AugurPanelManger.getManager().getFirstFrame());
		
		starting.toFront();
		
		if(AugurPanelManger.getManager()!=null)
		{
			if(AugurPanelManger.getManager().shutdown())
			{
				if(gallery!=null) gallery.shutdown();
                                gallery=null;
			}
			else
			{
				starting.setVisible(false);
				starting=null;
				mode=RUNNING;
				return;
			}
			starting.setVisible(false);
			System.exit(0);
		}
	}
	
	public int syncAll()
	{
		if(mode!=RUNNING)
		{
			return -1;
		}
		
		return gallery.syncAll();
	}
	public int syncDoor(String doorname)
	{
		if(mode!=RUNNING)
		{
			return -1;
		}
		
		return gallery.syncDoor(doorname);
	}
	
	SimpleDateFormat sdf=new SimpleDateFormat("HH:mm dd/MMM/yy");
	
	public SimpleDateFormat getDateFormat()
	{
		return sdf;
	}
	
	public void galleryEventRecieved(GalleryEvent pe)
	{
		System.out.println(pe.getMessage());
	}
	
	int windowcount=0;
	
	public void augurPanelOpened(AugurPanelManger avm,AugurPanel av)
	{
		windowcount++;
	}
	
	public void augurPanelClosed(AugurPanelManger avm,AugurPanel av)
	{
		windowcount--;
		if(windowcount==0)
		{
			SwingWorker worker=new SwingWorker()
			{
				public Object construct()
				{
					closedown();
					return null;
				}
			};
			worker.start();
		}
	}
	
	public void startRunning()
	{
		mode=RUNNING;
	}
	
	public void setProfileName(String profileName)
	{
		profile=new Profile(profileName);
	}
	
	public String getProfileName()
	{
		return profile.getProfileName();
	}
	
	public Controller()
	{
            
		setProfileName(System.getProperty("augur.profile","default"));
	}
	
	String currentstyle=null;
	
	public String wrapWithStyle(String header,String body)
	{
		StringBuffer sb=new StringBuffer();
		
		sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		
		sb.append("<HTML><HEAD>");
		sb.append(getHTMLStyle());
		if(header!=null) sb.append(header);
		sb.append("</HEAD><BODY>");
		if(body!=null) sb.append(body);
		sb.append("</BODY></HTML>");
		
		return sb.toString();
	}
	
	public String getHTMLStyle()
	{
		if(currentstyle==null)
		{
			String s=profile.get(Prefs.UI_HTML_FONT);
			Font f=	Font.decode(s);
			
			StringBuffer sb=new StringBuffer();
			sb.append("<STYLE TYPE='text/css'>\n");
			sb.append("BODY { font-family:'");
			sb.append(f.getFamily());
			sb.append("'; font-weight:normal; font-size:");
			sb.append(f.getSize());
			sb.append("; padding:1px }\n");
			
			sb.append("PRE { font-size:");
			sb.append(f.getSize());
			sb.append("}\n");
			
			sb.append("H1 { font-size:");
			sb.append(f.getSize()*2);
			sb.append("; font-weight:bolder; background-color:#AAAAFF; color:#000000; margin:0px; padding:3px }\n");
			
			sb.append("H2 { font-size:");
			sb.append(f.getSize()*1.75);
			sb.append("; background-color:#DDDDFF; color:#000000; padding:3px }\n");
			
			sb.append("H3 { font-size:");
			sb.append(f.getSize()*1.5);
			sb.append("; background-color:#DDDDFF; color:#000000; padding:0px; margin:0px }\n");
			
			sb.append("A.button{ color:#0000FF background-color:#BBBBFF;");
			sb.append("font-family:'Trebuchet MS','Lucida Console',courier ;");
			sb.append("font-size:");
			sb.append(f.getSize());
			sb.append("; text-decoration:none; margin:2px; border-color:#000000; border-style:inset }\n");
			
			sb.append("A { text-decoration:none; font-size:");
			sb.append(f.getSize());
			sb.append("; font-weight:bold; }\n");
			
			sb.append("TD.HEAD { background-color:#AAAAAA }\n");
			
			sb.append("</STYLE>\n");
			
			currentstyle=sb.toString();
		}
		
		return currentstyle;
	}
	
}

