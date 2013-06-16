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

    public Tokenizer Z;
    public Prefix Px;
    public Infix Ix;
    public Token Tree;
    
    public Milk init() throws Exception {
        Z = new Tokenizer();
        Z.init();
        Px = new Prefix();
        Px.init(Prefix.defaultConf());
        Ix = new Infix();
        Ix.initFactors(Infix.defaultFactorConf());
        Ix.initFixes(Infix.defaultFixConf());
        Ix.Px = Px;
        Px.Ix = Ix;
        return this;
    }

    public Milk eval(String source) throws Exception {
        
        List<Token> ts;
        ts = Z.eval(source);
        Tree = Px.eval(ts);
        
        return this;
    }    
    
    
    
}
