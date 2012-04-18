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

import java.util.Vector;

import com.dragonflow.Properties.NumericProperty;
import com.dragonflow.Properties.StringProperty;

// Referenced classes of package com.dragonflow.Utils:
// TextUtils

public class StringPropertyUtil
{
    public static class FIELDS
    {

        static final int UNDEFINED = 0;
        static final int LABEL = 1;
        static final int DESCRIPTION = 2;
        static final int VALUE = 3;
        final int TOTAL_FIELDS_NUM = 3;

        public FIELDS()
        {
        }
    }


    static final int TYPE_UNDEFINED = 0;
    static final int TYPE_STRING = 1;
    static final int TYPE_NUMERIC = 2;
    static final String FIELDS_DELIMITER = "&";
    static final String PROPS_DELIMITER = "?";
    static final String KEY_VAL_DELIMITER = "=";
    static final String DEFAULT_ENCODING = "UTF-8";

    public StringPropertyUtil()
    {
    }

    public static String propsToStream(com.dragonflow.Properties.StringProperty astringproperty[])
    {
        StringBuffer stringbuffer = new StringBuffer();
        try
        {
            for(int i = 0; i < astringproperty.length; i++)
            {
                stringbuffer.append(com.dragonflow.Utils.StringPropertyUtil.saveProperty(astringproperty[i]) + "?");
            }

        }
        catch(java.io.UnsupportedEncodingException unsupportedencodingexception)
        {
            unsupportedencodingexception.printStackTrace();
        }
        return stringbuffer.toString();
    }

    private static String saveProperty(com.dragonflow.Properties.StringProperty stringproperty)
        throws java.io.UnsupportedEncodingException
    {
        StringBuffer stringbuffer = new StringBuffer();
        byte byte0;
        if(stringproperty.getClass().equals(com.dragonflow.Properties.NumericProperty.class))
        {
            byte0 = 2;
        } else
        {
            byte0 = 1;
        }
        stringbuffer.append(java.net.URLEncoder.encode(String.valueOf(byte0), "UTF-8") + "&" + java.net.URLEncoder.encode(stringproperty.getName(), "UTF-8") + "&");
        com.dragonflow.Utils.StringPropertyUtil.appendField(stringbuffer, stringproperty.getLabel(), 1);
        com.dragonflow.Utils.StringPropertyUtil.appendField(stringbuffer, stringproperty.getDescription(), 2);
        com.dragonflow.Utils.StringPropertyUtil.appendField(stringbuffer, stringproperty.getValue(), 3);
        return stringbuffer.toString();
    }

    private static void appendField(StringBuffer stringbuffer, String s, int i)
        throws java.io.UnsupportedEncodingException
    {
        if(s != null)
        {
            stringbuffer.append(java.net.URLEncoder.encode(i + "=" + s, "UTF-8") + "&");
        }
    }

    public static com.dragonflow.Properties.StringProperty[] loadProperties(String s)
    {
        if(s == null || s.length() == 0)
        {
            return new com.dragonflow.Properties.StringProperty[0];
        }
        java.util.Vector vector = new Vector();
        int i = 0;
        try
        {
            while(i < s.length()) 
            {
                int j = s.indexOf("?", i);
                if(j < 0)
                {
                    throw new Exception("Failed to load StringProperties. Missing property-end marker");
                }
                com.dragonflow.Properties.StringProperty stringproperty = com.dragonflow.Utils.StringPropertyUtil.loadProperty(s.substring(i, j));
                vector.add(stringproperty);
                i = j + 1;
            }
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
            return new com.dragonflow.Properties.StringProperty[0];
        }
        com.dragonflow.Properties.StringProperty astringproperty[] = new com.dragonflow.Properties.StringProperty[vector.size()];
        for(int k = 0; k < vector.size(); k++)
        {
            astringproperty[k] = (com.dragonflow.Properties.StringProperty)vector.get(k);
        }

        return astringproperty;
    }

    private static com.dragonflow.Properties.StringProperty loadProperty(String s)
        throws java.lang.Exception
    {
        int i = s.indexOf("&", 0);
        if(i < 0)
        {
            throw new Exception("Failed to load StringProperty. Missing type-end marker");
        }
        int j = com.dragonflow.Utils.TextUtils.toInt(java.net.URLDecoder.decode(s.substring(0, i), "UTF-8"));
        int k = i + 1;
        if(k >= s.length())
        {
            throw new Exception("Failed to load StringProperty. Missing name field");
        }
        int l = s.indexOf("&", k);
        if(l < 0)
        {
            throw new Exception("Failed to load StringProperty. Missing name-end marker");
        }
        String s1 = java.net.URLDecoder.decode(s.substring(k, l), "UTF-8");
        java.lang.Object obj = null;
        switch(j)
        {
        case 1: // '\001'
            obj = new StringProperty(s1);
            break;

        case 2: // '\002'
            obj = new NumericProperty(s1);
            break;

        default:
            throw new Exception("Failed to load StringProperty. Cannot resolve property type");
        }
        int j1;
        for(int i1 = l + 1; i1 < s.length(); i1 = j1 + 1)
        {
            j1 = s.indexOf("&", i1);
            if(j1 < 0)
            {
                throw new Exception("Failed to load StringProperties. Missing field-end marker");
            }
            String s2 = java.net.URLDecoder.decode(s.substring(i1, j1), "UTF-8");
            int k1 = s2.indexOf("=");
            if(k1 < 0)
            {
                throw new Exception("Failed to load StringProperty. Missing key-value delimiter");
            }
            int l1 = com.dragonflow.Utils.TextUtils.toInt(s2.substring(0, k1));
            switch(l1)
            {
            case 1: // '\001'
                ((com.dragonflow.Properties.StringProperty) (obj)).setLabel(s2.substring(k1 + 1));
                break;

            case 2: // '\002'
                ((com.dragonflow.Properties.StringProperty) (obj)).setDescription(s2.substring(k1 + 1));
                break;

            case 3: // '\003'
                ((com.dragonflow.Properties.StringProperty) (obj)).setValue(s2.substring(k1 + 1));
                break;

            default:
                throw new Exception("Failed to load StringProperty. Cannot resolve field " + l1);
            }
        }

        return ((com.dragonflow.Properties.StringProperty) (obj));
    }

    public static void sortPropsArray(com.dragonflow.Properties.StringProperty astringproperty[])
    {
        java.util.Arrays.sort(astringproperty, new java.util.Comparator() {

            public int compare(java.lang.Object obj, java.lang.Object obj1)
            {
                return ((com.dragonflow.Properties.StringProperty)obj).getLabel().compareTo(((com.dragonflow.Properties.StringProperty)obj1).getLabel());
            }

        });
    }
}
