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
    public Prefix P;
    public Token Tree;
    
    public Milk init() throws Exception {
        Z = new Tokenizer();
        Z.init();
        P = new Prefix();
        P.init(Prefix.defaultConf());
        return this;
    }

    public Milk eval(String source) throws Exception {
        
        List<Token> ts;
        ts = Z.eval(source);
        Tree = P.eval(ts);
        
        return this;
    }    
    
    
    
}
