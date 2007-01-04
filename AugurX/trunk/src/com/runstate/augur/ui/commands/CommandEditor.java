/**
 * CommandEditor.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.commands;

import com.runstate.augur.gallery.commands.parameters.*;
import javax.swing.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.gallery.GalleryException;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.augur.gallery.commands.Parameter;
import com.runstate.augur.ui.augurpanel.AugurPanelManger;
import com.runstate.augur.ui.augurpanel.AugurPanel;
import com.runstate.util.swing.LabelledItemPanel;
import com.runstate.util.swing.SpellingChecker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.border.BevelBorder;

public class CommandEditor extends AugurPanel
{
	
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
	
	private Command command;
	private Parameter[] params;
	private JComponent[] comps;
	JTextPane msgpane;
	
	/**
	 * Method getPrefsName
	 *
	 * @return   a String
	 *
	 */
	public String getPrefsName()
	{
		return "commandeditor";
	}
	
	/**
	 * Method prefersTab
	 *
	 * @return   a boolean
	 *
	 */
	public boolean prefersTab()
	{
		return false;
	}
	
	/**
	 * Method getIcon
	 *
	 * @return   an ImageIcon
	 *
	 */
	public ImageIcon getIcon()
	{
		// TODO
		return null;
	}
	
	public Action save_action = new AbstractAction("Save") {
		{
			putValue(Action.SHORT_DESCRIPTION, "Save");
			putValue(Action.LONG_DESCRIPTION, "Save");
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
			
		}
		
		// This method is called when the action is invoked
		public void actionPerformed(ActionEvent evt)
		{
//			Parameter[] params=command.getParameters();
//
			for(int i=0;i<params.length;i++)
			{
				Parameter p=params[i];
				
				if(p instanceof IntParameter)
				{
					IntParameter ip=(IntParameter)p;
					JSpinner jsp=(JSpinner)comps[i];
					int iv=((Integer)jsp.getValue()).intValue();
					ip.setIntValue(iv);
				}
				else if(p instanceof BundleParameter)
				{
					BundleParameter pp=(BundleParameter)p;
					JTextField patf=(JTextField)comps[i];
					String pv=patf.getText();
					pp.setBundleName(Controller.getController().getGallery().getBundleManager().getBundle(pv).getBundlename());
				}
				else if(p instanceof StringParameter)
				{
					StringParameter sp=(StringParameter)p;
					JTextField strf=(JTextField)comps[i];
					String sv=strf.getText();
					sp.setValue(sv);
				}
				else if(p instanceof TextParameter)
				{
					TextParameter tp=(TextParameter)p;
					String tv=msgpane.getText();
					tp.setText(tv);
				}
				else if(p instanceof BooleanParameter)
				{
					BooleanParameter bp=(BooleanParameter)p;
					JCheckBox jcp=(JCheckBox)comps[i];
					boolean b=jcp.isSelected();
					bp.setBooleanValue(b);
				}
				else
				{
					System.out.println("unhandled parameter");
				}
			}
			
			if(command.setParameters(params))
			{
				try
				{
					Controller.getGallery().updateCommand(command);
				}
				catch (GalleryException e) { e.printStackTrace(); }
			}
			
			getContainer().closeView(true);
		}
	};
	
	public Action cancel_action = new AbstractAction("Cancel") {
		{
			putValue(Action.SHORT_DESCRIPTION, "Cancel");
			putValue(Action.LONG_DESCRIPTION, "Cancel");
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
		}
		
		// This method is called when the action is invoked
		public void actionPerformed(ActionEvent evt)
		{
			getContainer().closeView(false);
		}
	};
	
	Action spelling_action = new AbstractAction("Spelling") {
		{
			putValue(Action.SHORT_DESCRIPTION, "Spelling");
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E,KeyEvent.CTRL_MASK));
		}
		
		// This method is called when the action is invoked
		public void actionPerformed(ActionEvent e)
		{
			SpellingChecker sp=SpellingChecker.getInstance();
			sp.checkSpelling(msgpane);
			save_action.setEnabled(true);
		}
	};
	
	public String getName() { return "Command Editor"; }
	
	public CommandEditor(Command c)
	{
		super();
		//	super("Command Editor");
//		Controller prefs=Augur.getController();
		
		this.command=c;
		
		params=c.getParameters();
		comps=new JComponent[params.length];
		
		JPanel editpanel=new JPanel(new BorderLayout());
		
		JPanel buttons=new JPanel();
		
		LabelledItemPanel lit=new LabelledItemPanel();
		
		editpanel.add(BorderLayout.NORTH,lit);
		
		for(int i=0;i<params.length;i++)
		{
			Parameter p=params[i];
			
			if(p instanceof IntParameter)
			{
				IntParameter ip=(IntParameter)p;
				JSpinner intf=new JSpinner(new SpinnerNumberModel(ip.getIntValue(),ip.getMin(),ip.getMax(),ip.getStep()));
				intf.setValue(new Integer(ip.getIntValue()));
				comps[i]=intf;
				lit.addItem(ip.getLabel(),intf);
			}
			else if(p instanceof BundleParameter)
			{
				BundleParameter pp=(BundleParameter)p;
				JTextField patf=new JTextField(pp.getBundleName());
				comps[i]=patf;
				lit.addItem(pp.getLabel(),patf);
			}
			else if(p instanceof StringParameter)
			{
				StringParameter sp=(StringParameter)p;
				JTextField strf=new JTextField(sp.getValue());
				comps[i]=strf;
				lit.addItem(sp.getLabel(),strf);
			}
			else if(p instanceof BooleanParameter)
			{
				BooleanParameter bp=(BooleanParameter)p;
				JCheckBox jcp=new JCheckBox("",bp.getBooleanValue());
				comps[i]=jcp;
				lit.addItem(bp.getLabel(),jcp);
			}
			else if(p instanceof TextParameter)
			{
				TextParameter tp=(TextParameter)p;
				if(msgpane==null)
				{
					msgpane=new JTextPane();
//					msgpane.setFont(new Font("courier",Font.PLAIN,10));
					int f=msgpane.getFontMetrics(msgpane.getFont()).stringWidth("0123456789");
					int h=msgpane.getFontMetrics(msgpane.getFont()).getHeight();
					msgpane.setPreferredSize(new Dimension(f*(80/10),h*10));
					msgpane.setBackground(Color.WHITE);
					msgpane.setForeground(Color.BLACK);
					
					msgpane.setEditable(true);
					msgpane.setText(tp.getText());
					msgpane.setCaretPosition(0);
					
					editpanel.add(BorderLayout.CENTER,new JScrollPane(msgpane));
					
					buttons.add(new JButton(spelling_action));
					
					if(Controller.getProfile().getBool(Prefs.UI_SPELL_FORCE,false))
					{
						save_action.setEnabled(false);
					}
				}
				else
				{
					System.err.println("Tried to have a parameter with two text params");
				}
			}
				
			else
			{
				System.out.println("unhandled parameter");
			}
		}
		
		lit.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		JLabel description=new JLabel(c.toString(),SwingConstants.CENTER);
		
		
		buttons.add(new JButton(save_action));
		buttons.add(new JButton(cancel_action));
		
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH,description);
		add(BorderLayout.CENTER,editpanel);
		add(BorderLayout.SOUTH,buttons);
		
	}
	
	public String getLongtitle()
	{
		return command.toString();
	}
	
	public boolean requestClose(boolean force)
	{
		return true;
	}
}

