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
import javax.swing.text.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.augur.gallery.commands.commands.CommentCommand;
import com.runstate.augur.ui.augurpanel.AugurPanel;
import com.runstate.augur.ui.augurpanel.AugurPanelManger;
import com.runstate.augur.ui.textpanes.MsgPane;
import com.runstate.augur.ui.textpanes.MsgPaneListener;
import com.runstate.augur.ui.viewer.StatusListener;
import com.runstate.util.ImageCache;
import com.runstate.util.swing.SpellingChecker;
import java.util.ArrayList;
import java.util.Hashtable;

public class ComposeComment extends AugurPanel implements StatusListener,MsgPaneListener
{
	public boolean wantsMenubar()
	{
		return true;
	}
	

	public boolean isApplicationMainWindow()
	{
		return false;
	}
	
	public ArrayList<JMenu> getMenus()
	{
		ArrayList<JMenu> al=new ArrayList<JMenu>();
		al.add(composemenu);
		al.add(editmenu);
		return al;
	}

	JMenu composemenu;
	JMenu editmenu;
	
	public void URLActivated(String anchor)
	{
		System.out.println("ComposeComment URLActivated("+anchor+") not implemented");
	}
	
	JSplitPane commentsplit;
	MsgPane srcmsg;
	JTextPane newmsg;
	JScrollPane newmsgpane;
	Profile profile=Controller.getProfile();
	JPanel commenttools=new JPanel();
	Msg msg=null;
	JPopupMenu popupmenu=new JPopupMenu("Edit");
	Hashtable<Object, Action> actions;
	
	public String getPrefsName()
	{
		return "composecomment";
	}
	
	Action quotetext_action = new AbstractAction("Quote All") { {
			putValue(Action.SHORT_DESCRIPTION, "Quote All");
			putValue(Action.MNEMONIC_KEY,new Integer(KeyEvent.VK_Q));
		}
		
		// This method is called when the action is invoked
		public void actionPerformed(ActionEvent evt) {

				String txt=msg.getBody();
				
				String[] l=txt.split("\\n");
				
				StyledDocument sd=(StyledDocument)newmsg.getDocument();
				
				for(int i=0;i<l.length;i++) {
					try {
						newmsg.getDocument().insertString(newmsg.getCaretPosition(),">"+l[i]+"\n",null);
					} catch (BadLocationException e) { e.printStackTrace(); }
				}
		
		}
		
	};
	
