package COM.dragonflow.XmlApi;


import java.util.Enumeration;
import java.util.Vector;

import jgl.HashMap;
import COM.dragonflow.Api.APIAlert;
import COM.dragonflow.Api.APIGroup;
import COM.dragonflow.Api.APISiteView;
import COM.dragonflow.Api.Alert;
import COM.dragonflow.Api.SSGroupInstance;
import COM.dragonflow.Api.SSInstanceProperty;
import COM.dragonflow.Api.SSPropertyDetails;
import COM.dragonflow.Api.SSStringReturnValue;
import COM.dragonflow.SiteViewException.SiteViewException;

// Referenced classes of package COM.dragonflow.XmlApi:
// XmlApiResponse

public class XmlApiGroup {

    private APIGroup api;

    private APIAlert apiAlert;

    public XmlApiGroup() {
        api = null;
        apiAlert = null;
        api = new APIGroup();
    }

    public java.lang.Object add(jgl.Array array, jgl.Array array1, jgl.Array array2, String s) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            int i = 0;
            String s1 = "";
            String s3 = "";
            Object obj = null;
            Enumeration enumeration = array.elements();
            java.util.Vector vector = new Vector();
            while (enumeration.hasMoreElements()) {
                String s2 = (String) enumeration.nextElement();
                String s4 = (String) array1.at(i);
                jgl.HashMap hashmap = (jgl.HashMap) array2.at(i);
                SSInstanceProperty assinstanceproperty[] = new SSInstanceProperty[hashmap.size()];
                Enumeration enumeration1 = hashmap.keys();
                for (int j = 0; enumeration1.hasMoreElements(); j ++) {
                    String s5 = (String) enumeration1.nextElement();
                    assinstanceproperty[j] = new SSInstanceProperty(s5, hashmap.get(s5));
                }

                SSStringReturnValue ssstringreturnvalue = api.create(s2, s4, assinstanceproperty);
                SSInstanceProperty assinstanceproperty1[] = api.getInstanceProperties(ssstringreturnvalue.getValue(), APISiteView.FILTER_CONFIGURATION_ALL);
                jgl.HashMap hashmap1 = new HashMap();
                for (int k = 0; k < assinstanceproperty1.length; k ++) {
                    hashmap1.put(assinstanceproperty1[k].getName(), assinstanceproperty1[k].getValue());
                }

                hashmap1.put("_id", ssstringreturnvalue.getValue());
                vector.add(hashmap1);
                i ++;
            }
            xmlapiresponse.setReturnVector(vector);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object update(jgl.Array array, jgl.Array array1, jgl.Array array2, jgl.Array array3, jgl.Array array4) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            int i = 0;
            String s = "";
            Object obj = null;
            java.util.Vector vector = new Vector();
            jgl.HashMap hashmap1;
            for (Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); vector.add(hashmap1)) {
                String s1 = (String) enumeration.nextElement();
                jgl.HashMap hashmap = (jgl.HashMap) array2.at(i);
                if (hashmap.get("_id") != null) {
                    hashmap.remove("_id");
                }
                SSInstanceProperty assinstanceproperty[] = new SSInstanceProperty[hashmap.size()];
                Enumeration enumeration1 = hashmap.keys();
                for (int j = 0; enumeration1.hasMoreElements(); j ++) {
                    String s2 = (String) enumeration1.nextElement();
                    assinstanceproperty[j] = new SSInstanceProperty(s2, hashmap.get(s2));
                }

                api.update(s1, assinstanceproperty);
                SSInstanceProperty assinstanceproperty1[] = api.getInstanceProperties(s1, APISiteView.FILTER_CONFIGURATION_ALL);
                hashmap1 = new HashMap();
                for (int k = 0; k < assinstanceproperty1.length; k ++) {
                    hashmap1.put(assinstanceproperty1[k].getName(), assinstanceproperty1[k].getValue());
                }

                hashmap1.put("_id", s1);
            }

            xmlapiresponse.setReturnVector(vector);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object delete(jgl.Array array, jgl.Array array1, jgl.Array array2, jgl.Array array3) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            String s = "";
            java.util.Vector vector = new Vector();
            String as[];
            for (Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); vector.add(as)) {
                String s1 = (String) enumeration.nextElement();
                api.delete(s1);
                as = new String[2];
                as[0] = s1;
            }

            xmlapiresponse.setReturnVector(vector);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object move(jgl.Array array, jgl.Array array1, jgl.Array array2) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            Enumeration enumeration = array.elements();
            int i = 0;
            String s = "";
            String s2 = "";
            java.util.Vector vector = new Vector();
            SSStringReturnValue ssstringreturnvalue;
            for (; enumeration.hasMoreElements(); vector.add(ssstringreturnvalue.getValue())) {
                String s1 = (String) enumeration.nextElement();
                String s3 = (String) array2.at(i);
                ssstringreturnvalue = api.move(s1, s3);
            }

            xmlapiresponse.setReturnVector(vector);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object copy(String s, String s1, String s2) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = new Vector();
            SSStringReturnValue ssstringreturnvalue = api.copy(s, s2);
            SSInstanceProperty assinstanceproperty[] = api.getInstanceProperties(ssstringreturnvalue.getValue(), APISiteView.FILTER_CONFIGURATION_ALL);
            jgl.HashMap hashmap = new HashMap();
            for (int i = 0; i < assinstanceproperty.length; i ++) {
                hashmap.put(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
            }

            hashmap.put("_id", ssstringreturnvalue.getValue());
            vector.add(hashmap);
            xmlapiresponse.setReturnVector(vector);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getClassPropertyDetails(String s, String s1, jgl.HashMap hashmap) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = new Vector();
            SSPropertyDetails asspropertydetails[] = api.getClassPropertiesDetails(APISiteView.FILTER_ALL);
            for (int i = 0; i < asspropertydetails.length; i ++) {
                jgl.HashMap hashmap1 = new HashMap();
                SSPropertyDetails.extractDetailsIntoHashMap(asspropertydetails[i], hashmap1);
                if (s.indexOf(asspropertydetails[i].getName()) != -1) {
                    vector.add(hashmap1);
                }
            }

            xmlapiresponse.setReturnVector(vector);
            xmlapiresponse.setReturnVector(vector);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getClassPropertyScalars(String s, String s1, jgl.HashMap hashmap) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = new Vector();
            SSPropertyDetails sspropertydetails = api.getClassPropertyDetails(s);
            vector.add(sspropertydetails.getName());
            vector.add(sspropertydetails.getSelectionIDs());
            vector.add(sspropertydetails.getSelectionDisplayNames());
            xmlapiresponse.setReturnVector(vector);
            xmlapiresponse.setReturnVector(vector);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getInstancePropertyScalars(String s, String s1, String s2, jgl.HashMap hashmap) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = new Vector();
            SSPropertyDetails sspropertydetails = api.getInstancePropertyScalars(s, s1);
            vector.add(sspropertydetails.getName());
            vector.add(sspropertydetails.getSelectionIDs());
            vector.add(sspropertydetails.getSelectionDisplayNames());
            xmlapiresponse.setReturnVector(vector);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getInstances(String s, String s1, String s2, String s3, java.lang.Integer integer) throws java.lang.Exception {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = new Vector();
            SSGroupInstance assgroupinstance[] = api.getInstances(s, integer.intValue());
            if (apiAlert == null) {
                apiAlert = new APIAlert();
            }
            for (int i = 0; i < assgroupinstance.length; i ++) {
                SSInstanceProperty assinstanceproperty[] = assgroupinstance[i].getInstanceProperties();
                jgl.HashMap hashmap = new HashMap();
                for (int j = 0; j < assinstanceproperty.length; j ++) {
                    hashmap.put(assinstanceproperty[j].getName(), assinstanceproperty[j].getValue());
                }

                String s4 = assgroupinstance[i].getGroupId();
                hashmap.put("_id", s4);
                if (Alert.getInstance().getAlertsResidingInGroupOrMonitor(s, "").size() > 0) {
                    hashmap.put("hasDependencies", "true");
                }
                if (s.length() == 0 || api.hasSubGroupDependencies(s4)) {
                    hashmap.put("hasSubGroupDependencies", "true");
                }
                vector.add(hashmap);
            }

            xmlapiresponse.setReturnVector(vector);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getInstanceProperties(String s, String s1, String s2, String s3, java.lang.Integer integer) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = new Vector();
            SSInstanceProperty assinstanceproperty[] = api.getInstanceProperties(s, integer.intValue());
            jgl.HashMap hashmap = new HashMap();
            for (int i = 0; i < assinstanceproperty.length; i ++) {
                hashmap.put(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
            }

            vector.add(hashmap);
            xmlapiresponse.setReturnVector(vector);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object refreshGroup(String groupid) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            api.refreshGroup(groupid, true);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getInstanceProperty(String s, String groupid, String s2, String s3, Integer filter) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = new Vector();
            SSInstanceProperty assinstanceproperty[] = api.getInstanceProperties(groupid, filter.intValue());
            jgl.HashMap hashmap = new HashMap();
            for (int i = 0; i < assinstanceproperty.length; i ++) {
                if (s.indexOf(assinstanceproperty[i].getName()) != -1) {
                    hashmap.put(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
                }
            }

            vector.add(hashmap);
            xmlapiresponse.setReturnVector(vector);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

    public java.lang.Object getCount(String s) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        try {
            java.util.Vector vector = new Vector();
            java.util.Collection collection = api.getAllGroupInstances();
            java.lang.Long long1 = new Long(collection.size());
            vector.add(long1);
            xmlapiresponse.setReturnVector(vector);
        } catch (SiteViewException siteviewexception) {
            xmlapiresponse.setErrorResponse(siteviewexception);
        }
        return xmlapiresponse;
    }

//    public java.lang.Object getTopazId(String s, String s1) {
//        XmlApiResponse xmlapiresponse = new XmlApiResponse();
//        try {
//            java.util.Vector vector = new Vector();
//            SSStringReturnValue ssstringreturnvalue = api.getTopazID(s);
//            vector.add(ssstringreturnvalue.getValue());
//            xmlapiresponse.setReturnVector(vector);
//        } catch (SiteViewException siteviewexception) {
//            xmlapiresponse.setErrorResponse(siteviewexception);
//        }
//        return xmlapiresponse;
//    }

    public java.lang.Object listObjects(String s) {
        XmlApiResponse xmlapiresponse = new XmlApiResponse();
        java.util.Vector vector = new Vector();
        String as[] = null;
        as = new String[3];
        as[0] = "Group";
        as[1] = "yes";
        as[2] = "yes";
        vector.add(as);
        xmlapiresponse.setReturnVector(vector);
        return xmlapiresponse;
    }
}
