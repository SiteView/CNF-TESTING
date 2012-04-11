/*
 * 
 * Created on 2005-2-16 17:38:12
 *
 * VWALL.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>VWALL</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// Platform

public class VWALL {

    public static HashMap messages = new HashMap();

    public static int currenttopline = 0;

    public static String currentconfig = "videowallupdate_administrator.vid";

    public static boolean statechange = false;

    public VWALL() {
    }

    public static void writeVwall() {
        statechange = false;
        deleteItems();
        int i = currenttopline;
        for (int j = 0; j < 4; j ++) {
            i = nextline(i);
            String s = (String) messages.get("message" + i);
            TextUtils.debugPrint("currentline:" + i + " Message: " + s);
            runcommand("\"" + s + "\"", j);
        }

        currenttopline = i;
        TextUtils.debugPrint("Topline: " + currenttopline);
        if (statechange) {
            saveConfig(currentconfig);
        }
        statechange = false;
    }

    public static int nextline(int i) {
        int j = TextUtils.toInt((String) messages.get("last"));
        if (i < j - 1) {
            return ++ i;
        } else {
            return 1;
        }
    }

    public static void runcommand(String s, int i) {
        try {
            CommandLine commandline = new CommandLine();
            Array array = commandline.exec("..\\scripts\\wall.bat " + i + " " + TextUtils.doSubstitution(s));
        } catch (Exception exception) {
            System.out.println("Couldn't run command wall.bat: " + exception);
        }
    }

    public static void deleteItems() {
        HashMap hashmap = new HashMap();
        int i = 1;
        int j = TextUtils.toInt((String) messages.get("last"));
        hashmap.put("account", "administrator");
        hashmap.put("vwallname", "" + (String) messages.get("vwallname"));
        for (int k = 1; k < j; k ++) {
            if (Platform.timeMillis() < TextUtils.toLong((String) messages.get("starttime" + k)) + 60000L * TextUtils.toLong((String) messages.get("timeout" + k))) {
                hashmap.put("message" + i, "" + (String) messages.get("message" + k));
                hashmap.put("color" + i, "" + (String) messages.get("color" + k));
                hashmap.put("linenum" + i, "" + (String) messages.get("linenum" + k));
                hashmap.put("colnum" + i, "" + (String) messages.get("colnum" + k));
                hashmap.put("timeout" + i, "" + (String) messages.get("timeout" + k));
                hashmap.put("delete" + i, "");
                hashmap.put("starttime" + i, "" + (String) messages.get("starttime" + k));
                i ++;
            } else {
                statechange = true;
            }
        }

        hashmap.put("last", "" + i);
        hashmap.put("message" + i, "");
        hashmap.put("color" + i, "");
        hashmap.put("linenum" + i, "");
        hashmap.put("colnum" + i, "");
        hashmap.put("timeout" + i, "");
        hashmap.put("delete" + i, "");
        hashmap.put("starttime" + i, "");
        messages.clear();
        String s;
        for (Enumeration enumeration = hashmap.keys(); enumeration.hasMoreElements(); messages.put(s, (String) hashmap.get(s))) {
            s = (String) enumeration.nextElement();
        }

    }

    public static void saveConfig(String s) {
        TextUtils.debugPrint("Saving config from VWALL class");
        messages.put("lastupdate", Long.toString(Platform.timeMillis()));
        Array array = new Array();
        array.add(messages);
        try {
            FrameFile.writeToFile(Platform.getRoot() + "/templates.view/" + s + "", array, "", true, true);
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public static void loadhashmap(String s) {
        try {
            Array array = FrameFile.readFromFile(Platform.getRoot() + "/templates.view/" + s + "");
            if (array != null && array.size() > 0) {
                messages = (HashMap) array.at(0);
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

}