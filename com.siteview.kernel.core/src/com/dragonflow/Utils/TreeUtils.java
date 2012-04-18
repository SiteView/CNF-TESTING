/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.TreeSet;

public class TreeUtils {

    java.util.TreeSet tree;

    public TreeUtils() {
        tree = null;
        tree = new TreeSet();
    }

    public boolean add(java.lang.Object obj) {
        return tree.add(obj);
    }

    public void clear() {
        tree.clear();
    }

    public java.lang.Object clone() {
        return tree.clone();
    }

    public boolean contains(java.lang.Object obj) {
        return tree.contains(obj);
    }

    public java.lang.Object first() {
        return tree.first();
    }

    public java.lang.Object last() {
        return tree.last();
    }

    public boolean isEmpty() {
        return tree.isEmpty();
    }

    public java.util.Iterator iterator() {
        return tree.iterator();
    }

    public java.lang.Object[] getTreeObjectArray() {
        java.lang.Object aobj[] = new java.lang.Object[tree.size()];
        int i = 0;
        for (java.util.Iterator iterator1 = tree.iterator(); iterator1.hasNext();) {
            aobj[i ++] = iterator1.next();
        }

        return aobj;
    }

    public boolean remove(java.lang.Object obj) {
        return tree.remove(obj);
    }

    public int size() {
        return tree.size();
    }
}
