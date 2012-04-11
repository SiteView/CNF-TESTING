/*
 * 
 * Created on 2005-2-28 7:02:40
 *
 * NumericProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>NumericProperty</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.Utils.TextUtils;
import jgl.HashMap;

//Referenced classes of package COM.dragonflow.Properties:
//         StringProperty, IllegalConversionException, PropertiedObject

public class NumericProperty extends StringProperty
{

 String units;
 String displayUnits;
 String unitSeparator;
 static HashMap defaultDisplayUnits;
 static HashMap unitDisplayName;
 static HashMap unitConversionTable;
 public int defaultPrecision;
 public static int percentPrecision = 2;
 public static int minutesPrecision = 0;
 public static int secondsPrecision = 2;
 public static int millisecondsPrecision = 3;

 public NumericProperty(String s)
 {
     this(s, "0");
 }

 public NumericProperty(String s, String s1)
 {
     super(s, s1);
     units = null;
     displayUnits = null;
     unitSeparator = " ";
     defaultPrecision = 0;
     if(s1 == null || s1.length() == 0)
     {
         s1 = "0";
     }
 }

 public NumericProperty(String s, String s1, String s2)
 {
     this(s, s1);
     setUnits(s2);
 }

 public boolean isThreshold()
 {
     if(useDefaultThreshold)
     {
         return isStateProperty && order > 0;
     } else
     {
         return isThresholdProperty;
     }
 }

 public String getFullLabel()
 {
     String s = getLabel();
     if(units != null && units.length() > 0)
     {
         return s + " in " + units;
     } else
     {
         return s;
     }
 }

 public void setUnits(String s)
 {
     units = s;
 }

 public String getUnits()
 {
     return units;
 }

 public String getDisplayUnits()
 {
     if(displayUnits != null)
     {
         return displayUnits;
     }
     String s = (String)defaultDisplayUnits.get(units);
     if(s != null)
     {
         return s;
     } else
     {
         return units;
     }
 }

 public String getUnitDisplayString(String s)
 {
     String s1 = (String)unitDisplayName.get(s);
     if(s1 == null)
     {
         return s;
     } else
     {
         return s1;
     }
 }

 public String convertValue(String s, String s1, String s2)
     throws IllegalConversionException
 {
     if(!s1.equals(s2))
     {
         float f = StringProperty.toFloat(s);
         Float float1 = (Float)unitConversionTable.get(s1 + " to " + s2);
         if(float1 != null)
         {
             s = String.valueOf(f * float1.floatValue());
         } else
         {
             throw new IllegalConversionException();
         }
     }
     return s;
 }

 private String valueStringImplementation(String s, int i, boolean flag)
 {
     i = getPrecision(i);
     if(s.equals("n/a") || s.length() == 0)
     {
         return "n/a";
     }
     if(units != null && units.length() > 0)
     {
         String s1 = getDisplayUnits();
         try
         {
             s = convertValue(s, units, s1);
         }
         catch(IllegalConversionException illegalconversionexception)
         {
             s1 = units;
         }
         if(flag)
         {
             return TextUtils.floatToString(toFloat(s), i) + unitSeparator + getUnitDisplayString(s1);
         } else
         {
             return TextUtils.floatToString(toFloat(s), i);
         }
     }
     if(TextUtils.isNumber(s))
     {
         return TextUtils.floatToString(toFloat(s), i);
     } else
     {
         return s;
     }
 }

 private String valueFormattedStringImplementation(String s, int i, boolean flag)
 {
     i = getPrecision(i);
     if(s.equals("n/a") || s.length() == 0)
     {
         return "n/a";
     }
     if(units != null && units.length() > 0)
     {
         String s1 = getDisplayUnits();
         try
         {
             s = convertValue(s, units, s1);
         }
         catch(IllegalConversionException illegalconversionexception)
         {
             s1 = units;
         }
         if(flag)
         {
             return TextUtils.floatToFormattedString(toFloat(s), i) + unitSeparator + getUnitDisplayString(s1);
         } else
         {
             return TextUtils.floatToFormattedString(toFloat(s), i);
         }
     }
     if(TextUtils.isNumber(s))
     {
         return TextUtils.floatToFormattedString(toFloat(s), i);
     } else
     {
         return s;
     }
 }

 private int getPrecision(int i)
 {
     if(i < 0)
     {
         i = defaultPrecision;
         if(units != null)
         {
             if(units.equals("%"))
             {
                 i = percentPrecision;
             } else
             if(units.equals("minutes"))
             {
                 i = minutesPrecision;
             } else
             if(units.equals("seconds"))
             {
                 i = secondsPrecision;
             } else
             if((units.equals("milliseconds") || units.equals("ms")) && getDisplayUnits().equals("seconds"))
             {
                 i = millisecondsPrecision;
             }
         }
     }
     return i;
 }

 public String valueString(String s)
 {
     return valueStringImplementation(s, -1, true);
 }

 public String valueOnlyString(String s)
 {
     return valueStringImplementation(s, -1, false);
 }

 public String valueString(String s, int i)
 {
     return valueStringImplementation(s, i, true);
 }

 public String valueOnlyString(String s, int i)
 {
     return valueStringImplementation(s, i, false);
 }

 public String valueFormattedString(String s)
 {
     return valueFormattedStringImplementation(s, -1, true);
 }

 public String displayValueToNativeValue(String s)
 {
     if(units != null && units.length() > 0)
     {
         String s1 = getDisplayUnits();
         try
         {
             s = convertValue(s, s1, units);
         }
         catch(IllegalConversionException illegalconversionexception) { }
     }
     return s;
 }

 /**
  * CAUTION: Decompiled by hand.
  */
 public void increment(PropertiedObject po)
 {
     String s = po.getProperty(this);
     try
     {
         long l = Long.parseLong(s);
         po.setProperty(this, String.valueOf(l + 1L));
     }
     catch(NumberFormatException e) {
         /*empty*/
     }
 }

 static 
 {
     defaultDisplayUnits = new HashMap();
     unitDisplayName = new HashMap();
     unitConversionTable = new HashMap();
     defaultDisplayUnits.add("milliseconds", "seconds");
     unitDisplayName.add("milliseconds", "ms");
     unitDisplayName.add("seconds", "sec");
     unitDisplayName.add("minutes", "min");
     unitConversionTable.add("milliseconds to seconds", new Float(0.001D));
     unitConversionTable.add("seconds to milliseconds", new Float(1000F));
 }
}