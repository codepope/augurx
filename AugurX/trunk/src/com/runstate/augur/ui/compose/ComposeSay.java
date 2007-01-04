/**
 * ConsoleFrame.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.compose;

import com.runstate.augur.controller.Profile;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.augur.gallery.commands.commands.SayCommand;
import com.runstate.augur.ui.augurpanel.AugurPanel;
import com.runstate.util.ImageCache;
import com.runstate.util.swing.SpellingChecker;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

public class ComposeSay extends AugurPanel {
	JTextArea newmsg;
	JScrollPane newmsgpane;
	JButton send=new JButton("Send");
	JPanel saytools=new JPanel();
	JPopupMenu popupmenu=new JPopupMenu();
	Hashtable<Object, Action> actions;
	Msg msg=null;
	Long bundleid=null;
	
	Profile profile=Controller.getProfile();
	
	public String getPrefsName()
	{
		return "composesay";
	}
	
	Action send_action = new AbstractAction("Send") { {
			putValue(Action.SHORT_DESCRIPTION, "Send");
			putValue(Action.MNEMONIC_KEY,new Integer(KeyEvent.VK_S));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.CTRL_MASK));
		}
		
		// This method is called when the action is invoked
		public void actionPerformed(ActionEvent e) {
			
			long now=System.currentTimeMillis();
			String text=newmsg.getText();
				
			Door door=Controller.getGallery().getDoorByBundleId(bundleid);
			
			SayCommand cmd=door.createSayCommand(bundleid,text);
	
			try {
				Controller.getGallery().addCommand((Command)cmd);
			}
			catch (com.runstate.augur.gallery.GalleryException ge) { ge.printStackTrace(); }
			
			getContainer().closeView(true);
		}
	};
	
	Action spelling_action = new AbstractAction("Spelling") { {
			putValue(Action.SHORT_DESCRIPTION, "Spelling");
			putValue(Action.MNEMONIC_KEY,new Integer(KeyEvent.VK_E));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E,KeyEvent.CTRL_MASK));
		}
		
		// This method is called when the action is invoked
		public void actionPerformed(ActionEvent e) {
			SpellingChecker sp=SpellingChecker.getInstance();
			sp.checkSpelling(newmsg);
			send_action.setEnabled(true);
		}
	};
	
		Action cancel_action = new AbstractAction("Cancel") { {
			putValue(Action.SHORT_DESCRIPTION, "Cancel");
			putValue(Action.MNEMONIC_KEY,new Integer(KeyEvent.VK_C));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C,KeyEvent.CTRL_MASK));
		}
		
		// This method is called when the action is invoked
		public void actionPerformed(ActionEvent e) {
			getContainer().closeView(false);
			//if(closeRequest()) ComposeSay.this.close();
		}
	};
	
	public boolean wantsMenubar() { return true; }
	public boolean isApplicationMainWindow() { return false; }
	
	JMenu composemenu;
	JMenu editmenu;
	
	public ArrayList<JMenu> getMenus()
	
	{
		ArrayList<JMenu> jm=new ArrayList<JMenu>();
		jm.add(composemenu);
		jm.add(editmenu);
		return jm;
	}
	
	public String getName() { return "Say in "+Controller.getController().getGallery().getBundleManager().idToName(bundleid); }
	
	public String getTitle() { return "Say in "+Controller.getController().getGallery().getBundleManager().idToName(bundleid); }
	
	public ComposeSay(Long bundleid)  {
		super();
		
		this.bundleid=bundleid;

		//	Document doc=new PlainDocument();
		
		newmsg=new JTextArea();
		newmsg.setWrapStyleWord(true);
		newmsg.setLineWrap(true);
		newmsg.setBackground(Color.WHITE);
		newmsg.setForeground(Color.BLACK);
//		newmsg.setPreferredSize(new Dimension(400,200));
		newmsg.setEditable(true);
		newmsg.setText("");
		newmsg.setFont(Font.decode(profile.get(Prefs.UI_COMPOSE_FONT,"Monospaced-PLAIN-12")));
		int w=newmsg.getFontMetrics(newmsg.getFont()).stringWidth("0123456789")/10;
		int h=newmsg.getFontMetrics(newmsg.getFont()).getHeight();
//		newmsg.setPreferredSize(new Dimension(w*80,h*20));
//		newmsg.setMinimumSize(new Dimension(w*80,h*20));
//		newmsg.setMaximumSize(new Dimension(w*80,h*20));
		
//		newmsg.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.CTRL_MASK),"send");
//		newmsg.getActionMap().put("send",send_action);
		
		newmsgpane=new JScrollPane(newmsg);
		newmsgpane.setPreferredSize(new Dimension(w*80,h*20));
		newmsgpane.setMinimumSize(new Dimension(w*80,h*10));
		newmsgpane.setMaximumSize(new Dimension(w*80,h*20));
		
		setLayout(new BorderLayout());
		
		saytools.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
		saytools.add(new JButton(spelling_action));
		saytools.add(new JButton(cancel_action));
		saytools.add(new JButton(send_action));
		
		composemenu=new JMenu("Compose");
		composemenu.setMnemonic('C');
		composemenu.add(new JMenuItem(send_action));
		composemenu.add(new JSeparator());
		composemenu.add(new JMenuItem(spelling_action));
		composemenu.add(new JSeparator());
		composemenu.add(new JMenuItem(cancel_action));
		
		editmenu=new JMenu("Edit");
		editmenu.setMnemonic('E');
		
		createActionTable(newmsg);
		Action cutAction=getActionByName(DefaultEditorKit.cutAction);
		JMenuItem cutMenu=new JMenuItem(cutAction);
		cutMenu.setText("Cut");
		editmenu.add(cutMenu);
		
		popupmenu.add(cutMenu);
		
		Action copyAction=getActionByName(DefaultEditorKit.copyAction);
		JMenuItem copyMenu=new JMenuItem(copyAction);
		copyMenu.setText("Copy");
		popupmenu.add(copyMenu);
		editmenu.add(copyMenu);
		
		Action pasteAction=getActionByName(DefaultEditorKit.pasteAction);
		JMenuItem pasteMenu=new JMenuItem(pasteAction);
		pasteMenu.setText("Paste");
		popupmenu.add(pasteMenu);
		editmenu.add(pasteMenu);
		
		newmsg.addMouseListener(new PopupTrigger());

		add(BorderLayout.CENTER,newmsgpane);
		add(BorderLayout.SOUTH,saytools);
		
		if(profile.getBool(Prefs.UI_SPELL_FORCE,false))
		{
			send_action.setEnabled(false);
		}
	}
	
	public void becomingVisible(boolean b)
	{
		if(b) newmsg.requestFocusInWindow();
	}
	
	private void createActionTable(JTextComponent textComponent) {
		actions = new Hashtable<Object, Action>();
		Action[] actionsArray = textComponent.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			actions.put(a.getValue(Action.NAME), a);
		}
	}
	
	private Action getActionByName(String name) {
		return (Action)(actions.get(name));
	}
	
	class PopupTrigger extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			evaluatePopup(e);
		}
		
		public void mouseReleased(MouseEvent e) {
			evaluatePopup(e);
		}
		
		private void evaluatePopup(MouseEvent e) {
			if(e.isPopupTrigger()) {
				int x=e.getX();
				int y=e.getY();
				popupmenu.show(newmsg,x,y);
			}
		}
	}

	public boolean closeRequest(boolean force) {
		
		if(force) return true;
		
		if(newmsg.getText().length()==0) return true;
		
		Object[] options={ "Discard","Keep composing this message" };
		
		int rv=JOptionPane.showOptionDialog(this,"Do you want to discard this say?","Warning",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
		
		if(rv==0) return true;
		
		return false;
	}
	
	public ImageIcon getIcon() {
		return ImageCache.get("say");
	}
}

