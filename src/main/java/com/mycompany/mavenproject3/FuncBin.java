/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author mitsuaki
 */
public interface FuncBin<T1, T2, TR> {
    TR eval(T1 v1, T2 v2);
}
