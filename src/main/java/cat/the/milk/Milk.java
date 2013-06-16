/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.util.List;

/**
 *
 * @author mitsuaki
 */
public class Milk {

    public Tokenizer Tz;
    public Prefix Px;
    public Infix Ix;
    public Token Tree;
    public Generator Ge;
    
    public Milk init() throws Exception {
        Tz = new Tokenizer();
        Tz.init();
        Px = new Prefix();
        Px.init(Prefix.defaultConf());
        Ix = new Infix();
        Ix.initFactors(Infix.defaultFactorConf());
        Ix.initFixes(Infix.defaultFixConf());
        Ix.Px = Px;
        Px.Ix = Ix;
        Ge = new Generator();
        return this;
    }

    public Milk eval(String source) throws Exception {
        
        List<Token> ts;
        ts = Tz.eval(source);
        Tree = Px.eval(ts);
        Ge.recvSrc(Tree);
        
        return this;
    }    
    
    
    
}
