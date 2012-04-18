/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Page;

import com.recursionsw.jgl.Array;

public interface treeInterface {

    public abstract boolean process(boolean flag, boolean flag1,
            StringBuffer stringbuffer);

    public abstract Array selected();
}
