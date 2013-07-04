/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author mitsuaki
 */
public class FormTest extends TestCase {
    
    public Milk M;
    public String Inp;
    public String Act;
    public String Epc;
    
    public FormTest(String testName) throws Exception {
        super(testName);
        M = new Milk();
        M.init();
        Inp = "";
        Epc = "";
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of recv method, of class Form.
     */
    public void testForm() {
        Inp += "#class C                        \n";
        Inp += "                                \n";
        
        Epc = "[class[C|({)[expr[cons[(|)|({)[expr]|(})]]]|(})]]";
        exec();
        assertEquals(Epc, Act);
    }
    
    public void exec() {
        try {
            M.formTree(Inp);
            Act = M.Formed.toString();
        } catch(Exception e) {
            Act = e.getMessage();
        }
//        System.out.println(Epc);
//        System.out.println(Act);
    }

}
