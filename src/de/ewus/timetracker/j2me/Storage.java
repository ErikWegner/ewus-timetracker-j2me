package de.ewus.timetracker.j2me;

import javax.microedition.rms.*;

/**
 *
 * @author Erik Wegner
 */
public class Storage {

    private int customerid, projectid, taskid;
    private final String DATASTORENAME = "EWUSTimeTracker";
    private final String SETTINGSSTORENAME = "EWUSTimeTrackerSettings";
    private RecordStore datastore, settingsstore;

    /**
     * Returns the id of a record or -1
     * @param settingname The setting's name
     * @return ID of the record or -1 if none is found
     */
    private int settingExists(String settingname) {
        try {
            RecordEnumeration e = settingsstore.enumerateRecords(null, null, true);
            while (e.hasNextElement()) {
                String s = new String(e.nextRecord());
                if (s.startsWith(settingname + "="))
                    return e.
            }
            e.destroy();
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Store a setting
     * @param setting The setting's name
     * @param value The value to store
     */
    public void set(String setting, String value) {
        int id = settingExists(setting);
        if (id > -1) {
            settingsstore.setRecord(id, setting.getBytes(), 0, setting.length());
        } else {
            settingsstore.addRecord(setting.getBytes(), 0, settings.length());
        }
    }

    private void readSettings() throws RecordStoreException {
        if (settingsstore == null) {
            try {
                settingsstore = RecordStore.openRecordStore(SETTINGSSTORENAME, false);
            } catch (RecordStoreNotFoundException ex) {
            }
            if (settingsstore == null) {
                settingsstore = RecordStore.openRecordStore(SETTINGSSTORENAME, true);
                set("Running", "0");
            }
        }
    }

    public Storage() throws RecordStoreException {
        readSettings();
    }

    private void setRecord(int id, String data) {
        byte[] record = data.getBytes();
        boolean recordExists = false;
        try {
            rs.getRecord(id);
            recordExists = true;
        } catch (InvalidRecordIDException) {
        }
        if (recordExists) {
            rs.setRecord(id, record, 0, data.length());
        } else {
            rs.ad
        }
    }

    /**
     * Stores the state of the timer.
     * @param isRunning True, if the timmer is running
     */
    public void setRunning(boolean isRunning) throws RecordStoreException {
        byte[] record = new byte[1];
        if (isRunning) {
            record[0] = 1;
        } else {
            record[0] = 0;
        }
        if (rs.) {
            rs.setRecord(RECORD_RUNNING, record, 0, 1);
        }
    }

    /**
     * Return true if timer is started.
     * @return True, if timer is running.
     */
    public boolean getRunning() {
        try {
            byte[] record = rs.getRecord(RECORD_RUNNING);
            if (record != null) {
                String s = new String(record);
                return s.equals("1");
            }
            return false;
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
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
