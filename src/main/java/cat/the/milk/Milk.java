/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.io.IOException;
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
    public Form Fm;
    public Token Formed;
    public Generator Ge;
    
    public Milk init() throws Exception {
        Tz = new Tokenizer();
        Tz.init(Tokenizer.defaultConf());
        Px = new Prefix();
        Px.init(Prefix.defaultConf());
        Ix = new Infix();
        Ix.initFactors(Infix.defaultFactorConf());
        Ix.initFixes(Infix.defaultFixConf());
        Ix.Px = Px;
        Px.Ix = Ix;
        Fm = new Form().init();
        Ge = new Generator().init();
        return this;
    }
    
    public List<Token> Ts;

    public Milk evalToTokens(String source) throws IOException {
        Ts = Tz.eval(source);
        return this;
    }
    
    public Milk evalToTree(String source) throws Exception {
        evalToTokens(source);
        Tree = Px.eval(Ts);
        return this;
    }
    
    public Milk formTree(String source) throws Exception {
        evalToTree(source);
        Formed = Fm.recv(Tree).Fd;
        return this;
    }
    
    public Milk eval(String source) throws Exception {
        Ts = null;
        Tree = null;
        formTree(source);
//        evalToTree(source);
        Ge.recvSrc(Tree);
        return this;
    }
    
    
    
    
}
