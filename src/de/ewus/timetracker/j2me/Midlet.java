package de.ewus.timetracker.j2me;

import java.io.IOException;
import javax.microedition.midlet.*;

import com.sun.lwuit.*;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.*;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.plaf.Style;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.table.Table;
import com.sun.lwuit.table.TableLayout;
import com.sun.lwuit.util.Resources;
import java.util.Vector;

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
    /** Command to add a project */
    private static final int CMD_ADDPROJECT = 6;
    /** Command to remove a project */
    private static final int CMD_DELPROJECT = 7;
    /** Command to add a customer */
    private static final int CMD_ADDCUSTOMER = 8;
    /** Command to remove a customer */
    private static final int CMD_DELCUSTOMER = 9;
    /** Command to add a task */
    private static final int CMD_ADDTASK = 8;
    /** Command to remove a task */
    private static final int CMD_DELTASK = 9;

    private boolean savesettings = false;
    Form mainform;
    Command exitCommand;
    Label main_customer, main_project, main_task, main_status;
    Button main_startstop;
    Table main_table;
    ComboBox set_fileroot;
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

        int[] relative_sizes = new int[]{10, 10, 10, 20, 10, 40};
        int elemcounter = 0;
        TableLayout layout = new TableLayout(relative_sizes.length, 1);
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

    private Image getImage(String name) {
        Image i = null;
        try {
            i = Image.createImage("/" + name);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return i;
    }

    private void createFormSettings() {
        settingsform = new Form(LocalizationSupport.getMessage("Settings"));
        settingsform.setLayout(new BorderLayout());
        settingsform.addCommand(new Command(LocalizationSupport.getMessage("Back"), CMD_BACK));
        settingsform.addCommand(exitCommand);
        settingsform.addCommandListener(this);

        TabbedPane tp = new TabbedPane(TabbedPane.BOTTOM);

        Container c1 = new Container(); //General settings
        Container c2 = new Container(); //Customers, Projects, tasks

        // Building customers, projects, task tab
        TableLayout.Constraint constraint;
        TableLayout layout = new TableLayout(11, 3);
        c2.setLayout(layout);

        c2.setScrollableY(true);
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        constraint.setWidthPercentage(100);
        Label l = new Label(LocalizationSupport.getMessage("SettingsCPTLabel"));
        l.setPreferredW(Display.getInstance().getDisplayWidth());
        c2.addComponent(constraint, l);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        l = new Label(LocalizationSupport.getMessage("Project"));
        c2.addComponent(constraint, l);
        ComboBox cbpr = new ComboBox(controller.getProjects());
        c2.addComponent(cbpr);
        Button addp = new Button(new Command("", getImage("list-add.png"), CMD_ADDPROJECT));
        c2.addComponent(addp);
        Button delp = new Button(new Command("", getImage("list-remove.png"), CMD_DELPROJECT));
        c2.addComponent(delp);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        l = new Label(LocalizationSupport.getMessage("Customer"));
        c2.addComponent(constraint, l);
        ComboBox cbc = new ComboBox(controller.getCustomers());
        c2.addComponent(cbc);
        Button addc = new Button(new Command("", getImage("list-add.png"), CMD_ADDCUSTOMER));
        c2.addComponent(addc);
        Button delc = new Button(new Command("", getImage("list-remove.png"), CMD_DELCUSTOMER));
        c2.addComponent(delc);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        l = new Label(LocalizationSupport.getMessage("Budget"));
        c2.addComponent(constraint, l);
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        TextField tf_budget = new TextField();
        c2.addComponent(constraint, tf_budget);
        tp.addTab(LocalizationSupport.getMessage("SettingsCPT"), getImage("accessories-text-editor.png"), c2);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        l = new Label(LocalizationSupport.getMessage("DisplayFormat"));
        c2.addComponent(constraint, l);
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        ComboBox dspf = new ComboBox(getDisplayFormats());
        c2.addComponent(constraint, dspf);

        l = new Label(LocalizationSupport.getMessage("Tasks"));
        c2.addComponent(l);
        Button addt = new Button(new Command("", getImage("list-add.png"), CMD_ADDTASK));
        c2.addComponent(addt);
        Button delt = new Button(new Command("", getImage("list-remove.png"), CMD_DELTASK));
        c2.addComponent(delt);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        Table tasktable = new Table(controller.getTasks());
        tasktable.setScrollableY(true);
        c2.addComponent(constraint, tasktable);


        // Building general settings tab
        Vector fileroots = new Vector();
        fileroots = controller.getAvailableFileroots();
        set_fileroot = new ComboBox(fileroots);
        set_fileroot.setSelectedIndex(fileroots.indexOf(controller.getFileroot()));
        c1.addComponent(set_fileroot);
        tp.addTab(LocalizationSupport.getMessage("SettingsGeneral"), getImage("applications-system.png"), c1);

        settingsform.addComponent(BorderLayout.CENTER, tp);
        savesettings = true;
    }

    /**
     * @inheritDoc
     */
    public void startApp() {
        Display.init(this);
        try {
            //set the theme
            Resources theme = Resources.open("/lwuit.res");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
        } catch (Throwable ex) {
            ex.printStackTrace();

            errorDialog("Error loading theme", ex.getMessage());
        }
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
        Dialog.show(title, new TextArea(message), new Command[]{new Command(LocalizationSupport.getMessage("Dismiss"))});
    }

    /**
     * @inheritDoc
     */
    public void destroyApp(boolean unconditional) {
        controller.end();
        notifyDestroyed();
    }

    /**
     * @inheritDoc
     */
    public void actionPerformed(ActionEvent ae) {
        Command c = ae.getCommand();
        if (c.getId() == CMD_EXIT) {
            destroyApp(false);
        }
        if (c.getId() == CMD_STARTSTOP) {
            controller.startstop();
        }
        if (c.getId() == CMD_BACK) {
            if (savesettings) {
                savesettings();
            }
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

    public void close() {
        destroyApp(false);
    }

    private void savesettings() {
        controller.savesettings(set_fileroot.getSelectedItem().toString());
    }

    private Vector getDisplayFormats() {
        Vector v = new Vector();
        v.addElement(LocalizationSupport.getMessage("DisplayFormatCustomerProject1"));
        v.addElement(LocalizationSupport.getMessage("DisplayFormatProjectCustomer1"));
        return v;
    }
}
