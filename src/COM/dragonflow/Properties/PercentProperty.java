/*
 * 
 * Created on 2005-2-28 7:03:12
 *
 * PercentProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>PercentProperty</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
//Referenced classes of package COM.dragonflow.Properties:
//         NumericProperty

public class PercentProperty extends NumericProperty
{

 public PercentProperty(String s)
 {
     this(s, "0");
 }

 public PercentProperty(String s, String s1)
 {
     super(s, s1, "%");
     unitSeparator = "";
     displayMaximum = 100F;
 }

 public String getFullLabel()
 {
     return getLabel() + "%";
 }

 /**
  * CAUTION: Decompiled by hand.
  */
 public String verify(String s)
 {
     s = super.verify(s);
     if(s != null) {
         try {
            float f = toFloat(s);
            if((double)f >= 0.0D && (double)f <= 100D)
             {
                 return s;
             }
        } catch (NumberFormatException e) {
        }
     }
     return null;
 }
}
