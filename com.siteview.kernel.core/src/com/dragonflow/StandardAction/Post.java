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

import java.util.Vector;

import com.dragonflow.Properties.BooleanProperty;
import com.dragonflow.Properties.ScalarProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.StandardMonitor.CPUMonitor;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

public class Post extends com.dragonflow.SiteView.Action
{

    public static com.dragonflow.Properties.StringProperty pAction;
    public static com.dragonflow.Properties.StringProperty pProxy;
    public static com.dragonflow.Properties.StringProperty pChallengeResponse;
    public static com.dragonflow.Properties.StringProperty pUsername;
    public static com.dragonflow.Properties.StringProperty pPassword;
    public static com.dragonflow.Properties.StringProperty pTemplate;
    public static com.dragonflow.Properties.StringProperty pProxyUserName;
    public static com.dragonflow.Properties.StringProperty pProxyPassword;
    private static com.dragonflow.Properties.StringProperty myProperties[];

    public void initializeFromArguments(Array array, HashMap hashmap)
    {
        setProperty(pTemplate, "Default");
        String s = com.dragonflow.HTTP.HTTPRequest.decodeString((String)array.get(0));
        setProperty(myProperties[0], s);
        if(array.size() > 1)
        {
            setProperty(pTemplate, (String)array.get(1));
        }
        if(array.size() > 2)
        {
            String s1 = (String)array.get(2);
            if(s1.startsWith(com.dragonflow.StandardMonitor.URLMonitor.NT_CHALLENGE_RESPONSE_TAG))
            {
                setProperty(pChallengeResponse, "true");
                s1 = s1.substring(com.dragonflow.StandardMonitor.URLMonitor.NT_CHALLENGE_RESPONSE_TAG.length());
            }
            setProperty(pUsername, s1);
        }
        if(array.size() > 3)
        {
            setProperty(pPassword, (String)array.get(3));
        }
        String s2 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_proxy");
        if(s2.length() > 0)
        {
            setProperty(pProxy, s2);
        }
        String s3 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_proxyusername");
        if(s3.length() > 0)
        {
            setProperty(pProxyUserName, s3);
        }
        String s4 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_proxypassword");
        if(s4.length() > 0)
        {
            setProperty(pProxyPassword, s4);
        }
    }

