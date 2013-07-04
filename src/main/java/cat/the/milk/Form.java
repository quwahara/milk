/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mitsuaki
 */
public class Form {
    
    
    public Form init() {
        return this;
    }
    
    /** befores */
    public List<Token> Bfs;
    /** postponements */
    public List<Token> Pps;
    /** afters */
    public List<Token> Afs;
    
    /** syntax tree */
    public Token St;
    /** Formed syntax tree */
    public Token Fd;
    
    public static Token IsClass = new Token("class", "#");
    public static Token IsCons = new Token("cons", "#");
    
    public Form recv(Token syntaxTree) throws MilkException {
        
        initContext(syntaxTree);
        
        locateType();
        
        {
            for (Token ty : Fd.subs()) {
                completeType(ty);
            }
        }
        
        
        return this;
    }        
        
    
    public static boolean isType(Token t) {
        return IsClass.matches(t);
    }

    public void initContext(Token syntaxTree) {
        St = syntaxTree;
        Fd = new Token();
    }

    public void locateType() throws MilkException {
        
        initBfsPpsAfs();
        
        Token typeholder = St.query("expr");
        if (null == typeholder) {
            throw new MilkException("type holder token was not found");
        }
        
        Bfs.addAll(typeholder.subs());
        
        for (Token t : Bfs) {
            if (isType(t)) {
                Afs.add(t);
            } else {
                Pps.add(t);
            }
        }
        
        if (0 < Pps.size()) {
            Token n = Pps.get(0);
            throw new MilkException("Can not put the token in the global area. :" + n.V);
        }
        
        Fd.subs().addAll(Afs);
    }
    
    public void completeType(Token ty) throws MilkException {

        Token body = ty.query("%bg/expr");
        
        // varidate constructor existence
        if (null == body) {
            Token n = ty.query("%id");
            throw new MilkException("This type has no body.:" + n.V);
        }

        //  Does it have a constructor(s)?
        {
            boolean hasCons = false;
            for (Token t : body.subs()) {
                hasCons |= IsCons.matches(t);
                if (hasCons) {
                    break;
                }
            }
            if (false == hasCons) {
                // call completeConstructor()
                Token cons = Token.parse("cons:cn[(|)|({)[expr]|(})]");
                body.subs().add(cons);
            }
        }

        
    }
    
    public void locateFunc(Token ty) throws MilkException {
        
        initBfsPpsAfs();
        
        Bfs.addAll(ty.subs());
        
        for (Token t : Bfs) {
            if (isType(t)) {
                Afs.add(t);
            } else {
                Pps.add(t);
            }
        }
        
        if (0 < Pps.size()) {
            Token n = Pps.get(0);
            throw new MilkException("Can not put the token in the global area. :" + n.V);
        }
        
        Fd.subs().addAll(Afs);
    }

    public void initBfsPpsAfs() {
        Bfs = new ArrayList<Token>();
        Pps = new ArrayList<Token>();
        Afs = new ArrayList<Token>();
    }
    
    
    
}
