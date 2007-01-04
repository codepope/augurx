/**
 * PathInfoPanel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.views;
import javax.swing.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.PathInfo;
import com.runstate.augur.gallery.commands.CommandListener;
import com.runstate.augur.gallery.events.PoolEvent;
import com.runstate.augur.gallery.listeners.PoolEventListener;
import com.runstate.augur.ui.pathinfo.PathInfoUI;
import com.runstate.augur.ui.pathinfo.PathInfoUIContainer;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import com.runstate.augur.ui.viewer.MessageStrip;
import com.runstate.augur.ui.viewer.commands.VCExecuteCommand;
import com.runstate.augur.ui.viewer.commands.ViewerCommand;
import com.runstate.util.ImageCache;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;

public class ViewerPathInfo
extends JPanel
implements PoolEventListener,CommandListener,BrowserCommandHandler,PathInfoUIContainer {
	
	
	JLabel status;
	Long bundleid;
	PathInfo currentpathinfo;
	MessageStrip messagestrip;
	BrowserCommandHandler vch;
	
	JPanel uipanel;
	CardLayout uipanes;
	PathInfoUI currentui;
	
	HashMap<String,PathInfoUI> piuimap=new HashMap<String,PathInfoUI>();
	
	public ViewerPathInfo(BrowserCommandHandler vch) {
		super();
		this.vch = vch;
		messagestrip = new MessageStrip();
		
		uipanes = new CardLayout();
		uipanel = new JPanel(uipanes);
		
		status = new JLabel();
		setLayout(new BorderLayout());
		
		add(BorderLayout.NORTH, messagestrip);
		add(BorderLayout.CENTER, uipanel);
		add(BorderLayout.SOUTH, status);
		
		setBundleid(Controller.getController().getGallery().getBundleManager().getRootBundleId());
		
		this.setPreferredSize(new Dimension(640, 384));
		Controller.getGallery().addPoolEventListener(this);
		currentui = null;
	}
	
	public JComponent getMainPane() {
		return vch.getMainPane();
	}
	
	public void updateMenus() {
		vch.updateMenus();
	}
	
	public JMenu getMenu() {
		if (currentui == null) return null;
		return currentui.getMenu();
	}
	
	public void setBundleid(final Long bundleid) {
		
		this.bundleid = bundleid;
		
		if (bundleid == null) {
			System.out.println("BAD setBundleId");
		}
		
//		SwingWorker sw=new SwingWorker() {
//
		currentpathinfo = null;
		
		if (Controller.getController().getGallery().getBundleManager().getRootBundleId().equals(bundleid)) {
			currentpathinfo = new RootPathInfo();
		}
		else {
			currentpathinfo = Controller.getController().getGallery().getPathInfo(bundleid);
			
			if (currentpathinfo == null) {
				currentpathinfo = Controller.getController().getGallery().getDoorByBundleId(bundleid).createPathInfo(bundleid);
			}
		}
		
		
		PathInfoUI pui=piuimap.get(currentpathinfo.getUIClassName());
		
		if (pui == null) {
			// Instansiate the UI
			try {
				pui = (PathInfoUI)Class.forName(currentpathinfo.getUIClassName()).newInstance();
				pui.setPathInfoUIContainer(this);
				pui.createUI();
				uipanel.add(pui, currentpathinfo.getUIClassName());
				piuimap.put(currentpathinfo.getUIClassName(), pui);
			}
			catch (ClassNotFoundException e) { e.printStackTrace(); }
			catch (InstantiationException e) {e.printStackTrace();}
			catch (IllegalAccessException e) {e.printStackTrace();}
		}
		
		uipanes.show(uipanel, currentpathinfo.getUIClassName());
		currentui = pui;
		
		
		if (currentui != null) {
			currentui.setPathInfo(currentpathinfo);
			
			updateTitle();
		}
		else {
			System.out.println("Could not set ui pathinfo");
		}
	}
	
	public void updateTitle() {
		if (currentpathinfo != null) {
			StringBuffer sb=new StringBuffer();
			sb.append(currentpathinfo.getBundleName());
			
			if (!(currentpathinfo instanceof RootPathInfo)) {
				sb.append(" [");
				
				sb.append(currentpathinfo.getUpdateDate() == null ?
						  "Never updated":
						  Controller.getController().getDateFormat().format(currentpathinfo.getUpdateDate()));
				
				if (currentpathinfo.isRefreshpending()) {
					sb.append(" Refresh pending");
				}
				sb.append("]");
			}
			messagestrip.setMsg(sb.toString());
		}
	}
	
	private void setPathInfo(PathInfo newpathinfo) {
		currentpathinfo = newpathinfo;
		updateTitle();
		if (currentui != null)	currentui.setPathInfo(currentpathinfo);
	}
	
	public void poolEventOccurred(PoolEvent ce) {
		System.out.println("ce " + ce);
		
		if (bundleid == null) return;
		
		if (!ce.getPool().equals(PathInfo.getPoolName())) {
			return;
		}
		
		String bstring=bundleid.toString();
		
		if (ce.getType() == PoolEvent.UPDATED) {
			if (ce.getPool().equals(PathInfo.getPoolName())) {
				if (ce.getKey().equals(bstring)) {
					setPathInfo((PathInfo)ce.getPoolable());
				}
			}
		}
	}
	
	public void doCommand(ViewerCommand vc) {
		vch.doCommand(vc);
	}
	
	public void openURL(String url) {
		vch.doCommand(new VCExecuteCommand(url));
	}
	
	public void showMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(ViewerPathInfo.this, message, "Path Info ", JOptionPane.INFORMATION_MESSAGE);
				}
			});
	}
	
	public void updateCommandLine() {
		vch.updateCommandLine();
	}

}

