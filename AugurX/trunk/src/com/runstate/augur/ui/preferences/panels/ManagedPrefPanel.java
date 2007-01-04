/**
 * UIHTMLStyle.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.preferences.panels;

import com.runstate.augur.ui.preferences.PreferencesUI;
import com.runstate.util.swing.FontButton;
import com.runstate.util.swing.FontButtonEvent;
import com.runstate.util.swing.FontButtonListener;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class ManagedPrefPanel extends PrefPanel implements DocumentListener,ChangeListener,FontButtonListener {
	
	ArrayList<ManagedPrefPanel.PrefItem> items=new ArrayList<ManagedPrefPanel.PrefItem>();
	
	public ManagedPrefPanel() {
		super();
		setLayout(new BorderLayout());
//		LabelledItemPanel lip=new LabelledItemPanel();
//		lip.addItem("Cix User Name:",username);
//		lip.addItem("Password:",password);
//		add(BorderLayout.CENTER,lip);
//		username.getDocument().addDocumentListener(this);
//		password.getDocument().addDocumentListener(this);
	}
	
	public void registerPrefItem(JComponent jc,String prefname,String defval) {
		PrefItem pi=new PrefItem(jc,prefname,defval);
		items.add(pi);
	}
	
	
	class PrefItem {
		JComponent comp;
		String prefname;
		String defval;
		String value;
		
		PrefItem(JComponent comp,String prefname,String defval) {
			this.comp=comp;
			this.prefname=prefname;
			this.defval=defval;
			setToStoredValue();
			listenToField();
			
		}
		
		void setToStoredValue() {
                    if(profile.get(prefname)==null)
                    {
                        profile.set(prefname,defval);
                        this.value=defval;
                    }
                    else
                    {
			this.value=profile.get(prefname);
                    }
		}
		
		void setField() {
			if(comp instanceof JTextComponent) {
				((JTextComponent)comp).setText(value);
			}
			else if(comp instanceof JCheckBox) {
				((JCheckBox)comp).setSelected(new Boolean(value).booleanValue());
			}
			else if(comp instanceof FontButton)
			{
				((FontButton)comp).setFontname(value);
			}
		}
		
		void listenToField() {
			if(comp instanceof JTextComponent) {
				((JTextComponent)comp).getDocument().addDocumentListener(ManagedPrefPanel.this);
			}
			else if(comp instanceof JCheckBox) {
				((JCheckBox)comp).addChangeListener(ManagedPrefPanel.this);
			}
			else if(comp instanceof FontButton) {
				((FontButton)comp).addFontButtonListener(ManagedPrefPanel.this);
			}
			else {
				System.out.println("unhandled "+comp);
			}
		}
		
		boolean hasChanged() {
                    return !this.value.equals(profile.get(prefname));
		}
		
		void saveChanged() {
			if(hasChanged()) {
				profile.set(prefname,value);
			}
		}
	}
	
	public void setPrefsEditor(PreferencesUI prefseditor) {
		super.setPrefsEditor(prefseditor);
		getValues();
		setFields();
		fireNoChange();
	}
	
	void getValues() {
		Iterator<ManagedPrefPanel.PrefItem> i=items.iterator();
		
		while(i.hasNext()) {
			PrefItem pi=i.next();
			pi.setToStoredValue();
		}
	}
	
	void setFields() {
		Iterator<ManagedPrefPanel.PrefItem> i=items.iterator();
		
		while(i.hasNext()) {
			PrefItem pi=i.next();
			pi.setField();
		}
	}
	
	public boolean hasChanged() {
		Iterator<ManagedPrefPanel.PrefItem> i=items.iterator();
		
		while(i.hasNext()) {
			PrefItem pi=i.next();
			if(pi.hasChanged()) {
                            System.out.println("Pref changed "+pi.prefname);
                            return true;
                        }
		}
		
		return false;
	}
	
	public boolean saveChanged() {
		Iterator<ManagedPrefPanel.PrefItem> i=items.iterator();
		
		while(i.hasNext()) {
			PrefItem pi=i.next();
			pi.saveChanged();
		}
		
		boolean saveresult=postSaveAction();
		if(saveresult)
		{
			fireNoChange();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean postSaveAction() {
		return true;
	}
	
	
	public void discardChanges() {
		getValues();
		setFields();
		fireNoChange();
	}
	
	
	public void stateChanged(ChangeEvent e) {
		Iterator<ManagedPrefPanel.PrefItem> i=items.iterator();
		
		while(i.hasNext()) {
			PrefItem pi=i.next();
			
			if(pi.comp instanceof JCheckBox) {
				JCheckBox jc=(JCheckBox)pi.comp;
				if(e.getSource()==jc) {
					pi.value=jc.isSelected()?"true":"false";
					fireHasChanged();
				}
			}
		}
	}
	
	
	private void docChange(DocumentEvent e) {
		Document d=e.getDocument();
		
		Iterator<ManagedPrefPanel.PrefItem> i=items.iterator();
		
		while(i.hasNext()) {
			PrefItem pi=i.next();
			
			if(pi.comp instanceof JTextComponent) {
				JTextComponent ta=(JTextComponent)pi.comp;
				if(ta.getDocument()==d) {
					pi.value=ta.getText();
					fireHasChanged();
					return;
				}
			}
		}
	}
	
	public void insertUpdate(DocumentEvent e) {
		docChange(e);
	}
	public void removeUpdate(DocumentEvent e) {
		docChange(e);
	}
	
	public void changedUpdate(DocumentEvent e) {
		docChange(e);
	}
	

	public void fontButtonChanged(FontButtonEvent fbe)
	{
		FontButton fb=fbe.getSource();
		
		Iterator<ManagedPrefPanel.PrefItem> i=items.iterator();
		
		while(i.hasNext()) {
			PrefItem pi=i.next();
			
			if(pi.comp instanceof FontButton) {
				FontButton ta=(FontButton)pi.comp;
				if(ta==fb) {
					pi.value=ta.getFontname();
					fireHasChanged();
					return;
				}
			}
		}
	}
	
	
	
}

