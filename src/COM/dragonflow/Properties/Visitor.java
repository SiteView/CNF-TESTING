/*
 * 
 * Created on 2005-2-28 7:10:40
 *
 * Visitor.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>Visitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
//Referenced classes of package COM.dragonflow.Properties:
//         PropertiedObject

public abstract class Visitor
{

 public static final int CONTINUE_VISIT = 0;
 public static final int END_VISIT = 1;
 public static final int NO_SUBTREE_VISIT = 2;

 public Visitor()
 {
 }

 public abstract int visit(PropertiedObject propertiedobject);
}
