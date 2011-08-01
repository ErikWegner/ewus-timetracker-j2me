package de.ewus.timetracker.j2me;

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
            midlet.main_status.setText(ex.getMessage());
        }
        if (this.storage != null) {
            //TODO: read last settings from storage
            this.customer = "Big Company";
            this.project = "New time tracking software";
            this.task = "Mobile app";
            this.running = storage.getRunning();
            if (this.running) {
                startstop();
            }
        }
    }
    
    private void errorDialog(String title, String message) {
        midlet.errorDialog(title, message);
    }
    
    private void setStatus(String text) {
        if (midlet != null && midlet.main_status != null) 
            midlet.main_status.setText(text);
    }
    
    public void startstop() {
        running = !running;
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
        this.startTime = System.currentTimeMillis();
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
}
