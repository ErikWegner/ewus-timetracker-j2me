package de.ewus.timetracker.j2me;

import javax.microedition.midlet.*;

import com.sun.lwuit.*;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.*;
import com.sun.lwuit.plaf.Style;
import com.sun.lwuit.table.Table;
import com.sun.lwuit.table.TableLayout;

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
    Table main_table;
    
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
        main_customer = new Label(LocalizationSupport.getMessage("Customer"));
        main_project = new Label(LocalizationSupport.getMessage("Project"));
        main_task = new Label(LocalizationSupport.getMessage("Task"));
        main_status = new Label(LocalizationSupport.getMessage("Status"));
        
        main_startstop = new Button(new Command(LocalizationSupport.getMessage("StartStop"), CMD_STARTSTOP));
        setStartStopButtonStyle();      
        
        main_table = new Table(controller.getTableModel());
        main_table.setScrollableY(true);
        
        mainform = new Form(LocalizationSupport.getMessage("EWUSTimeTracker"));
        
        int[] relative_sizes = new int[] {10,10,10,20,10,40};
        int elemcounter = 0;
        TableLayout layout = new TableLayout(relative_sizes.length,1);
        mainform.setLayout(layout);
        TableLayout.Constraint constraint;
        
        constraint = layout.createConstraint();
        constraint.setHeightPercentage(relative_sizes[elemcounter]);
        constraint.setWidthPercentage(100);
        mainform.addComponent(constraint, main_customer);
        elemcounter++;
        
        constraint = layout.createConstraint();
        constraint.setHeightPercentage(relative_sizes[elemcounter]);
        constraint.setWidthPercentage(100);
        mainform.addComponent(constraint, main_project);
        elemcounter++;
        
        constraint = layout.createConstraint();
        constraint.setHeightPercentage(relative_sizes[elemcounter]);
        constraint.setWidthPercentage(100);
        mainform.addComponent(constraint, main_task);
        elemcounter++;
        
        constraint = layout.createConstraint();
        constraint.setHeightPercentage(relative_sizes[elemcounter]);
        constraint.setWidthPercentage(100);
        mainform.addComponent(constraint, main_startstop);
        elemcounter++;
        
        constraint = layout.createConstraint();
        constraint.setHeightPercentage(relative_sizes[elemcounter]);
        constraint.setWidthPercentage(100);
        mainform.addComponent(constraint, main_status);
        elemcounter++;
        
        constraint = layout.createConstraint();
        constraint.setHeightPercentage(relative_sizes[elemcounter]);
        constraint.setWidthPercentage(100);
        mainform.addComponent(constraint, main_table);
        elemcounter++;
        
        exitCommand = new Command(LocalizationSupport.getMessage("Exit"), CMD_EXIT);
        mainform.addCommand(exitCommand);

        mainform.addCommand(new Command(LocalizationSupport.getMessage("Settings"), CMD_SETTINGS));
        
        mainform.addCommand(new Command(LocalizationSupport.getMessage("Dataview"), CMD_DATA));
        
        mainform.setTransitionOutAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, false, 500));
        mainform.setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, false, 500));
        mainform.addCommandListener(this);
        mainform.show();
    }
    
    private void createFormDataview() {
        dataform = new Form(LocalizationSupport.getMessage("Dataview"));
        
        dataform.addCommand(new Command(LocalizationSupport.getMessage("Back"), CMD_BACK));
        dataform.addCommandListener(this);
    }
    
    private void createFormSettings() {
        settingsform = new Form(LocalizationSupport.getMessage("Settings"));
        
        settingsform.addCommand(new Command(LocalizationSupport.getMessage("Back"), CMD_BACK));
        settingsform.addCommand(exitCommand);
        
        settingsform.addCommandListener(this);
    }
    
    /**
     * @inheritDoc
     */
    public void startApp() {
        Display.init(this);
        if (!LocalizationSupport.initLocalizationSupport()) {
            errorDialog("Error loading translations", LocalizationSupport.getErrorMessage());
        }
        controller = new Control(this);
        createFormMain();
    }
    
    /**
     * @inheritDoc
     */
    public void pauseApp() {
    }
    
    /**
     * Shows an error dialog.
     * @param title Title
     * @param message The message
     */
    public void errorDialog(String title, String message) {
        Dialog.show(title, new TextArea(message), new Command[] {new Command(LocalizationSupport.getMessage("Dismiss"))} );
    }
    
    /**
     * @inheritDoc
     */
    public void destroyApp(boolean unconditional) {
        controller.end();
    }
    
    /**
     * @inheritDoc
     */
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
