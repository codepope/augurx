/**
 * BundleManager.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery;

import java.util.*;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.events.CoreEvent;

public class BundleManager {
	TreeMap<Long, Bundle> bundlemapbyid=new TreeMap<Long, Bundle>();
	HashMap<String, Bundle> bundlemapbyname=new HashMap<String, Bundle>();
	
	Long root=new Long(0L);
	
	long bundleidgen=10000;
	
	Gallery gallery;
	
	public BundleManager(Gallery gallery) {
		this.gallery=gallery;
		getBundle(root);
	}
	
	/**
	 * Method createBundle
	 *
	 * @param    textpath            a  String
	 * @param    p1                  a  boolean
	 *
	 * @return   a  Bundle
	 */
	public Bundle createBundle(String textpath,Long doorid, boolean p1) {
		Bundle b=getBundle(textpath);
		
		if(b!=null) {
			System.out.println("Trying to create existing bundle "+textpath);
			return b;
		}
		
		
		
		Bundle bundle=new Bundle(doorid,null,textpath,0,0,0,p1,null);
		
		if(bundle.getParentid()==null) {
			if(bundle.getParentPath()!=null) {
				Bundle parentbundle=getBundle(bundle.getParentPath());
				if(parentbundle==null) {
					parentbundle=createBundle(bundle.getParentPath(),doorid,true);
				}
				
				parentbundle.addChild(bundle);
				
				bundle.setParentid(parentbundle.getBundleid());
			}
			else {
				bundle.setParentid(root);
				getBundle(root).addChild(bundle);
			}

			bundle.setBundleid(new Long(bundleidgen++));
			
			Controller.getGallery().writeBundle(bundle);
			
		}
		
		bundle.setBundlemanager(this);
		
		putMap(bundle);
		
		newBundleEvent(bundle);
		
		return bundle;
	}
	
	public Long getRootBundleId()
	{
		return root;
	}
	
	/**
	 * Method getBundles
	 *
	 * @return   an ArrayList
	 */
	public ArrayList<Bundle> getBundles() {
		return new ArrayList<Bundle>(bundlemapbyid.values());
	}
	
	/**
	 * Method getBundleExists
	 *
	 * @param    path                a  String
	 *
	 * @return   a  boolean
	 */
	public boolean getBundleExists(String path) {
		Bundle b=getBundle(path);
		
		return b!=null;
	}
	
	public void addBundle(Bundle bundle) {
		if(bundleidgen<=bundle.getBundleid().longValue()) {
			bundleidgen=bundle.getBundleid().longValue()+1;
		}
		
		bundle.setBundlemanager(this);
		
		if(bundle.getParentid()!=null) {
			Bundle parent=getBundle(bundle.getParentid());
			parent.addChild(bundle);
		}
		
		putMap(bundle);
	}
	
	public void updateBundle(Bundle bundle) {
		Long parentid=bundle.getParentid();
		
		if(parentid!=null) {
			Bundle parent=getBundle(parentid);
			parent.recalculate();
		} else
		{
			Bundle parent=getBundle(root);
			parent.recalculate();
		}
		
		if(!bundle.isContainer()) {
			Controller.getGallery().updateBundle(bundle);
		}
		
		updatedBundleEvent(bundle);
	}
	
	Door getDoor(Long doorid) {
		return Controller.getGallery().getDoorByDoorId(doorid);
	}
	
	private void putMap(Bundle bundle) {
//		bundlemapbyref.put(bundle.getBundleRef(),bundle);
		bundlemapbyid.put(bundle.getBundleid(),bundle);
		bundlemapbyname.put(bundle.getBundlename(),bundle);
	}
	
	private void removeMap(Bundle bundle) {
		if(bundle==null) {
			System.out.println("asked to remove null");
		}
		
//		bundlemapbyref.remove(bundle.getBundleRef());
		bundlemapbyid.remove(bundle.getBundleid());
		bundlemapbyname.remove(bundle.getBundlename());
	}
	
	public Bundle getBundle(Long bundleid) {
		return bundlemapbyid.get(bundleid);
	}
	
	public Bundle getBundle(String name) {
		Bundle bundle=bundlemapbyname.get(name);
		return bundle;
	}
	
	public Long nameToId(String name) {
		Bundle bundle=getBundle(name);
		if(bundle==null) return null;
		return bundle.getBundleid();
	}
	
	public String idToName(Long id) {
		Bundle bundle=getBundle(id);
		if(bundle==null) return null;
		return bundle.getBundlename();
	}
	
	Vector<BundleManagerListener> bundleManagerListeners=new Vector<BundleManagerListener>();
	
	public void addBundleManagerListener(BundleManagerListener bml) {
		synchronized(bundleManagerListeners) {
			bundleManagerListeners.add(bml);
		}
	}
	
	public void removeBundleManagerListener(BundleManagerListener bml) {
		synchronized(bundleManagerListeners) {
			bundleManagerListeners.remove(bml);
		}
	}
	
	public void fireCoreEvent(CoreEvent ce) // Everyone wants a bit of me
	{
		synchronized(bundleManagerListeners) {
			
			for(Iterator<BundleManagerListener> i=bundleManagerListeners.iterator();i.hasNext();) {
				BundleManagerListener bml=i.next();
				try {
					//mel.coreEventOccurred(ce);
				}
				catch (Exception e) { System.out.println("Error caught in fireCore Event"); e.printStackTrace(); }
			}
			
		}
	}
	
	public void newBundleEvent(Bundle bundle) // Everyone wants a bit of me
	{
		synchronized(bundleManagerListeners) {
			
			for(Iterator<BundleManagerListener> i=bundleManagerListeners.iterator();i.hasNext();) {
				BundleManagerListener bml=i.next();
				try {
					bml.newBundleEvent(bundle);
				}
				catch (Exception e) { System.out.println("Error caught in fireCore Event"); e.printStackTrace(); }
			}
			
		}
	}
	
	public void updatedBundleEvent(Bundle bundle) // Everyone wants a bit of me
	{
		synchronized(bundleManagerListeners) {
			
			for(Iterator<BundleManagerListener> i=bundleManagerListeners.iterator();i.hasNext();) {
				BundleManagerListener bml=i.next();
				try {
					bml.updateBundleEvent(bundle);
				}
				catch (Exception e) { System.out.println("Error caught in fireCore Event"); e.printStackTrace(); }
			}
			
		}
	}
}

