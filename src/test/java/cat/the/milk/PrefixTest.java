/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import com.mycompany.mavenproject3.Prefix;
import com.mycompany.mavenproject3.Token;
import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mitsuaki
 */
public class PrefixTest extends TestCase {
    
    public PrefixTest(String testName) {
        super(testName);
    }
    
    Prefix P = null;
    List<Token> Ts;
    Token result;
    
    @Override
    protected void setUp() throws Exception {
        P = new Prefix();
        P.init(Prefix.defaultConf());
        Ts = new ArrayList<Token>();
        result = null;
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    /**
//     * Test of defaultConf method, of class Prefix.
//     */
//    public void testDefaultConf() {
//        System.out.println("defaultConf");
//        String expResult = "";
//        String result = Prefix.defaultConf();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
    public void addT(String v, String g) {
        Token t = new Token(v, g, -1, -1);
        Ts.add(t);
    }

    public void testViewConf() throws Exception {
        System.out.print(P);
    }

    /**
     * Test of init method, of class Prefix.
     */
    public void testInit_reference_in_reference() throws Exception {
        
        String s;
        Prefix p = new Prefix();

        s = "";
        //      |-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------
        s += "  @exprs  %{bg    $expr*  $}      \n";
        s += "  #{if    $expr   @exprs  @elif   %}en    \n";
        s += "  @elif   #{elif* $expr   @exprs  $}      \n";

        String epc = "if[expr|bg[expr]|elif[expr|bg[expr]]|en]";
        p.init(s);
        String act = StringUtils.trim(p.toString());
//        System.out.print(act);
        assertEquals(epc, act);
    }


    /**
     * Test of eval method, of class Prefix.
     */
    public void testEval_List_root() throws Exception {
        result = P.eval(Ts);
        System.out.println(result.toString());
        assertEquals("(bof)[expr|(eof)]", result.toString());
    }

    public void testEval_List_root_test() throws Exception {
        addT("test", "id");
        result = P.eval(Ts);
        System.out.println(result.toString());
        assertEquals("(bof)[expr[test]|(eof)]", result.toString());
    }

    public void testEval_List_fun() throws Exception {
        addT("fun", "fn");
        addT("name", "id");
        addT("({)", "bg");
        addT("(})", "en");
        result = P.eval(Ts);
        System.out.println(result.toString());
        assertEquals("(bof)[expr[fun[name|({)[expr]|(})]]|(eof)]", result.toString());
    }

    public void testEval_List_type() throws Exception {
        addT("class", "ty");
        addT("name", "id");
        addT("({)", "bg");
        addT("(})", "en");
        result = P.eval(Ts);
        System.out.println(result.toString());
        assertEquals("(bof)[expr[class[name|({)[expr]|(})]]|(eof)]", result.toString());
    }
    

    public void testEval_List_begin_end() throws Exception {
        addT("({)", "bg");
        addT("(})", "en");
        result = P.eval(Ts);
        System.out.println(result.toString());
        assertEquals("(bof)[expr[({)[expr|(})]]|(eof)]", result.toString());
    }
    
//    public void testEval_List_if() throws Exception {
//        addT("if", "id");
//        addT("true", "");
//        addT("then", "bg");
//        addT("end", "en");
//        Token result = P.eval(Ts);
//        assertEquals("(root)[({)[(})[if[true|then[end]]]]]", result.toString());
//    }
//    
//    public void testEval_List_xif_else() throws Exception {
//        addT("xif", "id");
//        addT("true", "");
//        addT("then", "bg");
//        addT("else", "id");
//        addT("begin", "bg");
//        addT("end", "en");
//        addT("end", "en");
//        Token result = P.eval(Ts);
//        assertEquals("(root)[({)[(})[xif[true|then[else|begin[end]|end]]]]]", result.toString());
//    }
    
    
//    /**
//     * Test of eval method, of class Prefix.
//     */
//    public void testEval_List() throws Exception {
//        System.out.println("eval");
//        List<Token> ts = null;
//        Prefix instance = new Prefix();
//        Token expResult = null;
//        Token result = instance.eval(ts);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
//    /**
//     * Test of eval method, of class Prefix.
//     */
//    public void testEval_3args() throws Exception {
//        System.out.println("eval");
//        List<Token> it = null;
//        IntBox idx = null;
//        Token def = null;
//        Prefix instance = new Prefix();
//        Token expResult = null;
//        Token result = instance.eval(it, idx, def);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of evalExpr method, of class Prefix.
//     */
//    public void testEvalExpr() {
//        System.out.println("evalExpr");
//        List<Token> it = null;
//        IntBox idx = null;
//        Prefix instance = new Prefix();
//        Token expResult = null;
//        Token result = instance.evalExpr(it, idx);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of getKeydef method, of class Prefix.
//     */
//    public void testGetKeydef() {
//        System.out.println("getKeydef");
//        String v = "";
//        String g = "";
//        Prefix instance = new Prefix();
//        Token expResult = null;
//        Token result = instance.getKeydef(v, g);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of unmatches method, of class Prefix.
//     */
//    public void testUnmatches() {
//        System.out.println("unmatches");
//        Token def = null;
//        Token t = null;
//        boolean expResult = false;
//        boolean result = Prefix.unmatches(def, t);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of matches method, of class Prefix.
//     */
//    public void testMatches() {
//        System.out.println("matches");
//        Token def = null;
//        Token t = null;
//        boolean expResult = false;
//        boolean result = Prefix.matches(def, t);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of prefixDef method, of class Prefix.
//     */
//    public void testPrefixDef() throws Exception {
//        System.out.println("prefixDef");
//        String[] defs = null;
//        List expResult = null;
//        List result = Prefix.prefixDef(defs);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToL method, of class Prefix.
//     */
//    public void testAddToL() {
//        System.out.println("addToL");
//        List<Token> l = null;
//        String v = "";
//        char c = ' ';
//        char af = ' ';
//        Token expResult = null;
//        Token result = Prefix.addToL(l, v, c, af);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of findDef method, of class Prefix.
//     */
//    public void testFindDef() {
//        System.out.println("findDef");
//        Token t = null;
//        Prefix instance = new Prefix();
//        List expResult = null;
//        List result = instance.findDef(t);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of toString method, of class Prefix.
//     */
//    public void testToString() {
//        System.out.println("toString");
//        Prefix instance = new Prefix();
//        String expResult = "";
//        String result = instance.toString();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
