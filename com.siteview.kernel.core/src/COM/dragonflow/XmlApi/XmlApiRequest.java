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

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;

import COM.dragonflow.Api.APISiteView;
import COM.dragonflow.Resource.SiteViewErrorCodes;
import COM.dragonflow.SiteView.DetectConfigurationChange;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.SiteViewException.SiteViewOperationalException;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.XmlApi:
// XmlApiObject, XmlApiResponse

public class XmlApiRequest
{

    private APISiteView api;
    public static final int VERBOSE_LEVEL_MINIMUM = 0;
    public static final int VERBOSE_LEVEL_MEDIUM = 1;
    public static final int VERBOSE_LEVEL_ALL = 2;
    public static final String API_GET_CURRENT_VERSION_OPERATION = "getCurrentApiVersion";
    static java.lang.Object mutex = new Object();
    protected boolean debug;
    XmlApiObject request;
    private static HashMap apiLookup = new HashMap();
    private static java.util.HashSet detachAllowedOperations = null;
    private static java.lang.Object detachAllowedOperationsSync = new Object();
    private static Array packages = null;
    private static Array standardObjects = null;
    private static Array siteViewObjects = null;

    public XmlApiRequest()
    {
        api = null;
        debug = false;
        request = null;
        request = new XmlApiObject("", null);
        api = new APISiteView();
    }

    public void doRequests()
    {
        String s = request.getName();
        XmlApiObject xmlapiobject;
        for(Enumeration enumeration = (Enumeration) request.iterator(); enumeration.hasMoreElements(); processRequest(s, xmlapiobject))
        {
            xmlapiobject = (XmlApiObject)enumeration.nextElement();
        }

        try
        {
            DetectConfigurationChange detectconfigurationchange = DetectConfigurationChange.getInstance();
            if(detectconfigurationchange.isConfigChanged())
            {
                APISiteView _tmp = api;
                APISiteView.forceConfigurationRefresh();
            }
        }
        catch(SiteViewException siteviewexception)
        {
            COM.dragonflow.Log.LogManager.log("error", "Unable to refresh SiteView's configuration");
        }
    }

