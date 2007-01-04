/**
 * CommandCellEditor.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.commands;
import javax.swing.*;

import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.augur.ui.augurpanel.AugurPanelManger;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.TableCellEditor;

public class CommandCellEditor extends AbstractCellEditor
	implements TableCellEditor,
	ActionListener {
    Command currentCommand;
    JButton button;
	
	Frame parent;

    protected static final String EDIT = "edit";
	
    public CommandCellEditor(Frame parent) {
		this.parent=parent;
		button = new JButton();
		button.setActionCommand(EDIT);
		button.addActionListener(this);
		button.setBorderPainted(false);
    }
	
    public void actionPerformed(ActionEvent e) {
		if (EDIT.equals(e.getActionCommand())) {
			if(currentCommand.isEditable()) {
				CommandEditor ce=new CommandEditor(currentCommand);
				AugurPanelManger.getManager().addToDesktop(ce);
				fireEditingStopped();
				//ce.toFront();
			}
			else {
				JOptionPane.showMessageDialog(parent,"Sorry, this command isn't editable");
			}
			fireEditingStopped(); //Make the renderer reappear.
			
		} else { //User pressed dialog's "OK" button.
			//            currentCommand = colorChooser.getColor();
		}
    }
	
    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
		return currentCommand;
    }
	
    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
												 Object value,
												 boolean isSelected,
												 int row,
												 int column) {
		currentCommand = (Command)value;
		button.setText(currentCommand.toString());
		button.setEnabled(currentCommand.isEditable());
		return button;
    }
}
