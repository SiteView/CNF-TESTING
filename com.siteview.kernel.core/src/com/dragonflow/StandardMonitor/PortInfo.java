/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * PortInfo.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>PortInfo</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */


class PortInfo
{

    String port;
    String name;
    String sendString;
    String matchString;
    boolean udp;
    int udpPort;

    PortInfo(String s, String s1, String s2, String s3, boolean flag, int i)
    {
        port = "";
        name = "";
        sendString = "";
        matchString = "";
        udp = false;
        udpPort = 0;
        port = s;
        name = s1;
        sendString = s2;
        matchString = s3;
        udp = flag;
        udpPort = i;
    }
}
