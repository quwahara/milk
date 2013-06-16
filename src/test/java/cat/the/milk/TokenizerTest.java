/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mitsuaki
 */
public class TokenizerTest extends TestCase {
    
    public Tokenizer Z;
    public Effect Ef;
    public List<Token> Ts;
    public String Inp;
    public String EpcV;
    public String EpcG;
    public List<Token> Act;
    
    public TokenizerTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Z = new Tokenizer();
        Z.init();
        Ef = new Effect();
        Ts = new ArrayList<Token>();
        Inp = "";
        EpcV = "";
        EpcG = "";
        Act = null;
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of eval method, of class Tokenizer.
     */
    public void testEval_empty() throws Exception {
        Inp = "";
        EpcV += "";
        EpcG += "";
        Act = Z.eval(Inp);
        assertEquals(EpcV, vs(Act));
        assertEquals(EpcG, gs(Act));
    }
    
    /**
     * Test of eval method, of class Tokenizer.
     */
    public void testEval_id() throws Exception {
        Inp = "a";
        EpcV += "a";
        EpcG += "id";
        Act = Z.eval(Inp);
        assertEquals(EpcV, vs(Act));
        assertEquals(EpcG, gs(Act));
    }
    
    public void addT(String v, String g) {
        Ts.add(new Token(v, g));
    }
    
    public void addT(String v, String g, int col) {
        Ts.add(new Token(v, g, -1, col));
    }
    
    public void testSection_sec1() throws Exception {
        
        Ts.add(new Token("#", "sc", -1, 0));
        Ts.add(new Token("sec", "id"));
        Ts.add(new Token("(nl)", "nl"));
        Ef.init(Ts)
                .each(Tokenizer.section())
                ;
        
        EpcV += "# sec (nl) ({) (})";
        
//        System.out.println(vs(Ef.L));
        assertEquals(EpcV, vs(Ef.L));
    }
    
    /**
     * Test of eval method, of class Tokenizer.
     */
    public void testSection_sec2() throws Exception {
        
        Ts.add(new Token("#", "sc", -1, 0));
        Ts.add(new Token("sec", "id"));
        Ts.add(new Token("(nl)", "nl"));
        Ts.add(new Token("#", "sc", -1, 0));
        Ts.add(new Token("sec", "id"));
        Ts.add(new Token("(nl)", "nl"));
        Ef.init(Ts)
                .each(Tokenizer.section())
                ;
        
        EpcV += "# sec (nl) ({) (})";
        EpcV += " # sec (nl) ({) (})";
        
//        System.out.println(vs(Ef.L));
        assertEquals(EpcV, vs(Ef.L));
    }
    
    /**
     * Test of eval method, of class Tokenizer.
     */
    public void testSection_sec3() throws Exception {
        
        Ts.add(new Token("#", "sc", -1, 0));
        Ts.add(new Token("sec", "id"));
        Ts.add(new Token("(nl)", "nl"));
        Ts.add(new Token("line", "id"));
        Ts.add(new Token("##", "sc", -1, 0));
        Ts.add(new Token("sec", "id"));
        Ts.add(new Token("(nl)", "nl"));
        Ts.add(new Token("line", "id"));
        Ts.add(new Token("line", "id"));
        Ef.init(Ts)
                .each(Tokenizer.section())
                ;
        
        EpcV += "# sec (nl) ({) line";
        EpcV += " ## sec (nl) ({) line line (})";
        EpcV += " (})";
        
        System.out.println(vs(Ef.L));
        assertEquals(EpcV, vs(Ef.L));
    }
    
    /**
     * Test of eval method, of class Tokenizer.
     */
    public void testSection_sec4() throws Exception {
        
        Ts.add(new Token("#", "sc", -1, 0));
        Ts.add(new Token("sec", "id"));
        Ts.add(new Token("(nl)", "nl"));
        Ts.add(new Token("line", "id"));
        Ts.add(new Token("###", "sc", -1, 0));
        Ts.add(new Token("sec", "id"));
        Ts.add(new Token("(nl)", "nl"));
        Ts.add(new Token("line", "id"));
        Ts.add(new Token("line", "id"));
        Ef.init(Ts)
                .each(Tokenizer.section())
                ;
        
        EpcV += "# sec (nl) ({) line";
        EpcV += " ({)";
        EpcV += " ### sec (nl) ({) line line (})";
        EpcV += " (})";
        EpcV += " (})";
        
        System.out.println(vs(Ef.L));
        assertEquals(EpcV, vs(Ef.L));
    }
    
    /**
     * Test of eval method, of class Tokenizer.
     */
    public void testSection_sec5() throws Exception {
        
        Ts.add(new Token("#", "sc", -1, 0));
        Ts.add(new Token("sec", "id"));
        Ts.add(new Token("(nl)", "nl"));
        Ts.add(new Token("line", "id"));
        Ts.add(new Token("####", "sc", -1, 0));
        Ts.add(new Token("sec", "id"));
        Ts.add(new Token("(nl)", "nl"));
        Ts.add(new Token("line", "id"));
        Ts.add(new Token("##", "sc", -1, 0));
        Ts.add(new Token("sec", "id"));
        Ts.add(new Token("(nl)", "nl"));
        Ts.add(new Token("line", "id"));
        Ef.init(Ts)
                .each(Tokenizer.section())
                ;
        
        EpcV += "# sec (nl) ({) line";
        EpcV += " ({)";
        EpcV += " ({)";
        EpcV += " #### sec (nl) ({) line (})";
        EpcV += " (})";
        EpcV += " (})";
        EpcV += " ## sec (nl) ({) line (})";
        EpcV += " (})";
        
        System.out.println(vs(Ef.L));
        assertEquals(EpcV, vs(Ef.L));
    }
    
    public void testSection_sec6() throws Exception {
        
        Ts.add(new Token("##", "sc", -1, 0));
        Ts.add(new Token("sec", "id"));
        Ts.add(new Token("(nl)", "nl"));
        Ef.init(Ts)
                .each(Tokenizer.section())
                ;
        
        EpcV += "({) ## sec (nl) ({) (}) (})";
        
        System.out.println(vs(Ef.L));
        assertEquals(EpcV, vs(Ef.L));
    }
    
    
//    /**
//     * Test of eval method, of class Tokenizer.
//     */
//    public void testEval_id() throws Exception {
//        Inp = "a";
//        Epc += "a\n";
//        Act = Z.eval(Inp);
//        assertEquals(Epc, toS(Act));
//    }
    
    public static String vs(List<Token> ts) {
        List<String> ss = new ArrayList<String>();
        for (Token t : ts) {
            ss.add(t.V);
        }
        return StringUtils.join(ss, " ");
    }
    
    public static String gs(List<Token> ts) {
        List<String> ss = new ArrayList<String>();
        for (Token t : ts) {
            ss.add(t.G);
        }
        return StringUtils.join(ss, " ");
    }
    
//    /**
//     * Test of init method, of class Tokenizer.
//     */
//    public void testInit() throws Exception {
//        System.out.println("init");
//        Tokenizer instance = new Tokenizer();
//        Tokenizer expResult = null;
//        Tokenizer result = instance.init();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of eval method, of class Tokenizer.
//     */
//    public void testEval() throws Exception {
//        System.out.println("eval");
//        String ftx = "";
//        Tokenizer instance = new Tokenizer();
//        List expResult = null;
//        List result = instance.eval(ftx);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
    
}
