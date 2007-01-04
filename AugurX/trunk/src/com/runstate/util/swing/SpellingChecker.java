/**
 * SpellingChecker.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util.swing;

import com.runstate.augur.controller.Profile;
import java.io.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import com.runstate.util.ResourceLoader;
import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.swing.JTextComponentSpellChecker;
import javax.swing.text.JTextComponent;

public class SpellingChecker {
	private static SpellingChecker mycheck=null;
	
	JTextComponentSpellChecker jtpspchk=null;
	
	public static SpellingChecker getInstance() {
		if(mycheck==null) {
			mycheck=new SpellingChecker();
		}
		
		return mycheck;
	}
	
	private SpellingChecker() {
		try {
			Profile profile=Controller.getProfile();
			
			String dict=profile.get(Prefs.UI_SPELL_FILE,"dict/UK.dic");
			
			File f=new File(profile.get(Prefs.HOMEDIR)+dict);
			
			InputStream is=null;
			
			if(f.exists())
			{
				try {
					is=new FileInputStream(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			else
			{
				is=ResourceLoader.getInputStream("com/runstate/augur/support/"+dict);
			}
			
			InputStreamReader isr=new InputStreamReader(is);
			
			SpellDictionary sd=new SpellDictionaryHashMap(isr);
			jtpspchk=new JTextComponentSpellChecker(sd);
			
			String filename= Controller.getProfile().get(Prefs.HOMEDIR)+"augurdictionary";
			
			File userDictionaryFile = new File(filename);
			if (!userDictionaryFile.exists())
				userDictionaryFile.createNewFile();
			
			// set it
			SpellDictionary userDictionary = new SpellDictionaryHashMap(userDictionaryFile);
			
			jtpspchk.setUserDictionary(userDictionary);
			
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void checkSpelling(JTextComponent jtc) {
		jtpspchk.spellCheck(jtc);
	}
}

