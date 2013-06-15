/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.util.ArrayList;
import java.util.List;
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
    public char AF = ' ';       /// appearance frequency
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

    public Token(String v, String g, int line, int col) {
        V = v;
        G = g;
        Line = line;
        Col = col;
    }
    
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
