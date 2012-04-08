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
import java.util.HashMap;

import jgl.Array;

// Referenced classes of package COM.dragonflow.Health:
// FileBase

public class MgHealth extends COM.dragonflow.Health.FileBase
{

    private String mgFiles[];
    private jgl.Array mgs;
    private java.util.HashMap groupsHM;
    jgl.Array checks;

    public MgHealth()
    {
        mgFiles = null;
        mgs = new Array();
        groupsHM = new HashMap();
        checks = new Array();
        mgHealth("");
    }

    public MgHealth(String s)
    {
        mgFiles = null;
        mgs = new Array();
        groupsHM = new HashMap();
        checks = new Array();
        mgHealth(s);
    }

    public void mgHealth(String s)
    {
        if(!s.equals(""))
        {
            groupsLocation = s;
        }
        checks.add(new FileBase.CheckMe("_class", "monitorRequired", "missing '_class' in group: '<group>'"));
        checks.add(new FileBase.CheckMe("_class", "single", "multiple '_class' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_class", "subGroupRequired", "missing '_class' in group: '<group>'"));
        checks.add(new FileBase.CheckMe("_alertCondition", "alert", "'_alertCondition' error in group: '<group>'; "));
        checks.add(new FileBase.CheckMe("_nextConditionID", "single", "multiple '_nextConditionID' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_nextConditionID", "number", "malformed '_nextConditionID' in group: '<group>'; value: '<value>'; must be a counting number like 1,2,3..."));
        checks.add(new FileBase.CheckMe("_nextConditionID", "range(0-1000000)", "'_nextConditionID' out of range in group: '<group>'; value: '<value>' must be between: '0-1000000'"));
        checks.add(new FileBase.CheckMe("_dependsCondition", "set(good|error)", "bad '_dependsCondition' in group: '<group>'; value: '<value>' not one of these: 'good|error'"));
        checks.add(new FileBase.CheckMe("_dependsCondition", "single", "multiple '_dependsCondition' tags in group: '<group>'; with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_nextID", "single", "multiple '_nextID' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_nextID", "groupRequired", "missing '_nextID' in group header frame: '<group>'"));
        checks.add(new FileBase.CheckMe("_nextID", "number", "malformed '_nextID' in group: '<group>'; value: '<value>'; must be a counting number like 1,2,3..."));
        checks.add(new FileBase.CheckMe("_nextID", "range(0-1000000)", "'_nextID' out of range in group: '<group>'; value: '<value>' must be between: '0-1000000'"));
        checks.add(new FileBase.CheckMe("_parent", "single", "multiple '_group' tags in group: '<group>'; with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_parent", "groupParentChild", "ORPHAN ((Group File: '<group>'));(( _parent: '<value>')) in header frame does not match ((parent filename: '<parentFileName>))'"));
        checks.add(new FileBase.CheckMe("_dependsOn", "single", "multiple '_dependsOn' tags in group: '<group>'; with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_dependsOn", "monitorOrGroup", "'_dependsOn' tag in group: '<group>'; this monitor or group not found: '<value>'"));
        checks.add(new FileBase.CheckMe("_group", "subGroupRequired", "missing '_group' tag for a subGroup in group: '<group>'"));
        checks.add(new FileBase.CheckMe("_id", "subGroupRequired", "missing '_id' in group: '<group>'"));
        checks.add(new FileBase.CheckMe("_name", "subGroupRequired", "missing '_name' in group: '<group>'"));
        checks.add(new FileBase.CheckMe("_name", "monitorRequired", "missing '_name' in group: '<group>'"));
        checks.add(new FileBase.CheckMe("_name", "noDupValues", "duplicate '<tag>'s in group: '<group>' with value: '<value>'"));
        checks.add(new FileBase.CheckMe("_name", "single", "multiple '_name' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_name", "groupRequired", "missing '_name' in group header frame: '<group>'"));
        checks.add(new FileBase.CheckMe("_id", "single", "multiple '_id' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_id", "monitorRequired", "missing '_id' in group: '<group>'"));
        checks.add(new FileBase.CheckMe("_id", "number", "malformed '_id' in group: '<group>'; value: '<value>'; must be a counting number like 1,2,3..."));
        checks.add(new FileBase.CheckMe("_id", "range(0-1000000)", "'_id' out of range in group: '<group>'; value: '<value>' must be between: '0-1000000'"));
        checks.add(new FileBase.CheckMe("_id", "nextId(_nextID)", "'_id' in '<group>'; value: '<value>'; must be less than _nextID: '<nextId>' in the header frame."));
        checks.add(new FileBase.CheckMe("_id", "noDupValues", "duplicate '<tag>'s in group: '<group>' with value: '<value>'"));
        checks.add(new FileBase.CheckMe("_frequency", "single", "multiple '_frequency' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_frequency", "monitorRequired", "missing '_frequency' in group: '<group>'"));
        checks.add(new FileBase.CheckMe("_frequency", "number", "malformed '_frequency' in group: '<group>'; value: '<value>'; must be a number like 0,1,2,3..."));
        String s1 = "15";
        String s2 = COM.dragonflow.Utils.TextUtils.getSingleValue(masterConfig, "_monitorMinInterval");
        if(s2.length() > 0)
        {
            s1 = s2;
        }
        checks.add(new FileBase.CheckMe("_frequency", "range(" + s1 + "-864000000,0)600", "'_frequency' out of range in group: '<group>'; value: '<value>' must be between: '" + s1 + "-864000000'"));
        checks.add(new FileBase.CheckMe("_frequency", "frequencyTimeout", "'_frequency' in group: '<group>'; value: '<value>' is less than or equal to the _timeout: '<timeout>'. This may cause skipping."));
        checks.add(new FileBase.CheckMe("_timeout", "single", "multiple '_timeout' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_timeout", "number", "malformed '_timeout' in group: '<group>'; value: '<value>'; must be a number like 1,2,3..."));
        checks.add(new FileBase.CheckMe("_timeout", "range(0-360000)60", "'_timeout' out of range in group: '<group>'; value: '<value>' must be between: '0-360000'"));
        checks.add(new FileBase.CheckMe("_errorFrequency", "single", "multiple '_errorFrequency' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_errorFrequency", "number", "malformed '_errorFrequency' in group: '<group>'; value: '<value>'; must be a number like 0,1,2,3..."));
        checks.add(new FileBase.CheckMe("_errorFrequency", "range(15-864000000)600", "'_errorFrequency' out of range in group: '<group>'; value: '<value>' must be between: '15-864000000'"));
        checks.add(new FileBase.CheckMe("_errorFrequency", "frequencyTimeout", "'_errorFrequency' in group: '<group>'; value: '<value>' is less than or equal to the _timeout: '<timeout>'. This may cause skipping."));
        checks.add(new FileBase.CheckMe("_dependsOnRecursive", "single", "multiple '_dependsOnRecursive' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_notLogToTopaz", "single", "multiple '_notLogToTopaz' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_maxrun", "single", "multiple '_maxrun' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_maxrun", "number", "malformed '_maxrun' in group: '<group>'; value: '<value>'; must be a number like 0,1,2,3..."));
        checks.add(new FileBase.CheckMe("_maxrun", "range(0-864000000)600", "'_maxrun' out of range in group: '<group>'; value: '<value>' must be between: '0-864000000'"));
        checks.add(new FileBase.CheckMe("__template", "single", "multiple '__template' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_alertDisabled", "single", "multiple '_alertDisabled' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_alertLogName", "single", "multiple '_alertLogName' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_disabled", "single", "multiple '_disabled' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_disabledDescription", "single", "multiple '_disabledDescription' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_encoding", "single", "multiple '_encoding' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_errorLogName", "single", "multiple '_errorLogName' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_errorTextLimit", "single", "multiple '_errorTextLimit' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_errorTextLimit", "number", "malformed '_errorTextLimit' in group: '<group>'; value: '<value>'; must be a number like 0,1,2,3..."));
        checks.add(new FileBase.CheckMe("_errorTextLimit", "range(0-2500000)0", "'_errorTextLimit' out of range in group: '<group>'; value: '<value>' must be between: '1-2500000'"));
        checks.add(new FileBase.CheckMe("_goodTextLimit", "single", "multiple '_goodTextLimit' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_goodTextLimit", "number", "malformed '_goodTextLimit' in group: '<group>'; value: '<value>'; must be a number like 0,1,2,3..."));
        checks.add(new FileBase.CheckMe("_goodTextLimit", "range(0-2500000)0", "'_goodTextLimit' out of range in group: '<group>'; value: '<value>' must be between: '1-2500000'"));
        checks.add(new FileBase.CheckMe("_monitorDescription", "single", "multiple '_monitorDescription' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_schedule", "single", "multiple '_schedule' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_siteviewLogfilePath", "single", "multiple '_siteviewLogfilePath' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_siteViewLogName", "single", "multiple '_siteViewLogName' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_timedDisable", "single", "multiple '_timedDisable' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_urlLogName", "single", "multiple '_urlLogName' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_verifyError", "single", "multiple '_verifyError' tags in group: '<group>', with values: '<value>'"));
        checks.add(new FileBase.CheckMe("_dependsOn", "circular", "Circular group relationship on tag: '<tag>' for: '<group>'. Here is the list of monitors that cirularly depend on each other '<value>'"));
        checks.add(new FileBase.CheckMe("_item", "circular", "Circular group relationship on tag: '<tag>' for: '<group>'. Here is the list of monitors that cirularly depend on each other '<value>'"));
    }

