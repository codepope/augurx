/**
 * ConsoleFrame.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui;
import com.runstate.augur.controller.Profile;
import com.runstate.augur.ui.augurpanel.AugurPanel;
import java.io.*;

import bsh.ConsoleInterface;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import bsh.util.NameCompletionTable;
import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import com.runstate.util.ImageCache;
import com.runstate.util.ResourceLoader;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JMenu;

public class BeanShellConsoleAugurPanel extends AugurPanel
{
	
	static BeanShellConsoleAugurPanel sysIOOwner=null;
	
	ConsoleInterface console;
	Interpreter interpreter;
	InputStream sysIn;
	PrintStream sysOut;
	PrintStream sysErr;
	
	public String getPrefsName()
	{
		return "beanshellconsole";
	}

	@Override
        public String getName() { return "BeanShell"; }
	
        public String getTitle()
        {
            return "BeanShell";
        }
        
	public BeanShellConsoleAugurPanel() {
		super();
		
		console = new JConsole();
		interpreter=new Interpreter(console);
		
		Profile profile=Controller.getProfile();
		
		File f=new File(profile.get(Prefs.HOMEDIR)+"scripts"+File.separatorChar+"augur.bsh");

		try {
			if(f.exists()) {
				interpreter.source(f.getAbsolutePath());
			}
			else {
				String execstr=ResourceLoader.getText("scripts/augur.bsh");
				
				interpreter.eval(execstr);
			}
		} catch (IOException e) {} catch (EvalError e) {}
		
		
		NameCompletionTable nct=new NameCompletionTable();
		nct.add(interpreter.getNameSpace());
		((JConsole)console).setNameCompletion(nct);
		
		setLayout(new BorderLayout());
		
		add((Component)console,BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(640,480));
		new Thread(interpreter).start();
	}
	
//	public void captureSysIO() {
//		if(sysIOOwner==null) {
//			sysIn=System.in;
//			sysOut=System.out;
//			sysErr=System.err;
//			System.setIn(((JConsole)console).getInputStream());
//			System.setOut(console.getOut());
//			System.setErr(console.getErr());
//			sysIOOwner=this;
//		}
//	}
//
//	public void returnSysIO() {
//		if(sysIOOwner==this) {
//			System.setIn(sysIn);
//			System.setOut(sysOut);
//			System.setErr(sysErr);
//			sysIOOwner=null;
//		}
//	}
		
	public ImageIcon getIcon() {
		return ImageCache.get("console");
	}
	
	
	public boolean requestClose(boolean force)
	{
		// TODO
		return true;
	}

}

