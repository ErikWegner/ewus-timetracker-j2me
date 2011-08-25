package de.ewus.timetracker.j2me;

import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.list.ListModel;
import com.sun.lwuit.table.TableModel;
import java.util.Vector;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author Erik Wegner
 */
public class Control implements Runnable {
    
    private String customer, project, task;
    private Midlet midlet;
    private boolean running = false;
    private Storage storage;
    private long startTime;
    
    public Control(Midlet midlet) {
        this.midlet = midlet;
        try {
            this.storage = new Storage();
        } catch (RecordStoreException ex) {
            setStatus(ex.getMessage());
        }
        if (this.storage != null) {
            //TODO: read last settings from storage
            this.customer = "Big Company";
            this.project = "New time tracking software";
            this.task = "Mobile app";
            this.running = storage.getRunning();
            if (this.running) {
                this.startTime = Long.parseLong(storage.get(Storage.STARTTIME, "0"));
                startstop2();
            }
        } else {
            errorDialog("Settings unavailable", "No database opened.");
        }
    }
    
    private void errorDialog(String title, String message) {
        if (midlet != null) {
            midlet.errorDialog(title, message);
        }
    }
    
    /**
     * Sets the status text in the midlet
     * @param text The status text
     */
    private void setStatus(String text) {
        if (midlet != null && midlet.main_status != null) 
            midlet.main_status.setText(text);
    }
    
    /**
     * Refresh the table in the midlet
     */
    private void updateTable() {
        if (midlet != null && midlet.main_table != null)
            midlet.main_table.setModel(getTableModel());
    }
    
    /**
     * Starts or stops the timer, stores the result in the database and
     * updates the display.
     */
    public void startstop() {
        running = !running;
        startstop2();
    }
    
    private void startstop2() {
        setStatus(String.valueOf(running));
        try {
            storage.setRunning(running);
        } catch (RecordStoreException ex) {
            errorDialog("Memory error", ex.getMessage());
        }
        if (running) {
            Thread t = new Thread(this);
            t.start();
        }
    }
    
    /**
     * Get customer name.
     * @return the customer's name
     */
    public String getCustomer() {
        return this.customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    /**
     * @return the project
     */
    public String getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(String project) {
        this.project = project;
    }

    /**
     * @return the task
     */
    public String getTask() {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(String task) {
        this.task = task;
    }

    private String format(long l) {
        String s = String.valueOf(l);
        if (l<10)s="0"+s;
        return s;
    }
    
    public void run() {
        if (this.startTime == 0) {
            this.startTime = System.currentTimeMillis();
            storage.set(Storage.STARTTIME, String.valueOf(startTime));
        }
        long now = 0, diff, p1, p2, p3;
        String s;
        while (running) {
            try {
                Thread.sleep(100);
                now = System.currentTimeMillis();
                diff = (now - startTime) / 1000;
                p3 = diff % 60; diff = diff / 60; // seconds
                p2 = diff % 60; diff = diff / 60; // minutes
                p1 = diff; // hours
                setStatus(String.valueOf(p1) + ":" + format(p2) + ":" + format(p3));
            } catch (InterruptedException e) {}
        }
        setStatus("Adding to database…");
        storage.adddTimeSlot(startTime, System.currentTimeMillis(), 0);
        setStatus("Done.");
        updateTable();
    }
    
    /**
     * Signals the controller to shutdown and free all resources 
     */
    public void end() {
        storage.shutdown();
    }
    
    public boolean addTimeSlot(long begin, long end, int task) {
        return storage.adddTimeSlot(begin, end, task);
    }

    public int countTimeSlots() {
        return storage.countTimeSlots();
    }

    public void clearTimeSlots() {
        storage.clearTimeSlots();
    }

    public TableModel getTableModel() {
        return this.storage;
    }
    
    public Vector getAvailableFileroots() {
        return this.storage.getAvailableFileroots();
    }

    public String getFileroot() {
        String fr = storage.get(Storage.FILEROOT, "");
        if (fr.length() == 0) {
            Vector fileroots = getAvailableFileroots();
            if (fileroots.isEmpty()) {
                errorDialog(LocalizationSupport.getMessage("NoDriveTitle"), LocalizationSupport.getMessage("NoDriveMsg"));
                close();
            }
            fr = (String)fileroots.elementAt(0);
            storage.set(Storage.FILEROOT, fr);
        }
        return fr;
    }

    /**
     * Save settings
     * @param fileroot Name of the filesystem root
     */
    public void savesettings(String fileroot) {
        storage.set(Storage.FILEROOT, fileroot);
    }

    private void close() {
        if (midlet != null) midlet.close();
    }
    
    public Vector getProjects() {
        return this.storage.getProjects();
    }
    
    public Vector getCustomers() {
        return this.storage.getCustomers();
    }
    
    public TableModel getTasks() {
        return this.storage.getTasks();
    }
}
