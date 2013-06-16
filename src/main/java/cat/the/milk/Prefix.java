/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

import static cat.the.milk.Token.getNextToken;

/**
 *
 * @author mitsuaki
 */
public class Prefix {
    
    public static String defaultConf() {
        String s = "";
        //      |-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------
        s += "  @exprs  %{bg    $expr*  $}      \n";
        s += "  %{fn    %id     @exprs  %}en    \n";
        s += "  %{ty    %id     @exprs  %}en    \n";
        s += "  #{if    $expr   @exprs  @elif   @else   %}en    \n";
        s += "  @elif   #{elif* $expr   @exprs  $}              \n";
        s += "  @else   #{else? $expr*  $}                      \n";
        s += "  %{bg    $expr*  %}en    \n";
        s += "  #{(bof) $expr*  #}(eof)                 \n";
        return s;
    }

    public Token eval(List<Token> ts) throws Exception {
        ts.add(0, new Token("(bof)", "id"));
        ts.add(/**/new Token("(eof)", "id"));
        Token def = Token.findIn(Defs, ts.get(0));
        IntBox idx = new IntBox();
        idx.dec();
        Token t = eval(ts, idx, def);
        return t;
    }
    
    public Token eval(List<Token> ts, IntBox idx, Token def) throws Exception {
        if ('*' == def.AF) {
            return evalMany(ts, idx, def);
        } else {
            return evalSingle(ts, idx, def);
        }
    }
        
    public Token evalMany(List<Token> ts, IntBox idx, Token def) throws Exception {
    
        List<Token> rs = new ArrayList<Token>();
        Token c = getNextToken(ts, idx);
        Token r;
        List<Token> ends = def.ends();
        Token defOne = new Token(def.V, def.G, '1');
        Token t = new Token(def.V, def.G);
        
        boolean notEnd = Token.unmatches(ends, c);
        while (notEnd) {
            idx.dec();
            r = evalSingle(ts, idx, defOne);
            if (null != r) {
                rs.add(r);
            }
            c = getNextToken(ts, idx);
            notEnd = Token.unmatches(ends, c);
        }
        idx.dec();
        t.subs().addAll(rs);
        return t;
    }

    public Token evalSingle(List<Token> ts, IntBox idx, Token def) throws Exception {
        
        Token c;            /// current token
        c = getNextToken(ts, idx);
        Token r;
        if (S.eq("#", def.G) || S.eq("%", def.G)) {
            if (def.unmatches(c)) {
                if (def.AF == '1') {
                    throw new Exception("not expected token:" + c.toString());
                } else {
                    idx.dec();
                    return null;
                }
            }
            for (Token d: def.subs()) {
                r = eval(ts, idx, d);
                if (null != r) {
                    c.subs().add(r);
                }
            }
            return c;
        }
        if (S.eq("$", def.G)) {
            idx.dec();
            r = evalExpr(ts, idx);
            if (null == r) {
                throw new Exception("expected a token but none");
            }
            return r;
        }
        
        throw new Exception("expected a token but none");

//        if (S.eq("[", def.G)) {
//
//            idx.V = idx.V - 1;
//            IntBox tmpidx = new IntBox();
//            tmpidx.V = idx.V;
//            
//            r = null;
//            boolean success = false;
//            for (Token d : def.subs()) {
//                r = null;
//                success = false;
//                try {
//                    tmpidx.V = idx.V;
//                    r = eval(ts, tmpidx, d);
//                    success = true;
//                } catch (Exception e) {
//                    
//                }
//                if (success) {
//                    break;
//                }
//            }
//            if (success) {
//                idx.V = tmpidx.V;
//            } else {
//                throw new Exception("no more token");
//            }
//            return r;
//            
//        }
//        
//        {
//            if (unmatches(def, c)) {
//                throw new Exception("not expected token:" + c.toString());
//            }
//            return c;
//        }
    }
    
    public Infix Ix;
    
    public Token evalExpr(List<Token> ts, IntBox idx) throws Exception {
        Token c, def;
        c = getNextToken(ts, idx);
        def = Token.findIn(Defs, c);
        if (def != null) {
            idx.dec();
            Token expr = evalSingle(ts, idx, def);
            return expr;
        }
        
        if (null != Ix) {
            idx.dec();
            return Ix.eval(ts, idx, 0);
        }
        
        return c;
    }
    
    public List<Token> Defs = new ArrayList<Token>();
    
