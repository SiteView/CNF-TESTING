/*
 * 
 * Created on 2005-2-16 17:02:26
 *
 * StatusMail.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>StatusMail</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.SiteView:
// Action, SiteViewGroup, SiteViewMail
public class StatusMail extends Action {

    public StatusMail() {
        runType = 1;
    }

    public boolean execute() {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        SiteViewMail.statusMail(siteviewgroup, "daily status");
        return true;
    }

    public String toString() {
        return "status mail";
    }
}