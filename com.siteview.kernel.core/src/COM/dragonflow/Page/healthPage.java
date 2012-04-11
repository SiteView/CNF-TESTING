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

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class healthPage extends COM.dragonflow.Page.CGI {

    public healthPage() {
    }

    public void printBody() throws java.lang.Exception {
        for (int i = 0; i < 2; i ++) {
            COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup
                    .currentSiteView();
            jgl.Array array = siteviewgroup.getRawElements();
            boolean flag = false;
            int j = 0;
            while (array != null && j < array.size()) {
                java.lang.Object obj = array.at(j);
                if ((obj instanceof COM.dragonflow.SiteView.MonitorGroup)
                        && ((COM.dragonflow.SiteView.MonitorGroup) obj)
                                .isTopLevelGroup()
                        && ((COM.dragonflow.SiteView.MonitorGroup) obj)
                                .isHealthGroup()) {
                    ((COM.dragonflow.SiteView.MonitorGroup) obj).printPage(
                            outputStream, request, true);
                    flag = true;
                    break;
                }
                j++;
            } 
            
            if (flag) {
                break;
            }
            COM.dragonflow.SiteView.Health.getHealth();
            COM.dragonflow.SiteView.Health.createHealthGroup();
            COM.dragonflow.Api.APISiteView.forceConfigurationRefresh();
        } 
    }
}
