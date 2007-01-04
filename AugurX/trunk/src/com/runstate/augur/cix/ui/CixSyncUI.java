/**
 * PollCixView.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.ui;
import javax.swing.*;

import com.runstate.augur.cix.sync.CixSync;
import com.runstate.augur.controller.SyncUI;
import com.runstate.augur.gallery.Sync;
import com.runstate.augur.gallery.SyncEvent;
import com.runstate.augur.gallery.SyncListener;
import com.runstate.util.ImageCache;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import com.runstate.util.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CixSyncUI extends SyncUI implements SyncListener
{
//	TransferStatus ts=new TransferStatus(null,null,null);
	
	CixSync cixsyncmission=null;
	
	/**
	 * Method prefersTab
	 *
	 * @return   a boolean
	 *
	 */
	public boolean prefersTab()
	{
		// TODO
		return false;
	}
	
	
	Action session_action=new AbstractAction("Session")
	{
		{
			putValue(Action.SHORT_DESCRIPTION, "Session");
			putValue(Action.LONG_DESCRIPTION, "Session");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("Session");
			SwingWorker sw=new SwingWorker()
			{
				public Object construct()
				{
					cixsyncmission.session();
					return null;
				}
			};
			sw.start();
		}
	};
	
	Action info_action=new AbstractAction("Info")
	{
		{
			putValue(Action.SHORT_DESCRIPTION, "Info");
			putValue(Action.LONG_DESCRIPTION, "Info");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("Inline");
			SwingWorker sw=new SwingWorker()
			{
				public Object construct()
				{
					cixsyncmission.info();
					return null;
				}
			};
			sw.start();
		}
	};
	
	Action queued_action=new AbstractAction("Queued")
	{
		{
			putValue(Action.SHORT_DESCRIPTION, "Queued");
			putValue(Action.LONG_DESCRIPTION, "Queued");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("Queued");
			SwingWorker sw=new SwingWorker()
			{
				public Object construct()
				{
					cixsyncmission.sendAndCollect();
					return null;
				}
			};
			sw.start();
		}
	};
	
	Action collect_action=new AbstractAction("Collect")
	{
		{
			putValue(Action.SHORT_DESCRIPTION, "Collect");
			putValue(Action.LONG_DESCRIPTION, "Collect");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("Collect");
			SwingWorker sw=new SwingWorker()
			{
				public Object construct()
				{
					cixsyncmission.collect();
					return null;
				}
			};
			sw.start();
		}
	};
	
	Action disconnect_action=new AbstractAction("Disconnect")
	{
		{
			putValue(Action.SHORT_DESCRIPTION, "Disconnect");
			putValue(Action.LONG_DESCRIPTION, "Disconnect");
		}
		
		
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("Disconnect");
			SwingWorker sw=new SwingWorker()
			{
				public Object construct()
				{
					cixsyncmission.disconnect();
					return null;
				}
			};
			sw.start();
		}
	};
	
	Action connect_action=new AbstractAction("Connect")
	{
		{
			putValue(Action.SHORT_DESCRIPTION, "Connect");
			putValue(Action.LONG_DESCRIPTION, "Connect");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("Connect");
			SwingWorker sw=new SwingWorker()
			{
				public Object construct()
				{
					cixsyncmission.connect();
					return null;
				}
			};
			sw.start();
		}
	};
	
	public boolean forceDefault()
	{
		return true;
	}
	
	boolean filetransfer=false;
	
	JTextArea log;
	
	public CixSyncUI()
	{
		super();
	}
	
	public void initialiseSyncUI()
	{
		
		if(!(getSync() instanceof CixSync)) throw new RuntimeException("Attempted to give non CixSync class to CixSyncUI");
		
		setLayout(new BorderLayout());
		
		cixsyncmission=(CixSync)getSync();
		cixsyncmission.addSyncListener(this);
		
		log=new JTextArea();
		
		log.setFont(log.getFont().deriveFont(10.0f));
		
		JPanel buttons=new JPanel(new FlowLayout());
		buttons.add(new JButton(session_action));
		buttons.add(new JButton(info_action));
		buttons.add(new JButton(queued_action));
		buttons.add(new JButton(collect_action));
		buttons.add(new JButton(connect_action));
		buttons.add(new JButton(disconnect_action));
	
		add(BorderLayout.CENTER,new JScrollPane(log));
		
		add(BorderLayout.NORTH,buttons);
		
		updateButtons();
	}
	
	SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
	
	public void syncEventOccurred(SyncEvent sme)
	{
		Date d=new Date();
		
		log.append(sdf.format(d)+":"+sme.getDescription()+"\n");
		
		log.setCaretPosition(log.getText().length());
		
		updateButtons();
	}
	
	private void updateButtons()
	{
		session_action.setEnabled(!cixsyncmission.isBusy());
		info_action.setEnabled(!cixsyncmission.isBusy());
		queued_action.setEnabled(!cixsyncmission.isBusy());
		collect_action.setEnabled(!cixsyncmission.isBusy());
		connect_action.setEnabled(cixsyncmission.canConnect());
		disconnect_action.setEnabled(cixsyncmission.canDisconnect());
	}
	
	public String getPrefsName()
	{
		return "synccix";
	}
	
	public boolean closeRequest(boolean force) {
		return true;
	}
	
	private void shrinkFont(Component c)
	{
		c.setFont(c.getFont().deriveFont(9.0f));
	}
	
	public ImageIcon getIcon()
	{
		return ImageCache.get("synccix");
	}
	
	/**
	 * Method getMenus
	 *
	 * @return   a JMenu[]
	 *
	 */
	public JMenu[] getMenus()
	{
		// TODO
		return null;
	}
	
	
	
}

