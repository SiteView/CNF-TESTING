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
import java.util.Vector;

import COM.dragonflow.Api.APIReport;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.XmlApi:
// XmlApiResponse

public class XmlApiReport {

    private COM.dragonflow.Api.APIReport api;

    public XmlApiReport() {
        api = null;
        api = new APIReport();
    }

    public java.lang.Object add(Array array, Array array1, Array array2, String s) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            int i = 0;
            Object obj = null;
            Enumeration enumeration = (Enumeration) array.iterator();
            java.util.Vector vector = new Vector();
            while (enumeration.hasMoreElements()) {
                String s1 = (String) enumeration.nextElement();
                String s2 = (String) array1.get(i);
                HashMap hashmap = (HashMap) array2.get(i);
                api.create(s1, s2, hashmap);
                vector.add(hashmap);
                i ++;
            }
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object update(Array array, Array array1, Array array2, Array array3, Array array4) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            int i = 0;
            Object obj = null;
            Enumeration enumeration = (Enumeration) array.iterator();
            java.util.Vector vector = new Vector();
            while (enumeration.hasMoreElements()) {
                String s = (String) enumeration.nextElement();
                String s1 = (String) array1.get(i);
                HashMap hashmap = (HashMap) array2.get(i);
                api.update(s, s1, hashmap);
                vector.add(hashmap);
                i ++;
            }
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object delete(Array array, Array array1, Array array2, Array array3) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            int i = 0;
            Enumeration enumeration = (Enumeration) array.iterator();
            java.util.Vector vector = new Vector();
            while (enumeration.hasMoreElements()) {
                String s = (String) enumeration.nextElement();
                String s1 = (String) array1.get(i);
                api.delete("", s1, COM.dragonflow.Utils.TextUtils.toInt(s));
                String as[] = new String[2];
                as[0] = s;
                as[1] = s1;
                vector.add(as);
                i ++;
            }
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getClassPropertyScalars(String s, String s1, HashMap hashmap) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = api.getTemplateList();
            String as[] = new String[vector.size()];
            String as1[] = new String[vector.size()];
            for (int i = 0; i < vector.size(); i ++) {
                String as2[] = (String[]) vector.get(i);
                as[i] = as2[0];
                as1[i] = as2[1];
            }

            java.util.Vector vector1 = new Vector();
            vector1.add(s);
            vector1.add(as);
            vector1.add(as1);
            xmlapiresponse.setReturnVector(vector1);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getInstances(String s, String s1, String s2, String s3, java.lang.Integer integer) {
        COM.dragonflow.XmlApi.XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = api.getInstances(s1, s, integer.intValue());
            xmlapiresponse.setReturnVector(vector);
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }
}
