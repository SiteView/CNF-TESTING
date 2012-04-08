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

import jgl.HashMap;

public class XmlApiObject extends jgl.Array
{

    private String name;
    private jgl.HashMap properties;
    private jgl.HashMap resultProperties;
    private boolean inRequest;
    private COM.dragonflow.XmlApi.XmlApiObject parent;

    public XmlApiObject(String s, jgl.HashMap hashmap)
    {
        this(s, hashmap, null, null, true);
    }

    private XmlApiObject(String s, jgl.HashMap hashmap, jgl.HashMap hashmap1, COM.dragonflow.XmlApi.XmlApiObject xmlapiobject, boolean flag)
    {
        name = "";
        properties = null;
        resultProperties = null;
        inRequest = true;
        parent = null;
        if(s == null)
        {
            name = "";
        } else
        {
            name = s;
        }
        if(hashmap == null)
        {
            properties = new HashMap();
        } else
        {
            properties = hashmap;
        }
        if(hashmap1 == null)
        {
            resultProperties = new HashMap();
        } else
        {
            resultProperties = hashmap1;
        }
        parent = xmlapiobject;
        inRequest = flag;
    }

    public COM.dragonflow.XmlApi.XmlApiObject add()
    {
        return add("", null);
    }

    public COM.dragonflow.XmlApi.XmlApiObject add(String s, jgl.HashMap hashmap)
    {
        return add(s, hashmap, true);
    }

    public COM.dragonflow.XmlApi.XmlApiObject add(String s, jgl.HashMap hashmap, boolean flag)
    {
        COM.dragonflow.XmlApi.XmlApiObject xmlapiobject = new XmlApiObject(s, null, hashmap, this, flag);
        add(xmlapiobject);
        return xmlapiobject;
    }

    public boolean inRequest()
    {
        return inRequest;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String s)
    {
        name = s;
    }

    public String getProperty(String s)
    {
        return getProperty(s, true);
    }

    public String getProperty(String s, boolean flag)
    {
        String s1 = "";
        if(flag)
        {
            s1 = (String)properties.get(s);
        } else
        {
            s1 = (String)resultProperties.get(s);
        }
        if(s1 == null)
        {
            s1 = "";
        }
        return s1;
    }

    public void setProperty(String s, String s1)
    {
        setProperty(s, s1, true);
    }

    public void setProperty(String s, String s1, boolean flag)
    {
        if(s1 != null)
        {
            if(flag)
            {
                properties.put(s, s1);
            } else
            {
                resultProperties.put(s, s1);
            }
        }
    }

    public void setProperty(String s, String as[])
    {
        setProperty(s, as, true);
    }

    public void setProperty(String s, String as[], boolean flag)
    {
        if(as != null)
        {
            if(flag)
            {
                properties.put(s, as);
            } else
            {
                resultProperties.put(s, as);
            }
        }
    }

    public jgl.HashMap getProperties()
    {
        return getProperties(true);
    }

    public jgl.HashMap getProperties(boolean flag)
    {
        if(flag)
        {
            return properties;
        } else
        {
            return resultProperties;
        }
    }

    public void setProperties(jgl.HashMap hashmap)
    {
        setProperties(hashmap, true);
    }

    public void setProperties(jgl.HashMap hashmap, boolean flag)
    {
        jgl.HashMap hashmap1 = new HashMap();
        hashmap1.copy(hashmap);
        if(flag)
        {
            properties = hashmap1;
        } else
        {
            resultProperties = hashmap1;
        }
    }

    public String getTreeProperty(String s)
    {
        return getTreeProperty(s, true);
    }

    public String getTreeProperty(String s, boolean flag)
    {
        String s1 = "";
        if(flag)
        {
            s1 = (String)properties.get(s);
        } else
        {
            s1 = (String)resultProperties.get(s);
        }
        if(s1 == null)
        {
            s1 = "";
        }
        if(s1 == "" && parent != null)
        {
            s1 = parent.getTreeProperty(s, flag);
        }
        return s1;
    }

