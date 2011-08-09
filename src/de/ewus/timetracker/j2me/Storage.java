package de.ewus.timetracker.j2me;

import com.sun.lwuit.events.DataChangedListener;
import com.sun.lwuit.table.TableModel;
import javax.microedition.rms.*;

/**
 *
 * @author Erik Wegner
 */
public class Storage implements TableModel {

    private int customerid, projectid, taskid;
    private final String DATASTORENAME = "EWUSTimeTracker";
    private final String SETTINGSSTORENAME = "EWUSTimeTrackerSettings";
    private RecordStore datastore, settingsstore;
    private final String SETTING_RUNNING = "Running";
    public final static String STARTTIME = "Starttime";
    
    /**
     * Returns the id of a record or -1
     * @param settingname The setting's name
     * @return ID of the record or -1 if none is found
     */
    private int settingExists(String settingname) {
        int r = -1;
        try {
            RecordEnumeration e = settingsstore.enumerateRecords(null, null, true);
            int r_temp = -1;
            while (e.hasNextElement()) {
                r_temp = e.nextRecordId();
                String s = new String(settingsstore.getRecord(r_temp));
                if (s.startsWith(settingname + "="))
                    r = r_temp;
            }
            e.destroy();
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
        return r;
    }

    /**
     * Store a setting
     * @param setting The setting's name
     * @param value The value to store
     */
    public void set(String setting, String value) {
        int id = settingExists(setting);
        byte[] recordvalue = (setting + "=" + value).getBytes();
        try {
            if (id > -1) {
                settingsstore.setRecord(id, recordvalue, 0, recordvalue.length);
            } else {
                settingsstore.addRecord(recordvalue, 0, recordvalue.length);
            }
        } catch (RecordStoreException e) {
            e.printStackTrace();
            //TODO: show user notice
        }
    }

    /**
     * Get a setting
     * @param setting Name of the setting
     * @param defaultvalue Default value to return
     * @return The value
     */
    public String get(String setting, String defaultvalue) {
        String r = defaultvalue;
        int record_id = this.settingExists(setting);
        if (record_id > -1) {
            try {
                r = new String(this.settingsstore.getRecord(record_id));
                r = r.substring(r.indexOf("=")+1);
            } catch (RecordStoreException ex) {
                ex.printStackTrace();
            }
        }
        return r;
    }
    
    private void readSettings() throws RecordStoreException {
        if (settingsstore == null) {
            try {
                settingsstore = RecordStore.openRecordStore(SETTINGSSTORENAME, false);
            } catch (RecordStoreNotFoundException ex) {
            }
            if (settingsstore == null) {
                settingsstore = RecordStore.openRecordStore(SETTINGSSTORENAME, true);
                setRunning(false);
            }
        }
    }

    public Storage() throws RecordStoreException {
        readSettings();
    }


    /**
     * Stores the state of the timer.
     * @param isRunning True, if the timmer is running
     */
    public void setRunning(boolean isRunning) throws RecordStoreException {
        String value = "0";
        if (isRunning) {
            value = "1";
        }
        this.set(SETTING_RUNNING, value);
    }

    /**
     * Return true if timer is started.
     * @return True, if timer is running.
     */
    public boolean getRunning() {
        String v = get(SETTING_RUNNING, "0");
        if (v.equals("1")) return true;
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

    public void shutdown() {
        if (this.settingsstore != null) {
            try {
                settingsstore.closeRecordStore();
            } catch (RecordStoreException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Add a time slot to the database
     * @param begin Starting time
     * @param end End time
     * @param task time for this task
     */
    public boolean adddTimeSlot(long begin, long end, int task) {
        boolean r = false;
        error = "";
        try {
            datastore = RecordStore.openRecordStore(DATASTORENAME, true);
            byte[] data = (String.valueOf(begin) + "#" + String.valueOf(end) + "#" + String.valueOf(task)).getBytes();
            datastore.addRecord(data, 0, data.length);
            datastore.closeRecordStore();
            r = true;
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
            error = ex.getMessage();
        }
        return r;
    }
    
    
    /**
     * Contains the latest error message
     */
    public String error = "";

    /**
     * Reads the number of records
     * @return number of records or -1 if an error occurs
     */
    public int countTimeSlots() {
        int r = -1;
        error = "";
        try {
            datastore = RecordStore.openRecordStore(DATASTORENAME, true);
            r = datastore.getNumRecords();
            datastore.closeRecordStore();
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
            error = ex.getMessage();
        }
        return r;
    }

    /**
     * Removes all time slots from the database by erasing the database.
     */
    public void clearTimeSlots() {
        error = "";
        try {
            RecordStore.deleteRecordStore(DATASTORENAME);
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
            error = ex.getMessage();
        }
    }

    public int getRowCount() {
        return this.countTimeSlots();
    }

    public int getColumnCount() {
        return 4;
    }

    public String getColumnName(int i) {
        switch(i) {
            case 0: return LocalizationSupport.getMessage("ID");
            case 1: return LocalizationSupport.getMessage("P#");
            case 2: return LocalizationSupport.getMessage("Time");
            case 3: return LocalizationSupport.getMessage("Duration");
        }
        return "";
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public Object getValueAt(int row, int column) {
        return String.valueOf(row) + "x" + String.valueOf(column);
    }

    public void setValueAt(int row, int column, Object o) {
        
    }

    public void addDataChangeListener(DataChangedListener d) {
        
    }

    public void removeDataChangeListener(DataChangedListener d) {
        
    }
}
