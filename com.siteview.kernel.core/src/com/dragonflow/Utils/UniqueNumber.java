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

public class UniqueNumber {

    private static final com.dragonflow.Utils.UniqueNumber INSTANCE = new UniqueNumber();

    protected int nextInt;

    protected UniqueNumber() {
        nextInt = 0;
    }

    public static com.dragonflow.Utils.UniqueNumber getInstance() {
        return INSTANCE;
    }

    public synchronized int getNumber() {
        return ++ nextInt;
    }

}
