/**
 * MsgPane.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.textpanes;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.gallery.events.CoreEvent;
import com.runstate.augur.gallery.events.MsgEvent;
import com.runstate.augur.gallery.listeners.CoreEventListener;
import com.runstate.augur.ui.textpanes.FindDialog;
import com.runstate.util.ImageCache;
import com.runstate.util.swing.ImageCreator;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.net.URL;
import java.text.SimpleDateFormat;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;

public class MsgPane extends JPanel implements HyperlinkListener,FindDialogListener,CoreEventListener
{
	HTMLPane header;
	HTMLPane htmlpane;
	JScrollPane htmlsp;
	JLabel status;
	JLabel stateLabel;

	Msg msg=null;
	Msg[] msgs=null;
	
	public static final int SOURCE=1;
	public static final int HTML=2;
	
	int mode=HTML;
	
	JPopupMenu linkmenu;
	JPopupMenu editmenu;
	
	Action select_all_action=new AbstractAction("Select All")
	{
		/**
		 * Invoked when an action occurs.
		 */
		public void actionPerformed(ActionEvent e)
		{
			htmlpane.selectAll();
		}
	};
	
	Action copy_link_to_clipboard_action=new AbstractAction("Copy Link to Clipboard")
	{
		/**
		 * Invoked when an action occurs.
		 */
		public void actionPerformed(ActionEvent e)
		{
			if(savedanchor==null) return;
			
			Clipboard cp=java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable t=new StringSelection(savedanchor);
			cp.setContents(t,null);
		}
	};
	
	FindDialog find;
	
	Action find_action=new AbstractAction("Find")
	{
		{
			putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_F));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F,KeyEvent.CTRL_MASK));
			
		}
		
		/**
		 * Invoked when an action occurs.
		 */
		public void actionPerformed(ActionEvent e)
		{
			if(find==null || !find.isVisible())
			{
				find=new FindDialog(MsgPane.this);
			}
			
			find.setVisible(true);
			find.toFront();
		}
	};
	

	private void setActions()
	{
		if(anchor==null)
		{
			status(null);
		}
		else
		{
			if(anchor.startsWith("http:") || anchor.startsWith("https:"))
			{
				status("Open "+anchor);
			}
			else if(anchor.startsWith("/"))
			{
				status("Go to "+anchor);
			}
			else if(anchor.startsWith("augur:resume("))
			{
				status("Show "+anchor.substring("augur:resume:".length(),anchor.length()-1)+" user information");
			}
			else
			{
				status("Do "+anchor);
			}
		}
	}
	
	String statusstring;
	
	private void status(String statusstring)
	{
		this.statusstring=statusstring;
		if(statusstring==null) status.setText(" ");
		else status.setText(statusstring);
	}
	
	public String getLastStatus()
	{
		return statusstring;
	}

	MsgPaneListener msgpanelistener;
	
	ImageIcon readwarmicon=ImageCache.get("envrdwarm");
	ImageIcon readcoldicon=ImageCache.get("envrdcold");
	ImageIcon readhoticon=ImageCache.get("envrdhot");
	ImageIcon readicon=ImageCache.get("envrdcool");
	ImageIcon unreadwarmicon=ImageCache.get("envurwarm");
	ImageIcon unreadhoticon=ImageCache.get("envurhot");
	ImageIcon unreadicon=ImageCache.get("envurcold");
	
	public MsgPane(MsgPaneListener msgpaneuser,boolean displayonly)
	{
		this.msgpanelistener=msgpaneuser;
		
		htmlpane=new HTMLPane();
		htmlpane.setBackground(Color.WHITE);
		htmlpane.setForeground(Color.BLACK);
		
		htmlpane.setContentType("text/html");
		
		htmlpane.addHyperlinkListener(this);
		
		htmlsp=new JScrollPane(htmlpane);
		
		JPanel headerpanel=new JPanel(new BorderLayout(0,0));
		stateLabel=new JLabel(ImageCache.get("auguricon"));
		stateLabel.setBorder(BorderFactory.createEmptyBorder());
		stateLabel.setBackground(Color.LIGHT_GRAY);
		stateLabel.setOpaque(true);
		headerpanel.add(BorderLayout.WEST,stateLabel);
		
		header=new HTMLPane();
		header.setContentType("text/html");
		header.setEditable(false);
		header.addHyperlinkListener(this);
		header.setBackground(Color.LIGHT_GRAY);
		header.setRequestFocusEnabled(false);
		headerpanel.setBorder(BorderFactory.createEmptyBorder());
		headerpanel.add(BorderLayout.CENTER,header);

		status=new JLabel();
		status.setMinimumSize(new Dimension(10,10));
		Font f=status.getFont();
		status.setFont(f.deriveFont(10.0f));
		
		
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH,headerpanel);
		add(BorderLayout.CENTER,htmlsp);
		add(BorderLayout.SOUTH,status);
		
		linkmenu=new JPopupMenu("Link");
		linkmenu.add(new JMenuItem(copy_link_to_clipboard_action));
		editmenu=new JPopupMenu("Edit");
		Action copyAction = new DefaultEditorKit.CopyAction();
		copyAction.putValue(Action.NAME,"Copy");
		editmenu.add(new JMenuItem(copyAction));
		editmenu.add(new JMenuItem(select_all_action));
		editmenu.add(new JMenuItem(find_action));
		
		htmlpane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F,KeyEvent.CTRL_MASK),"find");
		htmlpane.getActionMap().put("find",find_action);
		
		LinkPopupListener lpl=new LinkPopupListener();
		htmlpane.addMouseListener(lpl);
		header.addMouseListener(lpl);
		
		Controller.getGallery().addCoreEventListener(this);
	}
	

	public void focusGained(FocusEvent p1)
	{
		anchor=null;
		overlink=false;
	}
	
	/**
	 * Method focusLost
	 *
	 * @param    p1                  a  FocusEvent
	 *
	 */
	public void focusLost(FocusEvent p1)
	{
		anchor=null;
		overlink=false;
	}
	
	
	
	/**
	 * Method getMode
	 *
	 * @return   an int
	 */
	public int getMode()
	{
		return mode;
	}
	
	public void setMode(int mode)
	{
		this.mode=mode;
		displayMsg();
	}
	
	public String getSelectedText()
	{
		return htmlpane.getSelectedText();
	}
	
	public Msg getMsg()
	{
		return msg;
	}
	
	public void refresh()
	{
		displayMsg();
	}
	
	public void setMsg(Msg m)
	{
		msg=m;
		msgs=null;
		anchor=null;
		overlink=false;
		displayMsg();
	}
	
	public String getAuthor()
	{
		if(msg==null) return null;
		
		return msg.getAugurAddress();
	}
	
	public Long getBundleid()
	{
		if(msg==null) return null;
		
		return msg.getBundleId();
	}
	
	public Long getKnotid()
	{
		if(msg==null) return null;
		return msg.getKnotId();
	}
	
	public void setMsgs(Msg[] m)
	{
		msg=null;
		msgs=m;
		displayMsg();
	}
	
	private void displayMsg()
	{
		if(msg==null)
		{
			if(msgs!=null)
			{
//				unreadbutton.setEnabled(false);
//				hotbutton.setEnabled(false);
//				tagbutton.setEnabled(false);
//				ignorebutton.setEnabled(false);
				
				header.setHTML(msgs[0].getBundleName()+" multiple messages selected");
				StringBuffer sb=new StringBuffer();
				sb.append("<html><body><table width='100%' cellpadding='0'>");
				sb.append("<tr style='background-color:#CCCCCC'><td width='10%'>Id</td><td width='20%'>Author</td><td>Subject</td></tr>");
				int limit=msgs.length;
				if(limit>20)
				{
					limit=20;
				}
				for(int i=0;i<limit;i++)
				{
					sb.append("<tr><td>"+msgs[i].getKnotId()+"</td><td>"+msgs[i].getAuthor()+"</td><td>"+msgs[i].getSubject()+"</td></tr>");
				}
				if(limit<msgs.length)
				{
					sb.append("</table>And "+(msgs.length-limit)+" more messages<br></body></html>");
				}
				else
				{
					sb.append("</table></body></html>");
				}
				htmlpane.setHTML(sb.toString());
				htmlpane.setCaretPosition(0);
				return;
			}
			else
			{
				stateLabel.setIcon(ImageCache.get("auguricon"));
				htmlpane.setHTML("");
				header.setHTML("No Message Selected");
				return;
			}
		}
		
		setHeader(msg);
		setMsgText(msg);
		htmlpane.setCaretPosition(0);
		anchor=null;
	}
	
	private void setHeader(Msg m)
	{
		StringBuffer nm=new StringBuffer();
		
//		nm.append("<HTML>");
//		nm.append("<HEAD>");
//		nm.append(Augur.getController().getHTMLStyle());
//		nm.append("</HEAD>");
//		nm.append("<BODY>");
		nm.append(m.getBundleName()+":"+m.getKnotId());
		
		if(m.getCommentto()!=null)
		{
			nm.append(" re:"+m.getCommentto());
		}
		
		nm.append(" by "+m.getAuthor()+" "+sdf.format(m.getMsgDate()));
//
//
//		nm.append("</body></html>");
//
				
		if(m.isIgnore()) nm.append(" I");
		if(m.isDeleted()) nm.append(" D");
		if(m.isKeep()) nm.append(" K");
		
		header.setHTML("");
		
		if(m.isUnread())
		{
			
			if(m.isHot()) stateLabel.setIcon(unreadhoticon);
			else if(m.isWarm()) stateLabel.setIcon(unreadwarmicon);
			else stateLabel.setIcon(unreadicon);
		}
		else
		{
			if(m.isHot()) stateLabel.setIcon(readhoticon);
			else if(m.isWarm()) stateLabel.setIcon(readwarmicon);
			else if(m.isUnreadthread()) stateLabel.setIcon(readcoldicon);
			else stateLabel.setIcon(readicon);
		}
		
		header.setHTML(Controller.getController().wrapWithStyle(null,nm.toString()));
		
	}
	
	SimpleDateFormat sdf=new SimpleDateFormat("d/MMM/yy HH:mm");
	
	private void setMsgText(Msg m)
	{
		if(mode==SOURCE)
		{
			htmlpane.setPlain(m.getText());
		}
		else
		{
			htmlpane.setContentType("text/html");
			htmlpane.setHTML(m.getDisplayHTML());
		}
	}
	
	public boolean pagedown()
	{
		JScrollBar jsb=htmlsp.getVerticalScrollBar();
		BoundedRangeModel mdl=jsb.getModel();
		int curr=mdl.getValue();
		int max=mdl.getMaximum();
		int pageinc=mdl.getExtent();
		if(curr+pageinc>=max)
		{
			return false;
		}
		
		mdl.setValue(curr+pageinc);
		
		return true;
	}
	
	public void pageup()
	{
		JScrollBar jsb=htmlsp.getVerticalScrollBar();
		BoundedRangeModel mdl=jsb.getModel();
		int curr=mdl.getValue();
		int max=mdl.getMaximum();
		int pageinc=mdl.getExtent();
		if(curr-pageinc<=0)
		{
			mdl.setValue(0);
			return;
		}
		
		mdl.setValue(curr-pageinc);
		
		return;
	}
	
	
	boolean overlink=false;
	boolean popup=false;
	
	String anchor=null;
	String savedanchor=null;
	
	public void hyperlinkUpdate(HyperlinkEvent e)
	{
//		System.out.println("hlu "+e);
		if(e.getEventType()==HyperlinkEvent.EventType.ACTIVATED)
		{
			System.out.println("activated "+popup);
			URL url=e.getURL();
			if(url==null)
			{
				// Not valid but....
				anchor=findHrefText(e);
				
			}
			else
			{
				anchor=url.toString();
			}
			
			if(!popup) msgpanelistener.URLActivated(anchor);
//			vch.doCommand(new VCExecuteCommand(anchor));
		}
		else
			if(e.getEventType()==HyperlinkEvent.EventType.ENTERED)
			{
				URL url=e.getURL();
				if(url==null)
				{
					// Not valid but....
					anchor=findHrefText(e);
					
				}
				else
				{
					anchor=url.toString();
				}
				
				overlink=true;
				setActions();
			}
			else
			{
				anchor=null;
				overlink=false;
				linkmenu.setVisible(false);
				setActions();
			}
	}
	
	private String findHrefText(HyperlinkEvent e)
	{
		HTML.Tag t=javax.swing.text.html.HTML.getTag("a");
		HTML.Attribute ta=javax.swing.text.html.HTML.getAttributeKey("href");
		
		Element se=e.getSourceElement();
		AttributeSet as=se.getAttributes();
		AttributeSet aas=(AttributeSet)se.getAttributes().getAttribute(t);
		return (String)aas.getAttribute(ta);
	}
	
	
	class LinkPopupListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			maybeShowPopup(e);
		}
		
		public void mouseReleased(MouseEvent e)
		{
//			maybeShowPopup(e);
		}
		
		private void maybeShowPopup(MouseEvent e)
		{
			if(e.getSource()==header)
			{
				popup=false;
				return;
			}
			
			if (e.isPopupTrigger())
			{
				if(overlink)
				{
					linkmenu.show(e.getComponent(),e.getX(),e.getY());
					savedanchor=anchor;
					popup=true;
				}
				else
				{
					editmenu.show(e.getComponent(),e.getX(),e.getY());
					popup=true;
				}
			}
			else
			{
				popup=false;
			}
		}
		
		public void mouseEntered(MouseEvent e)
		{
			MsgPane.this.anchor=null;
			MsgPane.this.overlink=false;
			setActions();
		}
		
		public void mouseExited(MouseEvent e)
		{
			MsgPane.this.anchor=null;
			MsgPane.this.overlink=false;
			setActions();
		}
	}
	
	public Component getComponentForCentre()
	{
		return this;
	}
	
	public String getPlainText()
	{
		if(msg==null)return null;
		
		if(mode==HTML) return msg.getBody();
		else return msg.getText();
		
	}
	
	
	public void select(int i, int length)
	{
		if(mode==HTML)
		{
			htmlpane.setSelectionStart(i+1);
			htmlpane.setSelectionEnd(length+i+1);
		}
		else
		{
			htmlpane.setSelectionStart(i);
			htmlpane.setSelectionEnd(length+i);
		}
	}
	
	public void noselect()
	{
		htmlpane.setSelectionStart(-1);
		htmlpane.setSelectionEnd(-1);
	}
	
	public void coreEventOccurred(CoreEvent ce)
	{
		if(msg==null) return; // We have no message, nothing to do
		
	//	System.out.println("ce "+ce);
		if(ce instanceof MsgEvent)
		{
			MsgEvent me=(MsgEvent)ce;
			
			if(me.getBundleid().equals(msg.getBundleId()) && me.getKnotid().equals(msg.getKnotId()))
			{
				msg=Controller.getController().getGallery().getBundleManager().getBundle(me.getBundleid()).getMsg(me.getKnotid());
				
				displayMsg();
			}
		}
	}
	
	
	
}

