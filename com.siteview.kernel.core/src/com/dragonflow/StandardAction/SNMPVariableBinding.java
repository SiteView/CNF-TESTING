/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.StandardAction;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

class SNMPVariableBinding {

    String objectID;

    String type;

    String value;

    SNMPVariableBinding(String s, String s1, String s2) {
        objectID = s;
        type = s1;
        value = s2;
    }
}
