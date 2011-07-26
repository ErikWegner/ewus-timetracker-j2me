package de.ewus.timetracker.j2me;

/**
 *
 * @author Erik Wegner
 */
public class Storage {
    private int customerid, projectid, taskid;
    
    public boolean getRunning() {
        //TODO: read status from storage
        return false;
    }
    
    public String getCustomer(int id) {
        return "TODO";
    }
    
    public String getProject(int id) {
        return "TODO";
    }
    
    public String getTask(int id) {
        return "TODO";
    }
    /**
     * @return the customerid
     */
    public int getCustomerid() {
        return customerid;
    }

    /**
     * @param customerid the customerid to set
     */
    public void setCustomerid(int customerid) {
        this.customerid = customerid;
    }

    /**
     * @return the projectid
     */
    public int getProjectid() {
        return projectid;
    }

    /**
     * @param projectid the projectid to set
     */
    public void setProjectid(int projectid) {
        this.projectid = projectid;
    }

    /**
     * @return the taskid
     */
    public int getTaskid() {
        return taskid;
    }

    /**
     * @param taskid the taskid to set
     */
    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }
    
}