    public String getActionString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("Post");
        String s = getProperty(pAction);
        if(s != null && s.length() > 0)
        {
            stringbuffer.append(" ");
            if(s.indexOf("://") < 0)
            {
                s = "http://" + s;
            }
            stringbuffer.append(java.net.URLEncoder.encode(s));
            stringbuffer.append(" ");
            stringbuffer.append(getProperty(pTemplate));
            String s1 = getProperty(pUsername);
            if(s1.length() > 0)
            {
                if(getProperty(pChallengeResponse).length() > 0)
                {
                    s1 = com.dragonflow.StandardMonitor.URLMonitor.NT_CHALLENGE_RESPONSE_TAG + s1;
                }
                stringbuffer.append(" ");
                stringbuffer.append(s1);
                stringbuffer.append(" ");
                stringbuffer.append(getProperty(pPassword));
            }
            if(getProperty(pProxy).length() > 0)
            {
                stringbuffer.append(" _proxy=");
                stringbuffer.append(getProperty(pProxy));
            }
            if(getProperty(pProxyUserName).length() > 0)
            {
                stringbuffer.append(" _proxyusername=");
                stringbuffer.append(getProperty(pProxyUserName));
            }
            if(getProperty(pProxyPassword).length() > 0)
            {
                stringbuffer.append(" _proxypassword=");
                stringbuffer.append(getProperty(pProxyPassword));
            }
        }
        return stringbuffer.toString();
    }

    public String getActionDescription()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append((String)getClassProperty("label"));
        String s = getProperty(pTemplate);
        if(s.length() > 0 && !s.equals("Default"))
        {
            stringbuffer.append(" " + s);
        }
        if(getProperty(pAction).length() > 0)
        {
            stringbuffer.append(" ");
            stringbuffer.append(getProperty(pAction));
        }
        return stringbuffer.toString();
    }

    public boolean defaultsAreSet(HashMap hashmap)
    {
        return true;
    }

    public String verify(com.dragonflow.Properties.StringProperty stringproperty, String s, com.dragonflow.HTTP.HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pAction)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        }
        if(stringproperty == pProxy)
        {
            if(com.dragonflow.Utils.TextUtils.hasSpaces(s))
            {
                hashmap.put(stringproperty, "no spaces are allowed");
            } else
            if(s.length() > 0)
            {
                int i = s.indexOf(':');
                int j = -1;
                if(i != -1)
                {
                    j = com.dragonflow.Utils.TextUtils.readInteger(s, i + 1);
                }
                if(j == -1)
                {
                    hashmap.put(stringproperty, "missing port number in Proxy address");
                }
            }
            return s;
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public java.util.Vector getScalarValues(com.dragonflow.Properties.ScalarProperty scalarproperty, com.dragonflow.HTTP.HTTPRequest httprequest, com.dragonflow.Page.CGI cgi)
        throws com.dragonflow.SiteViewException.SiteViewException
    {
        if(scalarproperty == pTemplate)
        {
            java.util.Vector vector = com.dragonflow.SiteView.SiteViewGroup.getTemplateList("templates.post", httprequest);
            java.util.Vector vector1 = new Vector();
            for(int i = 0; i < vector.size(); i++)
            {
                vector1.addElement(vector.elementAt(i));
                vector1.addElement(vector.elementAt(i));
            }

            return vector1;
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public Post()
    {
        runType = 3;
    }

    public boolean execute()
    {
        boolean flag = false;
        String s = "unknown";
        String s1 = "";
        boolean flag1 = false;
        int j = 0;
        int k = 0;
        String s2 = "";
        StringBuffer stringbuffer = new StringBuffer();
        Array array = new Array();
        maxRuns = getSettingAsLong("_postAttempts", 4);
        attemptDelay = getSettingAsLong("_postAttemptDelay", 120) * 1000;
        long l = getSettingAsLong("_urlContentMatchMax", 50000);
        String s3 = getSetting("_postNewlineReplacement");
        if(args.length > 0)
        {
            s2 = args[0];
        }
        if(s2.length() == 0)
        {
            s = "missing CGI URL";
        } else
        {
            s2 = com.dragonflow.HTTP.HTTPRequest.decodeString(s2);
            String s4 = "Default";
            if(args.length > 1)
            {
                s4 = args[1];
            }
            String s6 = "";
            if(args.length > 2)
            {
                s6 = args[2];
            }
            String s7 = "";
            if(args.length > 3)
            {
                s7 = args[3];
            }
            try
            {
                String s9;
                s9 = s9 = com.dragonflow.SiteView.Platform.getUsedDirectoryPath("templates.post", monitor.getProperty(com.dragonflow.SiteView.Monitor.pGroupID)) + java.io.File.separator + s4;
                String s11 = com.dragonflow.Utils.FileUtils.readFile(s9).toString();
                Array array1 = com.dragonflow.SiteView.Platform.split('\n', s11);
                for(int j1 = 0; j1 < array1.size(); j1++)
                {
                    String s17 = (String)array1.get(j1);
                    int l1 = s17.indexOf("=");
                    if(l1 >= 0)
                    {
                        String s19 = s17.substring(0, l1);
                        String s21 = s17.substring(l1 + 1);
                        s21 = com.dragonflow.Utils.TextUtils.replaceString(s21, "\\n", "\n");
                        s19 = monitor.createFromTemplate(s19).trim();
                        s21 = monitor.createFromTemplate(s21).trim();
                        if(s3.length() > 0)
                        {
                            s21 = com.dragonflow.Utils.TextUtils.replaceString(s21, "\n", s3);
                        }
                        s17 = s19 + "=" + s21;
                    }
                    array.add(s17);
                }

                s = "";
            }
            catch(java.io.FileNotFoundException filenotfoundexception)
            {
                s = "missing template: " + s4;
            }
            catch(java.io.IOException ioexception)
            {
                s = "error reading template: " + s4;
            }
            if(s.length() == 0)
            {
                String s12 = "";
                String s14 = "";
                String s16 = "";
                int k1 = 0xf4240;
                String s18 = getProperty(pProxy);
                String s20 = getProperty(pProxyUserName);
                String s22 = getProperty(pProxyPassword);
                String s23 = "";
                com.dragonflow.Utils.SocketSession socketsession = com.dragonflow.Utils.SocketSession.getSession(null);
                long al[] = com.dragonflow.StandardMonitor.URLMonitor.checkURL(socketsession, s2, s12, s14, s18, s20, s22, array, s6, s7, s16, stringbuffer, l, s23, 0, k1, null);
                socketsession.close();
                int i = (int)al[0];
                j = (int)al[1];
                k = (int)al[2];
                flag = i == com.dragonflow.SiteView.Monitor.kURLok;
                s1 = com.dragonflow.SiteView.Monitor.lookupStatus(i);
                s = s1 + ", " + s2;
            }
        }
        String s5 = "";
        if(!flag || triggerCount > 1)
        {
            s5 = " (" + triggerCount + "/" + maxRuns + ")";
        }
        StringBuffer stringbuffer1 = new StringBuffer();
        for(int i1 = 0; i1 < array.size(); i1++)
        {
            stringbuffer1.append(array.get(i1));
            stringbuffer1.append("\n");
        }

        String s8 = " alert-url: " + s2 + com.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-postData: " + stringbuffer1.toString() + com.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-replyStatus: " + s1 + com.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-replySize: " + k + com.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-replyDuration: " + j + com.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-replyContent: " + stringbuffer + com.dragonflow.SiteView.Platform.FILE_NEWLINE;
        String s10 = "Post alert performed";
        if(!flag)
        {
            s10 = "Post alert retry" + s5;
            if(triggerCount >= maxRuns)
            {
                s10 = "POST ALERT NOT PERFORMED" + s5;
                String s13 = getSetting("_autoEmail");
                String s15 = "There was a problem sending a SiteView post alert." + com.dragonflow.SiteView.Platform.FILE_NEWLINE + com.dragonflow.SiteView.Platform.FILE_NEWLINE + s + com.dragonflow.SiteView.Platform.FILE_NEWLINE + com.dragonflow.SiteView.Platform.FILE_NEWLINE + s8 + com.dragonflow.SiteView.Platform.FILE_NEWLINE;
                if(s13.length() != 0)
                {
                    com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                    siteviewgroup.simpleMail(s13, "SiteView " + s10, s15);
                }
            }
        }
        messageBuffer.append(s10 + ", " + s);
        logAlert(baseAlertLogEntry(s10, s, flag) + s8 + com.dragonflow.SiteView.Platform.FILE_NEWLINE);
        return flag;
    }

    public static void main(String args[])
    {
        java.lang.System.out.println("\n-------------------------");
        java.lang.System.out.println("  Testing Post Alerts");
        java.lang.System.out.println("-------------------------\n\n");
        com.dragonflow.Log.LogManager.initialize(com.dragonflow.SiteView.Platform.getRoot(), 0xf4240, 0xf4240, 10, true, false, 0, 0xf4240L);
        com.dragonflow.StandardAction.Post post = new Post();
        post.args = new String[1];
        post.args[0] = java.net.URLEncoder.encode("http://hoohoo.ncsa.uiuc.edu/cgi-bin/post-query");
        post.messageBuffer = new StringBuffer();
        com.dragonflow.StandardMonitor.CPUMonitor cpumonitor = new CPUMonitor();
        com.dragonflow.StandardMonitor.CPUMonitor _tmp = cpumonitor;
        cpumonitor.setProperty(com.dragonflow.SiteView.Monitor.pAlertLogName, "Alert");
        com.dragonflow.StandardMonitor.CPUMonitor _tmp1 = cpumonitor;
        cpumonitor.setProperty(com.dragonflow.SiteView.Monitor.pName, "TestMonitor");
        post.setMonitor(cpumonitor);
        post.setOwner(cpumonitor);
        post.execute();
        java.lang.System.out.println("\n\n-------------------------");
        java.lang.System.out.println("  done testing post alerts");
        java.lang.System.out.println("-------------------------\n");
    }

    static 
    {
        pAction = new StringProperty("_action", "", "");
        pAction.setDisplayText("Action", "the URL of the CGI to receive the form submission (example: http://www." + com.dragonflow.SiteView.Platform.exampleDomain + "/cgi/error_alert.pl)");
        pAction.setParameterOptions(true, 1, false);
        pTemplate = new ScalarProperty("_template", "Default");
        pTemplate.setDisplayText("Template", "the template used to create the form submission.");
        pTemplate.setParameterOptions(true, 2, false);
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("HTTP Proxy", "proxy server to use including port (example: proxy." + com.dragonflow.SiteView.Platform.exampleDomain + ":8080)");
        pProxy.setParameterOptions(true, 3, true);
        pChallengeResponse = new BooleanProperty("_challengeResponse", "");
        pChallengeResponse.setDisplayText("NT Challenge Response", "when selected, use NT Challenge Response authorization");
        pChallengeResponse.setParameterOptions(true, 4, true);
        pUsername = new StringProperty("_username", "", "");
        pUsername.setDisplayText("Authorization User Name", "optional user name if the Action URL requires authorization");
        pUsername.setParameterOptions(true, 5, true);
        pPassword = new StringProperty("_password", "", "");
        pPassword.setDisplayText("Authorization Password", "optional password if the Action URL requires authorization");
        pPassword.setParameterOptions(true, 6, true);
        pPassword.isPassword = true;
        pProxyUserName = new StringProperty("_proxyusername");
        pProxyUserName.setDisplayText("Proxy Server User Name", "optional user name if the proxy server requires authorization");
        pProxyUserName.setParameterOptions(true, 7, true);
        pProxyPassword = new StringProperty("_proxypassword");
        pProxyPassword.setDisplayText("Proxy Server Password", "optional password if the proxy server requires authorization");
        pProxyPassword.setParameterOptions(true, 8, true);
        pProxyPassword.isPassword = true;
        myProperties = new com.dragonflow.Properties.StringProperty[8];
        myProperties[0] = pAction;
        myProperties[1] = pTemplate;
        myProperties[2] = pUsername;
        myProperties[3] = pPassword;
        myProperties[4] = pProxy;
        myProperties[5] = pChallengeResponse;
        myProperties[6] = pProxyUserName;
        myProperties[7] = pProxyPassword;
        com.dragonflow.StandardAction.Post.addProperties("com.dragonflow.StandardAction.Post", myProperties);
        com.dragonflow.StandardAction.Post.setClassProperty("com.dragonflow.StandardAction.Post", "description", "Sends an alert message via submitting a form to a CGI script.");
        com.dragonflow.StandardAction.Post.setClassProperty("com.dragonflow.StandardAction.Post", "help", "AlertPost.htm");
        com.dragonflow.StandardAction.Post.setClassProperty("com.dragonflow.StandardAction.Post", "title", "Post to a Form");
        com.dragonflow.StandardAction.Post.setClassProperty("com.dragonflow.StandardAction.Post", "label", "Post form to");
        com.dragonflow.StandardAction.Post.setClassProperty("com.dragonflow.StandardAction.Post", "name", "Post");
        com.dragonflow.StandardAction.Post.setClassProperty("com.dragonflow.StandardAction.Post", "class", "Post");
        com.dragonflow.StandardAction.Post.setClassProperty("com.dragonflow.StandardAction.Post", "prefs", "");
        com.dragonflow.StandardAction.Post.setClassProperty("com.dragonflow.StandardAction.Post", "loadable", "true");
        com.dragonflow.StandardAction.Post.setClassProperty("com.dragonflow.StandardAction.Post", "classType", "advanced");
    }
}
