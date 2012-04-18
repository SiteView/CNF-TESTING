/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Api;

import com.recursionsw.jgl.HashMap;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */


// Referenced classes of package com.dragonflow.Api:
// SSBaseReturnValues

public class SSPropertyDetails extends com.dragonflow.Api.SSBaseReturnValues
{

    private String name;
    private String value;
    private String propertyType;
    private String description;
    private String label;
    private boolean editable;
    private boolean multiValued;
    private String defaultValue;
    private String selectionDisplayNames[];
    private String selectionIDs[];
    private String selectionChoiceType;
    private boolean required;
    private boolean prerequisite;
    private int order;
    private String displayUnits;
    private boolean advanced;
    private boolean password;
    public static final String BOOLEAN = "BOOLEAN";
    public static final String BROWSABLE = "BROWSABLE";
    public static final String FILE = "FILE";
    public static final String FREQUENCY = "FREQUENCY";
    public static final String NUMERIC = "NUMERIC";
    public static final String PERCENT = "PERCENT";
    public static final String RATE = "RATE";
    public static final String SCALAR = "SCALAR";
    public static final String SCHEDULE = "SCHEDULE";
    public static final String SERVER = "SERVER";
    public static final String TEXT = "TEXT";
    public static final String PASSWORD = "PASSWORD";
    public static final String SELECTION_CHOICE_XML = "XML";
    public static final String SELECTION_CHOICE_LIST = "LIST";
    public static final String SELECTION_CHOICE_THRESHOLD = "THRESHOLD";

    public SSPropertyDetails(String s, String s1, String s2, String s3, boolean flag, boolean flag1, String s4, 
            String as[], String as1[], String s5, boolean flag2, boolean flag3, int i, String s6, 
            boolean flag4, boolean flag5, String s7)
    {
        name = s;
        propertyType = s1;
        description = s2;
        label = s3;
        editable = flag;
        multiValued = flag1;
        defaultValue = s4;
        selectionDisplayNames = as;
        selectionIDs = as1;
        selectionChoiceType = s5;
        required = flag2;
        prerequisite = flag3;
        order = i;
        value = s7;
        if(s1.equals("NUMERIC") || s1.equals("FREQUENCY") || s1.equals("PERCENT"))
        {
            displayUnits = s6;
        } else
        {
            displayUnits = "";
        }
        advanced = flag4;
        password = flag5;
    }

    public java.lang.Object getReturnValueType()
    {
        java.lang.Class class1 = getClass();
        return class1.getName();
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public String getPropertyType()
    {
        return propertyType;
    }

    public String getDescription()
    {
        return description;
    }

    public String getLabel()
    {
        return label;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public boolean isMultiValued()
    {
        return multiValued;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public String[] getSelectionDisplayNames()
    {
        return selectionDisplayNames;
    }

    public String[] getSelectionIDs()
    {
        return selectionIDs;
    }

    public String getSelectionChoiceType()
    {
        return selectionChoiceType;
    }

    public boolean isRequired()
    {
        return required;
    }

    public boolean isPrerequisite()
    {
        return prerequisite;
    }

    public int getOrder()
    {
        return order;
    }

    public String getDisplayUnits()
    {
        return displayUnits;
    }

    public boolean isAdvanced()
    {
        return advanced;
    }

    public boolean isPassword()
    {
        return password;
    }

    public static void extractDetailsIntoHashMap(com.dragonflow.Api.SSPropertyDetails sspropertydetails, HashMap hashmap)
    {
        if(sspropertydetails.getClass() != null)
        {
            hashmap.put("class", sspropertydetails.getClass());
        } else
        {
            hashmap.put("class", "");
        }
        if(sspropertydetails.getDefaultValue() != null)
        {
            hashmap.put("defaultValue", sspropertydetails.getDefaultValue());
        } else
        {
            hashmap.put("defaultValue", "");
        }
        if(sspropertydetails.getDescription() != null)
        {
            hashmap.put("description", sspropertydetails.getDescription());
        } else
        {
            hashmap.put("description", "");
        }
        if(sspropertydetails.getDisplayUnits() != null)
        {
            hashmap.put("displayUnits", sspropertydetails.getDisplayUnits());
        } else
        {
            hashmap.put("displayUnits", "");
        }
        if(sspropertydetails.getLabel() != null)
        {
            hashmap.put("label", sspropertydetails.getLabel());
        } else
        {
            hashmap.put("label", "");
        }
        if(sspropertydetails.getName() != null)
        {
            hashmap.put("name", sspropertydetails.getName());
        } else
        {
            hashmap.put("name", "");
        }
        if(sspropertydetails.getValue() != null)
        {
            hashmap.put("value", sspropertydetails.getValue());
        } else
        {
            hashmap.put("value", "");
        }
        hashmap.put("order", (new Integer(sspropertydetails.getOrder())).toString());
        if(sspropertydetails.getPropertyType() != null)
        {
            hashmap.put("propertyType", sspropertydetails.getPropertyType());
        } else
        {
            hashmap.put("propertyType", "");
        }
        if(sspropertydetails.getSelectionChoiceType() != null)
        {
            hashmap.put("selectionChoiceType", sspropertydetails.getSelectionChoiceType());
        } else
        {
            hashmap.put("selectionChoiceType", "");
        }
        if(sspropertydetails.getSelectionDisplayNames() != null)
        {
            hashmap.put(sspropertydetails.getName() + "DisplayNames", sspropertydetails.getSelectionDisplayNames());
        } else
        {
            hashmap.put(sspropertydetails.getName() + "DisplayNames", "");
        }
        if(sspropertydetails.getSelectionIDs() != null)
        {
            hashmap.put(sspropertydetails.getName() + "SelectionIDs", sspropertydetails.getSelectionIDs());
        } else
        {
            hashmap.put(sspropertydetails.getName() + "SelectionIDs", "");
        }
        hashmap.put("isAdvanced", (new Boolean(sspropertydetails.isAdvanced())).toString());
        hashmap.put("isEditable", (new Boolean(sspropertydetails.isEditable())).toString());
        hashmap.put("isMultivalued", (new Boolean(sspropertydetails.isMultiValued())).toString());
        hashmap.put("isPassword", (new Boolean(sspropertydetails.isPassword())).toString());
        hashmap.put("isPrerequisite", (new Boolean(sspropertydetails.isPrerequisite())).toString());
        hashmap.put("isRequired", (new Boolean(sspropertydetails.isRequired())).toString());
    }
}
