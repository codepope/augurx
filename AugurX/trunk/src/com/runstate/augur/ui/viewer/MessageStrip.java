/**
 * ControlStatusStrip.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer;

import com.runstate.util.swing.JMenuButton;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class MessageStrip extends JPanel
{
	JLabel status;
	
	public MessageStrip()
	{
		super();
		setLayout(new FlowLayout(FlowLayout.LEFT,2,1));
		status=new JLabel();
		status.setFont(status.getFont().deriveFont(11.0f));
		add(status);
	}
	
	public void setMsg(String msg)
	{
		status.setText(msg);
	}
	
}

