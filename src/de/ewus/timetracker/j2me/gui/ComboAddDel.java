/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ewus.timetracker.j2me.gui;

import com.sun.lwuit.*;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.SelectionListener;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.list.ListModel;
import com.sun.lwuit.table.TableLayout;
import com.sun.lwuit.util.Resources;
import de.ewus.timetracker.j2me.LocalizationSupport;
import de.ewus.timetracker.j2me.Midlet;
import java.util.Vector;

/**
 *
 * @author erikwegner
 */
public class ComboAddDel extends Container implements SelectionListener {
    
    ComboBox combobox;
    Button addbutton, delbutton;
    Midlet midlet;
    
    /** Command when selection changes */
    int command_selection_change;
    
    public ComboAddDel(String labeltext, int cmd_selchange, int cmd_add, int cmd_del, Resources res, Midlet actionlistener) {
        super();
        this.midlet = midlet;
        this.command_selection_change = cmd_selchange;
        
        TableLayout layout = new TableLayout(2, 3);
        this.setLayout(layout);
        
        TableLayout.Constraint constraint;
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        Label label = new Label(LocalizationSupport.getMessage(labeltext));
        this.addComponent(constraint, label);

        this.combobox = new ComboBox();
        this.addComponent(combobox);
        this.combobox.addSelectionListener(this);
        
        this.addbutton = new Button(new Command("", res.getImage("list-add.png"), cmd_add));
        this.addComponent(this.addbutton);
        this.addbutton.addActionListener(actionlistener);
        this.delbutton = new Button(new Command("", res.getImage("list-remove.png"), cmd_del));
        this.addComponent(this.delbutton);
        this.delbutton.addActionListener(actionlistener);
    }
    
    public void setElements(Vector elements) {
        ListModel model = new DefaultListModel(elements);
        this.combobox.setModel(model);
    }

    public void selectionChanged(int oldSelected, int newSelected) {
        ActionEvent e = new ActionEvent(this, command_selection_change);
        this.midlet.actionPerformed(e);
    }
    
}
