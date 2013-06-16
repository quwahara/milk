/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author mitsuaki
 */
public class InfixTest extends TestCase {
    
    public InfixTest(String testName) {
        super(testName);
    }

    public Infix X = new Infix();
    public List<Token> Ts = new ArrayList<Token>();
    public IntBox Idx = new IntBox();
    public Token Act;
    public String Epc;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        X.initFactors(Infix.defaultFactorConf());
        X.initFixes(Infix.defaultFixConf());
        Ts.clear();;
        Idx.V = -1;
        Act = null;
        Epc = "";
    }
    
    public void AddT(String v, String g) {
        Token t = new Token(v, g);
        Ts.add(t);
    }

    /**
     * Test of bindingPower method, of class Infix.
     */
    public void testEval_l() throws Exception {
        
        AddT("a", "id");
        AddT("+", "sy");
        AddT("b", "id");
        AddT("(eof)", "id");
        Epc = "+[a|b]";
        Act = X.eval(Ts, Idx, 0);
//        System.out.println(Act);
        assertEquals(Epc, Act.toString());
    }
    

    /**
     * Test of bindingPower method, of class Infix.
     */
    public void testEval_l_l() throws Exception {
        
        AddT("a", "id");
        AddT("+", "sy");
        AddT("b", "id");
        AddT("*", "sy");
        AddT("c", "id");
        AddT("(eof)", "id");
        Epc = "+[a|*[b|c]]";
        Act = X.eval(Ts, Idx, 0);
//        System.out.println(Act);
        assertEquals(Epc, Act.toString());
    }
    
    /**
     * Test of bindingPower method, of class Infix.
     */
    public void testEval_r_l() throws Exception {
        
        AddT("a", "id");
        AddT("=", "sy");
        AddT("b", "id");
        AddT("+", "sy");
        AddT("c", "id");
        AddT("(eof)", "id");
        Epc = "=[a|+[b|c]]";
        Act = X.eval(Ts, Idx, 0);
//        System.out.println(Act);
        assertEquals(Epc, Act.toString());
    }    
    
    /**
     * Test of bindingPower method, of class Infix.
     */
    public void testEval_r_r_l() throws Exception {
        
        AddT("a", "id");
        AddT("=", "sy");
        AddT("b", "id");
        AddT("=", "sy");
        AddT("c", "id");
        AddT("+", "sy");
        AddT("d", "id");
        AddT("(eof)", "id");
        Epc = "=[a|=[b|+[c|d]]]";
        Act = X.eval(Ts, Idx, 0);
//        System.out.println(Act);
        assertEquals(Epc, Act.toString());
    }
    
    /**
     * Test of bindingPower method, of class Infix.
     */
    public void testEval_c() throws Exception {
        
        AddT("a", "id");
        AddT("{", "sy");
        AddT("b", "id");
        AddT("}", "sy");
        AddT("(eof)", "id");
        Epc = "{[a|b|}]";
        Act = X.eval(Ts, Idx, 0);
//        System.out.println(Act);
        assertEquals(Epc, Act.toString());
    }    
    
}
