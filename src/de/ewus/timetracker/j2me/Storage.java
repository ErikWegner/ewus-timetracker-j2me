package de.ewus.timetracker.j2me;

import com.sun.lwuit.events.DataChangedListener;
import com.sun.lwuit.table.TableModel;
import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.*;
import javax.microedition.rms.*;

/**
 *
 * @author Erik Wegner
 */
public class Storage {
    private int customerid, projectid, taskid;
    private final String DATASTORENAME = "EWUSTimeTracker";
    private final String SETTINGSSTORENAME = "EWUSTimeTrackerSettings";
    private RecordStore settingsstore;
    private final String SETTING_RUNNING = "Running";
    private final String SETTING_IMMEDIATESAVE = "ImmediateSave";
    public final static String STARTTIME = "Starttime";
    public final static String FILEROOT = "Fileroot";
    private final static String PREFIX_CUSTOMER = "C";
    private final static String PREFIX_PROJECT = "P";
    private final static String PREFIX_TASK = "T";
    private final static String PREFIX_TIMESLOT = "S";
    private final static String SEPARATOR = "#";
    /** List of data change listener */
    private java.util.Vector dcl_timeslotmodel, dcl_taskmodel;
    /** Elements of the data store */
    private Vector data;
    /** Indicates that "data" has changes */
    private boolean data_dirty = false;
    private boolean immediatesave = false;

    private TableModel tasktablemodel, timeslottablemodel;
    
