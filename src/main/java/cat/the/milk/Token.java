/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mitsuaki
 */
public class Token {

    public String V = "";       /// Value
    public String G = "";       /// Group
    public int Line = -1;
    public int Col = -1;
    /** appearance frequency */
    public char AF = ' ';
    /** binding power */
    public int BP;
    /** association */
    public char AS = ' ';
    public List<Token> _subs = null;
    public List<Token> _ends = null;
    
    public List<Token> subs() {
        if (null == _subs) { _subs = new ArrayList<Token>(); }
        return _subs;
    }
    
    public List<Token> ends() {
        if (null == _ends) { _ends = new ArrayList<Token>(); }
        return _ends;
    }
    
    public int length() {
        return V.length();
    }

    public Token() {
    }

    public Token(String v, String g) {
        V = v;
        G = g;
    }

    public Token(String v, String g, char af) {
        V = v;
        G = g;
        AF = af;
    }

    public Token(String v, String g, int bp, char as) {
        V = v;
        G = g;
        BP = bp;
        AS = as;
    }

    public Token(String v, String g, int line, int col) {
        V = v;
        G = g;
        Line = line;
        Col = col;
    }
    
    public static Token getNextToken(List<Token> ts, IntBox idx) throws Exception {
        Token c;
        idx.inc();
        if (idx.V >= ts.size()) {
            throw new Exception("no more token");
        }
        c = ts.get(idx.V);
        return c;
    }
    
    /**
     * Query in sub token of this. Returns found token.
     * Delimiter is "/".
     * Matches group when the token starts with "%" in the parameter path.
     * Returns this if the parameter path is empty; 
     * 
     * @param path
     * @return 
     */
    public Token query(String path) {
        if (S.isEmpty(path)) {
            return this;
        }
        String[] ps = path.split("/");
        String p = ps[0];
        boolean isGroup = '%' == p.charAt(0);
        if (isGroup) {
            p = p.substring(1);
        }
        String trg;
        for (Token s : subs()) {
            trg = isGroup ? s.G : s.V;
            if (S.eq(trg, p)) {
                if (1 == ps.length) {
                    return s;
                } else {
                    List<String> pss = Arrays.asList(ps).subList(1, ps.length);
                    String psss = StringUtils.join(pss, "/");
                    return s.query(psss);
                }
            }
        }
        return null;
    }
    
    public static boolean unmatches(List<Token> defs, Token t) {
        return false == matches(defs, t);
    }
    
    public static boolean matches(List<Token> defs, Token t) {
        return null != findIn(defs, t);
    }
    
    public static Token findIn(List<Token> defs, Token t) {
        for (Token def : defs) {
            if (def.matches(t)) {
                return def;
            }
        }
        return null;
    }
    
    public boolean unmatches(Token t) {
        return false == matches(t);
    }
    
    public boolean matches(Token t) {
        
        char g = G.charAt(0);
        
        //  #: id
        if ('#' == g) {
            return S.eq(V, t.V);
        }
        
        //  %: group
        if ('%' == g) {
            return S.eq(V, t.G);
        }
        
        //  {: begin
        if ('{' == g) {
            return S.eq(V, t.G);
        }
        
        //  }: end
        if ('}' == g) {
            return S.eq(V, t.G);
        }
                
        //  @: reference 
        if ('@' == g) {
            return S.eq(G, t.G) && S.eq(V, t.V);
        }

        //  $: special      G
        //  [: or           G
        if ('$' == g || '[' == g) {
            return false;
        }
        
        return false;
    }
    
    public static Token parse(String line) throws MilkException {
        
        Token r, parent, holder;
        parent = holder = new Token();
        r = null;
        Stack<Token> stk = new Stack<Token>();
        // "'" is escape for "[" "]" "|"
        Pattern p = Pattern.compile("(''|'\\[|'\\]|'\\||\\[|\\]|\\|)");
        Matcher m = p.matcher(line);
        String prevs = "";
        String t ="";
        String[] vg;
        int prev = 0;
        while (m.find()) {
            if (prev < m.start()) {
                prevs += line.substring(prev, m.start());
            }
            t = line.substring(m.start(), m.end());
            
            if (S.eq(t, "[") || S.eq(t, "]") || S.eq(t, "|")) {
                if (S.isNotEmpty(prevs)) {
                    vg = (prevs + ":").split(":", 3);
                    r = new Token(vg[0], vg[1]);
                    parent.subs().add(r);
                    prevs = "";
                }
            }
            
            if (S.eq(t, "[")) {
                if (null == r) {
                    throw new MilkException("No parent token for \"[\"");
                }
                stk.push(parent);
                parent = r;
            } else if (S.eq(t, "]")) {
                if (stk.size() == 0) {
                    throw new MilkException("\"]\" is unmatch");
                }
                parent = stk.pop();
            } else if (S.eq(t, "|")) {
                if (parent == holder) {
                    throw new MilkException("No parent token for \"|\"");
                }
            } else {
                prevs += t;
            }
            prev = m.end();
        }
        if (prev < line.length()) {
            prevs += line.substring(prev, line.length());
            vg = (prevs + ":").split(":", 2);
            r = new Token(vg[0], vg[1]);
            parent.subs().add(r);
            prevs = "";
        }
        if (0 < stk.size()) {
            throw new MilkException("\"[\" is unmatch");
        }
        if (0 == holder.subs().size()) {
            throw new MilkException("No token was parsed");
        }
        
        return holder.subs().get(0);
    }
    
    
    
    
    @Override
    public Token clone() {
        Token t = new Token();
        t.V = V;
        t.G = G;
        t.AF = AF;
        t.Line = Line;
        t.Col = Col;
        if (null != _subs && 0 < _subs.size()) {
            t._subs = cloneList(_subs);
        }
        if (null != _ends && 0 < _ends.size()) {
            t._ends = cloneList(_ends);
        }
        return t;
    }
    
    public static List<Token> cloneList(List<Token> ts) {
        if (null == ts) {
            return ts;
        }
        List<Token> neu = new ArrayList<Token>(ts.size());
        for (Token t : ts) {
            neu.add(t.clone());
        }
        return neu;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.V != null ? this.V.hashCode() : 0);
        hash = 83 * hash + (this.G != null ? this.G.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Token other = (Token) obj;
        if ((this.V == null) ? (other.V != null) : !this.V.equals(other.V)) {
            return false;
        }
        if ((this.G == null) ? (other.G != null) : !this.G.equals(other.G)) {
            return false;
        }
        return true;
    }
    
    public static String vOrG(Token t) {
        return S.isBlank(t.V) ? t.G : t.V;
    }

//    @Override
    public String toStringD() {
        return String.valueOf(Line)
                + ":" + String.valueOf(Col)
                + ":" + G + (AF == ' ' ? "" : String.valueOf(AF))
                + ":" + V
                + (null == _subs ? "" : ":[" + StringUtils.join(_subs, '|') + "]")
                ;

    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (1 == V.length() && 0<= "\\[]|".indexOf(V)) {
            b.append("\\");
        }
        b.append(V);
        if(subs().size() > 0) {
            b.append("[");
            for (int i = 0; i < subs().size(); ++i) {
                if (i > 0) {
                    b.append("|");
                }
                b.append(subs().get(i).toString());
            }
            b.append("]");
        }
        return b.toString();
    }

}
