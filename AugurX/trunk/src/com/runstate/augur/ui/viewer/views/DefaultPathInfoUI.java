/**
 * DefaultPathInfoUI.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.views;

import com.runstate.augur.ui.pathinfo.PathInfoUI;
import com.runstate.augur.ui.textpanes.HTMLPane;
import com.runstate.augur.ui.viewer.commands.VCExecuteCommand;
import java.net.URL;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;


public class DefaultPathInfoUI extends PathInfoUI implements HyperlinkListener {
	public DefaultPathInfoUI() {
		super();
	}
	
	HTMLPane htmlpane;
	JScrollPane htmlscrollpane;
	
	public void createUI() {
		htmlpane=new HTMLPane();
		htmlpane.addHyperlinkListener(this);
		htmlscrollpane=new JScrollPane(htmlpane);
		add(htmlscrollpane);
	}
	
	public void updatedPathInfo() {
		if(getPathInfo()!=null) {
			htmlpane.setHTML(getPathInfo().getReportText());
		}
		else {
			htmlpane.setHTML("");
		}
	}
	
	String anchor;
	boolean overlink=false;
	
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if(e.getEventType()==HyperlinkEvent.EventType.ACTIVATED || e.getEventType()==HyperlinkEvent.EventType.ENTERED) {
			URL url=e.getURL();
			if(url==null) {
				// Not valid but....
				anchor=findHrefText(e);
				
			}
			else {
				anchor=url.toString();
			}
			
			if(e.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
				// Is this an internal command?
				
				if(anchor.startsWith("@")) {
					processCommand(anchor);
				}
				else {
					openURL(anchor);
				}
			}
			
			overlink=true;
		}
		else {
			anchor=null;
			overlink=false;
		}
		
	}
	
	public void processCommand(String command)
	{
		
	}
	
	private String findHrefText(HyperlinkEvent e) {
		HTML.Tag t=javax.swing.text.html.HTML.getTag("a");
		HTML.Attribute ta=javax.swing.text.html.HTML.getAttributeKey("href");
		
		Element se=e.getSourceElement();
		AttributeSet as=se.getAttributes();
		AttributeSet aas=(AttributeSet)se.getAttributes().getAttribute(t);
		return (String)aas.getAttribute(ta);
	}
}

