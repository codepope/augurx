/**
 * AboutAugur.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.ui.augurpanel.AugurPanel;
import javax.swing.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.ui.textpanes.HTMLPane;
import com.runstate.util.ResourceLoader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.BevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class AboutAugur extends AugurPanel implements HyperlinkListener
{
	
	Thread missionthread=null;
	
	/**
	 * Method wantsMenubar
	 *
	 * @return   a boolean
	 *
	 */
	public boolean wantsMenubar()
	{
		// TODO
		return false;
	}
	
	/**
	 * Method wantAppMenus
	 *
	 * @return   a boolean
	 *
	 */
	public boolean isApplicationMainWindow()
	{
		// TODO
		return false;
	}
	
	
	
	public String getPrefsName()
	{
		return "aboutwindow";
	}

	public boolean prefersTab()
	{
		return false;
	}
	
	
	
	public boolean forceDefault()
	{
		return true;
	}
	
	Action ok_action=new AbstractAction("Ok")
	{
		
		/**
		 * Invoked when an action occurs.
		 */
		public void actionPerformed(ActionEvent e)
		{
			getContainer().closeView(true);
		}
		
		
	};
	
	public String getTitle()
	{
		return "About Augur";
	}
	
	public AboutAugur()
	{
		super();
		setBackground(Color.WHITE);
		
		ImageIcon	im=ResourceLoader.getIcon("artwork/badge.jpg");
		
		JLabel jl=new JLabel(im);
		jl.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		HTMLPane mtp=new HTMLPane();
		mtp.addHyperlinkListener(this);
		mtp.setHTML(buildHelpPage());
		mtp.setPreferredSize(new Dimension(500,500));
		JPanel okpanel=new JPanel();
		okpanel.add(new JButton(ok_action));
		
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER,new JScrollPane(mtp));
		add(BorderLayout.WEST,jl);
		add(BorderLayout.SOUTH,okpanel);
		
	}
	
	private String buildHelpPage()
	{
		StringBuffer sb=new StringBuffer();
		
//		sb.append("<HTML>");
//		sb.append("<HEAD>");
//		sb.append(Augur.getController().getHTMLStyle());
//		sb.append("</HEAD>");
//		sb.append("<BODY>");
//
		sb.append("<H1>Augur : Final Client</H1>");
		
//		sb.append("<TABLE WIDTH='100%'><TR><TD>");
//		sb.append("<IMG ALIGN='RIGHT' HSPACE=10 VSPACE=10 SRC='class://ResourceAnchor/icons/afc.jpg'>");
//		sb.append("</TD><TD>");
		sb.append(AugurX.name+" "+AugurX.edition+" "+AugurX.build+"<BR><BR>");
		sb.append("© Dj Walker-Morgan 2000-2006<BR><BR>");
		sb.append("Augur is developed by <A href='http://www.runstate.com/'>Dj Walker-Morgan</A> who would like to thank ");
		
		
		sb.append("<A href='http://www.abigail.org.uk/'>Sarah</A> for the support and the logo, ");
		sb.append("<A href='http://mst3kinfo.com'>MST3K</A> for developmental entertainment,");
		sb.append("<A href='http://www.omnicore.com/'>Omnicore</A> for Codeguide (a wonderful Java IDE),");
		sb.append("and all the early victims of previous incarnations.");
		
		sb.append("<BR><BR>Augur uses ");
		sb.append("<A HREF='http://hsqldb.sourceforge.net/'>HSQLDB</A> for data storage, ");
		sb.append("<A HREF='http://www.jcraft.com/jsch'>Java Secure Channel</A> for SSH communications, ");
		sb.append("<A HREF='http://www.beanshell.org'>BeanShell</A> for scripting and ");
		sb.append("<A HREF='http://jetty.mortbay.org/jetty/index.html'>Jetty</A> for web services.");
		

		return Controller.getController().wrapWithStyle(null,sb.toString());
	}
	
	public boolean requestClose(boolean force)
	{
		return true;
	}
	
	/**
	 * Called when a hypertext link is updated.
	 *
	 * @param e the event responsible for the update
	 */
	@Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        URI u;
        try {
            u = e.getURL().toURI();

            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(u);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(AboutAugur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	
}

