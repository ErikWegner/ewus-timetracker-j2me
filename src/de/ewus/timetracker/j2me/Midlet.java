/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ewus.timetracker.j2me;

import javax.microedition.midlet.*;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.layouts.GridLayout;

/**
 * @author Erik.Wegner
 */
public class Midlet extends MIDlet implements ActionListener {

    Form mainform;
    
    Command exitCommand;
    Label main_customer, main_project, main_task;
    Button main_startstop;
    
    private void createFormMain() {
        main_customer = new Label("Customer");
        main_project = new Label("Project");
        main_task = new Label("Task");
        
        main_startstop = new Button(new Command("Start/Stop"));
        
        mainform = new Form("EWUS TimeTracker");
        
        mainform.setLayout(new GridLayout(5, 1));
        mainform.addComponent(main_customer);
        mainform.addComponent(main_project);
        mainform.addComponent(main_task);
        mainform.addComponent(main_startstop);
        
        exitCommand = new Command("Exit");
        mainform.addCommand(exitCommand);

        mainform.addCommandListener(this);
        mainform.show();
    }
    
    public void startApp() {
        Display.init(this);
        
        createFormMain();
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }
    
    public void actionPerformed(ActionEvent ae) {
        Command c = ae.getCommand();
        if (c.getCommandName().equals("Exit")) {
            notifyDestroyed();
        }
    }
}
