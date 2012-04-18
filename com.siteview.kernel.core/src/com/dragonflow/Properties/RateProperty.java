/*
 * 
 * Created on 2005-2-28 7:05:30
 *
 * RateProperty.java
 *
 * History:
 *
 */
package com.dragonflow.Properties;

/**
 * Comment for <code>RateProperty</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
//Referenced classes of package com.dragonflow.Properties:
//         NumericProperty

public class RateProperty extends NumericProperty
{

 String timeUnits;

 public RateProperty(String s)
 {
     this(s, "0");
 }

 public RateProperty(String s, String s1)
 {
     super(s, s1);
     timeUnits = null;
 }

 public RateProperty(String s, String s1, String s2)
 {
     super(s, s1, s2);
     timeUnits = null;
 }

 public RateProperty(String s, String s1, String s2, String s3)
 {
     this(s, s1, s2);
     timeUnits = s3;
     units = s2 + "/" + getUnitDisplayString(s3);
 }
}