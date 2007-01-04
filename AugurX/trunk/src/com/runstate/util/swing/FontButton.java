/**
 * FontChooserButton.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util.swing;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import com.runstate.augur.ui.preferences.panels.ManagedPrefPanel;

public class FontButton extends JButton
{
	Font font;
	String fontname;
	FontChooser chooser=null;
	
	FontButtonListener fbl=null;
	
	public FontButton()
	{
		super();
		
		addActionListener(new ActionListener()
						  {
					public void actionPerformed(ActionEvent e)
					{
						// Pop up the dialog
						if(chooser==null)
						{
							chooser=new FontChooser(null);
							chooser.setLocationRelativeTo(FontButton.this);
						}
						if(font==null)
						{
							font=Font.decode(getFontname());
							System.out.println(font);
						}
						
						chooser.setSelectedFont(font);
						
						chooser.setVisible(true);
						// Get the user's selection
						Font font = chooser.getSelectedFont();
						// If not cancelled, set the button font
						if (font != null)
						{
							String style=null;
							
							switch(font.getStyle())
							{
								case Font.PLAIN:
									style="PLAIN";
									break;
								case Font.BOLD:
									style="BOLD";
									break;
								case Font.ITALIC:
									style="ITALIC";
									break;
							}
							if(style==null)
							{
								style="PLAIN";
							}
							
							String fontname=font.getName()+"-"+style+"-"+font.getSize();
							
							FontButton.this.fontname=fontname;
							FontButton.this.setText(FontButton.this.fontname);
							
							fireFontButtonChanged();
						}
					}
				});
	}
	
	/**
	 * Method addFontButtonListener
	 *
	 * @param    managedPrefPanel    a  ManagedPrefPanel
	 *
	 */
	public void addFontButtonListener(FontButtonListener fbl)
	{
		this.fbl=fbl;
	}
	
	private void fireFontButtonChanged()
	{
		if(fbl==null)
		{
			System.out.println("No fbl");
			return;
		}
		
		fbl.fontButtonChanged(new FontButtonEvent(this,fontname));
		
	}
	
	public String getFontname()
	{
		return fontname;
	}
	
	public void setFontname(String fontname)
	{
		this.fontname=fontname;
		font=Font.decode(fontname);
		setText(fontname);
	}
	
}