    public String getClassName()
    {
        return "MgHealth";
    }

    public jgl.Array errorCheck()
    {
        buildTree();
        checkORPHANGroups();
        COM.dragonflow.Health.FileBase.DoCheck docheck = new FileBase.DoCheck();
        docheck.processTree(checks);
        return errorLog;
    }

    private void buildTree()
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
            if(as[j].endsWith(".mg"))
            {
                i++;
            }
        }

        mgFiles = new String[i];
        i = 0;
        for(int k = 0; k < as.length; k++)
        {
            if(as[k].endsWith(".mg"))
            {
                mgFiles[i++] = new String(as[k]);
            }
        }

        mgs = new Array();
        for(int l = 0; l < mgFiles.length;l++)/*dingbing.xu add l++*/
        {
            try
            {
                jgl.Array array = COM.dragonflow.Properties.FrameFile.readFromFile(COM.dragonflow.SiteView.Platform.getRoot() + groupsLocation + java.io.File.separator + mgFiles[l]);
                mgs.add(array);
                groupsHM.put(mgFiles[l], array);
                continue;
            }
            catch(java.lang.Exception exception)
            {
                String s = "MgHealth.buildTree() - Error with file : " + COM.dragonflow.SiteView.Platform.getRoot() + groupsLocation + java.io.File.separator + mgFiles[l];
                java.lang.System.out.println(s);
                exception.printStackTrace();
                COM.dragonflow.Log.LogManager.log("Error", "MgHealth.buildTree(): " + s + "\n" + COM.dragonflow.Utils.FileUtils.stackTraceText(exception));
                //l++;/*dingbing.xu delete l++*/
            }
        }

        for(int i1 = 0; i1 < mgFiles.length; i1++)
        {
            jgl.Array array1 = (jgl.Array)mgs.at(i1);
            if(array1.size() <= 0)
            {
                continue;
            }
            jgl.HashMap hashmap = (jgl.HashMap)array1.at(0);
            String s1 = (String)hashmap.get("_parent");
            if(s1 == null)
            {
                COM.dragonflow.Health.FileBase.HealthNode healthnode = new FileBase.HealthNode(null, 0, array1, mgFiles[i1]);
                fillTree(array1, healthnode, mgs);
            }
        }

    }

    private int nameToIndex(String s)
    {
        if(mgFiles != null)
        {
            for(int i = 0; i < mgFiles.length; i++)
            {
                if(mgFiles[i].equals(s + ".mg"))
                {
                    return i;
                }
            }

        }
        return -1;
    }

    public boolean fillTree(jgl.Array array, COM.dragonflow.Health.FileBase.HealthNode healthnode, jgl.Array array1)
    {
        for(int i = 1; i < array.size(); i++)
        {
            jgl.HashMap hashmap = (jgl.HashMap)array.at(i);
            String s = checkSingleValue(healthnode, hashmap, "_id", "frame number: '" + i + "'");
            String s1 = checkSingleValue(healthnode, hashmap, "_class", s);
            checkSingleValue(healthnode, hashmap, "_name", s);
            boolean flag;
            if(s1.equals("SubGroup"))
            {
                String s2 = checkSingleValue(healthnode, hashmap, "_group", s);
                int j = nameToIndex(s2);
                jgl.Array array2 = null;
                if(j >= 0 && j < array1.size())
                {
                    array2 = (jgl.Array)array1.at(j);
                }
                String s3 = "ERROR, Group File: '" + healthnode.fileName + "' did not find sub-group file(_group=): '" + s2 + "' with ID: '" + s + "'";
                COM.dragonflow.Health.FileBase.ErrorMessage errormessage = new FileBase.ErrorMessage("OrphanGroup", s3, healthnode.fileName);
                if(!checkCondition(array2 != null, errormessage))
                {
                    continue;
                }
                if(!findSelfUpLine(healthnode, s2))
                {
                    COM.dragonflow.Health.FileBase.HealthNode healthnode1 = new FileBase.HealthNode(healthnode, i, array2, s2 + ".mg");
                    if(healthnode1.nodeOK)
                    {
                        fillTree(array2, healthnode1, array1);
                    }
                } else
                {
                    String s4 = "SubGroup: '" + s2 + "' with ID: '" + s + "'in Group file: '" + healthnode.fileName + " found itself upline.";
                    COM.dragonflow.Health.FileBase.ErrorMessage errormessage1 = new FileBase.ErrorMessage("OrphanGroup", s4, healthnode.fileName);
                    checkCondition(false, errormessage1);
                    return false;
                }
            } else
            {
                flag = addMonitorNames(hashmap, healthnode.fileName);
            }
        }

        return true;
    }

    private String checkSingleValue(COM.dragonflow.Health.FileBase.HealthNode healthnode, jgl.HashMap hashmap, String s, String s1)
    {
        jgl.Array array = COM.dragonflow.Utils.TextUtils.getMultipleValues(hashmap, s);
        COM.dragonflow.Health.FileBase.ErrorMessage errormessage = null;
        String s2 = "Group File: " + healthnode.fileName + " blank '" + s + "' tag in frame with ID: '" + s1 + "'.";
        errormessage = new FileBase.ErrorMessage("OrphanGroup", s2, healthnode.fileName);
        if(checkCondition(array.size() > 0, errormessage))
        {
            String s3 = "ERROR, Group File: '" + healthnode.fileName + "' multiple '" + s + "' tags found. Values: '" + array.toString() + "'.";
            COM.dragonflow.Health.FileBase.ErrorMessage errormessage1 = new FileBase.ErrorMessage("OrphanGroup", s3, healthnode.fileName);
            checkCondition(array.size() <= 1, errormessage1);
            s3 = "Group File: " + healthnode.fileName + " missing '" + s + "' tag in frame with ID: '" + s1 + "'.";
            errormessage1 = new FileBase.ErrorMessage("OrphanGroup", s3, healthnode.fileName);
            checkCondition(array.size() != 0, errormessage1);
            s3 = "Group File: " + healthnode.fileName + " blank '" + s + "' tag in frame with ID: '" + s1 + "'.";
            errormessage1 = new FileBase.ErrorMessage("OrphanGroup", s3, healthnode.fileName);
            checkCondition(((String)array.at(0)).length() > 0, errormessage1);
            return (String)array.at(0);
        } else
        {
            return "";
        }
    }

    private void checkORPHANGroups()
    {
        for(int i = 0; i < mgFiles.length; i++)
        {
            String s = mgFiles[i];
            String s1 = s;
            int l = s.lastIndexOf(".mg");
            if(l > 0)
            {
                s1 = s.substring(0, l);
            }
            jgl.Array array = (jgl.Array)mgs.at(i);
            String s2 = "error.log MgHealth: Group: '" + s + "' file is empty; it has no lines.";
            COM.dragonflow.Health.FileBase.ErrorMessage errormessage = new FileBase.ErrorMessage("OrphanGroup", s2, s);
            if(!checkCondition(array.size() > 0, errormessage))
            {
                continue;
            }
            String s3 = COM.dragonflow.Utils.TextUtils.getValue((jgl.HashMap)array.at(0), "_parent");
            if(s3.length() <= 0 || hasNode(s))
            {
                continue;
            }
            java.lang.Object obj = groupsHM.get(s3 + ".mg");
            s2 = "error.log MgHealth: ORPHAN Group: '" + s + "'  has tag _parent=" + s3 + ". '" + s3 + ".mg' group file does not exist. Look in groups directory for these files.";
            errormessage = new FileBase.ErrorMessage("OrphanGroup", s2, s);
            checkCondition(obj != null, errormessage);
            boolean flag = false;
            if(!(obj instanceof jgl.Array))
            {
                continue;
            }
            jgl.Array array1 = new Array();
            array1 = (jgl.Array)obj;
            int i1 = 0;
            do
            {
                if(i1 >= array1.size())
                {
                    break;
                }
                jgl.HashMap hashmap = (jgl.HashMap)array1.at(i1);
                String s4 = (String)hashmap.get("_group");
                if(s4 != null && s4.equals(s1))
                {
                    flag = true;
                    break;
                }
                i1++;
            } while(true);
            s2 = "error.log MgHealth: ORPHAN Group: '" + s + "'  has tag _parent=" + s3 + ". '" + s3 + ".mg' group file exists, but is missing a subgroup frame with _group=" + s1;
            errormessage = new FileBase.ErrorMessage("OrphanGroup", s2, s);
            checkCondition(flag, errormessage);
            s2 = "error.log MgHealth: ORPHAN Group: '" + s + "'  has tag _parent=" + s3 + ". '" + s3 + ".mg' group file exists, and has a subgroup frame with _group=" + s1 + ", but group: " + s + " is orphan, upline parent may be orphan.";
            errormessage = new FileBase.ErrorMessage("OrphanGroup", s2, s);
            checkCondition(!flag, errormessage);
        }

        for(int j = 0; j < tree.size(); j++)
        {
            COM.dragonflow.Health.FileBase.HealthNode healthnode = (COM.dragonflow.Health.FileBase.HealthNode)tree.at(j);
            for(int k = 0; k < healthnode.children.size(); k++)
            {
                checkParentChild(healthnode, (COM.dragonflow.Health.FileBase.HealthNode)healthnode.children.at(k));
            }

        }

    }

    private void checkParentChild(COM.dragonflow.Health.FileBase.HealthNode healthnode, COM.dragonflow.Health.FileBase.HealthNode healthnode1)
    {
        jgl.Array array = COM.dragonflow.Utils.TextUtils.getMultipleValues((jgl.HashMap)getAt(healthnode.frames, healthnode1.parentFrameIndex, "jgl.HashMap"), "_name");
        String s = (String)getAt(array, 0, "String");
        jgl.Array array1 = COM.dragonflow.Utils.TextUtils.getMultipleValues((jgl.HashMap)getAt(healthnode1.frames, 0, "jgl.HashMap"), "_name");
        String s1 = (String)getAt(array1, 0, "String");
        boolean flag = s1.equals("config");
        boolean flag1 = s1.equals(s);
        boolean flag2 = healthnode1.fileName.equals(s + ".mg");
        String s2 = "ERROR, ORPHAN ((Group FileName: '" + healthnode1.fileName + "')); (( _name in header: '" + s1 + "')); ((_name in Parent Frame: '" + s + "))';... If ((_name in header)) == 'config' then ((_name in Parent Frame)) must == ((Group FileName))." + ".. If ((_name in header)) != 'config' then ((_name in header)) must == ((_name in Parent Frame)).";
        COM.dragonflow.Health.FileBase.ErrorMessage errormessage = new FileBase.ErrorMessage("NameConsistency", s2, healthnode1.fileName);
        checkCondition(flag && flag2 || flag1, errormessage);
        for(int i = 0; i < healthnode1.children.size(); i++)
        {
            checkParentChild(healthnode1, (COM.dragonflow.Health.FileBase.HealthNode)healthnode1.children.at(i));
        }

    }

    public boolean findSelfUpLine(COM.dragonflow.Health.FileBase.HealthNode healthnode, String s)
    {
        if(!healthnode.fileName.equals(s + ".mg"))
        {
            if(healthnode.parent == null)
            {
                return false;
            }
            return findSelfUpLine(healthnode.parent, s);
        } else
        {
            return true;
        }
    }

    public boolean addMonitorNames(jgl.HashMap hashmap, String s)
    {
        int i = s.lastIndexOf(".mg");
        String s1 = s.substring(0, i);
        java.lang.Object obj = hashmap.get("_id");
        java.lang.Object obj1 = hashmap.get("_name");
        if(obj1 != null && !obj1.equals(""))
        {
            monitorAndGroupNames.add(obj1);
        }
        if(s != null && !s.equals("") && obj != null && !obj.equals(""))
        {
            monitorAndGroupNames.add(obj + " " + s1);
            monitorAndGroupNames.add(s1 + " " + obj);
            circularHm.put(s1 + " " + obj, hashmap);
        }
        return true;
    }

    public String toString()
    {
        return "Mg Health - checks groups/*.mg files which holds SiteView monitor definitions set up by the user";
    }

    public static void main(String args[])
    {
    }
}