    public jgl.HashMap getTreeProperties()
    {
        return getTreeProperties(true);
    }

    public jgl.HashMap getTreeProperties(boolean flag)
    {
        jgl.HashMap hashmap = new HashMap();
        mergeTreeProperties(hashmap, this, flag);
        return hashmap;
    }

    private void mergeTreeProperties(jgl.HashMap hashmap, COM.dragonflow.XmlApi.XmlApiObject xmlapiobject, boolean flag)
    {
        if(xmlapiobject != null)
        {
            if(xmlapiobject.parent != null)
            {
                mergeTreeProperties(hashmap, xmlapiobject.parent, flag);
            }
            jgl.HashMap hashmap1 = xmlapiobject.getProperties(flag);
            String s;
            String s1;
            for(Enumeration enumeration = hashmap1.keys(); enumeration.hasMoreElements(); hashmap.put(s, s1))
            {
                s = (String)enumeration.nextElement();
                s1 = (String)hashmap1.get(s);
            }

        }
    }

    public COM.dragonflow.XmlApi.XmlApiObject getDocument()
    {
        COM.dragonflow.XmlApi.XmlApiObject xmlapiobject;
        for(xmlapiobject = this; xmlapiobject.parent != null; xmlapiobject = xmlapiobject.parent) { }
        return xmlapiobject;
    }

    public String getDocumentProperty(String s)
    {
        return getDocument().getProperty(s);
    }

    public COM.dragonflow.XmlApi.XmlApiObject getOperation()
    {
        if(parent == null)
        {
            return null;
        }
        if(parent.parent == null)
        {
            return this;
        }
        COM.dragonflow.XmlApi.XmlApiObject xmlapiobject;
        for(xmlapiobject = this; xmlapiobject.parent.parent != null; xmlapiobject = xmlapiobject.parent) { }
        return xmlapiobject;
    }

    public String getOperationProperty(String s)
    {
        COM.dragonflow.XmlApi.XmlApiObject xmlapiobject = getOperation();
        String s1 = "";
        if(xmlapiobject != null)
        {
            s1 = xmlapiobject.getProperty(s);
        }
        return s1;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        printObject(stringbuffer, 0);
        return stringbuffer.toString();
    }

    void printObject(StringBuffer stringbuffer, int i)
    {
        COM.dragonflow.XmlApi.XmlApiObject.printOffset(stringbuffer, i);
        stringbuffer.append(name);
        if(inRequest)
        {
            stringbuffer.append(" Request(");
            if(properties != null)
            {
                Enumeration enumeration = properties.keys();
                boolean flag = true;
                String s;
                for(; enumeration.hasMoreElements(); stringbuffer.append(s + "=" + properties.get(s)))
                {
                    s = (String)enumeration.nextElement();
                    if(!flag)
                    {
                        stringbuffer.append(", ");
                    }
                    flag = false;
                }

            }
            stringbuffer.append("),");
        }
        stringbuffer.append((inRequest ? " Response" : " ResponseOnly") + "(");
        if(resultProperties != null)
        {
            Enumeration enumeration1 = resultProperties.keys();
            boolean flag1 = true;
            String s1;
            for(; enumeration1.hasMoreElements(); stringbuffer.append(s1 + "=" + resultProperties.get(s1)))
            {
                s1 = (String)enumeration1.nextElement();
                if(!flag1)
                {
                    stringbuffer.append(", ");
                }
                flag1 = false;
            }

        }
        stringbuffer.append(")\n");
        i++;
        COM.dragonflow.XmlApi.XmlApiObject xmlapiobject;
        for(Enumeration enumeration2 = elements(); enumeration2.hasMoreElements(); xmlapiobject.printObject(stringbuffer, i))
        {
            xmlapiobject = (COM.dragonflow.XmlApi.XmlApiObject)enumeration2.nextElement();
        }

    }

    static void printOffset(StringBuffer stringbuffer, int i)
    {
        for(int j = 0; j < i; j++)
        {
            stringbuffer.append("   ");
        }

    }
}
