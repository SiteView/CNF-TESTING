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

import com.recursionsw.jgl.Array;

// Referenced classes of package COM.dragonflow.Health:
// FileBase

public class MasterHealth extends COM.dragonflow.Health.FileBase
{

    private String mgFiles[];
    private Array mgs;
    Array checks;

    public MasterHealth()
    {
        mgFiles = null;
        mgs = new Array();
        checks = new Array();
        masterHealth("");
    }

    public MasterHealth(String s)
    {
        mgFiles = null;
        mgs = new Array();
        checks = new Array();
        masterHealth(s);
    }

    public void masterHealth(String s)
    {
        if(!s.equals(""))
        {
            groupsLocation = s;
        }
        checks.add(new FileBase.CheckMe("_alertCondition", "alert", "'_alertCondition' error in group: '<group>'; "));
        checks.add(new FileBase.CheckMe("_nextReportID", "single", "multiple '_nextReportID' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_nextReportID", "number", "malformed '_nextReportID' in group: '<group>'; value: '<value>'; must be a number like 1,2,3..."));
        checks.add(new FileBase.CheckMe("_nextReportID", "range(0-1000000)", "'_nextReportID' out of range in group: '<group>'; value: '<value>' must be between: '1-1000000'"));
        checks.add(new FileBase.CheckMe("_nextRemoteID", "single", "multiple '_nextRemoteID' tags in master.config, with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_nextRemoteID", "number", "malformed '_nextRemoteID' in master.config; value: '<value>'; must be a number like 0,1,2,3..."));
        checks.add(new FileBase.CheckMe("_nextRemoteID", "range(0-1000000)", "'_nextRemoteID' out of range in master.config; value: '<value>' must be between: '1-1000000'"));
        checks.add(new FileBase.CheckMe("_remoteMachine._id", "required", "sub-tag '_id' missing in master.config for _remoteMachine: '<value>'"));
        checks.add(new FileBase.CheckMe("_remoteMachine._id", "number", "malformed '_remoteMachine._id' in master.config; value: '<value>'; must be a number like 0,1,2,3..."));
        checks.add(new FileBase.CheckMe("_remoteMachine._id", "range(0-1000000)", "'_remoteMachine._id' out of range in master.config; value: '<value>' must be between: '1-1000000'"));
        checks.add(new FileBase.CheckMe("_remoteMachine._id", "nextId(_nextRemoteID)", "'_remoteMachine._id' in master.config; value: '<value>'; must be less than _nextRemoteID: '<nextId>'"));
        checks.add(new FileBase.CheckMe("_remoteMachine._id", "noDupValues", "duplicate sub-tag '_id's for '_remoteMachine' tag in master.config with value: '<value>'"));
        checks.add(new FileBase.CheckMe("_remoteMachine._os", "required", "sub-tag '_os' missing in master.config for _remoteNTMachine: '<value>'"));
        checks.add(new FileBase.CheckMe("_remoteMachine._trace", "set(on)", "bad sub-tag: '_trace' for _remoteMachine in group: '<group>'; value: '<value>' not one of these: 'on' or empty(no characters)"));
        checks.add(new FileBase.CheckMe("_remoteMachine._method", "required", "sub-tag '_method' missing in master.config for _remoteMachine: '<value>'"));
        checks.add(new FileBase.CheckMe("_remoteMachine._login", "required", "sub-tag '_login' missing in master.config for _remoteMachine: '<value>'"));
        checks.add(new FileBase.CheckMe("_remoteMachine._host", "required", "sub-tag '_host' missing in master.config for _remoteMachine: '<value>'"));
        checks.add(new FileBase.CheckMe("_remoteMachine._name, _remoteNTMachine._name", "noDupValues", "duplicate sub-tag '_name's for <property> tag in master.config with value: '<value>'"));
        checks.add(new FileBase.CheckMe("_nextNTRemoteID", "single", "multiple '_nextNTRemoteID' tags in master.config, with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_nextNTRemoteID", "number", "malformed '_nextNTRemoteID' in master.config; value: '<value>'; must be a number like 0,1,2,3..."));
        checks.add(new FileBase.CheckMe("_nextNTRemoteID", "range(0-1000000)", "'_nextNTRemoteID' out of range in master.config; value: '<value>' must be between: '1-1000000'"));
        checks.add(new FileBase.CheckMe("_remoteNTMachine._id", "required", "sub-tag '_id' missing in master.config for _remoteNTMachine: '<value>'"));
        checks.add(new FileBase.CheckMe("_remoteNTMachine._id", "number", "malformed '_remoteNTMachine._id' in master.config; value: '<value>'; must be a number like 0,1,2,3..."));
        checks.add(new FileBase.CheckMe("_remoteNTMachine._id", "range(0-1000000)", "'_remoteNTMachine._id' out of range in master.config; value: '<value>' must be between: '1-1000000'"));
        checks.add(new FileBase.CheckMe("_remoteNTMachine._id", "nextId(_nextNTRemoteID)", "'_remoteNTMachine._id' in master.config; value: '<value>'; must be less than _nextNTRemoteID: '<nextId>'"));
        checks.add(new FileBase.CheckMe("_remoteNTMachine._id", "noDupValues", "duplicate sub-tag '_id's for _remoteNTMachine tag in master.config with value: '<value>'"));
        checks.add(new FileBase.CheckMe("_remoteNTMachine._host", "noDupValues", "duplicate sub-tag '_host's for _remoteNTMachine tag in master.config with value: '<value>'"));
        checks.add(new FileBase.CheckMe("_remoteNTMachine._trace", "set(on)", "bad sub-tag: '_trace' for _remoteNTMachine in group: '<group>'; value: '<value>' not one of these: 'on' or empty(no characters)"));
        checks.add(new FileBase.CheckMe("_remoteNTMachine._login", "required", "sub-tag '_login' missing in master.config for _remoteNTMachine: '<value>'"));
        checks.add(new FileBase.CheckMe("_remoteNTMachine._host", "required", "sub-tag '_host' missing in master.config for _remoteNTMachine: '<value>'"));
        checks.add(new FileBase.CheckMe("_additionalSchedule._id", "nextId(_nextAdditionalScheduleID)", "'_additionalSchedule._id' in master.config; value: '<value>'; must be less than _nextAdditionalScheduleID: '<nextId>' in the header frame."));
        checks.add(new FileBase.CheckMe("_additionalSchedule._id", "required", "sub-tag '_id' missing in master.config for _additionalSchedule: '<value>'"));
        checks.add(new FileBase.CheckMe("_additionalSchedule._id", "number", "malformed '_additionalSchedule._id' in master.config; value: '<value>'; must be a number like 0,1,2,3..."));
        checks.add(new FileBase.CheckMe("_additionalSchedule._id", "range(0-1000000)", "'_additionalSchedule._id' out of range in master.config; value: '<value>' must be between: '1-1000000'"));
        checks.add(new FileBase.CheckMe("_additionalSchedule._id", "noDupValues", "duplicate sub-tag '_id's for _additionalSchedule tag in master.config with value: '<value>'"));
        checks.add(new FileBase.CheckMe("_additionalMail._id", "nextId(_nextAdditionalMailID)", "'_additionalMail._id' in master.config; value: '<value>'; must be less than _nextAdditionalMailID: '<nextId>' in the header frame."));
        checks.add(new FileBase.CheckMe("_additionalMail._id", "required", "sub-tag '_id' missing in master.config for _additionalMail: '<value>'"));
        checks.add(new FileBase.CheckMe("_additionalMail._id", "number", "malformed '_additionalMail._id' in master.config; value: '<value>'; must be a number like 0,1,2,3..."));
        checks.add(new FileBase.CheckMe("_additionalMail._id", "range(0-1000000)", "'_additionalMail._id' out of range in master.config; value: '<value>' must be between: '1-1000000'"));
        checks.add(new FileBase.CheckMe("_additionalMail._id", "noDupValues", "duplicate sub-tag '_id's for _additionalMail tag in master.config with value: '<value>'"));
        checks.add(new FileBase.CheckMe("_healthCheckForUniqueMonitorNames", "single", "multiple '_healthCheckForUniqueMonitorNames' tags in master.config with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_cpuMaxProcessors", "single", "multiple '_cpuMaxProcessors' tags in master.config with values: '<value>'"));
    }