    private boolean readData() {
        boolean r = false;
        FileConnection fc = null;
        DataInputStream is = null;

        try {
            fc = (FileConnection) Connector.open(filename(), Connector.READ);
            if (fc.exists()) {
                is = fc.openDataInputStream();
                String line;
                while (true) {
                    line = is.readUTF();
                    data.addElement(line);
                }
            }
            r = true;
        } catch (EOFException eof) { /* Do nothing */ } catch (IOException ex) {
            ex.printStackTrace();
            error = ex.getMessage();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException ex) {
                }
            }
        }
        return r;
    }

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
                if (s.startsWith(settingname + "=")) {
                    r = r_temp;
                }
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
                r = r.substring(r.indexOf("=") + 1);
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

    /**
     * Constructor for the storage class
     * @throws RecordStoreException 
     */
    public Storage() throws RecordStoreException {
        dcl_timeslotmodel = new Vector();
        dcl_taskmodel = new Vector();
        data = new Vector();
        readSettings();
        readData();
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
        if (v.equals("1")) {
            return true;
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

    public void shutdown() {
        saveData();
        if (this.settingsstore != null) {
            try {
                settingsstore.closeRecordStore();
            } catch (RecordStoreException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Saves the data if there are changes and the setting of isImmediatesave is true
     * A call to this method is always allowed
     * @return True if saved, false otherwise
     */
    public boolean immediateSaveData() {
        if (data_dirty && this.isImmediatesave()) {
            return saveData();
        }
        return false;
    }

    /**
     * Add a time slot to the database
     * @param begin Starting time
     * @param end End time
     * @param task time for this task
     */
    public boolean adddTimeSlot(long begin, long end, int task) {
        String timeslot = PREFIX_TIMESLOT + String.valueOf(begin) + SEPARATOR + String.valueOf(end) + SEPARATOR + String.valueOf(task);
        data.addElement(timeslot);
        data_dirty = true;
        immediateSaveData();
        return true;
    }
    /**
     * Contains the latest error message
     */
    public String error = "";

    /**
     * Reads the number of records
     * @return number of records
     */
    public int countTimeSlots() {
        int r = 0;
        String elem;
        Enumeration e = data.elements();
        while (e.hasMoreElements()) {
            elem = (String) e.nextElement();
            if (elem.startsWith(PREFIX_TIMESLOT)) {
                r = r + 1;
            }
        }
        return r;
    }

    /**
     * Removes all time slots from the database by erasing the database.
     */
    public void clearTimeSlots() {
        error = "";
        fireDataChangeEvent(dcl_timeslotmodel, DataChangedListener.REMOVED, -1);
        data.removeAllElements();
        immediateSaveData();
    }

    /**
     * Query for available filesystems
     * @return A list of file roots
     */
    public Vector getAvailableFileroots() {
        Vector r = new Vector();
        Enumeration drives = FileSystemRegistry.listRoots();
        while (drives.hasMoreElements()) {
            String root = (String) drives.nextElement();
            r.addElement(root);
        }
        return r;
    }

    /**
     * Returns the value for this setting
     * @return the immediatesave
     */
    public boolean isImmediatesave() {
        return immediatesave;
    }

    /**
     * Updates the value for this setting
     * @param immediatesave the immediatesave to set
     */
    public void setImmediatesave(boolean immediatesave) {
        if (this.immediatesave != immediatesave) {
            String v = "0";
            if (immediatesave) {
                v = "1";
            }
            set(SETTING_IMMEDIATESAVE, v);
        }
        this.immediatesave = immediatesave;
    }

    public String filename() {
        return "file:///" + get(FILEROOT, (String) getAvailableFileroots().elementAt(0)) + DATASTORENAME + ".dat";
    }

    /**
     * Stores all data to file system
     * @return False, if an error occured
     */
    public boolean saveData() {
        boolean r = false;
        FileConnection fc = null;
        DataOutputStream os = null;

        try {
            fc = (FileConnection) Connector.open(filename());
            if (!fc.exists()) {
                fc.create();
            }
            os = fc.openDataOutputStream();
            String line;
            Enumeration lines = data.elements();
            while (lines.hasMoreElements()) {
                line = (String) lines.nextElement();
                os.writeUTF(line);
            }
            r = true;
        } catch (IOException ex) {
            ex.printStackTrace();
            error = ex.getMessage();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                }
            }
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException ex) {
                }
            }
        }
        return r;
    }

    public Vector getProjects() {
        Vector c = new Vector();
        c.addElement("A");
        c.addElement("N");
        c.addElement("K");
        return c;
    }

    public Vector getCustomers() {
        Vector v = new Vector();
        v.addElement("C1");
        v.addElement("C2");
        v.addElement("C3");
        return v;
    }

    public TableModel getTasks() {
        if (this.tasktablemodel == null)
            this.tasktablemodel = new TasksTableModel();
        return this.tasktablemodel;
    }

    public TableModel getTimeSlotTableModel() {
        if (this.timeslottablemodel == null)
            this.timeslottablemodel = new TimeslotsTableModel();
        return this.timeslottablemodel;
    }
    
    private void fireDataChangeEvent(Vector listeners, int type, int index) {
        for (int i = 0; i < listeners.size(); i++) {
            DataChangedListener dataChangedListener = (DataChangedListener) listeners.elementAt(i);
            dataChangedListener.dataChanged(type, index);
        }
    }


    protected class TimeslotsTableModel implements TableModel {

        public int getRowCount() {
            return countTimeSlots();
        }

        public int getColumnCount() {
            return 4;
        }

        public String getColumnName(int i) {
            switch (i) {
                case 0:
                    return LocalizationSupport.getMessage("ID");
                case 1:
                    return LocalizationSupport.getMessage("P#");
                case 2:
                    return LocalizationSupport.getMessage("Time");
                case 3:
                    return LocalizationSupport.getMessage("Duration");
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
            dcl_timeslotmodel.addElement(d);
        }

        public void removeDataChangeListener(DataChangedListener d) {
            dcl_timeslotmodel.removeElement(d);
        }
    }

    protected class TasksTableModel implements TableModel {
        public int getRowCount() {
            return 3;
        }

        public int getColumnCount() {
            return 2;
        }

        public String getColumnName(int i) {
            switch (i) {
                case 0:
                    return LocalizationSupport.getMessage("Task");
                case 1:
                    return LocalizationSupport.getMessage("Budget");
            }
            return "";
        }

        public boolean isCellEditable(int row, int column) {
            return column == 0;
        }

        public Object getValueAt(int row, int column) {
            return String.valueOf(row) + "x" + String.valueOf(column);
        }

        public void setValueAt(int row, int column, Object o) {
        }

        public void addDataChangeListener(DataChangedListener d) {
            dcl_taskmodel.addElement(d);
        }

        public void removeDataChangeListener(DataChangedListener d) {
            dcl_taskmodel.removeElement(d);
        }
    }
}
