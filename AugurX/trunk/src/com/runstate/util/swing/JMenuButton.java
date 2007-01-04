/**
 * JMenuButton.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util.swing;

import com.runstate.util.ImageCache;
import java.awt.AWTEvent;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPopupMenu;

public class JMenuButton extends JButton {
	public JPopupMenu popup;
	

	public JMenuButton(JPopupMenu m) {
		//super("\u25BE"); //,ImageCache.get("cog"));
		super("\u25bc");
		setFont(getFont().deriveFont((float)(getFont().getSize()/2)));
		//setMargin(new Insets(2,2,2,2));
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
		
		popup = m;
		setIcon(ImageCache.get("cog"));
		
		enableEvents(AWTEvent.KEY_EVENT_MASK);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}
	
	public void setMenu(JPopupMenu m)
	{
		popup=m;
	}
	
	// This method handles the MenuButton mouse events.
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		int id = e.getID();
		if (id == MouseEvent.MOUSE_PRESSED) {
			if (popup != null) {
				if(!popup.isVisible())
				{
					popup.show( this, this.getWidth()/2, 10 );
				}
				else
				{
						popup.setVisible(false);
				}
			}
		}
	}
	
	// This method handles the MenuButton key events.
	protected void processKeyEvent(KeyEvent e) {
		int id = e.getID();
		if (id == KeyEvent.KEY_PRESSED || id == KeyEvent.KEY_TYPED) {
			int key=e.getKeyCode();
			if (key == KeyEvent.VK_DOWN) {
				if(popup!=null)
				{
					popup.show( this, this.getWidth()/2, 10 );
				}
			}
			super.processKeyEvent(e);
		}
	}
}