    public String getClassName()
    {
        return "MasterHealth";
    }

    public Array errorCheck()
    {
        readMasterConfig();
        COM.dragonflow.Health.FileBase.DoCheck docheck = new FileBase.DoCheck();
        docheck.processTree(checks);
        return errorLog;
    }

    private void readMasterConfig()
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
            if(as[j].endsWith("master.config"))
            {
                i++;
            }
        }

        mgFiles = new String[i];
        i = 0;
        for(int k = 0; k < as.length; k++)
        {
            if(as[k].endsWith("master.config"))
            {
                mgFiles[i++] = new String(as[k]);
            }
        }

        for(int l = 0; l < mgFiles.length;l++)/*dingbing.xu add l++*/
        {
            try
            {
                mgs.add(COM.dragonflow.Properties.FrameFile.readFromFile(COM.dragonflow.SiteView.Platform.getRoot() + groupsLocation + java.io.File.separator + mgFiles[l]));
                continue;
            }
            catch(java.lang.Exception exception)
            {
                String s = "MasterHealth.readHistoryConfig() - Error with file : " + COM.dragonflow.SiteView.Platform.getRoot() + groupsLocation + java.io.File.separator + mgFiles[l];
                java.lang.System.out.println(s);
                exception.printStackTrace();
                COM.dragonflow.Log.LogManager.log("Error", "MasterHealth.readHistoryConfig(): " + s + "\n" + COM.dragonflow.Utils.FileUtils.stackTraceText(exception));
                //l++;/*dingbing.xu delete l++*/
            }
        }

        for(int i1 = 0; i1 < mgFiles.length; i1++)
        {
            Array array = (Array)mgs.get(i1);
            new FileBase.HealthNode(null, 0, array, mgFiles[i1]);
        }

    }

    public String toString()
    {
        return "Master Health - checks master.config which holds SiteView configuration definitions";
    }

    public static void main(String args[])
    {
    }
}
