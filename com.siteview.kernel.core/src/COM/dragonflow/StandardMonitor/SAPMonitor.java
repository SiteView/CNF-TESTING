/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * SAPMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>SAPMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import jgl.Array;

public class SAPMonitor extends BrowsableExeBase
{

    static StringProperty pSAPServer;
    static StringProperty pSystem;
    static StringProperty pClient;
    static StringProperty pUsername;
    static StringProperty pPassword;
    static String monDll = "sapmon.dll";

    public SAPMonitor()
    {
    }

    public String getHostname()
    {
        return getProperty(pSAPServer);
    }

    public Array getConnectionProperties()
    {
        Array array = new Array();
        array.add(pSAPServer);
        array.add(pSystem);
        array.add(pClient);
        array.add(pUsername);
        array.add(pPassword);
        return array;
    }

    protected String getMonDll()
    {
        return monDll;
    }

    public String getBrowseData(StringBuffer stringbuffer)
    {
        return "<browse_data>\t<object name='Database Performance' desc=''>\t\t<counter name='Calls - Parses' id='Database Performance/Calls - Parses' desc='Number of parses runs. This number should be reduced by application tuning if possible.'/>\t\t<counter name='Calls - Reads / User calls' id='Database Performance/Calls - Reads / User calls' desc='Ratio of reads to user calls in Oracle7.'/>\t\t<counter name='Calls - Recursive calls' id='Database Performance/Calls - Recursive calls' desc='Number of recursive database calls. A high value indicates the information was frequently not found in the dictionary buffer. The dictionary buffer may be sized too small or has been parsed too many times.'/>\t\t<counter name='Calls - User calls' id='Database Performance/Calls - User calls' desc='The number of user calls started for the database.'/>\t\t<counter name='Calls - User/Recursive calls' id='Database Performance/Calls - User/Recursive calls' desc='Ratio of user to recursive calls in Oracle7 monitor.'/>\t\t<counter name='Calls - commits' id='Database Performance/Calls - commits' desc='The number of commits carried out (finished transactions).'/>\t\t<counter name='Calls - rollbacks' id='Database Performance/Calls - rollbacks' desc='The number of the executed rollbacks (rolling back incomplete transactions).'/>\t\t<counter name='Data buffer - Buffer busy waits' id='Database Performance/Data buffer - Buffer busy waits' desc='No Description'/>\t\t<counter name='Data buffer - Buffer wait time   s' id='Database Performance/Data buffer - Buffer wait time   s' desc='No Description'/>\t\t<counter name='Data buffer - Physical reads' id='Database Performance/Data buffer - Physical reads' desc='Number of database blocks read physically from the hard disk.'/>\t\t<counter name='Data buffer - Quality            %' id='Database Performance/Data buffer - Quality            %' desc='The quality specifies how high the percentage of the reads without hard disk accesses from the total quantity of read accesses to the database buffer pool is.'/>\t\t<counter name='Data buffer - Reads' id='Database Performance/Data buffer - Reads' desc='The number of read accesses which could be satisfied directly (without hard disk access) from the database buffer pool.'/>\t\t<counter name='Data buffer - Size              kb' id='Database Performance/Data buffer - Size              kb' desc='The size of the databse pool,which holds the database blocks in the working storage and therefore, reduces time-consuming hard disk accesses.'/>\t\t<counter name='Data buffer - writes' id='Database Performance/Data buffer - writes' desc='No Description'/>\t\t<counter name='Log buffer - Alloc fault rate   %' id='Database Performance/Log buffer - Alloc fault rate   %' desc='The percentage of aborted allocation attempts (allocation fault) from the total quantity of entries in the redo log buffer (entries).'/>\t\t<counter name='Log buffer - Allocation retries' id='Database Performance/Log buffer - Allocation retries' desc='Frequency, with which a user process could not allocate sufficient space in the redo log buffer and had to start a further attempt. This case can then occur if the redo writer is working less efficiently or if an event occurs such as a log switch.'/>\t\t<counter name='Log buffer - Entries' id='Database Performance/Log buffer - Entries' desc='The number of entries in the redo log buffer.'/>\t\t<counter name='Log buffer - Log files (in use)' id='Database Performance/Log buffer - Log files (in use)' desc='Number of redo log files.'/>\t\t<counter name='Log buffer - Redo log wait      s' id='Database Performance/Log buffer - Redo log wait      s' desc='Redo log space wait time (s) from v$sysstat'/>\t\t<counter name='Log buffer - Size              kb' id='Database Performance/Log buffer - Size              kb' desc='Size of the redo log buffer in KB.In the redo log buffer, all changes to the database are logged.'/>\t\t<counter name='Redo logging - Latching time      s' id='Database Performance/Redo logging - Latching time      s' desc='The time which the logwriter process needed to recieve and release the necessary latches (short-term locks of data structures) to the redo log.'/>\t\t<counter name='Redo logging - Mb written' id='Database Performance/Redo logging - Mb written' desc='The number of MB which were written to the redo log file.'/>\t\t<counter name='Redo logging - OS-Blocks written' id='Database Performance/Redo logging - OS-Blocks written' desc='number of blocks which were written into the redo log file.'/>\t\t<counter name='Redo logging - Write time         s' id='Database Performance/Redo logging - Write time         s' desc='The time in milliseconds which was needed for writing to the redo log file.'/>\t\t<counter name='Redo logging - Writes' id='Database Performance/Redo logging - Writes' desc='the number of write operations which were executed on the redo log file. A database background process, the logwriter, cyclically transfers the contents of the redo log buffer into the redo log file.'/>\t\t<counter name='Shared Pool - DD-Cache quality   %' id='Database Performance/Shared Pool - DD-Cache quality   %' desc='Quality of the data dictionary cache in Oracle7.This value specifies how frequently the Oracle data dictionary has to be accessed when processing SQL commands. In the installed system, this value should at least be 90%.'/>\t\t<counter name='Shared Pool - SQL Area getratio  %' id='Database Performance/Shared Pool - SQL Area getratio  %' desc='Hit rate for the number of requests of objects which are managed in the library cache of Oracle7. A high value suggests a good sizing of the shared pool.'/>\t\t<counter name='Shared Pool - Size              kb' id='Database Performance/Shared Pool - Size              kb' desc='Size of the dictionary buffer in KB.The dictionary buffer has the function to store data dictionary information which is frequently required in the working storage which facilitates the acess.'/>\t\t<counter name='Shared Pool - pinratio  %' id='Database Performance/Shared Pool - pinratio  %' desc='Hit rate for the number of executions of objects which are managed in the library cache of Oracle7. A high value suggests a good sizing of the shared pool.'/>\t\t<counter name='Shared Pool - reloads/pins %' id='Database Performance/Shared Pool - reloads/pins %' desc='SQL area reloads (%) from v$librarycache'/>\t\t<counter name='Sorts - Disk' id='Database Performance/Sorts - Disk' desc='The number of sorting operations which were carried out on the hard disk in the temporary segments provided for this. This is necessary if sorting operations cannot be handled completely in memory.'/>\t\t<counter name='Sorts - Memory' id='Database Performance/Sorts - Memory' desc='The number of sorting operations which could be carried out in memory.'/>\t\t<counter name='Sorts - Rows sorted' id='Database Performance/Sorts - Rows sorted' desc='Number of lines which were sorted.'/>\t\t<counter name='Table scans &amp; fetches - Fetch by rowid' id='Database Performance/Table scans &amp; fetches - Fetch by rowid' desc='The number of blocks which were read via ROWID. the ROWID is a logical byte address which identifies every data record in the database and links it with the index value.'/>\t\t<counter name='Table scans &amp; fetches - Long table scans' id='Database Performance/Table scans &amp; fetches - Long table scans' desc='Number of read access to tables with at least 4 database blocks.'/>\t\t<counter name='Table scans &amp; fetches - Short table scans' id='Database Performance/Table scans &amp; fetches - Short table scans' desc='Number of read access to tables with a maximum size of 4 database blocks.'/>\t\t<counter name='Table scans &amp; fetches - by continued row' id='Database Performance/Table scans &amp; fetches - by continued row' desc='Number of accesses to concatenated database blocks (row chaining).The number should be as low as possible, otherwise, the affected tables should be reorganized.'/>\t\t<counter name='Time statistics - Busy wait time     s' id='Database Performance/Time statistics - Busy wait time     s' desc='Oracle: Busy wait time (from v$System_Event) (sec).'/>\t\t<counter name='Time statistics - CPU count' id='Database Performance/Time statistics - CPU count' desc='Number of CPUs.'/>\t\t<counter name='Time statistics - CPU time           s' id='Database Performance/Time statistics - CPU time           s' desc='Oracle: Total CPU time used by Oracle (sec).'/>\t\t<counter name='Time statistics - CPU usage          %' id='Database Performance/Time statistics - CPU usage          %' desc='Oracle: CPU usage by Oracle (%).'/>\t\t<counter name='Time statistics - Sessions busy      %' id='Database Performance/Time statistics - Sessions busy      %' desc='Oracle: Sessions busy (%).'/>\t\t<counter name='Time statistics - Time/User call    ms' id='Database Performance/Time statistics - Time/User call    ms' desc='Oracle: Time per user call (ms).'/>\t</object>\t<object name='Workload' desc=''>\t\t<counter name='CPU Time' id='Workload/CPU Time' desc='CPU Time consumed'/>\t\t<counter name='Dialog steps' id='Workload/Dialog steps' desc='Dialog steps'/>\t\t<counter name='Average CPU time' id='Workload/Average CPU time' desc='Average CPU time used in the work process.    During a dialog step, the CPU of the application server is used for processing (loading, generating, database request processing, ABAP processing and so on).    The CPU time is determined by the operating system. At the end of a transaction step, the R/3 work process queries the CPU time from the operating system. The CPU time is therefore not an additive component of the response time, unlike the wait, roll-in, load and database time.  '/>\t\t<counter name='Av. RFC+CPIC time' id='Workload/Av. RFC+CPIC time' desc='Average RFC &amp; CPIC time'/>\t\t<counter name='Av. response time' id='Workload/Av. response time' desc='The average response time of a dialog step is the average time measured from the time a dialog sends a request to the dispatcher work process through the processing of the dialog up to the time the dialog is completed and the data is passed to the presentation layer. The response time between the SAP GUI and the dispatcher is not included in this value.    The response time does NOT include the time taken to transfer data from the R/3 frontend to the application server. For networks with low performance, this can create a highly subjective response time. The transfer time is contained in the GUI network time.    Response time is usually split into wait time plus dispatch time.    The CPU time is not an additive component of the response time, but the sum of the CPU time that is used by the individual components. The CPU time therefore provides additional, independent information on the response time.  '/>\t\t<counter name='Average wait time' id='Workload/Average wait time' desc='The time an unprocessed dialog step waits in the dispatcher queue for a free work process.    Under normal conditions, the dispatcher work process should pass a dialog step to the application process immediately after receiving the request from the dialog step. Under these conditions, the average wait time would be a few milliseconds. A heavy load on the application server or on the entire system causes queues at the dispatcher queue.  '/>\t\t<counter name='Average load time' id='Workload/Average load time' desc='The time needed to load and generate objects such as ABAP source code and screen information from the database.'/>\t\t<counter name='Av. Roll i+w time' id='Workload/Av. Roll i+w time' desc='Average Roll i+w time'/>\t\t<counter name='Av. DB req. time' id='Workload/Av. DB req. time' desc='Average Database request time'/>\t\t<counter name='Av. enqueue time' id='Workload/Av. enqueue time' desc='Average enqueue time'/>\t\t<counter name='Average bytes req.' id='Workload/Average bytes req.' desc='Average bytes per requests'/>\t\t<counter name='Database calls' id='Workload/Database calls' desc='The number of parsed requests sent to the database'/>\t\t<counter name='Database requests' id='Workload/Database requests' desc='The number of logical ABAP requests for data in the database. These requests are passed through the R/3 database interface and parsed into individual database calls.    The proportion of database calls to database requests is important. If access to information in a table is buffered in the SAP buffers, database calls to the database server are not required. Therefore, the ratio of calls/requests gives an overall indication of the efficiency of table buffering. A good ratio would be 1:10.  '/>\t\t<counter name='DB Calls: Direct reads' id='Workload/DB Calls: Direct reads' desc='Database Calls: Direct reads'/>\t\t<counter name='DB Calls: Sequential reads' id='Workload/DB Calls: Sequential reads' desc='Database Calls: Sequential reads'/>\t\t<counter name='DB Calls: Changes' id='Workload/DB Calls: Changes' desc='Database Calls: Changes'/>\t\t<counter name='Time per DB request' id='Workload/Time per DB request' desc='Average response time for all commands sent to the database system (in milliseconds). The time depends on the CPU capacity of the database server, network, buffering, and on the input/output capabilities of the database server. Access times for buffered tables are many magnitudes faster and are not considered in the measurement.'/>\t\t<counter name='Time per Req.: Direct reads' id='Workload/Time per Req.: Direct reads' desc='Time per Database request.: Direct reads'/>\t\t<counter name='Time per Req.: Sequential reads' id='Workload/Time per Req.: Sequential reads' desc='Time per Database request.: Sequential reads'/>\t\t<counter name='Time per Req.: Changes and commits' id='Workload/Time per Req.: Changes and commits' desc='Time per Database request.: Changes and commits'/>\t\t<counter name='Roll-in time' id='Workload/Roll-in time' desc='Processing time for roll ins.'/>\t\t<counter name='Roll-out time' id='Workload/Roll-out time' desc='Processing time for roll outs.'/>\t\t<counter name='Roll wait time' id='Workload/Roll wait time' desc='Queue time in the roll area. When synchronous RFCs are called, the work process executes a roll out and may have to wait for the end of the RFC in the roll area, even if the dialog step is not yet completed. In the roll area, RFC server programs can also wait for other RFCs sent to them.'/>\t\t<counter name='Roll-ins' id='Workload/Roll-ins' desc='Number of rolled-in user contexts.'/>\t\t<counter name='Roll-outs' id='Workload/Roll-outs' desc='Number of rolled-out user contexts.'/>\t</object></browse_data>";
    }

