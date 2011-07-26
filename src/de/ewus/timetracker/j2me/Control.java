package de.ewus.timetracker.j2me;

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
        this.storage = new Storage();
        //TODO: read last settings from storage
        this.customer = "Big Company";
        this.project = "New time tracking software";
        this.task = "Mobile app";
        this.running = storage.getRunning();
    }
    
    public void startstop() {
        running = !running;
        midlet.main_status.setText(String.valueOf(running));
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

    public void run() {
        this.startTime = System.currentTimeMillis();
        long now = 0;
        while (running) {
            try {
                Thread.sleep(100);
                now = System.currentTimeMillis();
                midlet.main_status.setText(String.valueOf(now - startTime));
            } catch (InterruptedException e) {}
        }
    }
}
