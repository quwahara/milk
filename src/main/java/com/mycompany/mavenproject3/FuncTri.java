/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author mitsuaki
 */
public interface FuncTri<T1, T2, T3, TR> {
    TR eval(T1 v1, T2 v2, T3 v3);
}