    public static void main(String args[])
    {
    }

    static 
    {
        Array array = new Array();
        pSAPServer = new StringProperty("_server", "");
        pSAPServer.setDisplayText("Server", "SAP server name.");
        pSAPServer.setParameterOptions(false, true, BrowsableBase.COUNTER_PROPERTY_INDEX + 1, false);
        array.add(pSAPServer);
        pSystem = new StringProperty("_system", "00");
        pSystem.setDisplayText("System", "Enter the System number for SAP");
        pSystem.setParameterOptions(false, true, BrowsableBase.COUNTER_PROPERTY_INDEX + 2, false);
        array.add(pSystem);
        pClient = new StringProperty("_client");
        pClient.setDisplayText("Client", "Enter the Client for SAP, leave blank for default");
        pClient.setParameterOptions(false, true, BrowsableBase.COUNTER_PROPERTY_INDEX + 3, false);
        array.add(pClient);
        pUsername = new StringProperty("_username");
        pUsername.setDisplayText("Username", "Enter the Username for SAP");
        pUsername.setParameterOptions(false, true, BrowsableBase.COUNTER_PROPERTY_INDEX + 4, false);
        array.add(pUsername);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Password", "Enter the Password for SAP");
        pUsername.setParameterOptions(false, true, BrowsableBase.COUNTER_PROPERTY_INDEX + 5, false);
        pPassword.isPassword = true;
        array.add(pPassword);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for(int i = 0; i < array.size(); i++)
        {
            astringproperty[i] = (StringProperty)array.at(i);
        }

        String s = (COM.dragonflow.StandardMonitor.SAPMonitor.class).getName();
        addProperties(s, astringproperty);
        addClassElement(s, Rule.stringToClassifier("countersInError > 0\terror", true));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s, "description", "Monitor statistics from a SAP application server");
        setClassProperty(s, "help", "SAPMon.htm");
        setClassProperty(s, "title", "SAP");
        setClassProperty(s, "class", "SAPMonitor");
        setClassProperty(s, "target", "_server");
        setClassProperty(s, "topazName", "SAP");
        setClassProperty(s, "classType", "application");
        setClassProperty(s, "topazType", "ERP");
        if(!Platform.isWindows())
        {
            setClassProperty(s, "loadable", "false");
        }
    }
}
