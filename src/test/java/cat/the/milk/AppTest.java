package cat.the.milk;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.objectweb.asm.Type;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws ClassNotFoundException
    {
        String s = Type.getInternalName(String.class);
        Class c = Class.forName(s.replaceAll("/", "."));
        
        s = Type.getInternalName(int[].class);
//                Class 
        c = Class.forName("I");

        Type t = Type.getType("V");
        assertTrue( true );
    }
}
