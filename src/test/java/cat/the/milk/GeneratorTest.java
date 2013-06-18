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
public class GeneratorTest extends TestCase {

    public Milk Mi;
    public String Inp;
    public String Epc;
    public String Act;
    
    public GeneratorTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Mi = new Milk().init();
        Inp = "";
        Epc = "";
        Act = "";
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void test() throws Exception {
        Inp += "#class C                        \n";
        Inp += "                                \n";
        Inp += "##sfun main(args:String[])      \n";
        Inp += "                                \n";
        Inp += "    p(\"m\")                    \n";
        Inp += "                                \n";
        
        Epc += "// class version 49.0 (49)\n";
        Epc += "// access flags 0x21\n";
        Epc += "public class C {\n";
        Epc += "\n";
        Epc += "  // compiled from: C.java\n";
        Epc += "\n";
        Epc += "  // access flags 0x1\n";
        Epc += "  public <init>()V\n";
        Epc += "    ALOAD 0\n";
        Epc += "    INVOKESPECIAL java/lang/Object.<init> ()V\n";
        Epc += "    RETURN\n";
        Epc += "    MAXSTACK = 1\n";
        Epc += "    MAXLOCALS = 1\n";
        Epc += "\n";
        Epc += "  // access flags 0x9\n";
        Epc += "  public static main([Ljava/lang/String;)V\n";
        Epc += "    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;\n";
        Epc += "    LDC \"hello\"\n";
        Epc += "    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V\n";
        Epc += "    RETURN\n";
        Epc += "    MAXSTACK = 2\n";
        Epc += "    MAXLOCALS = 1\n";
        Epc += "}\n";
        
        Mi.eval(Inp);
        Act = Mi.Ge.toString();
//        System.out.println(Act);
        assertEquals(Epc, Act);
    }

}
