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

    public void testEval_20130616_1() throws Exception {
        
        Inp += "##sfun main(args:String[])  \n";
        Inp += "";
        
        Epc = "(bof)[expr[({)[expr[sfun[main|([:[args|\\[[String||\\]]]|,|)]|({)[expr]|(})]]|(})]]|(eof)]";
        
        M.eval(Inp);
        Act = M.Tree.toString();
        
        System.out.println(Act);
        assertEquals(Epc, Act);
    }
    
    public void testEval_20130616_2() throws Exception {
        
        Inp += "#class C                    \n";
        Inp += "";
        Inp += "##sfun main(args:String[])  \n";
        Inp += "";
        
        Epc = "(bof)[expr[class[C|({)[expr[sfun[main|([:[args|\\[[String||\\]]]|,|)]|({)[expr]|(})]]]|(})]]|(eof)]";
        
        M.eval(Inp);
        Act = M.Tree.toString();
        
        System.out.println(Act);
        assertEquals(Epc, Act);
    }
    
}
