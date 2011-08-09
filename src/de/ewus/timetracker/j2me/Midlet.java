package de.ewus.timetracker.j2me;

import javax.microedition.midlet.*;

import com.sun.lwuit.*;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.*;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.plaf.Style;

/**
 * @author Erik Wegner
 */
public class Midlet extends MIDlet implements ActionListener {

    /** Command exit application */
    private static final int CMD_EXIT = 1;
    
    /** Command go back */
    private static final int CMD_BACK = 2;
    
    /** Command start/stop */
    private static final int CMD_STARTSTOP = 3;
    
    /** Command go to settings */
    private static final int CMD_SETTINGS = 4;

    /** Command to open data screen */
    private static final int CMD_DATA = 5;
    
    Form mainform;
    
    Command exitCommand;
    Label main_customer, main_project, main_task, main_status;
    Button main_startstop;
    
    Form settingsform, dataform;
    
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
        
        main_startstop = new Button(new Command("Start/Stop", CMD_STARTSTOP));
        setStartStopButtonStyle();      
        
        mainform = new Form("EWUS TimeTracker");
        
        mainform.setLayout(new GridLayout(5, 1));
        mainform.addComponent(main_customer);
        mainform.addComponent(main_project);
        mainform.addComponent(main_task);
        mainform.addComponent(main_startstop);
        mainform.addComponent(main_status);
        
        exitCommand = new Command("Exit", CMD_EXIT);
        mainform.addCommand(exitCommand);

        mainform.addCommand(new Command("Settings", CMD_SETTINGS));
        
        mainform.addCommand(new Command("Data view", CMD_DATA));
        
        mainform.setTransitionOutAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, false, 500));
        mainform.setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, false, 500));
        mainform.addCommandListener(this);
        mainform.show();
    }
    
    private void createFormDataview() {
        dataform = new Form("Data view");
        
        dataform.addCommand(new Command("Back", CMD_BACK));
        dataform.addCommandListener(this);
    }
    
    private void createFormSettings() {
        settingsform = new Form("Settings");
        
        settingsform.addCommand(new Command("Back", CMD_BACK));
        settingsform.addCommand(exitCommand);
        
        settingsform.addCommandListener(this);
    }
    
    public void startApp() {
        Display.init(this);
        createFormMain();
        controller = new Control(this);
    }
    
    public void pauseApp() {
    }
    
    /**
     * Shows an error dialog.
     * @param title Title
     * @param message The message
     */
    public void errorDialog(String title, String message) {
        Dialog.show(title, new TextArea(message), new Command[] {new Command("Dismiss")} );
    }
    
    public void destroyApp(boolean unconditional) {
        controller.end();
    }
    
    public void actionPerformed(ActionEvent ae) {
        Command c = ae.getCommand();
        if (c.getId() == CMD_EXIT) {
            notifyDestroyed();
        }
        if (c.getId() == CMD_STARTSTOP) {
            controller.startstop();
        }
        if (c.getId() == CMD_BACK) {
            mainform.showBack();
        }
        if (c.getId() == CMD_SETTINGS) {
            if (settingsform == null) {
                createFormSettings();
            }
            settingsform.show();
        }
        if (c.getId() == CMD_DATA) {
            if (dataform == null) {
                createFormDataview();
            }
            dataform.show();
        }
    }
}