    public Prefix init(String conf) throws Exception {
        Pattern p;
        
        Defs.clear();
        p = Pattern.compile("\\s+");
        String[] lines = S.toLines(conf);
        String line;
        
        HashMap<Token, List<Token>> refmap = new HashMap<Token, List<Token>>();
        List<List<Token>> defsls = new ArrayList<List<Token>>();
        
        for (int i = 0; i < lines.length; ++i ) {
            line = StringUtils.trim(lines[i]);
            if (0 == line.length()) { continue; }
            String[] cols = p.split(line);
            List<Token> l = prefixDef(cols);
            if (0 == l.size()) {
                throw new Exception("line " + String.valueOf(i) + "is empty");
            }
            if (S.eq("@", l.get(0).G)) {
                refmap.put(l.get(0), l.subList(1, l.size()));
            } else {
                defsls.add(l);
            }
        }
        
        //  replace reference
        refmap = replaceReferenceInMap(refmap);
        List<List<Token>> defslsNoRef = new ArrayList<List<Token>>();
        for (List<Token> defs : defsls) {
            List<Token> defsNoRef = replaceReferenceInList(defs, refmap);
            defslsNoRef.add(defsNoRef);
        }
        
        //  set ends
        List<List<Token>> defslsEnds = new ArrayList<List<Token>>();
        for (List<Token> defs : defslsNoRef) {
            List<Token> l = new ArrayList<Token>();
            Token d;
            List<Token> backs;
            for (int i = 0; i < defs.size(); ++i) {
                d = defs.get(i);
                if ('*' == d.AF) {
                    backs = defs.subList(i + 1, defs.size());
                    for (Token b : backs) {
                        if (false == S.eq(b.G, "$")) {
                            d.ends().add(b);
                        }
                    }
                }
                l.add(d);
            }
            defslsEnds.add(l);
        }

        //  build structure
        for (List<Token> defs : defslsEnds) {
            List<Token> l = new ArrayList<Token>();
            Stack<List<Token>> stk = new Stack<List<Token>>();
            for (Token d : defs) {
                char h = ' ';
                if (StringUtils.startsWithAny(d.V, new String[]{"{", "}"})) {
                    h = d.V.charAt(0);
                    d.V = d.V.substring(1);
                }
                if (h == '{') {
                    if (S.isNotEmpty(d.V)) {
                        l.add(d);
                    } else {
                        d = l.get(l.size() - 1);
                    }
                    stk.push(l);
                    l = d.subs();
                    continue;
                }
                if (h == '}') {
                    if (S.isNotEmpty(d.V)) {
                        l.add(d);
                    }
                    l = stk.pop();
                    continue;
                }
//                if (S.eq("[", d.G)) {
//                    l.add(d);
//                    stk.push(l);
//                    l = d.subs();
//                    continue;
//                }
//                if (S.eq("]", d.G)) {
//                    stk.push(l);
//                    l = stk.pop();
//                    continue;
//                }
                l.add(d);
            }
            Defs.add(l.get(0));
        }
        
        return this;
    }
    
    public static List<Token> prefixDef(String[] defs) throws Exception {
        List<Token> l = new ArrayList<Token>();
        char af;
        char c;
        String v;
        for (String d : defs) {

            af = '1';
            if (StringUtils.endsWithAny(d, new String[] {"1", "?", "*"})) {
                af = d.charAt(d.length() - 1);
                d = d.substring(0, d.length() - 1);
            }
            c = d.charAt(0);
            v = d.substring(1);
            if ('[' == c) {
                addToL(l, "", c, af);

            } else if (']' == c) {
                addToL(l, "", c, af);

            } else if ('{' == c) {
                addToL(l, d.substring(1), c, af);

            } else if ('}' == c) {
                addToL(l, d.substring(1), c, af);

            } else if ('#' == c) {
                addToL(l, d.substring(1), c, af);

            } else if ('%' == c) {
                addToL(l, d.substring(1), c, af);

            } else if ('@' == c) {
                addToL(l, d.substring(1), c, af);

            } else if ('$' == c) {
                addToL(l, v, c, af);
            }
        }
        return l;
    }
    
    public static Token addToL(List<Token> l, String v, char c, char af) {
        Token t = new Token();
        t.V = v;
        t.G = String.valueOf(c);
        t.AF = af;
        l.add(t);
        return t;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Token k : Defs) {
            b.append(k).append("\n");
        }
        return b.toString();
    }

    public static HashMap<Token, List<Token>> replaceReferenceInMap(HashMap<Token, List<Token>> refmap) throws Exception {
        HashMap<Token, List<Token>> premap;
        HashMap<Token, List<Token>> newmap;
        premap = refmap;
        boolean hasRef = true;
        while (hasRef) {
            hasRef = false;
            newmap = new HashMap<Token, List<Token>>();
            for (Token k : premap.keySet()) {
                List<Token> v = premap.get(k);
                hasRef |= hasReference(v);
                if (hasRef) {
                    v = replaceReferenceInList(v, premap);
                }
                newmap.put(k, v);
            }
            premap = newmap;
        }
        return premap;
    }
    
    public static boolean hasReference(List<Token> defs) {
        for (int i = 0; i < defs.size(); ++i) {
            Token t;
            t = defs.get(i);
            if (S.eq("@", t.G)) {
                return true;
            }
        }
        return false;
    }
    
    public static List<Token> replaceReferenceInList(List<Token> defs, HashMap<Token, List<Token>> refmap) throws Exception {
        List<Token> defsNoRef = new ArrayList<Token>();
        for (int i = 0; i < defs.size(); ++i) {
            Token t;
            t = defs.get(i);
            if (S.eq("@", t.G)) {
                List<Token> refdefs;
                refdefs = refmap.get(t);
                if (null == refdefs) {
                    throw new Exception("reference was not defined for:" + t.G + t.V);
                }
                refdefs = Token.cloneList(refdefs);
                defsNoRef.addAll(refdefs);
            } else {
                defsNoRef.add(t);
            }
        }
        return defsNoRef;
    }
    
}
