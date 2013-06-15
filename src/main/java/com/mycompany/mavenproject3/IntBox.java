/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author mitsuaki
 */
public class IntBox {
    public int V = 0;

    public IntBox inc() {
        ++V;
        return this;
    }
    
    public IntBox dec() {
        --V;
        return this;
    }
    
    @Override
    public String toString() {
        return String.valueOf(V);
    }
    
}
