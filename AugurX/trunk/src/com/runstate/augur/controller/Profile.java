/*
 * Profile.java
 *
 * Created on October 6, 2006, 2:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.runstate.augur.controller;

import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.Preferences;

/**
 *
 * @author dj
 */
public class Profile {
    
    static final String PROPHEAD="augurx.";
    
    Preferences prefs;
    String profileName;
    
    public Profile(String profileName) {
        Preferences p=Preferences.userNodeForPackage(this.getClass());
        prefs=p.node(profileName);
    }

    String getProfileName() {
        return null;
    }
    
    	public String get(String key)
	{
		return get(key,null);
	}
	
	public String get(String key,String def)
	{
		if(System.getProperty(PROPHEAD+key)!=null)
		{
			// Override in place
			return System.getProperty(PROPHEAD+key);
		}
		
		return prefs.get(key,def);
	}
	
	public void set(String key,String val)
	{
		if(System.getProperty(PROPHEAD+key)!=null)
		{
			System.err.println("Preference change to "+key+" discarded as currently overridden");
			return;
		}
		String oldval=prefs.get(key,val);
		prefs.put(key,val);
		firePrefChanged(key,val,oldval);
	}
	
	public void setInt(String key,int val)
	{
		String sval=Integer.toString(val);
		set(key,sval);
	}
	
	public void setDouble(String key,double val)
	{
		String sval=Double.toString(val);
		set(key,sval);
	}
	
	public void setBool(String key,boolean val)
	{
		String sval=Boolean.toString(val);
		set(key,sval);
	}
	
	public int getInt(String key,int def)
	{
		String s=get(key);
		if(s==null) return def;
		return Integer.parseInt(s);
	}
	
	public double getDouble(String key,double def)
	{
		String s=get(key);
		if(s==null) return def;
		return Double.parseDouble(s);
	}
	
	public boolean getBool(String key,boolean def)
	{
		String s=get(key);
		if(s==null) return def;
		return new Boolean(s).booleanValue();
	}
	
	
	public void override(String key,String val)
	{
		System.setProperty(PROPHEAD+key,val);
	}
	
//	public boolean setDefault(String key,String val)
//	{
//		String s=prefs.get(key,null);
//
//		if(s==null)
//		{
//			prefs.put(key,val);
//			return true;
//		}
//
//		return false;
//	}
	
	Vector<ProfileListener> plisteners=new Vector<ProfileListener>();
	
	public void addPrefsListener(ProfileListener pl)
	{
		plisteners.add(pl);
	}
	
	private void firePrefChanged(String key,String val,String oldval)
	{
		Iterator<ProfileListener> i=plisteners.iterator();
		while(i.hasNext())
		{
			ProfileListener pi=i.next();
			pi.prefChanged(key,val,oldval);
		}
	}
}
