/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mitsuaki
 */
public class Tokenizer {
   
    protected LineIterator it;

//    String choprgx;
    LinkedHashMap<String, Pattern> pmap;    /// pattern map
    
    public static String defaultConf() {
        String s = "";
        s += "es ... \\\\." + "\n";               /// EScape
        s += "sy ... \\\\" + "\n";                /// SYmbol
        s += "nl ... (\\r\\n|\\r|\\n)" + "\n";    /// New Line
        s += "sp ... ( +|\\t+|\\s+)" + "\n";      /// SPace
        s += "sc ... ^#+" + "\n";                 /// SeCtion
        s += "ty ... class" + "\n";               /// TYpe
        s += "bg ... (begin|then|do)" + "\n";     /// BeGin
        s += "en ... end" + "\n";                 /// ENd
        s += "fn ... [so]?fun" + "\n";            /// FuNction
        s += "cn ... [s]?cons" + "\n";            /// CoNstructor
        s += "de ... \\d+" + "\n";                /// DEcimal
        s += "id ... \\w+" + "\n";                /// IDent
        s += "ay ... ." + "\n";                   /// AnY
        return s;
    }

    public Tokenizer init(String conf) throws Exception {
        
        pmap = new LinkedHashMap<String, Pattern>();

        String lines[] = S.toLines(conf);
        for (String l : lines) {
            String[] kv = l.split(" \\.\\.\\. ");
            if (S.isBlank(kv[0])) {
                continue;
            }
            pmap.put(kv[0], Pattern.compile(kv[1]));
        }
        return this;
    }
    
