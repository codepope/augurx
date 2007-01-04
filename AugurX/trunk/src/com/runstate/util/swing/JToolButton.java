/**
 * JToolButton.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util.swing;

import javax.swing.*;

import java.awt.Insets;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;

public class JToolButton extends JButton {
	
	public JToolButton(Action a) {
		super(a);
		Insets inset=getInsets();
		
		if(inset.top<inset.left)
		{
			inset.left=inset.top;
		}
		if(inset.bottom<inset.right)
		{
			inset.right=inset.bottom;
		}
		
		setMargin(inset);

//		setMargin(new Insets(2,2,2,2));
		setBorderPainted(true);
		setBorder(BorderFactory.createEtchedBorder());
		
		ImageIcon i=((ImageIcon)a.getValue(Action.SMALL_ICON));
		setSelectedIcon(i);
		setPressedIcon(new ImageIcon(createImage(new FilteredImageSource(i.getImage().getSource(),new Dimmer()))));
		
	}
	
	public void setText(String s) {
		super.setText(null);
	}
	
	
	
	class Dimmer extends RGBImageFilter {
		
		// Constructor.
		Dimmer() {
			super();
			canFilterIndexColorModel = true;
		}
		
		// Adjusts color of pixel or palette entry.
		public int filterRGB(int x, int y, int rgb) {
			return rgb & 0x5fffffff;
		}
		
	}
	
}

