/*
 * JMUnitTest.java
 * JMUnit based test
 *
 * Created on 01.08.2011, 13:05:19
 */
package test;

import de.ewus.timetracker.j2me.Storage;
import jmunit.framework.cldc11.*;

/**
 * @author Erik.Wegner
 */
public class JMUnitTest extends TestCase {
    
    public JMUnitTest() {
        //The first parameter of inherited constructor is the number of test cases
        super(2, "JMUnitTest EWUSTT");
    }    
    
    public void test(int testNumber) throws Throwable {
        switch (testNumber) {
            case 0 : test_storage_persistent_running(0); break;
            case 1 : test_storage_persistent_running(1); break;
        }
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
    }
    
}