    public List<Token> eval(String ftx) throws IOException {
        
        Effect ef; List<Token> l;
        
        ef = new Effect();
        l = new ArrayList<Token>(1);
        l.add(new Token(ftx, null, 0, 0));
        
        //  to lines
        FuncBin<Token, Effect, List<Token>> tolines;
        tolines = new FuncBin<Token, Effect, List<Token>>() {
            public List<Token> eval(Token c, Effect ctx) {
                List<Token> l = new ArrayList<Token>();
                Pattern p = Pattern.compile("(\\r\\n|\\r|\\n)");
                Matcher m = p.matcher(c.V);
                int line = -1;
                int prev = 0;
                while (m.find()) {
                    ++line;
                    l.add(new Token(c.V.substring(prev, m.end()), null, line, 0));
                    prev = m.end();
                }
                if (prev < c.V.length()) {
                    ++line;
                    l.add(new Token(c.V.substring(prev, c.V.length()) + "\n", null, line, 0));

                }
                return l;
            }
        };

//        //  chop to tokens
//        FuncBin<Token, Effect, List<Token>> chop;
//        final Pattern chopptn = Pattern.compile(choprgx);
        
//        chop = new FuncBin<Token, Effect, List<Token>>() {
//            public List<Token> eval(Token c, Effect ctx) {
//                List<Token> l = new ArrayList<Token>();
//                Matcher m = chopptn.matcher(c.V);
//                while(m.find()) {
//                    Token t = new Token(m.group(), null, c.Line, m.start());
//                    l.add(t);
//                }
//                return l;
//            }
//        };

//        chop = new FuncBin<Token, Effect, List<Token>>() {
//            public List<Token> eval(Token c, Effect ctx) {
//                List<Token> l = new ArrayList<Token>();
//                Matcher m;
//                int start = 0;
//                int end = c.V.length();
//                boolean found;
//                while (start < end) {
//                    found = false;
//                    for (String k : pmap.keySet()) {
//                        m = pmap.get(k).matcher(c.V);
//                        if(m.find(start) && m.start() == start) {
//                            Token t = new Token(m.group(), k, c.Line, m.start());
//                            l.add(t);
//                            start = m.end();
//                            found = true;
//                            break;
//                        }
//                    }
//                    if (false == found) {
//                        ctx.E = new Exception("could not much any patterns");
//                        break;
//                    }
//                }
//                return l;
//            }
//        };
//        chop = chopFun();

        //  concantinate double quote again
        FuncBin<Token, Effect, List<Token>> concatdq;
        concatdq = new FuncBin<Token, Effect, List<Token>>() {
            public List<Token> eval(Token c, Effect ctx) {
                List<Token> l = new ArrayList<Token>();
                if(StringUtils.equals(c.V, "\"")) {
                    if (false == ctx.S.empty()) {
                        //  end double quote
                        ctx.S.peek().V += c.V;
                        l.add(ctx.S.pop());
                    } else {
                        //  begin double quote
                        c.G = S.STG;
                        ctx.S.push(c);
                    }
                } else {
                    if (false == ctx.S.empty()) {
                        //  in double quote
                        ctx.S.peek().V += c.V;
                    } else {
                        //  not in double quote
                        l.add(c);
                    }
                }
                
                if (ctx.isLast()) {
                    if (false == ctx.S.empty()) {
                        ctx.E = new Exception("unclosed double quote");
                    }
                }
                
                return l;
            }
        };
        
        FuncBin<Token, Effect, List<Token>> trim;
        trim = new FuncBin<Token, Effect, List<Token>>() {
            public List<Token> eval(Token c, Effect ctx) {
                List<Token> l = new ArrayList<Token>();
                if (S.eq("sp", c.G)) { return l; }
                if (S.eq("nl", c.G)) { return l; }
                if (S.eq("sc", c.G)) { return l; }
                l.add(c);
                return l;
            }
        };
        
        try {
            ef
//                    .setTraceOn(true)
                    .init(l)
                    .each(tolines)
                    .each(chop())
//                    .each(chop)
                    .each(concatdq)
                    .each(section())
                    .each(trim)
                    ;
        } catch (Exception ex) {
            Logger.getLogger(Milk.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        

        return ef.L;
    }
    
    public FuncBin<Token, Effect, List<Token>> chop() {
        return new FuncBin<Token, Effect, List<Token>>() {
            public List<Token> eval(Token c, Effect ctx) {
                List<Token> l = new ArrayList<Token>();
                Matcher m;
                int start = 0;
                int end = c.V.length();
                boolean found;
                while (start < end) {
                    found = false;
                    for (String k : pmap.keySet()) {
                        m = pmap.get(k).matcher(c.V);
                        if(m.find(start) && m.start() == start) {
                            Token t = new Token(m.group(), k, c.Line, m.start());
                            l.add(t);
                            start = m.end();
                            found = true;
                            break;
                        }
                    }
                    if (false == found) {
                        ctx.E = new Exception("could not much any patterns");
                        break;
                    }
                }
                return l;
            }
        };
        
    }
    
    /**
     * insert range tokens for section
     * @return binary function 
     */
    public static FuncBin<Token, Effect, List<Token>> section() {
        return new FuncBin<Token, Effect, List<Token>>() {
            
            public List<Token> eval(Token c, Effect ctx) {
                List<Token> l = new ArrayList<Token>();
                Token t;

                Pattern p = Pattern.compile("^#+");
                Matcher m = p.matcher(c.V);

                if (c.Col == 0 && m.find()) {
                    if (false == ctx.S.empty()) {
                        t = ctx.S.pop();
                        int pad = t.length() - c.length();
                        if (0 <= pad) {
                            for (int i = 0; i <= pad; ++i) {
                                l.add(new Token(S.ENV, S.ENG, c.Line, c.Col));
                            }
                        } else {
                            for (int i = pad; i < -1; ++i) {
                                l.add(new Token(S.BGV, S.BGG, c.Line, c.Col));
                            }
                        }
                    } else {
                        // padding begin if first section is not level one
                        int pad = c.length() - 1;
                        for (int i = 0; i < pad; ++i) {
                            l.add(new Token(S.BGV, S.BGG, c.Line, c.Col));
                        }
                    }
                    ctx.S.push(c);
                    ctx.S.push(new Token(S.BGV, S.BGG, c.Line, -1));
                    l.add(c);
                    
                } else if (S.eq(c.G, S.NLG)) {
                    
                    l.add(c);
                    //  put begin region after new line
                    if (false == ctx.S.empty() && S.eq(ctx.S.peek().G, S.BGG)) {
                        t = ctx.S.pop();
                        t.Col = c.Col + c.length();
                        l.add(t);
                    }
                    
                } else {
                    
                    l.add(c);
                    
                }
                
                if (ctx.isLast() && false == ctx.S.empty()) {
                    t = ctx.S.pop();
                    int pad = t.length();
                    for (int i = 0; i < pad; ++i) {
                        l.add(new Token(S.ENV, S.ENG, c.Line, c.Col));
                    }
                }
                
                return l;
            }
        };
    }
    
}
