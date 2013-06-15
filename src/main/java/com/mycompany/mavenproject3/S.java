/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject3;

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
    
}
