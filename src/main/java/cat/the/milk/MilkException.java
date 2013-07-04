/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

/**
 *
 * @author mitsuaki
 */
public class MilkException extends Exception {

    /**
     * Creates a new instance of
     * <code>MilkException</code> without detail message.
     */
    public MilkException() {
    }

    /**
     * Constructs an instance of
     * <code>MilkException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public MilkException(String msg) {
        super(msg);
    }
}
