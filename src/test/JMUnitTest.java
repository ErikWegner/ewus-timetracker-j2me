/*
 * JMUnitTest.java
 * JMUnit based test
 *
 * Created on 01.08.2011, 13:05:19
 */
package test;

import de.ewus.timetracker.j2me.*;
import jmunit.framework.cldc11.*;

/**
 * @author Erik.Wegner
 */
public class JMUnitTest extends TestCase {
    
    public JMUnitTest() {
        //The first parameter of inherited constructor is the number of test cases
        super(9, "JMUnitTest EWUSTT");
    }    
    
    public void test(int testNumber) throws Throwable {
        switch (testNumber) {
            case 0 : test_storage_setget(); break;
            case 1 : test_storage_persistent_setget(); break;
            case 2 : test_storage_persistent_running(0); break;
            case 3 : test_storage_persistent_running(1); break;
            case 4 : test_storage_nosetting(); break;
            case 5 : test_clear_timeslots(); break;
            case 6 : test_storage_roots(); break;
            case 7 : test_storage_timeslot_saving(); break;
            case 8 : test_customers(); break;
        }
    }    
    
    private void test_storage_setget() throws Exception {
        Storage s = new Storage();
        s.set("UNITTEST", "unittest");
        assertEquals("Storing a value", "unittest", s.get("UNITTEST", "failed"));
        s.shutdown();
    }
    
    private void test_storage_persistent_setget() throws Exception {
        Storage s = new Storage();
        s.set("UNITTEST", "unittest");
        s.shutdown();
        s = new Storage();
        assertEquals("Getting a stored value", "unittest", s.get("UNITTEST", "failed"));
        s.shutdown();
    }
    
    private void test_storage_persistent_running(int index) throws Exception {
        boolean[] testStatus = new boolean[] {true, false};
        
        boolean status = testStatus[index];
        Storage s = new Storage();
        s.setRunning(status);
        assertEquals("Running instance keeps value", status, s.getRunning());
        s.shutdown();
        
        s = new Storage();
        assertEquals("Reopening app has the same value", status, s.getRunning());
        s.shutdown();
    }

    private void test_storage_nosetting() throws Exception {
        Storage s = new Storage();
        assertEquals("No value stored.", s.get("XXGJKL", "No value stored."));
        s.shutdown();
    }
    
    private void test_clear_timeslots() throws Exception {
        Control c = new Control(null);
        assertTrue("Adding time slot", c.addTimeSlot(0, 10, 0));
        assertTrue("Has time slots", c.countTimeSlots() > 0);
        c.clearTimeSlots();
        assertTrue("No time slots", c.countTimeSlots() == 0);
        c.end();
    }
    
    private void test_storage_roots()  throws Exception {
        Control c = new Control(null);
        assertFalse("Device has no drives", c.getAvailableFileroots().isEmpty());
        c.end();
    }

    private void test_storage_timeslot_saving()  throws Exception {
        int v = -1;
        Control c = new Control(null);
        v = c.countTimeSlots();
        for (int i = 0; i < 10; i++) {
            assertTrue("Adding time slot", c.addTimeSlot(0, 10, 0));
            v = v + 1;
            assertEquals("(a) Same value", v, c.countTimeSlots());
        }
        c.end();
        c = new Control(null);
        assertTrue("Has time slots", c.countTimeSlots() > 0);
        c.end();
        assertEquals("(b) Same value", v, c.countTimeSlots());
    }

    private void test_customers() throws Exception {
        Storage s = new Storage();
        int n = s.getCustomers().size();
        assertTrue("At least one customer", n > 0);
        int t1, t2, t3;
        t1 = s.addCustomer("Test1");
        t2 = s.addCustomer("Text 2");
        t3 = s.addCustomer("Test3");
        assertEquals("Adding customer #1", t1, n);
        assertEquals("Adding customer #2", t2, n + 1);
        assertEquals("Adding customer #3", t3, n + 2);
        assertTrue("Removing customer #2", s.removeCustomer(t2, t1));
        t3 = t3 - 1;
        assertEquals("Removed customer #2", n + 2, s.getCustomers().size());
        assertTrue("Removing customer #3", s.removeCustomer(t3, t1));
        assertEquals("Removed customer #3", n + 1, s.getCustomers().size());
        assertTrue("Removing customer #1", s.removeCustomer(t1, 0));
        assertEquals("Removed customer #1", n, s.getCustomers().size());
        assertFalse("Removing last customer", s.removeCustomer(0, 0));
    }

}
