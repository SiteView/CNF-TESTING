/*
 * 
 * Created on 2005-2-16 16:46:10
 *
 * ServerAction.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>ServerAction</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.ServerProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// Action, PortalSiteView, SiteViewGroup, Portal,
// Machine

public abstract class ServerAction extends Action {

    public static ServerProperty pMachineName;

    public ServerAction() {
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        String s1 = super.verify(stringproperty, s, httprequest, hashmap);
        if (stringproperty == pMachineName) {
            String s2 = getFullID();
            SiteViewObject siteviewobject = Portal.getSiteViewForID(s2);
            if (siteviewobject instanceof PortalSiteView) {
                PortalSiteView portalsiteview = (PortalSiteView) siteviewobject;
                HashMap hashmap1 = portalsiteview.getMasterConfig();
                if (!s.equals(TextUtils.getValue(hashmap1, "_defaultMachine"))) {
                    hashmap1.put("_defaultMachine", s);
                    portalsiteview.saveMasterConfig();
                }
            } else if (siteviewobject instanceof SiteViewGroup) {
                SiteViewGroup siteviewgroup = SiteViewGroup
                        .currentSiteView();
                if (!s.equals(siteviewgroup.getProperty("_defaultMachine"))) {
                    siteviewgroup.setProperty("_defaultMachine", s);
                    siteviewgroup.saveSettings();
                }
            }
        }
        return s1;
    }

    public void initializeMachine(Array array, HashMap hashmap) {
        if (array.size() > 0) {
            String s = (String) array.at(0);
            if (s.startsWith(Machine.REMOTE_PREFIX)) {
                if (pMachineName.isEditable) {
                    setProperty(pMachineName, s);
                }
                array.popFront();
            }
        }
    }

    public void setMachineInActionString(StringBuffer stringbuffer) {
        if (getProperty(pMachineName).length() > 0) {
            stringbuffer.append(" ");
            stringbuffer.append(getProperty(pMachineName));
        }
    }

    public void appendMachineDescription(StringBuffer stringbuffer) {
        if (getProperty(pMachineName).length() > 0) {
            stringbuffer.append(" on ");
            stringbuffer.append(Machine
                    .getMachineName(getProperty(pMachineName)));
        }
    }

    public String getMachineIDFromArgs() {
        String s = "";
        if (args.length > 0 && args[0].startsWith(Machine.REMOTE_PREFIX)) {
            if (pMachineName.isEditable) {
                s = args[0];
            }
            String as[] = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                as[i - 1] = args[i];
            }

            args = as;
        }
        return s;
    }

    static {
        pMachineName = new ServerProperty("_machine", "");
        if (SiteViewGroup.allowRemoteCommandLine()) {
            pMachineName.setParameterOptions(true, 1, false);
            pMachineName
                    .setDisplayText("Server",
                            "this server, or the Unix remote server to run the script on");
        }
        StringProperty astringproperty[] = { pMachineName };
        addProperties("COM.dragonflow.SiteView.ServerAction", astringproperty);
    }
}
