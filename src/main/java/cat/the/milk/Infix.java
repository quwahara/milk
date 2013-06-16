/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

import static cat.the.milk.Token.getNextToken;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mitsuaki
 */
public class Infix {

    public static String defaultFixConf() {
        
        //  number: binding power
        //  L: infix, left-associative
        //  R: infix, right-associative
        //  C: circumfix, $ is delimiter
        String s = "";
        //      |-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------
        s += "  310     L   as! as                \n";
        s += "  300     L   .                     \n";
        s += "  300     C   ($) [$] {$} <:$:>     \n";
        s += "  290     L   :                     \n";
        s += "  280     L   * / %                 \n";
        s += "  270     L   + -                   \n";
        s += "  260     L   < > <= >=             \n";
        s += "  240     L   == !=                 \n";
        s += "  230     L   and                   \n";
        s += "  220     L   xor                   \n";
        s += "  210     L   or                    \n";
        s += "  110     L   ->                    \n";
        s += "  110     R   = <- += -=            \n";
        s += "  100     L   ,                     \n";
        s += "  -1      L   ;                     \n";
        s += "  -1      L   return                \n";

        return s;
    }
    
    public List<Token> Fixes = new ArrayList<Token>();

    public Infix initFixes(String conf) throws Exception {
        
        Fixes.clear();

        String[] lines, cols;
        String g = "#";
        lines = S.toLines(conf);
        int bp;
        char as;

        for (String line : lines) {
            line = StringUtils.trim(line);
            if (S.isEmpty(line)) {
                continue;
            }
            cols = S.splitS(line);
            if (3 > cols.length) {
                throw new Exception("configuration format was wrong:'" + line + "'");
            }
            bp = Integer.valueOf(cols[0]);
            as = cols[1].charAt(0);
            for (int i = 2; i < cols.length; ++i) {
                Token t, t2;
                String col;
                String[] ss;
                col = cols[i];
                ss = col.split("\\$");
                t = new Token(ss[0], g, bp, as);
                if ('C' == as) {
                    t2 = new Token(ss[1], g, bp, as);
                    t.ends().add(t2);
                }
                Fixes.add(t);
            }
        }
        
        return this;
    }

    public static String defaultFactorConf() {
        
        String s = "";
        //      |-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------
        s += "  st  de  bl  id                  \n";

        return s;
    }
    
    public List<Token> Factors = new ArrayList<Token>();

    public Infix initFactors(String conf) throws Exception {
        
        Factors.clear();

        String[] lines, cols;
        lines = S.toLines(conf);

        for (String line : lines) {
            line = StringUtils.trim(line);
            if (S.isEmpty(line)) {
                continue;
            }
            cols = S.splitS(line);
            for (int i = 2; i < cols.length; ++i) {
                Token t;
                String col;
                col = cols[i];
                t = new Token(col, "%");
                Factors.add(t);
            }
        }
        
        return this;
    }
    
    public Token eval(List<Token> ts, IntBox idx, int rbp) throws Exception {
        
        Token cur;
        cur = getNextToken(ts, idx);
        // negative binding power means end of line
        if (0 > cur.BP) {
            return cur;
        }
        idx.dec();
        
        Token left, infix;
        left = getLeft(ts, idx);
        infix = getNextToken(ts, idx);
        applyAsBp(infix);
        while (infix.BP > rbp) {
            left = getInfix(ts, idx, infix, left);
            infix = getNextToken(ts, idx);
            applyAsBp(infix);
        }
        idx.dec();
        
        return left;
    }
    
    public void applyAsBp(Token infix) {
        Token def;
        def = Token.findIn(Fixes, infix);
        if (null == def) {
            return;
        }
        infix.AS = def.AS;
        infix.BP = def.BP;
        if ('C' == infix.AS) {
            infix.ends().addAll(Token.cloneList(def.ends()));
        }
    }
    
    public Token getInfix(List<Token> ts, IntBox idx, Token infix, Token left) throws Exception {
        Token right;

        infix.subs().add(left);
        if ('L' == infix.AS) {
            right = eval(ts, idx, infix.BP);
            infix.subs().add(right);
            return infix;
            
        } else if ('R' == infix.AS) {
            right = eval(ts, idx, infix.BP - 1);
            infix.subs().add(right);
            return infix;
            
        } else if ('C' == infix.AS) {
            Token end = infix.ends().get(0);
            Token c = getNextToken(ts, idx);
            Token middle;
            if (end.unmatches(c)) {
                idx.dec();
                middle = eval(ts, idx, 0);
            } else {
                middle = new Token("", "");
                idx.dec();
            }
            infix.subs().add(middle);
            c = getNextToken(ts, idx);
            if (end.unmatches(c)) {
                throw new Exception("Not expected token found: " + c.V);
            }
            infix.subs().add(c);
            return infix;
            
        }
        
        throw new Exception("Not expected token found: " + infix.V);
    }

    public Prefix Px;
        
    public Token getLeft(List<Token> ts, IntBox idx) throws Exception {

        Token c, def, r;
        
        c = getNextToken(ts, idx);

        if (null != Px) {
            def = Token.findIn(Px.Defs, c);
            if (null != def) {
                idx.dec();
                r = Px.eval(ts, idx, def);
                return r;
            }
        }
        
        if (Token.matches(Factors, c)) {
            return c;
        }
        
        throw new Exception("Not expected token found: " + c.V);
    }
    
    /**
     * applay binding power to the symbol tokens
     * @return binary function 
     */
    public FuncBin<Token, Effect, List<Token>> bindingPower() {
        return new FuncBin<Token, Effect, List<Token>>() {
            
            public List<Token> eval(Token c, Effect ctx) {
                List<Token> l = new ArrayList<Token>();
                Token t;
                t = Token.findIn(Fixes, c);
                if (null != t) {
                    c.BP = t.BP;
                }
                return l;
            }
        };
    }

    
}
