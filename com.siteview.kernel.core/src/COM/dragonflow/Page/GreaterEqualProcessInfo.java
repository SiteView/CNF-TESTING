/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Page;

class GreaterEqualProcessInfo implements BinaryPredicate {

    GreaterEqualProcessInfo() {
    }

    public boolean execute(Object obj, Object obj1) {
        String s = (String) ((HashMap) obj).get("name");
        String s1 = (String) ((HashMap) obj1).get("name");
        s = s.toLowerCase();
        s1 = s1.toLowerCase();
        return s1.compareTo(s) > 0;
    }
}
