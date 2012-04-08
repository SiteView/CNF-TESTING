/*
 * 
 * Created on 2005-2-16 17:39:16
 *
 * VirtualMachine.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>VirtualMachine</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Page.monitorSetPage;
import COM.dragonflow.Page.monitorSetTemplate;
import COM.dragonflow.Page.vMachinePage;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewObject, VMachineRunner, MonitorGroup, SiteViewGroup,
// Scheduler, BrowsableSNMPBase, DetectConfigurationChange, AtomicMonitor,
// MasterConfig, Monitor, Platform, Action

public class VirtualMachine extends SiteViewObject {

	public static StringProperty pHost;

	public static StringProperty pID;

	public static StringProperty pParent;

	public static StringProperty pRootOID;

	public static StringProperty pMonitorSet;

	public static StringProperty pCheckFrequency;

	public static StringProperty pGroup;

	public static StringProperty pCommunity;

	public static StringProperty pNodes;

	public static StringProperty pRetryDelay = new NumericProperty(
			"_retryDelay", "1", "seconds");

	public static StringProperty pName;

	public static StringProperty pExcludeIP;

	public static StringProperty pGroupSet;

	public static StringProperty pOtherOID;

	public static StringProperty pPoolIncluded;

	public static StringProperty pDBConnectionURL;

	public static StringProperty pDBDriver;

	public static StringProperty pDBSqlQuery;

	public static StringProperty pDBUsername;

	public static StringProperty pDBPassword;

	public static StringProperty pDBConnectTimeout;

	public static StringProperty pDBQueryTimeout;

	Action action;

	HTTPRequest request;

	boolean driverLoaded;

	Array singleframes;

	public VirtualMachine() {
		action = null;
		request = null;
		driverLoaded = false;
		singleframes = new Array();
	}

	public void schedule() {
		if (action == null) {
			action = new VMachineRunner(this);
			Scheduler scheduler = SiteViewGroup.currentSiteView().vMachineScheduler;
			try {
				scheduler.scheduleRepeatedPeriodicAction(action, TextUtils
						.toLong(getProperty(pCheckFrequency)) * 1000L);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	/**
	 * CAUTION: Decompiled by hand.
	 * 
	 * @param stringBuffer
	 * @param queryString
	 * @param driverClassName
	 * @param url
	 * @param user
	 * @param password
	 * @param loginTimeout
	 * @param queryTimeout
	 * @return
	 */
	public jgl.Array getDBResults(StringBuffer stringBuffer,
			String queryString, String driverClassName, String url,
			String user, String password, int loginTimeout, int queryTimeout) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		Array array = new Array();
		int columnCount;
		try {
			if (!this.driverLoaded) {
				LogManager.log("RunMonitor", "DB monitor, loading, driver="
						+ driverClassName);
				Class.forName(driverClassName).newInstance();
				this.driverLoaded = true;
			}
			LogManager.log("RunMonitor", "DB monitor, connecting, database="
					+ url + ", " + user);
			if (loginTimeout > 0) {
				DriverManager.setLoginTimeout(loginTimeout);
			}
			connection = DriverManager.getConnection(url, user, password);
			statement = connection.createStatement();
			if (queryTimeout > 0) {
				statement.setQueryTimeout(queryTimeout);
			}
			resultSet = statement.executeQuery(queryString);
			if (resultSet == null) {
				stringBuffer.append("no results from query");
				throw new Exception("no results from query");
			}
			columnCount = resultSet.getMetaData().getColumnCount();
			while (resultSet.next()) {
				try {
					String s5 = "";
					for (int i = 1; i <= columnCount; i++) {
						s5 = s5 + resultSet.getString(i);
						if (i < columnCount) {
							s5 = s5 + "\t";
						}
					}
					array.add(s5);
				} catch (Exception e) {
					stringBuffer.append(e.getMessage());
				}
			}
		} catch (SQLException e) {
			stringBuffer.append(" error, " + e.getMessage());
			while (e != null) {
				LogManager.log("Error", "DB error , " + e.getMessage() + ", "
						+ e.getErrorCode() + ", " + e.getSQLState());
				e = e.getNextException();
			}
		} catch (Throwable e) {
			stringBuffer.append(e.getMessage());
			LogManager.log("Error", "DB error, , " + e.getMessage() + ", " + e);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (Exception e) {
					LogManager.log("Error", "DBclose resultSet error, , " + e);
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (Exception e) {
					LogManager.log("Error",
							"Database close statement error, , "
									+ getGroupPathID() + ": "
									+ getProperty(VirtualMachine.pName) + ", "
									+ e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					LogManager.log("Error",
							"Database close connection error, , "
									+ getGroupPathID() + ": "
									+ getProperty(VirtualMachine.pName) + ", "
									+ e);
				}
			}
		}

		return array;
	}

	public String getProperty(StringProperty stringproperty) {
		return super.getProperty(stringproperty);
	}

	private Array checkDB(StringBuffer stringbuffer) {
		String s = getProperty(VirtualMachine.pDBConnectionURL);
		String s1 = getProperty(VirtualMachine.pDBDriver);
		String s2 = getProperty(VirtualMachine.pDBSqlQuery);
		String s3 = getProperty(VirtualMachine.pDBUsername);
		String s4 = getProperty(VirtualMachine.pDBPassword);
		int i = StringProperty
				.toInteger(getProperty(VirtualMachine.pDBConnectTimeout));
		int j = StringProperty
				.toInteger(getProperty(VirtualMachine.pDBQueryTimeout));
		return getDBResults(stringbuffer, s2, s1, s, s3, s4, i, j);
	}

	/**
	 * CAUTION: Decompiled by hand.
	 * 
	 */
