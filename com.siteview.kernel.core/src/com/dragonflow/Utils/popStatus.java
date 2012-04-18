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

public class popStatus {

    public boolean _OK;

    String _Response;

    String _Responses[];

    public popStatus() {
        _OK = false;
        _Responses = new String[0];
    }

    public String[] Responses() {
        return _Responses;
    }

    public String Response() {
        return _Response;
    }

    public boolean OK() {
        return _OK;
    }
}
