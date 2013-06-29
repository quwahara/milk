/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mitsuaki
 */
public class S {
    public static boolean eq(CharSequence a, CharSequence b) {
        return StringUtils.equals(a, b);
    }

    public static boolean isBlank(CharSequence a) {
        return StringUtils.isBlank(a);
    }

    public static boolean isEmpty(CharSequence a) {
        return StringUtils.isEmpty(a);
    }
    
    public static boolean isNotBlank(CharSequence a) {
        return StringUtils.isNotBlank(a);
    }

    public static boolean isNotEmpty(CharSequence a) {
        return StringUtils.isNotEmpty(a);
    }
    
    public static String[] toLines(String text) {
        Pattern p = Pattern.compile("(\\r\\n|\\r|\\n)");
        return p.split(text);
    }
    
    public static String[] splitS(String text) {
        Pattern p = Pattern.compile("\\s+");
        return p.split(text);
    }
    
    public static String cc(Iterable<String> c) {
        return StringUtils.join(c, "");
    }
    
    public static final String BGV = "({)";     /// begin value
    public static final String ENV = "(})";     /// end value
    
    public static final String BGG = "bg";      /// begin group
    public static final String ENG = "en";      /// end group
    
    public static final String NLG = "nl";      /// new line group
    
    public static final String STG = "st";      /// string literal group
}