/*	public synchronized void checkForNodes() {
		// dingbing.xu important
		String pID = I18N.toDefaultEncoding(getProperty(VirtualMachine.pID));
		String pRootOID = getProperty(VirtualMachine.pRootOID);
		String pOtherOID = getProperty(VirtualMachine.pOtherOID);
		String pHost = getProperty(VirtualMachine.pHost);
		String pNodes = getProperty(VirtualMachine.pNodes);
		String pNodesArray[] = TextUtils.split(pNodes, "\t");
		String pRetryDelay = getProperty(VirtualMachine.pRetryDelay);
		String pCommunity = getProperty(VirtualMachine.pCommunity);
		String pMonitorSet = getProperty(VirtualMachine.pMonitorSet);
		String pGroup = I18N
				.toDefaultEncoding(getProperty(VirtualMachine.pGroup));
		String pGroupSet = getProperty(VirtualMachine.pGroupSet);
		String pPoolIncluded = getProperty(VirtualMachine.pPoolIncluded);
		
		Array array = vMachinePage.readDynamicSets();
		Array array1 = new Array();
		HashMap hashmap = new HashMap();
		
		Enumeration enumeration = array.elements();
		while (enumeration.hasMoreElements()) {
			hashmap = (HashMap) enumeration.nextElement();
			if (hashmap.get("_id").equals(pID)) {
				break;
			}
		}
		HashMap hashmap1 = new HashMap();
		StringBuffer stringbuffer = new StringBuffer();
		this.request = new HTTPRequest();
		boolean flag = getProperty(VirtualMachine.pDBConnectionURL).length() > 0;
		if (flag) {
			array1 = checkDB(stringbuffer);
		} else {
			if (pOtherOID.length() > 0) {
				pRootOID = pOtherOID;
			} else if (pRootOID.indexOf("-(") > 0) {
				pRootOID = pRootOID.substring(pRootOID.indexOf("-(") + 2,
						pRootOID.length() - 1);
			}
			String s11 = pRootOID.indexOf(pHost) < 0 ? pRootOID : pRootOID
					.substring(0, pRootOID.indexOf(pHost));
			this.request.setUser("administrator");
			Array array3 = new Array();
			if (pRootOID.indexOf("++") < 0) {
				pRootOID = pRootOID + "++";
			}
			while (true) {
				String s12 = BrowsableSNMPBase.readSNMPValue(pHost,
						"Dynamic Upadate: " + pHost, 20, TextUtils
								.toInt(pRetryDelay), pCommunity, stringbuffer,
						pRootOID, "0", false);
				if (s12.length() <= 0 || s12.indexOf(s11) <= 0) {
					break;
				}
				String s13 = "";
				if (s12.startsWith(".")) {
					s13 = s12.substring(1);
				}
				s13 = s13.substring(s11.length() + 1);
				if (pPoolIncluded.length() <= 0) {
					array1.add(s13);
				} else {
					StringBuffer stringbuffer1 = new StringBuffer();
					s13 = extractPoolFromIP(stringbuffer1, s13);
					array1.add(s13);
					array3.add(stringbuffer1);
					hashmap1.put(s13, stringbuffer1.toString());
				}
				s13 = s12.substring(s12.lastIndexOf("."), s12.length());
				if (s13.equals(".0")) {
					pRootOID = s12.substring(0, s12.lastIndexOf(s13)) + "++";
				} else {
					pRootOID = s12 + "++";
				}
			}
		}
		
		
		Array array2 = new Array();
		String pExcludeIPArray[] = TextUtils.split(getProperty(VirtualMachine.pExcludeIP),
				",");
		for (int i = 0; i < pExcludeIPArray.length; i++) {
			int l = pExcludeIPArray[i].indexOf("*");
			if (l >= 0) {
				pExcludeIPArray[i] = pExcludeIPArray[i].substring(0, l);
				for (int i1 = 0; i1 < array1.size(); i++) {
					String s16 = (String) array1.at(i1);
					if (s16.startsWith(pExcludeIPArray[i])) {
						array1.remove(s16);
						if (pPoolIncluded.length() > 0) {
							hashmap1.remove(s16);
						}
					}
				}
			}
			array1.remove(pExcludeIPArray[i]);
			if (pPoolIncluded.length() > 0) {
				hashmap1.remove(pExcludeIPArray[i]);
			}
		}
		
		
		array1.remove(pHost);
		for (int j = 0; j < array1.size(); j++) {
			String s14 = ((String) array1.at(j)).trim();
			boolean flag1 = false;
			for (int j1 = 0; j1 < pNodesArray.length; j1++) {
				String s20 = pNodesArray[j1].trim();
				if (s20.equals(s14)) {
					flag1 = true;
					break;
				}
			}

			if (!flag1) {
				array2.add(s14);
			}
		}

		stringbuffer = new StringBuffer();
		if (pGroupSet.indexOf("$NODE-IP$") < 0
				&& pGroupSet.indexOf("$POOL-ID$") < 0) {
			try {
				singleframes = CGI.ReadGroupFrames(pGroup, request);
			} catch (IOException ioexception) {
				ioexception.printStackTrace();
			}
		}
		
		
		for (int k = 0; k < array2.size(); k++) {
			Array array4 = new Array();
			if (pPoolIncluded.length() > 0 && !flag) {
				array4.add(pHost);
			}
			array4.add((String) array2.at(k));
			String s15 = "";
			if (pPoolIncluded.length() > 0) {
				String s17 = (String) hashmap1.get((String) array2.at(k));
				if (s17 != null) {
					array4.add(s17);
					s15 = s17;
				}
			}
			if (pGroupSet.indexOf("$NODE-IP$") >= 0) {
				String s18 = flag ? TextUtils.replaceString(pGroupSet,
						"$NODE-IP$", (String) array2.at(k)) : TextUtils
						.replaceString(pGroupSet, "$NODE-IP$", (String) array4
								.at(1));
				s18 = TextUtils
						.keepChars(s18,
								"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
				try {
					boolean myflag=vMachinePage.postToGroupPage(s18, pGroup, request,
							FileUtils.MakeOutputWriter(System.out), false);
					if (!myflag) {
						continue;
					}
				} catch (IOException ioexception2) {
					ioexception2.printStackTrace();
				}
				createGroupFromTemplate(pMonitorSet, s18, array4, stringbuffer,
						pPoolIncluded, false);
			} else if (pGroupSet.indexOf("$POOL-ID$") >= 0) {
				String s19 = "1.3.6.1.4.1.3375.1.1.7.2.1.1." + s15;
				StringBuffer stringbuffer2 = new StringBuffer();
				String s21 = BrowsableSNMPBase.readSNMPValue(pHost,
						"Dynamic Upadate: " + pHost, 20, TextUtils
								.toInt(pRetryDelay), pCommunity, stringbuffer2,
						s19, "0", false);
				if (stringbuffer2.length() > 0) {
					StringBuffer stringbuffer3 = new StringBuffer();
					String s22 = s19.substring(0, s19.lastIndexOf("."));
					String s24 = s19.substring(s22.length() + 1, s19.length());
					s21 = BrowsableSNMPBase.readSNMPValue(pHost,
							"Dynamic Upadate: " + pHost, 20, TextUtils
									.toInt(pRetryDelay), pCommunity,
							stringbuffer3, s22, s24, false);
					if (stringbuffer3.length() > 0) {
						StringBuffer stringbuffer4 = new StringBuffer();
						s21 = BrowsableSNMPBase.readSNMPValue(pHost,
								"Dynamic Upadate: " + pHost, 20, TextUtils
										.toInt(pRetryDelay), pCommunity,
								stringbuffer4, s22, "0", false);
						if (stringbuffer4.length() > 0
								&& pRootOID.startsWith("1.3.6.1.2.1.25")) {
							stringbuffer4 = new StringBuffer();
							s21 = BrowsableSNMPBase.readSNMPValue(pHost,
									"Dynamic Upadate: " + pHost, 20, TextUtils
											.toInt(pRetryDelay), pCommunity,
									stringbuffer4, s22, "0", false);
						}
						if (stringbuffer4.length() > 0) {
							s21 = s15;
						}
					}
				}
				String s23 = TextUtils.replaceString(pGroupSet, "$POOL-ID$",
						s21);
				s23 = TextUtils
						.keepChars(s23,
								"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
				try {
					singleframes = CGI.ReadGroupFrames(s23, request);
				} catch (IOException ioexception3) {
				}
				if (singleframes.size() <= 0) {
					try {
						if (!vMachinePage.postToGroupPage(s23, pGroup, request,
								FileUtils.MakeOutputWriter(System.out), false)) {
							continue;
						}
					} catch (IOException ioexception4) {
						ioexception4.printStackTrace();
					}
					createGroupFromTemplate(pMonitorSet, s23, array4,
							stringbuffer, pPoolIncluded, false);
				} else {
					createGroupFromTemplate(pMonitorSet, s23, array4,
							stringbuffer, pPoolIncluded, true);
					try {
						CGI.WriteGroupFrames(s23, singleframes, request);
						singleframes = new Array();
					} catch (IOException ioexception5) {
						ioexception5.printStackTrace();
					}
				}
			} else {
				createGroupFromTemplate(pMonitorSet, pGroup, array4,
						stringbuffer, pPoolIncluded, true);
			}
			if (stringbuffer.length() > 0) {
				continue;
			}
			if (pNodes.length() > 0) {
				pNodes = pNodes + "\t";
			}
			if (!flag) {
				pNodes = pNodes + (String) array4.at(1);
			} else {
				pNodes = pNodes + (String) array2.at(k);
			}
		}

		if (pGroupSet.indexOf("$NODE-IP$") < 0
				&& pGroupSet.indexOf("$POOL-ID$") < 0) {
			try {
				CGI.WriteGroupFrames(pGroup, singleframes, request);
				singleframes = new Array();
			} catch (IOException ioexception1) {
				ioexception1.printStackTrace();
			}
		}
		if (array2.size() > 0) {
			setProperty(pNodes, pNodes);
			hashmap.put("_nodes", pNodes);
			try {
				FrameFile.writeToFile(vMachinePage.dynamicFilePath, array);
				SiteViewGroup.currentSiteView().loadDynamicNoSchedule();
				SiteViewGroup.currentSiteView().checkConfiguration.execute();
			} catch (Exception exception) {
				LogManager.log("Error", " Writing dynamic file "
						+ exception.getMessage());
				exception.printStackTrace();
			}
		}
	}*/
    public synchronized void checkForNodes()
    {
        String s = I18N.toDefaultEncoding(getProperty(pID));
        String s1 = getProperty(pRootOID);
        String s2 = getProperty(pOtherOID);
        String s3 = getProperty(pHost);
        String s4 = getProperty(pNodes);
        String as[] = TextUtils.split(s4, "\t");
        String s5 = getProperty(pRetryDelay);
        String s6 = getProperty(pCommunity);
        String s7 = getProperty(pMonitorSet);
        String s8 = I18N.toDefaultEncoding(getProperty(pGroup));
        String s9 = getProperty(pGroupSet);
        String s10 = getProperty(pPoolIncluded);
        Array array = vMachinePage.readDynamicSets();
        Array array1 = new Array();
        HashMap hashmap = new HashMap();
        Enumeration enumeration = array.elements();
        do
        {
            if(!enumeration.hasMoreElements())
                break;
            hashmap = (HashMap)enumeration.nextElement();
        } while(!hashmap.get("_id").equals(s));
        HashMap hashmap1 = new HashMap();
        StringBuffer stringbuffer = new StringBuffer();
        request = new HTTPRequest();
        boolean flag = getProperty(pDBConnectionURL).length() > 0;
        if(flag)
        {
            array1 = checkDB(stringbuffer);
        } else
        {
            if(s2.length() > 0)
                s1 = s2;
            else
            if(s1.indexOf("-(") > 0)
                s1 = s1.substring(s1.indexOf("-(") + 2, s1.length() - 1);
            String s11 = s1.indexOf(s3) < 0 ? s1 : s1.substring(0, s1.indexOf(s3));
            request.setUser("administrator");
            Array array3 = new Array();
            if(s1.indexOf("++") < 0)
                s1 = s1 + "++";
            do
            {
                String s12 = BrowsableSNMPBase.readSNMPValue(s3, "Dynamic Upadate: " + s3, 20, TextUtils.toInt(s5), s6, stringbuffer, s1, "0", false);
                if(s12.length() <= 0 || s12.indexOf(s11) <= 0)
                    break;
                String s13 = "";
                if(s12.startsWith("."))
                    s13 = s12.substring(1);
                s13 = s13.substring(s11.length() + 1);
                if(s10.length() <= 0)
                {
                    array1.add(s13);
                } else
                {
                    StringBuffer stringbuffer1 = new StringBuffer();
                    s13 = extractPoolFromIP(stringbuffer1, s13);
                    array1.add(s13);
                    array3.add(stringbuffer1);
                    hashmap1.put(s13, stringbuffer1.toString());
                }
                s13 = s12.substring(s12.lastIndexOf("."), s12.length());
                if(s13.equals(".0"))
                    s1 = s12.substring(0, s12.lastIndexOf(s13)) + "++";
                else
                    s1 = s12 + "++";
            } while(true);
        }
        Array array2 = new Array();
        String as1[] = TextUtils.split(getProperty(pExcludeIP), ",");
label0:
        for(int i = 0; i < as1.length; i++)
        {
            int l = as1[i].indexOf("*");
            if(l >= 0)
            {
                as1[i] = as1[i].substring(0, l);
                int i1 = 0;
                do
                {
                    if(i1 >= array1.size())
                        continue label0;
                    String s16 = (String)array1.at(i1);
                    if(s16.startsWith(as1[i]))
                    {
                        array1.remove(s16);
                        if(s10.length() > 0)
                            hashmap1.remove(s16);
                    }
                    i1++;
                } while(true);
            }
            array1.remove(as1[i]);
            if(s10.length() > 0)
                hashmap1.remove(as1[i]);
        }

        array1.remove(s3);
        for(int j = 0; j < array1.size(); j++)
        {
            String s14 = ((String)array1.at(j)).trim();
            boolean flag1 = false;
            int j1 = 0;
            do
            {
                if(j1 >= as.length)
                    break;
                String s20 = as[j1].trim();
                if(s20.equals(s14))
                {
                    flag1 = true;
                    break;
                }
                j1++;
            } while(true);
            if(!flag1)
                array2.add(s14);
        }

        stringbuffer = new StringBuffer();
        if(s9.indexOf("$NODE-IP$") < 0 && s9.indexOf("$POOL-ID$") < 0)
            try
            {
                singleframes = CGI.ReadGroupFrames(s8, request);
            }
            catch(IOException ioexception)
            {
                ioexception.printStackTrace();
            }
        for(int k = 0; k < array2.size(); k++)
        {
            Array array4 = new Array();
            if(s10.length() > 0 && !flag)
                array4.add(s3);
            array4.add((String)array2.at(k));
            String s15 = "";
            if(s10.length() > 0)
            {
                String s17 = (String)hashmap1.get((String)array2.at(k));
                if(s17 != null)
                {
                    array4.add(s17);
                    s15 = s17;
                }
            }
            if(s9.indexOf("$NODE-IP$") >= 0)
            {
                String s18 = flag ? TextUtils.replaceString(s9, "$NODE-IP$", (String)array2.at(k)) : TextUtils.replaceString(s9, "$NODE-IP$", (String)array4.at(1));
                s18 = TextUtils.keepChars(s18, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                try
                {                    
                    if(!vMachinePage.postToGroupPage(s18, s8, request, FileUtils.MakeOutputWriter(System.out), false))
                        continue;
                }
                catch(IOException ioexception2)
                {
                    ioexception2.printStackTrace();
                }
                createGroupFromTemplate(s7, s18, array4, stringbuffer, s10, false);
            } else
            if(s9.indexOf("$POOL-ID$") >= 0)
            {
                String s19 = "1.3.6.1.4.1.3375.1.1.7.2.1.1." + s15;
                StringBuffer stringbuffer2 = new StringBuffer();
                String s21 = BrowsableSNMPBase.readSNMPValue(s3, "Dynamic Upadate: " + s3, 20, TextUtils.toInt(s5), s6, stringbuffer2, s19, "0", false);
                if(stringbuffer2.length() > 0)
                {
                    StringBuffer stringbuffer3 = new StringBuffer();
                    String s22 = s19.substring(0, s19.lastIndexOf("."));
                    String s24 = s19.substring(s22.length() + 1, s19.length());
                    s21 = BrowsableSNMPBase.readSNMPValue(s3, "Dynamic Upadate: " + s3, 20, TextUtils.toInt(s5), s6, stringbuffer3, s22, s24, false);
                    if(stringbuffer3.length() > 0)
                    {
                        StringBuffer stringbuffer4 = new StringBuffer();
                        s21 = BrowsableSNMPBase.readSNMPValue(s3, "Dynamic Upadate: " + s3, 20, TextUtils.toInt(s5), s6, stringbuffer4, s22, "0", false);
                        if(stringbuffer4.length() > 0 && s1.startsWith("1.3.6.1.2.1.25"))
                        {
                            stringbuffer4 = new StringBuffer();
                            s21 = BrowsableSNMPBase.readSNMPValue(s3, "Dynamic Upadate: " + s3, 20, TextUtils.toInt(s5), s6, stringbuffer4, s22, "0", false);
                        }
                        if(stringbuffer4.length() > 0)
                            s21 = s15;
                    }
                }
                String s23 = TextUtils.replaceString(s9, "$POOL-ID$", s21);
                s23 = TextUtils.keepChars(s23, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                try
                {
                    singleframes = CGI.ReadGroupFrames(s23, request);
                }
                catch(IOException ioexception3) { }
                if(singleframes.size() <= 0)
                {
                    try
                    {
                        if(!vMachinePage.postToGroupPage(s23, s8, request, FileUtils.MakeOutputWriter(System.out), false))
                            continue;
                    }
                    catch(IOException ioexception4)
                    {
                        ioexception4.printStackTrace();
                    }
                    createGroupFromTemplate(s7, s23, array4, stringbuffer, s10, false);
                } else
                {
                    createGroupFromTemplate(s7, s23, array4, stringbuffer, s10, true);
                    try
                    {
                        CGI.WriteGroupFrames(s23, singleframes, request);
                        singleframes = new Array();
                    }
                    catch(IOException ioexception5)
                    {
                        ioexception5.printStackTrace();
                    }
                }
            } else
            {
                createGroupFromTemplate(s7, s8, array4, stringbuffer, s10, true);
            }
            if(stringbuffer.length() > 0)
                continue;
            if(s4.length() > 0)
                s4 = s4 + "\t";
            if(!flag)
                s4 = s4 + (String)array4.at(1);
            else
                s4 = s4 + (String)array2.at(k);
        }

        if(s9.indexOf("$NODE-IP$") < 0 && s9.indexOf("$POOL-ID$") < 0)
            try
            {
                CGI.WriteGroupFrames(s8, singleframes, request);
                singleframes = new Array();
            }
            catch(IOException ioexception1)
            {
                ioexception1.printStackTrace();
            }
        if(array2.size() > 0)
        {
            setProperty(pNodes, s4);
            hashmap.put("_nodes", s4);
            try
            {
                FrameFile.writeToFile(vMachinePage.dynamicFilePath, array);
                SiteViewGroup.currentSiteView().loadDynamicNoSchedule();
                SiteViewGroup.currentSiteView().checkConfiguration.execute();
            }
            catch(Exception exception)
            {
                LogManager.log("Error", " Writing dynamic file " + exception.getMessage());
                exception.printStackTrace();
            }
        }
    }

	public String extractPoolFromIP(StringBuffer stringbuffer, String s) {
		String s1 = "";
		String as[] = TextUtils.split(s, ".");
		for (int i = 0; i < as.length - 5; i++) {
			stringbuffer.append(as[i]);
			if (i < as.length - 6) {
				stringbuffer.append(".");
			}
		}

		for (int j = as.length - 5; j < as.length; j++) {
			s1 = s1 + as[j];
			if (j < as.length - 1) {
				s1 = s1 + ".";
			}
		}

		return s1;
	}

	public void unschedule() {
		SiteViewGroup.currentSiteView().vMachineScheduler.unschedule(action);
		action = null;
	}

	static VirtualMachine createVirtualMachineObject(HashMap hashmap) {
		VirtualMachine virtualmachine = new VirtualMachine();
		virtualmachine.setOwner(SiteViewGroup.currentSiteView());
		virtualmachine.readFromHashMap(hashmap);
		virtualmachine.initialize(hashmap);
		return virtualmachine;
	}

	public void createGroupFromTemplate(String s, String s1, Array array,
			StringBuffer stringbuffer, String s2, boolean flag) {
		String s3 = monitorSetPage.TEMPLATES_DIR + File.separator + s;
		monitorSetTemplate monitorsettemplate = new monitorSetTemplate(s3);
		String as[] = monitorsettemplate.getVariables();
		String as1[] = new String[as.length];
		if (s2.length() > 0) {
			for (int i = 0; i < as.length; i++) {
				if (as[i].indexOf("$host$") >= 0) {
					as1[0] = as[i];
				} else if (as[i].indexOf("$pool$") >= 0) {
					as1[2] = as[i];
				} else {
					as1[1] = as[i];
				}
			}

		} else {
			as1 = as;
		}
		for (int j = 0; j < array.size(); j++) {
			monitorsettemplate.replaceVariable(as1[j], (String) array.at(j));
		}

		int k = monitorsettemplate.getMonitorCount();
		for (int l = 0; l < k;l++) {
			HashMap hashmap = monitorsettemplate.getNthMonitor(l);
			try {
				if (flag) {
					createMonitorSingle(s1, hashmap, (String) array.at(0),
							stringbuffer);
				} else {
					createMonitor(s1, hashmap, (String) array.at(0),
							stringbuffer);
				}
				continue;
			} catch (Exception exception) {
				stringbuffer.append("createMonitor: " + exception.getMessage());
				exception.printStackTrace();
				//l++; dingbing.xu
			}
		}

		SiteViewGroup.updateStaticPages(s1);
	}

	/**
	 * CAUTION: Decompiled by hand.
	 * 
	 * @param s
	 * @param hashmap
	 * @param s1
	 * @param stringbuffer
	 * @throws Exception
	 */
	public void createMonitorSingle(String s, HashMap hashmap, String s1,
			StringBuffer stringbuffer) throws Exception {
		String s2 = TextUtils.getValue(hashmap, "_class");
		AtomicMonitor atomicmonitor = AtomicMonitor.MonitorCreate(s2);
		long l = atomicmonitor.getPropertyAsLong(AtomicMonitor.pFrequency);
		Object obj = atomicmonitor.getClassProperty("defaultFrequency");
		if (obj instanceof String) {
			long l1 = TextUtils.toInt((String) obj);
			if (l1 > 0L) {
				l = l1;
			}
		}
		atomicmonitor.setProperty(AtomicMonitor.pFrequency, l);
		String s3 = atomicmonitor.defaultTitle();
		HashMap hashmap1 = new HashMap();
		Array array = atomicmonitor.getProperties();
		array = StringProperty.sortByOrder(array);
		for (Enumeration enumeration = array.elements(); enumeration
				.hasMoreElements();) {
			StringProperty stringproperty = (StringProperty) enumeration
					.nextElement();
			String s4 = stringproperty.getName();
			if (stringproperty.isMultiLine) {
				Array array1 = TextUtils.getMultipleValues(hashmap,
						stringproperty.getName());
				atomicmonitor.unsetProperty(stringproperty);
				int i = 0;
				while (i < array1.size()) {
					String s9 = (String) array1.at(i);
					s9 = atomicmonitor.verify(stringproperty, s9,
							new HTTPRequest(), hashmap1);
					atomicmonitor.addProperty(stringproperty, s9);
					i++;
				}
			} else {
				String s6 = TextUtils.getValue(hashmap, s4);
				if ((stringproperty instanceof ScalarProperty)
						&& ((ScalarProperty) stringproperty).multiple) {
					Array array2 = TextUtils.getMultipleValues(hashmap,
							stringproperty.getName());
					atomicmonitor.unsetProperty(stringproperty);
					int k = 0;
					while (k < array2.size()) {
						String s11 = (String) array2.at(k);
						s11 = atomicmonitor.verify(stringproperty, s11,
								new HTTPRequest(), hashmap1);
						atomicmonitor.addProperty(stringproperty, s11);
						k++;
					}
				} else {
					AtomicMonitor _tmp = atomicmonitor;
					if (stringproperty == AtomicMonitor.pName) {
						if (s6.equals(s3)) {
							atomicmonitor.setProperty(stringproperty, "");
						} else {
							atomicmonitor.setProperty(stringproperty, s6);
						}
					}
					atomicmonitor.setProperty(stringproperty, s6);
				}
			}
		}

		HashMap hashmap2 = MasterConfig.getMasterConfig();
		Enumeration enumeration1 = hashmap2.values("_monitorEditCustom");
		while (enumeration1.hasMoreElements()) {
			String s5 = (String) enumeration1.nextElement();
			String as[] = TextUtils.split(s5, "|");
			String s8 = as[0];
			if (s8.length() > 0) {
				if (!s8.startsWith("_")) {
					s8 = "_" + s8;
				}
				String s10 = TextUtils.getValue(hashmap, s8);
				s10 = s10.replace('\r', ' ');
				s10 = s10.replace('\n', ' ');
				atomicmonitor.setProperty(s8, s10);
			}
		}

		AtomicMonitor.saveThresholds(atomicmonitor, hashmap, hashmap1);
		AtomicMonitor.saveClassifier(atomicmonitor, hashmap, hashmap1);
		AtomicMonitor.saveCustomProperties(MasterConfig.getMasterConfig(),
				atomicmonitor, new HTTPRequest());
		AtomicMonitor _tmp1 = atomicmonitor;
		if (atomicmonitor.getProperty(AtomicMonitor.pName).length() == 0) {
			AtomicMonitor _tmp2 = atomicmonitor;
			atomicmonitor.setProperty(AtomicMonitor.pName, atomicmonitor
					.defaultTitle());
		}
		HashMap hashmap3 = atomicmonitor.getValuesTable();
		String s7 = "";
		int j = singleframes.size();
		HashMap hashmap4 = (HashMap) singleframes.at(0);
		s7 = TextUtils.getValue(hashmap4, "_nextID");
		if (s7.length() == 0) {
			s7 = "1";
		}
		hashmap3.remove(Monitor.pID);
		hashmap3.remove("_id");
		hashmap3.put("_id", s7);
		singleframes.insert(j++, hashmap3);
		String s12 = TextUtils.increment(s7);
		hashmap4.put("_nextID", s12);
		Platform.sleep(2000L);
		SiteViewGroup.updateStaticPages(s);
	}

	public void createMonitor(String s, HashMap hashmap, String s1,
			StringBuffer stringbuffer) throws Exception {
		String s2 = TextUtils.getValue(hashmap, "_class");
		AtomicMonitor atomicmonitor = AtomicMonitor.MonitorCreate(s2);
		long l = atomicmonitor.getPropertyAsLong(AtomicMonitor.pFrequency);
		Object obj = atomicmonitor.getClassProperty("defaultFrequency");
		if (obj instanceof String) {
			long l1 = TextUtils.toInt((String) obj);
			if (l1 > 0L) {
				l = l1;
			}
		}
		atomicmonitor.setProperty(AtomicMonitor.pFrequency, l);
		String s3 = atomicmonitor.defaultTitle();
		HashMap hashmap1 = new HashMap();
		Array array = atomicmonitor.getProperties();
		array = StringProperty.sortByOrder(array);
		for (Enumeration enumeration = array.elements(); enumeration
				.hasMoreElements();) {
			StringProperty stringproperty = (StringProperty) enumeration
					.nextElement();
			String s4 = stringproperty.getName();
			if (stringproperty.isMultiLine) {
				Array array1 = TextUtils.getMultipleValues(hashmap,
						stringproperty.getName());
				atomicmonitor.unsetProperty(stringproperty);
				int i = 0;
				while (i < array1.size()) {
					String s9 = (String) array1.at(i);
					s9 = atomicmonitor.verify(stringproperty, s9,
							new HTTPRequest(), hashmap1);
					atomicmonitor.addProperty(stringproperty, s9);
					i++;
				}
			} else {
				String s6 = TextUtils.getValue(hashmap, s4);
				if ((stringproperty instanceof ScalarProperty)
						&& ((ScalarProperty) stringproperty).multiple) {
					Array array3 = TextUtils.getMultipleValues(hashmap,
							stringproperty.getName());
					atomicmonitor.unsetProperty(stringproperty);
					int j = 0;
					while (j < array3.size()) {
						String s11 = (String) array3.at(j);
						s11 = atomicmonitor.verify(stringproperty, s11,
								new HTTPRequest(), hashmap1);
						atomicmonitor.addProperty(stringproperty, s11);
						j++;
					}
				} else {
					AtomicMonitor _tmp = atomicmonitor;
					if (stringproperty == AtomicMonitor.pName) {
						if (s6.equals(s3)) {
							atomicmonitor.setProperty(stringproperty, "");
						} else {
							atomicmonitor.setProperty(stringproperty, s6);
						}
					}
					atomicmonitor.setProperty(stringproperty, s6);
				}
			}
		}

		HashMap hashmap2 = MasterConfig.getMasterConfig();
		Enumeration enumeration1 = hashmap2.values("_monitorEditCustom");
		while (enumeration1.hasMoreElements()) {
			String s5 = (String) enumeration1.nextElement();
			String as[] = TextUtils.split(s5, "|");
			String s7 = as[0];
			if (s7.length() > 0) {
				if (!s7.startsWith("_")) {
					s7 = "_" + s7;
				}
				String s10 = TextUtils.getValue(hashmap, s7);
				s10 = s10.replace('\r', ' ');
				s10 = s10.replace('\n', ' ');
				atomicmonitor.setProperty(s7, s10);
			}
		}

		AtomicMonitor.saveThresholds(atomicmonitor, hashmap, hashmap1);
		AtomicMonitor.saveClassifier(atomicmonitor, hashmap, hashmap1);
		AtomicMonitor.saveCustomProperties(MasterConfig.getMasterConfig(),
				atomicmonitor, new HTTPRequest());
		AtomicMonitor _tmp1 = atomicmonitor;
		if (atomicmonitor.getProperty(AtomicMonitor.pName).length() == 0) {
			AtomicMonitor _tmp2 = atomicmonitor;
			atomicmonitor.setProperty(AtomicMonitor.pName, atomicmonitor
					.defaultTitle());
		}
		HashMap hashmap3 = atomicmonitor.getValuesTable();
		Array array2 = CGI.ReadGroupFrames(s, request);
		String s8 = "";
		int k = array2.size();
		HashMap hashmap4 = (HashMap) array2.at(0);
		s8 = TextUtils.getValue(hashmap4, "_nextID");
		if (s8.length() == 0) {
			s8 = "1";
		}
		hashmap3.remove(Monitor.pID);
		hashmap3.remove("_id");
		hashmap3.put("_id", s8);
		array2.insert(k++, hashmap3);
		String s12 = TextUtils.increment(s8);
		hashmap4.put("_nextID", s12);
		CGI.WriteGroupFrames(s, array2, request);
		MonitorGroup monitorgroup = (MonitorGroup) SiteViewGroup
				.currentSiteView().getElement(s);
		atomicmonitor.setOwner(monitorgroup);
		Platform.sleep(2000L);
		SiteViewGroup.updateStaticPages(s);
	}

	static {
		pID = new StringProperty("_id");
		pRootOID = new StringProperty("_oid");
		pOtherOID = new StringProperty("_otheroid");
		pHost = new StringProperty("_host");
		pCommunity = new StringProperty("_community", "public");
		pMonitorSet = new StringProperty("_set");
		pCheckFrequency = new NumericProperty("_frequency", "600", "seconds");
		pGroup = new StringProperty("_group", "");
		pGroupSet = new StringProperty("_groupSet", "$SERVER-IP$");
		pPoolIncluded = new StringProperty("_poolIncluded", "");
		pParent = new StringProperty("_parent", "");
		pNodes = new StringProperty("_nodes");
		pName = new StringProperty("_name", "");
		pExcludeIP = new StringProperty("_excludeIP", "");
		pDBConnectionURL = new StringProperty("_dbConnectionURL", "");
		pDBDriver = new StringProperty("_dbDriver", "");
		pDBSqlQuery = new StringProperty("_dbSqlQuery", "");
		pDBUsername = new StringProperty("_dbUserName", "");
		pDBPassword = new StringProperty("_dbPassword", "");
		pDBConnectTimeout = new StringProperty("_dbConnectTimeout", "");
		pDBQueryTimeout = new StringProperty("_dbQueryTimeout", "");
		StringProperty astringproperty[] = { pDBQueryTimeout,
				pDBConnectTimeout, pDBPassword, pDBUsername, pDBSqlQuery,
				pDBDriver, pDBConnectionURL, pPoolIncluded, pOtherOID, pID,
				pParent, pRootOID, pHost, pMonitorSet, pCheckFrequency, pGroup,
				pGroupSet, pNodes, pCommunity, pName, pExcludeIP };
		addProperties("COM.dragonflow.SiteView.VirtualMachine", astringproperty);
	}
}
