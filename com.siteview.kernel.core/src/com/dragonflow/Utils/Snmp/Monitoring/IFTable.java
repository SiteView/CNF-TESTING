/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils.Snmp.Monitoring;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

// Referenced classes of package com.dragonflow.Utils.Snmp.Monitoring:
// NetInterface

public class IFTable
{

    private com.dragonflow.Utils.Snmp.SNMPSession session;
    private static final String INCOMING_OID = "1.3.6.1.2.1.2.2.1.10";
    private static final String OUTGOING_OID = "1.3.6.1.2.1.2.2.1.16";
    private static final String SPEED_OID = "1.3.6.1.2.1.2.2.1.5";
    private static final String OPSTATUS_OID = "1.3.6.1.2.1.2.2.1.8";
    private static final String DESC_OID = "1.3.6.1.2.1.2.2.1.2";
    private static final String PHYS_ADDR_OID = "1.3.6.1.2.1.2.2.1.6";
    private static final String INDISCARDS_OID = "1.3.6.1.2.1.2.2.1.13";
    private static final String INERRORS_OID = "1.3.6.1.2.1.2.2.1.14";
    private static final String OUTDISCARDS_OID = "1.3.6.1.2.1.2.2.1.19";
    private static final String OUTERRORS_OID = "1.3.6.1.2.1.2.2.1.20";
    private static final String INUCAST_OID = "1.3.6.1.2.1.2.2.1.11";
    private static final String OUTUCAST_OID = "1.3.6.1.2.1.2.2.1.17";
    private static final String OUTQLEN_OID = "1.3.6.1.2.1.2.2.1.21";
    private static final String NAME_OID = "1.3.6.1.2.1.31.1.1.1.1";
    private static final String ALIAS_OID = "1.3.6.1.2.1.31.1.1.1.18";
    private static final String oids[] = {
        "1.3.6.1.2.1.2.2.1.10", "1.3.6.1.2.1.2.2.1.16", "1.3.6.1.2.1.2.2.1.5", "1.3.6.1.2.1.2.2.1.8", "1.3.6.1.2.1.2.2.1.2", "1.3.6.1.2.1.2.2.1.6", "1.3.6.1.2.1.2.2.1.13", "1.3.6.1.2.1.2.2.1.14", "1.3.6.1.2.1.2.2.1.19", "1.3.6.1.2.1.2.2.1.20", 
        "1.3.6.1.2.1.2.2.1.11", "1.3.6.1.2.1.2.2.1.17", "1.3.6.1.2.1.2.2.1.21", "1.3.6.1.2.1.31.1.1.1.1", "1.3.6.1.2.1.31.1.1.1.18"
    };
    public static final int OBJECT_NOT_AVAILABLE = -1;
    public static final int INTERFACE_UP = 1;
    public static final int INTERFACE_DOWN = 2;
    private long time;
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding inOctets[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding outOctets[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding speed[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding operStatus[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding ifDescriptions[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding physAddresses[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding inErrors[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding inDiscards[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding inUCastPackets[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding outErrors[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding outDiscards[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding outQLen[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding outUCastPackets[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding ifNames[];
    private com.dragonflow.Utils.Snmp.SNMPVariableBinding ifAliases[];
    private java.util.Map descriptionToRowMap;
    private java.util.Map nameToRowMap;
    private java.util.Map physAddrToRowMap;
    private StringBuffer ifTableError;
    private boolean ifNamesAvailable;
    private boolean ifAliasesAvailable;

    public long getInDiscards(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(inDiscards, i)) != null)
        {
            return snmpvariablebinding.getValueAsLong();
        } else
        {
            return -1L;
        }
    }

    private com.dragonflow.Utils.Snmp.SNMPVariableBinding findVarBindingByIndex(com.dragonflow.Utils.Snmp.SNMPVariableBinding asnmpvariablebinding[], int i)
    {
        if(asnmpvariablebinding != null)
        {
            for(int j = 0; j < asnmpvariablebinding.length; j++)
            {
                if(asnmpvariablebinding[j].getLastSubID() == i)
                {
                    return asnmpvariablebinding[j];
                }
            }

        }
        return null;
    }

    public long getInErrors(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(inErrors, i)) != null)
        {
            return snmpvariablebinding.getValueAsLong();
        } else
        {
            return -1L;
        }
    }

    public long getInOctets(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(inOctets, i)) != null)
        {
            return snmpvariablebinding.getValueAsLong();
        } else
        {
            return -1L;
        }
    }

    public String getInterfaceDescription(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(ifDescriptions, i)) != null)
        {
            return snmpvariablebinding.getValue();
        } else
        {
            return "";
        }
    }

    public long getInUCastPackets(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(inUCastPackets, i)) != null)
        {
            return snmpvariablebinding.getValueAsLong();
        } else
        {
            return -1L;
        }
    }

    public long getOperStatus(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(operStatus, i)) != null)
        {
            return snmpvariablebinding.getValueAsLong();
        } else
        {
            return -1L;
        }
    }

    public long getOutDiscards(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(outDiscards, i)) != null)
        {
            return snmpvariablebinding.getValueAsLong();
        } else
        {
            return -1L;
        }
    }

    public long getOutErrors(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(outErrors, i)) != null)
        {
            return snmpvariablebinding.getValueAsLong();
        } else
        {
            return -1L;
        }
    }

    public long getOutOctets(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(outOctets, i)) != null)
        {
            return snmpvariablebinding.getValueAsLong();
        } else
        {
            return -1L;
        }
    }

    public long getOutQLen(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(outQLen, i)) != null)
        {
            return snmpvariablebinding.getValueAsLong();
        } else
        {
            return -1L;
        }
    }

    public long getOutUCastPackets(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(outUCastPackets, i)) != null)
        {
            return snmpvariablebinding.getValueAsLong();
        } else
        {
            return -1L;
        }
    }

    public long getSpeed(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(speed, i)) != null)
        {
            return snmpvariablebinding.getValueAsLong();
        } else
        {
            return -1L;
        }
    }

    public long getTime()
    {
        return time;
    }

    public String getInterfaceName(int i)
    {
        com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = null;
        if((snmpvariablebinding = findVarBindingByIndex(ifNames, i)) != null)
        {
            return snmpvariablebinding.getValue();
        } else
        {
            return "";
        }
    }

    public IFTable(com.dragonflow.Utils.Snmp.SNMPSession snmpsession)
    {
        ifTableError = new StringBuffer();
        ifNamesAvailable = true;
        ifAliasesAvailable = true;
        session = snmpsession;
        physAddrToRowMap = java.util.Collections.synchronizedMap(new HashMap());
        descriptionToRowMap = java.util.Collections.synchronizedMap(new HashMap());
        nameToRowMap = java.util.Collections.synchronizedMap(new HashMap());
    }

    public String getError()
    {
        return ifTableError.toString();
    }

    public boolean refreshTable(StringBuffer stringbuffer)
    {
        stringbuffer.setLength(0);
        ifTableError.setLength(0);
        StringBuffer stringbuffer1 = new StringBuffer();
        operStatus = getColumn("1.3.6.1.2.1.2.2.1.8", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            ifTableError.append("Could not retrieve ifOperStatus Object (").append("1.3.6.1.2.1.2.2.1.8").append(").");
            stringbuffer.append(ifTableError);
            return false;
        }
        ifDescriptions = getColumn("1.3.6.1.2.1.2.2.1.2", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            ifTableError.append("Could not retrieve ifDescr Object (").append("1.3.6.1.2.1.2.2.1.2").append(").");
            stringbuffer.append(ifTableError);
            return false;
        }
        updateDescriptionToRowMap();
        physAddresses = getColumn("1.3.6.1.2.1.2.2.1.6", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            ifTableError.append("Could not retrieve ifPhysAddress Object (").append("1.3.6.1.2.1.2.2.1.6").append(").");
            stringbuffer.append(ifTableError);
            return false;
        }
        updatePhysAddrToRowMap();
        inOctets = getColumn("1.3.6.1.2.1.2.2.1.10", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            ifTableError.append("Could not retrieve ifInOctets Object (").append("1.3.6.1.2.1.2.2.1.10").append(").");
            stringbuffer.append(ifTableError);
            return false;
        }
        outOctets = getColumn("1.3.6.1.2.1.2.2.1.16", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            ifTableError.append("Could not retrieve ifOutOctets Object (").append("1.3.6.1.2.1.2.2.1.16").append(").");
            stringbuffer.append(ifTableError);
            return false;
        }
        speed = getColumn("1.3.6.1.2.1.2.2.1.5", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            ifTableError.append("Could not retrieve ifSpeed Object (").append("1.3.6.1.2.1.2.2.1.5").append(").");
            stringbuffer.append(ifTableError);
            return false;
        }
        StringBuffer stringbuffer2 = new StringBuffer();
        inErrors = getColumn("1.3.6.1.2.1.2.2.1.14", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            stringbuffer2.append("ifInErrors (").append("1.3.6.1.2.1.2.2.1.14").append(")");
        }
        inDiscards = getColumn("1.3.6.1.2.1.2.2.1.13", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            if(stringbuffer2.length() > 0)
            {
                stringbuffer2.append(", ");
            }
            stringbuffer2.append("ifInDiscards (").append("1.3.6.1.2.1.2.2.1.13").append(")");
        }
        outErrors = getColumn("1.3.6.1.2.1.2.2.1.20", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            if(stringbuffer2.length() > 0)
            {
                stringbuffer2.append(", ");
            }
            stringbuffer2.append("ifOutErrors (").append("1.3.6.1.2.1.2.2.1.20").append(")");
        }
        outDiscards = getColumn("1.3.6.1.2.1.2.2.1.19", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            if(stringbuffer2.length() > 0)
            {
                stringbuffer2.append(", ");
            }
            stringbuffer2.append("ifOutDiscards (").append("1.3.6.1.2.1.2.2.1.19").append(")");
        }
        outQLen = getColumn("1.3.6.1.2.1.2.2.1.21", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            if(stringbuffer2.length() > 0)
            {
                stringbuffer2.append(", ");
            }
            stringbuffer2.append("ifOutQLen (").append("1.3.6.1.2.1.2.2.1.21").append(")");
        }
        inUCastPackets = getColumn("1.3.6.1.2.1.2.2.1.11", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            if(stringbuffer2.length() > 0)
            {
                stringbuffer2.append(", ");
            }
            stringbuffer2.append("ifInUcastPkts (").append("1.3.6.1.2.1.2.2.1.11").append(")");
        }
        outUCastPackets = getColumn("1.3.6.1.2.1.2.2.1.17", stringbuffer1);
        if(stringbuffer1.length() != 0)
        {
            if(stringbuffer2.length() > 0)
            {
                stringbuffer2.append(", ");
            }
            stringbuffer2.append("ifOutUcastPkts (").append("1.3.6.1.2.1.2.2.1.17").append(")");
        }
        if(ifNamesAvailable)
        {
            ifNames = getColumn("1.3.6.1.2.1.31.1.1.1.1", stringbuffer1);
            if(stringbuffer1.length() != 0 || ifNames.length == 0)
            {
                ifNamesAvailable = false;
            }
        }
        if(ifAliasesAvailable)
        {
            ifAliases = getColumn("1.3.6.1.2.1.31.1.1.1.18", stringbuffer1);
            if(stringbuffer1.length() != 0 || ifAliases.length == 0)
            {
                ifAliasesAvailable = false;
            }
        }
        if(stringbuffer2.length() > 0)
        {
            ifTableError.append("Could not retrieve the following ifTable objects: ").append(stringbuffer2).append(".");
        }
        time = (new Date()).getTime() / 1000L;
        return true;
    }

    private void updatePhysAddrToRowMap()
    {
        if(physAddresses == null)
        {
            return;
        }
        for(int i = 0; i < physAddresses.length; i++)
        {
            java.lang.Integer integer = new Integer(physAddresses[i].getLastSubID());
            String s;
            if((s = physAddresses[i].getValue()) != null)
            {
                s = s.trim();
            } else
            {
                s = "";
            }
            physAddrToRowMap.put(s, integer);
        }

    }

    private void updateDescriptionToRowMap()
    {
        if(ifDescriptions == null)
        {
            return;
        }
        for(int i = 0; i < ifDescriptions.length; i++)
        {
            String s;
            if((s = ifDescriptions[i].getValue()) != null)
            {
                s = s.trim();
            } else
            {
                s = "";
            }
            java.lang.Integer integer = new Integer(ifDescriptions[i].getLastSubID());
            descriptionToRowMap.put(s, integer);
        }

    }

    private void updateNameToRowMap()
    {
        if(ifNames == null)
        {
            return;
        }
        for(int i = 0; i < ifNames.length; i++)
        {
            String s;
            if((s = ifNames[i].getValue()) != null)
            {
                s = s.trim();
            } else
            {
                s = "";
            }
            java.lang.Integer integer = new Integer(ifNames[i].getLastSubID());
            nameToRowMap.put(s, integer);
        }

    }

    private com.dragonflow.Utils.Snmp.SNMPVariableBinding[] getColumn(String s, StringBuffer stringbuffer)
    {
        stringbuffer.setLength(0);
        com.dragonflow.Utils.Snmp.SNMPVariableBinding asnmpvariablebinding[] = session.getTableColumn(s);
        String s1 = session.getError();
        if("noError".equals(s1) || "".equals(s1))
        {
            return asnmpvariablebinding;
        } else
        {
            stringbuffer.append(s1);
            return null;
        }
    }

    public java.util.Vector getActiveInterfaces(int i)
    {
        java.util.Vector vector = new Vector();
        Object obj = null;
        for(int j = 0; j < operStatus.length; j++)
        {
            if(operStatus[j].getValueAsLong() != 1L)
            {
                continue;
            }
            int k = operStatus[j].getLastSubID();
            String s = "";
            com.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding;
            if((snmpvariablebinding = findVarBindingByIndex(ifNames, k)) != null)
            {
                s = snmpvariablebinding.getValue();
            }
            String s1 = "";
            if((snmpvariablebinding = findVarBindingByIndex(ifDescriptions, k)) != null)
            {
                s1 = snmpvariablebinding.getValue();
            }
            String s2 = "";
            if((snmpvariablebinding = findVarBindingByIndex(ifAliases, k)) != null)
            {
                s2 = snmpvariablebinding.getValue();
            }
            String s3 = "";
            if((snmpvariablebinding = findVarBindingByIndex(physAddresses, k)) != null)
            {
                s3 = snmpvariablebinding.getValue();
            }
            com.dragonflow.Utils.Snmp.Monitoring.NetInterface netinterface;
            if(i == 1)
            {
                netinterface = new NetInterface(s, s1, s2, s3, j, false);
            } else
            {
                netinterface = new NetInterface(s, s1, s2, s3, k, false);
            }
            vector.add(netinterface);
        }

        return vector;
    }

    public int getNumRequests()
    {
        return oids.length;
    }

    public String getPhysicalAddress(int i)
    {
        if(i < physAddresses.length)
        {
            return physAddresses[i].getValue();
        } else
        {
            return "";
        }
    }

    public int getRowFromPhysicalAddress(String s)
    {
        if(physAddrToRowMap.size() == 0)
        {
            updatePhysAddrToRowMap();
        }
        return lookupRowFromText(physAddrToRowMap, s);
    }

    public int getRowFromDescription(String s)
    {
        if(descriptionToRowMap.size() == 0)
        {
            updateDescriptionToRowMap();
        }
        return lookupRowFromText(descriptionToRowMap, s);
    }

    public int getRowFromName(String s)
    {
        if(nameToRowMap.size() == 0)
        {
            updateNameToRowMap();
        }
        return lookupRowFromText(nameToRowMap, s);
    }

    private int lookupRowFromText(java.util.Map map, String s)
    {
        if(s != null)
        {
            java.lang.Integer integer = (java.lang.Integer)map.get(s.trim());
            if(integer != null)
            {
                return integer.intValue();
            }
        }
        return -1;
    }

    public int translateArrayPositionToRowNum(int i)
    {
        if(operStatus != null && i < operStatus.length)
        {
            return operStatus[i].getLastSubID();
        } else
        {
            return -1;
        }
    }

    public boolean isIfNameObjectAvailable()
    {
        return ifNamesAvailable;
    }

}