	Action send_action = new AbstractAction("Send") { {
			putValue(Action.SHORT_DESCRIPTION, "Send");
			putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_S));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.CTRL_MASK));
		}
		
		// This method is called when the action is invoked
		public void actionPerformed(ActionEvent e) {
			
			if(msg!=null) {
				long now=System.currentTimeMillis();
				String text=newmsg.getText();
		
				Door door=Controller.getGallery().getDoorByBundleId(msg.getBundleId());
				CommentCommand cmd=door.createCommentCommand(msg.getBundleId(),msg.getKnotId(),text);
				
				try {
					Controller.getGallery().addCommand((Command)cmd);
				}
				catch (com.runstate.augur.gallery.GalleryException ge) { ge.printStackTrace(); }
				
				AugurPanelManger.getManager().closeView(ComposeComment.this,true);
			}
			
		}
		
	};
	
	
	Action original_action = new AbstractAction("Original") { {
			putValue(Action.SHORT_DESCRIPTION, "Original");
			putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_O));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O,KeyEvent.CTRL_MASK));
		}
		
		public void actionPerformed(ActionEvent e) {
			
			double d=commentsplit.getDividerLocation();
			
			if(d<20.0d) {
				commentsplit.setDividerLocation(0.5d);
			}
			else {
				commentsplit.setDividerLocation(0.0d);
			}
			
			newmsg.requestFocus();
		}
		
	};
	
	Action spelling_action = new AbstractAction("Spelling") { {
			putValue(Action.SHORT_DESCRIPTION, "Spelling");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E,KeyEvent.CTRL_MASK));
					putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_E));
		}
		
		// This method is called when the action is invoked
		public void actionPerformed(ActionEvent e) {
			SpellingChecker sp=SpellingChecker.getInstance();
			sp.checkSpelling(newmsg);
			send_action.setEnabled(true);
			newmsg.requestFocus();
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
			
			//if(closeRequest()) ComposeComment.this.close();
		}
	};

	public String getName() { return "Comment to "+Controller.getController().getGallery().getBundleManager().idToName(msg.getBundleId())+":"+msg.getKnotId(); }

	public String getTitle() { return "Comment to "+Controller.getController().getGallery().getBundleManager().idToName(msg.getBundleId())+":"+msg.getKnotId(); }
	
	public ComposeComment(Msg msg) {
		super();
		
		this.msg=msg;
		
		srcmsg=new MsgPane(this,true);
		srcmsg.setMsg(msg);
			
		newmsg=new JTextPane();
		newmsg.setEditable(true);
		newmsg.setBackground(Color.WHITE);
		newmsg.setForeground(Color.BLACK);
		newmsg.setText("");
		
		newmsg.setFont(Font.decode(profile.get(Prefs.UI_COMPOSE_FONT,"Monospaced-PLAIN-12")));
		int w=newmsg.getFontMetrics(newmsg.getFont()).stringWidth("0123456789")/10;
		int h=newmsg.getFontMetrics(newmsg.getFont()).getHeight();
		
		newmsgpane=new JScrollPane(newmsg);
		newmsgpane.setPreferredSize(new Dimension(w*80,h*20));
		newmsgpane.setMinimumSize(new Dimension(w*80,h*10));
		newmsgpane.setMaximumSize(new Dimension(w*80,h*20));
		
		newmsgpane=new JScrollPane(newmsg);
		newmsgpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		commentsplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,srcmsg,newmsgpane);
		commentsplit.setResizeWeight(0.5D);
		Dimension minimumSize = new Dimension(w*80, h*10);
		newmsgpane.setMinimumSize(minimumSize);
		srcmsg.setMinimumSize(minimumSize);
		
		commentsplit.setOneTouchExpandable(true);
		commentsplit.setDividerLocation(0.5D);
		
		setLayout(new BorderLayout());
		
		add(BorderLayout.CENTER,commentsplit);
		
		commenttools.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
		commenttools.add(new JButton(spelling_action));
		commenttools.add(new JButton(cancel_action));
		commenttools.add(new JButton(send_action));
		add(commenttools,BorderLayout.SOUTH);
		
		composemenu=new JMenu("Compose");
		composemenu.setMnemonic('C');
		composemenu.add(new JMenuItem(send_action));
		composemenu.add(new JSeparator());
		composemenu.add(new JMenuItem(quotetext_action));
		composemenu.add(new JMenuItem(original_action));
		composemenu.add(new JMenuItem(spelling_action));
		composemenu.add(new JSeparator());
		composemenu.add(new JMenuItem(cancel_action));

		editmenu=new JMenu("Edit");
		editmenu.setMnemonic('E');

		createActionTable(newmsg);

		Action cutAction=getActionByName(DefaultEditorKit.cutAction);
		JMenuItem cutMenu=new JMenuItem(cutAction);
		cutMenu.setText("Cut");
		popupmenu.add(cutMenu);
		editmenu.add(cutMenu);
		
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
		
		popupmenu.add(quotetext_action);
		
		newmsg.addMouseListener(new PopupTrigger());
		
		newmsg.setText("");
		
		newmsg.setRequestFocusEnabled(true);
		srcmsg.setRequestFocusEnabled(false);
				
		if(profile.getBool(Prefs.UI_SPELL_FORCE,false))
		{
			send_action.setEnabled(false);
		}
		
	}
	
	/**
	 * Method status
	 *
	 * @param    src                 a  JComponent
	 * @param    status              a  String
	 *
	 */
	public void status(JComponent src, String status) {
		System.out.println("Dropping msg "+status+" in comment window");
	}
	
	
	
//	public void setFrame(AFrameIF frame) {
//		super.setFrame(frame);
//		frame.setFocusTraversalPolicy(new CPFTP());
//	}
	
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
		
		int rv=JOptionPane.showOptionDialog(this,"Do you want to discard this comment?","Warning",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
		
		if(rv==0) return true;
		
		return false;
	}
	
	public void becomingVisible(boolean b) {
//		super.setVisible(b);
		
		if(b) {
			newmsg.requestFocusInWindow();
		}
//			javax.swing.SwingUtilities.invokeLater
//			(
//			 new Runnable() {
//					public void run() {
//						System.out.println("Focus Request");
//						newmsg.requestFocusInWindow();
//					}
//				}
//			);
//		}
	}
	
	

//	private class CPFTP extends FocusTraversalPolicy {
//
//		public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
//			return newmsg;
//		}
//
//		public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
//			return newmsg;
//		}
//
//
//		public Component getFirstComponent(Container focusCycleRoot) {
//			return newmsg;
//		}
//
//
//		public Component getLastComponent(Container focusCycleRoot) {
//			return newmsg;
//		}
//
//		public Component getDefaultComponent(Container focusCycleRoot) {
//			return newmsg;
//		}
//
//	}
	
	public ImageIcon getIcon() {
		return ImageCache.get("comment");
	}
	
}

