/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ewus.timetracker.j2me;

import javax.microedition.midlet.*;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.plaf.Style;

/**
 * @author Erik Wegner
 */
public class Midlet extends MIDlet implements ActionListener {

    Form mainform;
    
    Command exitCommand;
    Label main_customer, main_project, main_task, main_status;
    Button main_startstop;
    
    Control controller;
    
    private void setStartStopButtonStyle() {
        Style s = main_startstop.getUnselectedStyle();
        s.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_VERTICAL);
        s.setBackgroundGradientStartColor(0xcccccc);
        s.setBackgroundGradientEndColor(0x000000);
        s.setFont(Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE));
        s.setFgColor(0xffffff);
        
        s = main_startstop.getSelectedStyle();
        s.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_VERTICAL);
        s.setBackgroundGradientStartColor(0xcccccc);
        s.setBackgroundGradientEndColor(0x000000);
        s.setFont(Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE));
        s.setFgColor(0xffffff);
        
        s = main_startstop.getPressedStyle();
        s.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_VERTICAL);
        s.setBackgroundGradientStartColor(0xdddddd);
        s.setBackgroundGradientEndColor(0xaaaaaa);
        s.setFont(Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE));
        s.setFgColor(0xffffff);
        
        main_startstop.setAlignment(Button.CENTER);
    }
    
    private void createFormMain() {
        main_customer = new Label("Customer");
        main_project = new Label("Project");
        main_task = new Label("Task");
        main_status = new Label("Status");
        
        main_startstop = new Button(new Command("Start/Stop"));
        setStartStopButtonStyle();      
        
        mainform = new Form("EWUS TimeTracker");
        
        mainform.setLayout(new GridLayout(5, 1));
        mainform.addComponent(main_customer);
        mainform.addComponent(main_project);
        mainform.addComponent(main_task);
        mainform.addComponent(main_startstop);
        mainform.addComponent(main_status);
        
        exitCommand = new Command("Exit");
        mainform.addCommand(exitCommand);

        mainform.addCommandListener(this);
        mainform.show();
    }
    
    public void startApp() {
        Display.init(this);
        controller = new Control(this);
        
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
        if (c.getCommandName().equals("Start/Stop")) {
            controller.startstop();
        }
    }
}