    public void processRequest(String s, XmlApiObject xmlapiobject)
    {
        String operation = xmlapiobject.getName();
        if(debug && (operation == null || operation.length() == 0))
        {
            java.lang.System.out.println("documentName=" + s + ", operation=" + operation);
            java.lang.System.out.println(xmlapiobject.toString());
        }
        String apiObj = "" + s;
        java.lang.Object args[] = new java.lang.Object[1];
        args[0] = xmlapiobject;
        try
        {
            String controllingHost = xmlapiobject.getDocumentProperty("controllingHost");
//            COM.dragonflow.TopazIntegration.MAManager.validateControllingHost(s3, XmlApiRequest.isDetachAllowedOperation(s1));
            String version = Platform.getVersion();
            double apiVersion = APISiteView.getCurrentApiVersion();
            String s5 = xmlapiobject.getDocumentProperty("version");
            double d1 = 0.0D;
            try
            {
                d1 = (new Double(s5)).doubleValue();
            }
            catch(java.lang.Exception exception1)
            {
                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_UNSUPPORTED_VERSION, new String[] {
                    version, "Error - client API version \"" + s5 + "\" is not a valid double"
                });
            }
            if(!operation.equals("getCurrentApiVersion") && apiVersion != d1)
            {
                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_UNSUPPORTED_VERSION, new String[] {
                    version, s5
                });
            }
            if(SiteViewGroup.currentSiteView().hasCircularGroups())
            {
                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_CIRCULAR_GROUPS);
            }
        }
        catch(SiteViewOperationalException siteviewoperationalexception)
        {
            COM.dragonflow.Log.LogManager.log("error", siteviewoperationalexception.getMessage());
            xmlapiobject.setProperty("error", siteviewoperationalexception.getMessage(), false);
            xmlapiobject.setProperty("errortype", "operational", false);
            xmlapiobject.setProperty("errorcode", String.valueOf(siteviewoperationalexception.getErrorCode()), false);
            return;
        }
        Object obj = invokeMethod(apiObj, operation, args);
        if(obj instanceof java.lang.Exception)
        {
            java.lang.Exception exception = (java.lang.Exception)obj;
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append("Exception in APIRequest.processRequest()  " + exception.toString());
            stringbuffer.append(",  " + apiObj + "." + operation + "(");
            for(int i = 0; i < args.length; i++)
            {
                stringbuffer.append((i <= 0 ? "" : ", ") + args[i].getClass().getName());
            }

            stringbuffer.append(")");
            java.lang.reflect.Method amethod[] = XmlApiRequest.listAvailableMethods(apiObj, args);
            for(int j = 0; j < amethod.length; j++)
            {
                stringbuffer.append(",  " + amethod[j].getName() + "(");
                java.lang.Class aclass[] = amethod[j].getParameterTypes();
                for(int k = 0; k < aclass.length; k++)
                {
                    stringbuffer.append((k <= 0 ? "" : ", ") + aclass[k].getName());
                }

                stringbuffer.append(")");
            }

            COM.dragonflow.Log.LogManager.log("error", stringbuffer.toString());
            java.lang.System.out.println(stringbuffer.toString());
            xmlapiobject.setProperty("error", exception.toString(), false);
        }
    }

    private static boolean isDetachAllowedOperation(String s)
    {
        if(detachAllowedOperations == null)
        {
            synchronized(detachAllowedOperationsSync)
            {
                if(detachAllowedOperations == null)
                {
                    detachAllowedOperations = new HashSet();
                    detachAllowedOperations.add("controlSiteView");
                    detachAllowedOperations.add("getSiteViewInfo");
                    detachAllowedOperations.add("isServerRegistered");
                    detachAllowedOperations.add("isUIControled");
                }
            }
        }
        return detachAllowedOperations.contains(s);
    }

    public String toString()
    {
        return request.toString();
    }

    public java.lang.Object getRequest()
    {
        return request;
    }

    public java.lang.Object getResponse()
    {
        return request;
    }

    public java.lang.Object getAPIRequest()
    {
        return request;
    }

    public void setAPIRequest(XmlApiObject xmlapiobject)
    {
        request = xmlapiobject;
    }

    public static Object invokeMethod(String api, String operation, java.lang.Object aobj[])
    {
        java.lang.Object obj = null;
        Object obj1 = null;
        Object obj3 = null;
        Object obj4 = null;
        try
        {
            java.lang.Object apiObj = apiLookup.get(api);
            if(apiObj == null)
            {
                synchronized(mutex)
                {
                    apiObj = apiLookup.get(api);
                    if(apiObj == null)
                    {
                        java.lang.Class apiClass = java.lang.Class.forName(api);
                        apiObj = apiClass.newInstance();
                        apiLookup.put(api, apiObj);
                    }
                }
            }
            java.lang.Class aclass[] = new java.lang.Class[aobj.length];
            for(int i = 0; i < aobj.length; i++)
            {
                aclass[i] = aobj[i].getClass();
            }

            java.lang.reflect.Method method = apiObj.getClass().getMethod(operation, aclass);
            obj = method.invoke(apiObj, aobj);
        }
        catch(java.lang.Exception exception)
        {
            XmlApiResponse xmlapiresponse = new XmlApiResponse();
            SiteViewOperationalException siteviewoperationalexception = new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_EXCEPTION, new String[] {
                api, operation
            }, 0L, exception.getMessage());
            xmlapiresponse.setErrorResponse(siteviewoperationalexception);
            obj = xmlapiresponse;
        }
        return obj;
    }

    private static boolean parmsMatch(java.lang.Class aclass[], java.lang.Object aobj[])
    {
        if(aclass.length != aobj.length)
        {
            return false;
        }
        for(int i = 0; i < aclass.length; i++)
        {
            if(aclass[i] != aobj[i].getClass())
            {
                return false;
            }
        }

        return true;
    }

    public static java.lang.reflect.Method[] listAvailableMethods(String s, java.lang.Object aobj[])
    {
        Array array = new Array();
        try
        {
            java.lang.Class class1 = java.lang.Class.forName(s);
            java.lang.reflect.Method amethod1[] = class1.getMethods();
            for(int j = 0; j < amethod1.length; j++)
            {
                java.lang.Class aclass[] = amethod1[j].getParameterTypes();
                if(aobj == null || XmlApiRequest.parmsMatch(aclass, aobj))
                {
                    array.add(amethod1[j]);
                }
            }

        }
        catch(java.lang.Exception exception) { }
        java.lang.reflect.Method amethod[] = new java.lang.reflect.Method[array.size()];
        for(int i = 0; i < array.size(); i++)
        {
            amethod[i] = (java.lang.reflect.Method)array.get(i);
        }

        return amethod;
    }

    public static Array getPackages()
    {
        if(packages == null)
        {
            packages = new Array();
            File file = new File(Platform.getRoot() + "/classes/COM/dragonflow");
            String as[] = file.list();
            for(int i = 0; i < as.length; i++)
            {
                File file1 = new File(Platform.getRoot() + "/classes/COM/dragonflow/" + as[i]);
                if(file1.isDirectory())
                {
                    packages.add(as[i]);
                }
            }

        }
        return packages;
    }

    public static Array getStandardObjects()
    {
        if(standardObjects == null)
        {
            standardObjects = new Array();
            try
            {
                File file = new File(Platform.getRoot() + "/classes/COM/dragonflow");
                String as[] = file.list();
                for(int i = 0; i < as.length; i++)
                {
                    if(as[i].startsWith("Standard"))
                    {
                        String as1[] = new String[3];
                        as1[0] = as[i].substring(8);
                        as1[1] = "true";
                        as1[2] = "";
                        standardObjects.add(as1);
                    }
                }

            }
            catch(java.lang.Exception exception) { }
        }
        return standardObjects;
    }

    public static Array getSiteViewObjects()
    {
        return XmlApiRequest.getStandardObjects();
    }

    public static Array getObjects(String s)
    {
        Array array = new Array();
        try
        {
            java.lang.Class class1 = java.lang.Class.forName("" + s);
            File file = new File(Platform.getRoot() + "/classes/COM/dragonflow/Standard" + s);
            String as[] = file.list();
            for(int i = 0; i < as.length; i++)
            {
                if(as[i].endsWith(".class"))
                {
                    int j = as[i].lastIndexOf(".class");
                    String s1 = as[i].substring(0, j);
                    try
                    {
                        java.lang.Class class2 = java.lang.Class.forName("COM.dragonflow.Standard" + s + "." + s1);
                        if(class2.getSuperclass() == class1)
                        {
                            try
                            {
                                class2.getField("is" + s + "Object");
                                array.add(s1);
                            }
                            catch(java.lang.Exception exception1)
                            {
                                COM.dragonflow.Log.LogManager.log("Error", "XmlApiRequest.getObjects(" + s + ") failed with exception '" + exception1.toString() + "' while adding class '" + as[i] + "' to myObjects array");
                            }
                        }
                    }
                    catch(java.lang.Throwable throwable)
                    {
                        COM.dragonflow.Log.LogManager.log("Error", "XmlApiRequest.getObjects(" + s + ") Standard directory handler failed with exception '" + throwable.toString() + "' while processing file '" + as[i] + "'");
                    }
                }
            }

        }
        catch(java.lang.Exception exception) { }
        return array;
    }

}
