/*
 * 
 * Created on 2005-2-15 13:03:28
 *
 * EnterpriseImportMail.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>EnterpriseImportMail</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.SiteView:
// Action
public class EnterpriseImportMail extends Action {

    String mailServer;

    String account;

    String password;

    public EnterpriseImportMail(String s, String s1, String s2) {
        mailServer = "";
        account = "";
        password = "";
        mailServer = s;
        account = s1;
        password = s2;
        runType = 1;
    }

    public boolean execute() {
        return true;
    }
}