/**
 * SplashScreen.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util.swing;

import java.awt.*;
import javax.swing.*;

import com.runstate.util.ResourceLoader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
	
public class SplashScreen extends JWindow
{
    public SplashScreen(JFrame parent,String filename, Frame f, int waitTime)
    {
        super(f);
        JLabel l = new JLabel(ResourceLoader.getIcon(filename));
        getContentPane().add(l, BorderLayout.CENTER);
        pack();
        Dimension screenSize;
		
		if(parent==null)
		{
          screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		}
		else
		{
			screenSize=parent.getPreferredSize();
		}
        Dimension labelSize = l.getPreferredSize();
        setLocation(screenSize.width/2 - (labelSize.width/2),
                    screenSize.height/2 - (labelSize.height/2));
        addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                    setVisible(false);
                    dispose();
                }
            });
        final int pause = waitTime;
        final Runnable closerRunner = new Runnable()
            {
                public void run()
                {
                    setVisible(false);
                    dispose();
                }
            };
        Runnable waitRunner = new Runnable()
            {
                public void run()
                {
                    try
                        {
                            Thread.sleep(pause);
                            SwingUtilities.invokeAndWait(closerRunner);
                        }
                    catch(Exception e)
                        {
                            e.printStackTrace();
                            // can catch InvocationTargetException
                            // can catch InterruptedException
                        }
                }
            };
        setVisible(true);
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        splashThread.start();
    }
}



