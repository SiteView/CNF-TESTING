/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.XmlApi;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import COM.dragonflow.Api.APISiteView;

// Referenced classes of package COM.dragonflow.XmlApi:
// XmlApiResponse

public class XmlApiSiteView {

    private COM.dragonflow.Api.APISiteView api;

    public XmlApiSiteView() {
        api = null;
        api = new APISiteView();
    }

    public java.lang.Object enableRealTimeStatusInformation(String s, String s1, String s2, String s3, String s4, String s5) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.enableRealTimeStatusInformation(s, java.lang.Integer.parseInt(s1), s2, java.lang.Integer.parseInt(s3), java.lang.Long.parseLong(s4), s5);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object disableRealTimeStatusInformation(String s, String s1) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.disableRealTimeStatusInformation(s, java.lang.Integer.parseInt(s1));
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object controlSiteView(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.controlSiteView(s);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object releaseSiteView(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.releaseSiteView();
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getSiteViewInfo(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = api.getSiteViewInfo(s);
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getWebServers(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = api.getWebServers(s);
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getOSs(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = api.getOSs();
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getWebServiceFiles(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = api.getWebServiceFiles();
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getWebServiceMethodsAndURL(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = api.getWebServiceMethodsAndURL(s);
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getWebServiceArgs(String s, String s1) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = api.getWebServiceArgs(s, s1);
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object shutdownSiteView(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.shutdownSiteView();
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object createSession(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.createSession((new Long(s)).longValue());
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object sendHeartbeat(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.sendHeartbeat();
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object updateGeneralLicense(String s, String s1) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.updateGeneralLicense(s, (new Boolean(s1)).booleanValue());
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object updateSpecialLicense(String s, String s1) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.updateSpecialLicense(s, (new Boolean(s1)).booleanValue());
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object isTopazDisabled(String s, String s1) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
//        try {
//            java.util.Vector vector = new Vector();
////            boolean flag = api.isTopazDisabled(s);
//            vector.add(new Boolean(flag));
//            xmlapiresponse.setReturnVector(vector);
//        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
//            xmlapiresponse.setErrorResponse(siteviewexception);
//        }
//        return xmlapiresponse;
		return null;
    }

    public java.lang.Object setTopazDisabled(String s, String s1) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.setTopazDisabled(s, (new Boolean(s1)).booleanValue());
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object isTopazConnected(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = new Vector();
            boolean flag = api.isTopazConnected();
            vector.add(new Boolean(flag));
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object isUIControled(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = new Vector();
            boolean flag = api.isUIControled();
            vector.add(new Boolean(flag));
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object isServerRegistered(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = new Vector();
            boolean flag = api.isServerRegistered(s);
            vector.add(new Boolean(flag));
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getFreeProfiles(jgl.HashMap hashmap) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.HashMap hashmap1 = new HashMap();
            String s;
            String s1;
            for (Enumeration enumeration = hashmap.keys(); enumeration.hasMoreElements(); hashmap1.put(s, s1)) {
                s = (String) enumeration.nextElement();
                s1 = (String) hashmap.get(s);
            }

            api.getFreeProfiles(hashmap1);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object flushTopazData(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.flushTopazData();
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object resyncTopazData(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.resyncTopazData((new Boolean(s)).booleanValue());
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object resetTopazProfile(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
//        try {
//            api.resetTopazProfile(s);
//        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
//            xmlapiresponse.setErrorResponse(siteviewexception);
//        }
//        return xmlapiresponse;
		return null;
    }

    public java.lang.Object deleteSiteView(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.deleteSiteView(s);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getServerSettingsByEntity(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
//        try {
//            java.util.Vector vector = new Vector();
//            java.util.HashMap hashmap = api.getServerSettingsByEntity(s);
//            if (hashmap != null) {
//                jgl.HashMap hashmap1 = new jgl.HashMap();
//                java.util.Set set = hashmap.keySet();
//                String s1;
//                for (java.util.Iterator iterator = set.iterator(); iterator.hasNext(); hashmap1.put(s1, hashmap.get(s1))) {
//                    s1 = (String) iterator.next();
//                }
//
//                vector.add(hashmap1);
//            }
//            xmlapiresponse.setReturnVector(vector);
//        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
//            xmlapiresponse.setErrorResponse(siteviewexception);
//        }
//        return xmlapiresponse;
		return null;
    }

    public java.lang.Object getTopazServerSettings(String s, String s1) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
//        try {
//            java.util.Vector vector = new Vector();
//            java.util.HashMap hashmap = api.getTopazServerSettings(s);
//            if (hashmap != null) {
//                jgl.HashMap hashmap1 = new jgl.HashMap();
//                java.util.Set set = hashmap.keySet();
//                String s2;
//                for (java.util.Iterator iterator = set.iterator(); iterator.hasNext(); hashmap1.put(s2, hashmap.get(s2))) {
//                    s2 = (String) iterator.next();
//                }
//
//                vector.add(hashmap1);
//            }
//            xmlapiresponse.setReturnVector(vector);
//        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
//            xmlapiresponse.setErrorResponse(siteviewexception);
//        }
//        return xmlapiresponse;
      return null;
    }

    public java.lang.Object registerTopazProfile(String s, String s1, jgl.HashMap hashmap) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
//        try {
//            java.util.HashMap hashmap1 = new HashMap();
//            String s2;
//            String s3;
//            for (Enumeration enumeration = hashmap.keys(); enumeration.hasMoreElements(); hashmap1.put(s2, s3)) {
//                s2 = (String) enumeration.nextElement();
//                s3 = (String) hashmap.get(s2);
//            }
//
////            api.registerTopazProfile(s, s1, hashmap1);
//        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
//            xmlapiresponse.setErrorResponse(siteviewexception);
//        }
//        return xmlapiresponse;
		return null;
    }

    public java.lang.Object reRegisterTopazProfile(jgl.HashMap hashmap) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
//        try {
//            java.util.HashMap hashmap1 = new HashMap();
//            String s;
//            String s1;
//            for (Enumeration enumeration = hashmap.keys(); enumeration.hasMoreElements(); hashmap1.put(s, s1)) {
//                s = (String) enumeration.nextElement();
//                s1 = (String) hashmap.get(s);
//            }
//
//            api.reRegisterTopazProfile(hashmap1);
//        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
//            xmlapiresponse.setErrorResponse(siteviewexception);
//        }
//        return xmlapiresponse;
		return null;
    }
	

    public java.lang.Object getTopazFullId(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
//        try {
//            java.util.Vector vector = new Vector();
//            java.lang.Integer integer = new Integer(s);
//            String s1 = api.getTopazFullId(integer.intValue());
//            if (s1 != null && s1.length() > 0) {
//                vector.add(s1);
//            }
//            xmlapiresponse.setReturnVector(vector);
//        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
//            xmlapiresponse.setErrorResponse(siteviewexception);
//        }
//        return xmlapiresponse;
		return null;
    }

    public java.lang.Object refreshCache(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            COM.dragonflow.Api.APISiteView.forceConfigurationRefresh();
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

//    public java.lang.Object issueSiebelCmd(String s) {
//        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
//        try {
////            java.util.Vector vector = api.issueSiebelCmd(s);
//            xmlapiresponse.setReturnVector(vector);
//        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
//            xmlapiresponse.setErrorResponse(siteviewexception);
//        }
//        return xmlapiresponse;
//    }

    public java.lang.Object getCurrentApiVersion(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        COM.dragonflow.Api.APISiteView _tmp = api;
        double d = COM.dragonflow.Api.APISiteView.getCurrentApiVersion();
        String s1 = (new Double(d)).toString();
        java.util.Vector vector = new Vector();
        vector.add(s1);
        xmlapiresponse.setReturnVector(vector);
        return xmlapiresponse;
    }

    public java.lang.Object getSystemTime(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = api.getSystemTime(s);
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param s1
     * @return
     */
    public java.lang.Object getFileList(String s, String s1) {

        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector;
            StringBuffer stringbuffer = new StringBuffer();
            vector = api.getFileList(s, s1, stringbuffer);
            if (stringbuffer.length() != 0) {
                xmlapiresponse.setError(stringbuffer.toString());
                return xmlapiresponse;
            }
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object hasSolutionLicense(String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = new Vector();
            boolean flag = api.hasSolutionLicense(s);
            vector.add(new Boolean(flag));
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }
}
