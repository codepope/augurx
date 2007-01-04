/**
 * PrefPanelHolder.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.preferences;

import com.runstate.augur.ui.preferences.panels.PrefPanel;
import java.awt.CardLayout;
import javax.swing.JPanel;

public class PrefPanelHolder extends JPanel
{
	CardLayout cl;
	PrefPanel showing=null;
	
	public PrefPanelHolder()
	{
		cl=new CardLayout();
		
		setLayout(cl);
		add("blank",new PrefPanel());
		cl.show(this,"blank");
	}
	
	public void setPrefPanel(PrefPanel p)
	{
		if(p==null)
		{
			cl.show(this,"blank");
			if(showing!=null)
			{
			remove(showing);
			showing=null;
			}
		}
		else
		{
			add("content",p);
			cl.show(this,"content");
			showing=p;
		}
	}
}

