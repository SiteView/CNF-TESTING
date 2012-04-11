/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Health;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.File;

import jgl.Array;

// Referenced classes of package COM.dragonflow.Health:
// FileBase

public class HistoryHealth extends COM.dragonflow.Health.FileBase
{

    private String mgFiles[];
    private jgl.Array mgs;
    jgl.Array checks;

    public HistoryHealth()
    {
        mgFiles = null;
        mgs = new Array();
        checks = new Array();
        historyHealth("");
    }

    public HistoryHealth(String s)
    {
        mgFiles = null;
        mgs = new Array();
        checks = new Array();
        historyHealth(s);
    }

    public void historyHealth(String s)
    {
        if(!s.equals(""))
        {
            groupsLocation = s;
        }
        checks.add(new FileBase.CheckMe("monitors", "monitorOrGroup", "'monitors' tag in history.config; this monitor or group not found: '<value>'"));
        checks.add(new FileBase.CheckMe("monitors", "required", "missing 'monitors' tag in history.config"));
        checks.add(new FileBase.CheckMe("id", "single", "multiple 'id' tags in history.config, with values: '<value>'"));
        checks.add(new FileBase.CheckMe("id", "required", "missing 'id' in history.config"));
        checks.add(new FileBase.CheckMe("id", "number", "malformed 'id' in history.config; value: '<value>'; must be a number like 1,2,3..."));
        checks.add(new FileBase.CheckMe("id", "range(0-1000000)", "'id' out of range in history.config; value: '<value>' must be between: '1-1000000'"));
        checks.add(new FileBase.CheckMe("id", "nextId(_nextReportID)", "'id' in history.config; value: '<value>'; must be less than _nextReportID: '<nextId>'"));
        checks.add(new FileBase.CheckMe("id", "noDupValues", "duplicate 'id's in history.config with value: '<value>'"));
    }

    public String getClassName()
    {
        return "HistoryHealth";
    }

    public jgl.Array errorCheck()
    {
        readHistoryConfig();
        FileBase.DoCheck docheck = new FileBase.DoCheck();
        docheck.processTree(checks);
        return errorLog;
    }

    private void readHistoryConfig()
    {
        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + groupsLocation);
        if(file == null)
        {
            return;
        }
        String as[] = file.list();
        int i = 0;
        for(int j = 0; j < as.length; j++)
        {
            if(as[j].endsWith("history.config"))
            {
                i++;
            }
        }

        mgFiles = new String[i];
        i = 0;
        for(int k = 0; k < as.length; k++)
        {
            if(as[k].endsWith("history.config"))
            {
                mgFiles[i++] = new String(as[k]);
            }
        }

        for(int l = 0; l < mgFiles.length;l++)/*dingbing.xu add l++*/
        {
            try
            {
                String s = COM.dragonflow.SiteView.Platform.getRoot() + groupsLocation + java.io.File.separator + mgFiles[l];
                java.io.File file1 = new File(s);
                if(file1.exists())
                {
                    mgs.add(COM.dragonflow.Properties.FrameFile.readFromFile(s));
                }
                continue;
            }
            catch(java.lang.Exception exception)
            {
                String s1 = "HistoryHealth.readHistoryConfig() - Error with file : " + COM.dragonflow.SiteView.Platform.getRoot() + groupsLocation + java.io.File.separator + mgFiles[l];
                java.lang.System.out.println(s1);
                exception.printStackTrace();
                COM.dragonflow.Log.LogManager.log("Error", "HistoryHealth.readHistoryConfig(): " + s1 + "\n" + COM.dragonflow.Utils.FileUtils.stackTraceText(exception));
                /*dingbing.xu delete i++*/
            }
        }

        for(int i1 = 0; i1 < mgFiles.length; i1++)
        {
            jgl.Array array = (jgl.Array)mgs.at(i1);
            new FileBase.HealthNode(null, 0, array, mgFiles[i1]);
        }

    }

    public String toString()
    {
        return "History Health - checks history.config which holds report definitions";
    }

    public static void main(String args[])
    {
    }
}
