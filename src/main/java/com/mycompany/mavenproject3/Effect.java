/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject3;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mitsuaki
 */
public class Effect {
    
    public List<Token> L;
    public int I;   /// current index in each()
    public Token C; /// current token in each()
    public Stack<Token> S = new Stack<Token>();
    public Exception E;
    
    private boolean traceOn = false;
    public Effect setTraceOn(boolean v) {
        traceOn = v;
        return this;
    }
    
    public Effect init(List<Token> l) {
        L = l;
        return this;
    }
    
    protected void reset() {
        I = -1; C = null; S.clear(); E = null;
    }
    
    public Effect each(FuncBin<Token, Effect, List<Token>> f) throws Exception {
        reset();
        List<Token> l = new ArrayList<Token>(L.size());
        for(I = 0; I < L.size(); ++I) {
            C = L.get(I);
            l.addAll(f.eval(C, this));
            if (null != E) {
                throw E;
            }
        }
        L = l;
        if (traceOn) { trace(); }
        return this;
    }
    
    public boolean isFirst() {
        return I == 0;
    }
    
    public boolean isLast() {
        return (I + 1) == L.size();
    }
    
    public Effect trace() {
        String s;
        s = StringUtils.join(L, '|');
        s = StringUtils.replace(s, "\t", "\\t");
        s = StringUtils.replace(s, "\n", "\\n");
        s = StringUtils.replace(s, "\r", "\\r");
        System.out.println(String.valueOf(L.size()) + ":[" + s + "]");
        return this;
    }
    
}
