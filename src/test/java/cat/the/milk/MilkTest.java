/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import junit.framework.TestCase;

/**
 *
 * @author mitsuaki
 */
public class MilkTest extends TestCase {
    
    public Milk M;
    public String Inp;
    public String Act;
    public String Epc;
    
    public MilkTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        M = new Milk();
        M.init();
        Inp = "";
        Epc = "";
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    /**
//     * Test of init method, of class Milk.
//     */
//    public void testInit() throws Exception {
//        System.out.println("init");
//        Milk instance = new Milk();
//        Milk expResult = null;
//        Milk result = instance.init();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of eval method, of class Milk.
     */
    public void testEval() throws Exception {
        
        Inp += "#class C        \n";
        Inp += "#sfun main      \n";
        Inp += "";
        Epc = "(bof)[expr[#|class|C|({)[expr|(})]|#|sfun|main|({)[expr|(})]]|(eof)]";
        
        M.eval(Inp);
        Act = M.Tree.toString();
        
//        System.out.println(Act);
        assertEquals(Epc, Act);
    }
}
