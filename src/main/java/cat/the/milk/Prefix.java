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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author mitsuaki
 */
public class Prefix {
    
    public Log L = LogFactory.getLog(Prefix.class);
    public static Stack<String> LS = new Stack<String>();
    
    public static String defaultConf() {
        String s = "";
        //      |-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------
        s += "  %{fn    %id     #{(     @csv    $}      #)      #{:?    $expr   $}      @body   $}      \n";
        s += "  @csv    ${expr? #{,~    $expr   $}      $}      \n";
        s += "  @exprs  %{bg    $expr*  $}      \n";
        s += "  @rtype  #{:?    $expr   $}                                      \n";
        s += "  %{ty    %id     @exprs  %}en    \n";
        s += "  #{if    $expr   @exprs  @elif   @else   %}en    \n";
        s += "  @elif   #{elif* $expr   @exprs  $}              \n";
        s += "  @else   #{else? $expr*  $}                      \n";
        s += "  @body   %{bg    $expr*  $}      %en     \n";
        s += "  %{bg    $expr*  %}en    \n";
        s += "  %{bof   $expr*  %}eof                   \n";
        
        return s;
    }

    public Token eval(List<Token> ts) throws Exception {
        ts.add(0, new Token("(bof)", "bof"));
        ts.add(/**/new Token("(eof)", "eof"));
        Token def = Token.findIn(Defs, ts.get(0));
        IntBox idx = new IntBox();
        idx.dec();
        Token t = eval(ts, idx, def);
        return t;
    }
    
    public String IndentStr = "| ";
    public void indent() {
        LS.push(IndentStr);
    }
    public void unindent() {
        LS.pop();
    }
    public String indents() {
        return S.cc(LS);
    }
    public void trace(Object t) {
        System.out.println(t);
//        L.info(t);
    }    
    
    public Token eval(List<Token> ts, IntBox idx, Token def) throws Exception {
        Token r = null;
        
        if ('1' == def.AF) {
            r = evalRequireOne(ts, idx, def);
        } else if ('?' == def.AF) {
            r = evalMaybeOne(ts, idx, def);
        } else if ('*' == def.AF) {
            return evalUntilEnd(ts, idx, def);
        } else if ('~' == def.AF) {
            return evalUntilNotSelf(ts, idx, def);
        } else {
            throw new Exception("unkown AF:" + def.AF);
        }
        
        if (null == r) {
            return r;
        }
        
        Token sub;
        indent();
        for (Token d: def.subs()) {
            sub = eval(ts, idx, d);
            if (null == sub && '?' == d.AF ) {
                idx.dec();
                break;
            }
            if (null != sub) {
                r.subs().add(sub);
            }
        }
        unindent();
        return r;
    }
    
    public Token evalRequireOne(List<Token> ts, IntBox idx, Token def) throws Exception {
        Token c;            /// current token
        c = getNextToken(ts, idx);
        Token r;
        if (S.eq("#", def.G) || S.eq("%", def.G)) {
            if (def.unmatches(c)) {
                throw new Exception("not expected token:" + c.toString());
            }
            trace(S.cc(LS) + Token.vOrG(c));
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
    }
    
    public Token evalMaybeOne(List<Token> ts, IntBox idx, Token def) throws Exception {
        Token c;            /// current token
        c = getNextToken(ts, idx);
        Token r;
        if (S.eq("#", def.G) || S.eq("%", def.G)) {
            if (def.unmatches(c)) {
                idx.dec();
                return null;
            }
            trace(S.cc(LS) + Token.vOrG(c));
            return c;
        }
        if (S.eq("$", def.G)) {
            idx.dec();
            r = evalExpr(ts, idx);
            if (null != r) {
                trace(S.cc(LS) + Token.vOrG(r));
            }
            return r;
        }
        throw new Exception("expected a token but none");
    }
    
    public Token evalUntilEnd(List<Token> ts, IntBox idx, Token def) throws Exception {
        List<Token> rs = new ArrayList<Token>();
        Token c = getNextToken(ts, idx);
        Token r;
        List<Token> ends = def.ends();
        Token defExprRequireOne = new Token(def.V, def.G, '1');
        Token t = new Token(def.V, def.G);
        trace(S.cc(LS) + Token.vOrG(t));
        
        boolean notEnd = Token.unmatches(ends, c);
        while (notEnd) {
            idx.dec();
            indent();
            r = evalRequireOne(ts, idx, defExprRequireOne);
            unindent();
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
        
    public Token evalUntilNotSelf(List<Token> ts, IntBox idx, Token def) throws Exception {
        List<Token> rs = new ArrayList<Token>();
        Token c = getNextToken(ts, idx);
        Token r;
        
        Token defExprRequireOne = def.clone();
        defExprRequireOne.AF = '1';
//        Token defExprRequireOne = new Token(def.V, def.G, '1');
        Token t = new Token(def.V, def.G);
        trace(S.cc(LS) + Token.vOrG(t));
        
        boolean notEnd = def.matches(c);
        while (notEnd) {
            idx.dec();
            indent();
            r = eval(ts, idx, defExprRequireOne);
            unindent();
            if (null != r) {
                rs.add(r);
            }
            c = getNextToken(ts, idx);
            notEnd = def.matches(c);
        }
        idx.dec();
        t.subs().addAll(rs);
        
        return t;
    }
        
    public Infix Ix;
    
    public Token evalExpr(List<Token> ts, IntBox idx) throws Exception {
        Token c, def;
        c = getNextToken(ts, idx);
        def = Token.findIn(Defs, c);
        if (def != null) {
            idx.dec();
            Token expr = eval(ts, idx, def);
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
        LS.clear();
        
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
                if ('?' == d.AF || '*' == d.AF) {
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
                l.add(d);
            }
            if (0 != stk.size()) {
                throw new Exception("curly bracket is unmatch");
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
            if (StringUtils.endsWithAny(d, new String[] {"1", "?", "*", "~"})) {
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
