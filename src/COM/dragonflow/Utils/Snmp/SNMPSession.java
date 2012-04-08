/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Utils.Snmp;

import java.util.HashMap;

import com.netaphor.snmp.CommunityTarget;
import com.netaphor.snmp.GenericAddress;
import com.netaphor.snmp.OID;
import com.netaphor.snmp.PDU;
import com.netaphor.snmp.SecureTarget;
import com.netaphor.snmp.Session;
import com.netaphor.snmp.UsmUser;
import com.netaphor.snmp.VariableBinding;

// Referenced classes of package COM.dragonflow.Utils.Snmp:
// SNMPSessionException, BrowsableMIBException, SNMPVariableBinding,
// BrowsableMIB

public class SNMPSession
{

    public static final String MD5 = "MD5";
    public static final String SHA = "SHA";
    public static final String NoAuthentication = "NoAuthentication";
    public static final String NOT_DISPLAYABLE = "NOT DISPLAYABLE";
    private com.netaphor.snmp.GenericAddress address;
    private int version;
    private com.netaphor.snmp.Target target;
    private static com.netaphor.snmp.Session session;
    private COM.dragonflow.Utils.Snmp.BrowsableMIB mib;
    private com.netaphor.snmp.UsmUser user;
    private byte engineID[];
    private byte contextEngineID[];
    private byte contextName[];
    private com.netaphor.snmp.usm.Authenticator authenticationType;
    private StringBuffer error;
    private int errorIndex;
    private static java.lang.Object mibLock = new Object();
    public boolean formatOctetStrings;
    private static java.util.HashMap errorStrings;
    private final int DEFAULT_RETRIES = 2;
    private final long DEFAULT_TIMEOUT = 100L;
    static final boolean $assertionsDisabled; /* synthetic field */

    public SNMPSession(String s, String s1, String s2, String s3, String s4, String s5, String s6)
        throws COM.dragonflow.Utils.Snmp.SNMPSessionException
    {
        engineID = null;
        contextEngineID = null;
        contextName = null;
        version = 3;
        address = new GenericAddress(s);
        if(!address.isValid())
        {
            throw new SNMPSessionException("host " + s + " is not a valid IP address or hostname.");
        }
        formatOctetStrings = true;
        if(s2 == null)
        {
            throw new SNMPSessionException("username cannot be null for a v3 session");
        }
        if(s1.equals("NoAuthentication"))
        {
            authenticationType = null;
        } else
        if(s1.equals("MD5"))
        {
            authenticationType = com.netaphor.snmp.Session.MD5AUTHENTICATION;
        } else
        if(s1.equals("SHA"))
        {
            authenticationType = com.netaphor.snmp.Session.SHAAUTHENTICATION;
        } else
        {
            throw new SNMPSessionException("unrecognized authentication type: " + s1);
        }
        if(authenticationType != null && (s3 == null || s3.length() < 8))
        {
            throw new SNMPSessionException("Authentication password must be 8 characters or greater for " + s1 + " authentication.");
        }
        if(session == null)
        {
            session = new Session();
        }
        getMIB();
        if((engineID = session.discoverAuthoritativeEngineID(address)) == null)
        {
            throw new SNMPSessionException("Could not discover engine ID for " + address + ".");
        }
        session.synchronizeWithEngine(engineID, address, s2, s3, authenticationType);
        if(s5 != null && s5.length() > 0)
        {
            contextEngineID = COM.dragonflow.Utils.Snmp.SNMPSession.getByteArrayFromHexString(s5);
        }
        if(s6 != null && s6.length() > 0)
        {
            contextName = s6.getBytes();
        }
        if(s4 == null || s4.length() == 0)
        {
            user = new UsmUser(s2, s3, authenticationType, null, null);
        } else
        if(s4.length() >= 8)
        {
            user = new UsmUser(s2, s3, authenticationType, s4, com.netaphor.snmp.Session.CBCDESPRIVACY);
        } else
        {
            throw new SNMPSessionException("Privacy password must be 8 characters or greater for privacy");
        }
        target = new SecureTarget(address, engineID, user);
        target.setVersion(3);
        target.setRetries(2);
        target.setTimeout(100L);
        error = new StringBuffer();
    }

    public SNMPSession(String s, int i, String s1)
        throws COM.dragonflow.Utils.Snmp.SNMPSessionException
    {
        engineID = null;
        contextEngineID = null;
        contextName = null;
        version = i;
        formatOctetStrings = true;
        address = new GenericAddress(s);
        if(!address.isValid())
        {
            throw new SNMPSessionException("host " + s + " is not a valid IP address or hostname.");
        }
        com.netaphor.snmp.CommunityTarget communitytarget = new CommunityTarget(address);
        communitytarget.setCommunity(s1);
        target = communitytarget;
        if(i == 1)
        {
            target.setVersion(0);
        } else
        if(i == 2)
        {
            target.setVersion(1);
        } else
        {
            throw new SNMPSessionException("invalid version " + i + ": must be 1 or 2");
        }
        target.setRetries(2);
        target.setTimeout(100L);
        if(session == null)
        {
            session = new Session();
        }
        getMIB();
        error = new StringBuffer();
    }

    private void getMIB()
    {
        if(mib == null)
        {
            try
            {
                mib = COM.dragonflow.Utils.Snmp.BrowsableMIB.getInstance();
            }
            catch(COM.dragonflow.Utils.Snmp.BrowsableMIBException browsablemibexception)
            {
                COM.dragonflow.Log.LogManager.log("Error", "Could not create BrowsableMIB in SNMPSession constructor: " + browsablemibexception.getMessage());
                mib = null;
            }
            catch(java.io.IOException ioexception)
            {
                COM.dragonflow.Log.LogManager.log("Error", "Could not create BrowsableMIB in SNMPSession constructor: " + ioexception.getMessage());
                mib = null;
            }
        }
    }

    public void setRetries(int i)
    {
        target.setRetries(i);
    }

    public void setTimeout(long l)
    {
        if(l >= 1L)
        {
            target.setTimeout(l);
        }
    }

    public String getError()
    {
        return error.toString();
    }

    public int getErrorIndex()
    {
        return errorIndex;
    }

    public COM.dragonflow.Utils.Snmp.SNMPVariableBinding[] get(COM.dragonflow.Utils.Snmp.SNMPVariableBinding asnmpvariablebinding[])
    {
        error.setLength(0);
        com.netaphor.snmp.PDU pdu = new PDU();
        for(int i = 0; i < asnmpvariablebinding.length; i++)
        {
            com.netaphor.snmp.OID oid = asnmpvariablebinding[i].getOID();
            com.netaphor.snmp.VariableBinding variablebinding = new VariableBinding();
            variablebinding.setOid(oid);
            pdu.append(variablebinding);
        }

        if(contextEngineID != null)
        {
            pdu.setContextEngineID(contextEngineID);
        }
        if(contextName != null)
        {
            pdu.setContextName(contextName);
        }
        com.netaphor.snmp.PDU pdu1 = null;
        try
        {
            pdu1 = session.get(pdu, target);
        }
        catch(java.lang.NullPointerException nullpointerexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "NullPointerException in Netaphor Session.get(): " + nullpointerexception.getMessage());
            error.append("Unknown error while processing SNMP GET request.");
            return null;
        }
        if(pdu1 != null)
        {
            if(pdu1.getErrorStatus().getValue() != 0)
            {
                error.append(pdu1.toStringErrorStatus());
                errorIndex = pdu1.getErrorIndex().getValue();
            }
            com.netaphor.snmp.VariableBinding avariablebinding[] = pdu1.getVariableBindingList();
            if(!$assertionsDisabled && avariablebinding.length != asnmpvariablebinding.length)
            {
                throw new AssertionError();
            }
            for(int j = 0; j < avariablebinding.length; j++)
            {
                com.netaphor.snmp.Variable variable = avariablebinding[j].getVariable();
                String s = variable.toString();
                if(formatOctetStrings && variable.getSyntax() == 4)
                {
                    s = formatOctetString(avariablebinding[j]);
                }
                asnmpvariablebinding[j].setOID(avariablebinding[j].getOid());
                asnmpvariablebinding[j].setSyntax(avariablebinding[j].getSyntax());
                asnmpvariablebinding[j].setValue(s);
            }

            return asnmpvariablebinding;
        } else
        {
            error.append("SNMP GET request timed out");
            return null;
        }
    }

    public COM.dragonflow.Utils.Snmp.SNMPVariableBinding[] get(String as[], String as1[])
    {
        int i = java.lang.Math.min(as.length, as1.length);
        COM.dragonflow.Utils.Snmp.SNMPVariableBinding asnmpvariablebinding[] = new COM.dragonflow.Utils.Snmp.SNMPVariableBinding[i];
        for(int j = 0; j < i; j++)
        {
            asnmpvariablebinding[j] = new SNMPVariableBinding(validateOID(as[j], as1[j]));
        }

        return get(asnmpvariablebinding);
    }

    protected com.netaphor.snmp.VariableBinding[] getColumn(String s)
    {
        error.setLength(0);
        com.netaphor.snmp.VariableBinding avariablebinding[] = null;
        try
        {
            avariablebinding = session.getColumn(new OID(s), target, contextEngineID, contextName);
        }
        catch(java.lang.NullPointerException nullpointerexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "NullPointerException in Netaphor Session.getColumn(): " + nullpointerexception.getMessage());
            error.append("Unknown error while retrieving columnar SNMP data.");
            return null;
        }
        if(avariablebinding == null || avariablebinding.length == 0)
        {
            error.append("Could not retrieve data from  " + address.toString());
            return null;
        } else
        {
            return avariablebinding;
        }
    }

    public COM.dragonflow.Utils.Snmp.SNMPVariableBinding[] getTableColumn(String s)
    {
        error.setLength(0);
        com.netaphor.snmp.VariableBinding avariablebinding[] = null;
        try
        {
            avariablebinding = session.getColumn(new OID(s), target, contextEngineID, contextName);
        }
        catch(java.lang.NullPointerException nullpointerexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "NullPointerException in Netaphor Session.getColumn(): " + nullpointerexception.getMessage());
            error.append("Unknown error while retrieving columnar SNMP data.");
            return null;
        }
        if(avariablebinding == null || avariablebinding.length == 0)
        {
            error.append("Could not retrieve data from  " + address.toString());
            return null;
        }
        Object obj = null;
        COM.dragonflow.Utils.Snmp.SNMPVariableBinding asnmpvariablebinding[] = new COM.dragonflow.Utils.Snmp.SNMPVariableBinding[avariablebinding.length];
        for(int i = 0; i < avariablebinding.length; i++)
        {
            com.netaphor.snmp.Variable variable = avariablebinding[i].getVariable();
            String s1;
            if(formatOctetStrings && variable.getSyntax() == 4)
            {
                s1 = formatOctetString(avariablebinding[i]);
            } else
            {
                s1 = variable.toString();
            }
            asnmpvariablebinding[i] = new SNMPVariableBinding(avariablebinding[i].getOid(), avariablebinding[i].getSyntax(), s1);
        }

        return asnmpvariablebinding;
    }

    private String validateOID(String s, String s1)
    {
        String as[] = COM.dragonflow.Utils.TextUtils.split(s, ".");
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < as.length; i++)
        {
            stringbuffer.append(as[i]);
            if(i != as.length - 1)
            {
                stringbuffer.append(".");
            }
        }

        if(s1 != null)
        {
            String s2 = COM.dragonflow.Utils.TextUtils.removeChars(s1, ".");
            if(s2.length() > 0)
            {
                stringbuffer.append("." + s2);
            }
        }
        return stringbuffer.toString();
    }

    public COM.dragonflow.Utils.Snmp.SNMPVariableBinding getNext(String s)
    {
        error.setLength(0);
        com.netaphor.snmp.OID oid = new OID(s);
        com.netaphor.snmp.PDU pdu = new PDU();
        com.netaphor.snmp.VariableBinding variablebinding = new VariableBinding();
        Object obj = null;
        variablebinding.setOid(oid);
        pdu.append(variablebinding);
        if(contextEngineID != null)
        {
            pdu.setContextEngineID(contextEngineID);
        }
        if(contextName != null)
        {
            pdu.setContextName(contextName);
        }
        com.netaphor.snmp.PDU pdu1 = null;
        try
        {
            pdu1 = session.getNext(pdu, target);
        }
        catch(java.lang.NullPointerException nullpointerexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "NullPointerException in Netaphor Session.getNext(): " + nullpointerexception.getMessage());
            error.append("Unknown error while processing GETNEXT request.");
            return null;
        }
        if(pdu1 != null)
        {
            String s1 = null;
            com.netaphor.snmp.VariableBinding variablebinding1 = pdu1.getVariableBinding(0);
            com.netaphor.snmp.Variable variable = variablebinding1.getVariable();
            if(formatOctetStrings && variable.getSyntax() == 4)
            {
                s1 = formatOctetString(variablebinding1);
            } else
            {
                s1 = variable.toString();
            }
            COM.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = new SNMPVariableBinding(variablebinding1.getOid(), variablebinding1.getSyntax(), s1);
            return snmpvariablebinding;
        } else
        {
            error.append("SNMP GETNEXT request timed out");
            return null;
        }
    }

    protected String formatOctetString(com.netaphor.snmp.VariableBinding variablebinding)
    {
        com.netaphor.snmp.Variable variable = variablebinding.getVariable();
        com.netaphor.snmp.OID oid = variablebinding.getOid();
        com.netaphor.mibcompiler.MIBNode mibnode = null;
        com.netaphor.snmp.OctetString octetstring = null;
        if(variable.getSyntax() != 4 && variable.getSyntax() != 68)
        {
            return variable.toString();
        }
        octetstring = (com.netaphor.snmp.OctetString)variable;
        if((mibnode = mib.getLeafNode(oid)) == null)
        {
            return "NOT DISPLAYABLE";
        }
        com.netaphor.mibcompiler.ObjectType objecttype;
        if((objecttype = mibnode.getObjectType()) == null)
        {
            return "NOT DISPLAYABLE";
        }
        String s;
        if((s = objecttype.getSyntax()) == null)
        {
            return "NOT DISPLAYABLE";
        }
        String s1 = null;
        if(s.equals("DisplayString"))
        {
            s1 = octetstring.toString();
        } else
        if(s.equals("PhysAddress") || s.equals("MacAddress"))
        {
            s1 = octetstring.toHexString(':');
        } else
        if(s.equals("DateAndTime"))
        {
            s1 = formatDateAndTime(octetstring);
        } else
        if(s.equals("TAddress"))
        {
            s1 = formatTAddress(octetstring);
        } else
        if(s.equals("Float"))
        {
            return "NOT DISPLAYABLE";
        }
        if(s1 == null)
        {
            return "NOT DISPLAYABLE";
        } else
        {
            return filterNonPrintableCharacters(s1, ' ');
        }
    }

    private String filterNonPrintableCharacters(String s, char c)
    {
        if(s == null)
        {
            return s;
        }
        StringBuffer stringbuffer = new StringBuffer(s);
        for(int i = 0; i < stringbuffer.length(); i++)
        {
            if(java.lang.Character.isISOControl(stringbuffer.charAt(i)))
            {
                stringbuffer.setCharAt(i, c);
            }
        }

        return stringbuffer.toString();
    }

    private String formatDateAndTime(com.netaphor.snmp.OctetString octetstring)
    {
        byte abyte0[] = octetstring.getValue();
        if(abyte0.length < 8)
        {
            return null;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = (abyte0[0] & 0xff) << 8 | abyte0[1] & 0xff;
        byte byte0 = abyte0[2];
        byte byte1 = abyte0[3];
        byte byte2 = abyte0[4];
        byte byte3 = abyte0[5];
        byte byte4 = abyte0[6];
        byte byte5 = abyte0[7];
        stringbuffer.append(i + "-" + byte0 + "-" + byte1 + "," + byte2 + ":" + byte3 + ":" + byte4 + "." + byte5);
        if(abyte0.length == 11)
        {
            char c = (char)abyte0[8];
            byte byte6 = abyte0[9];
            byte byte7 = abyte0[10];
            stringbuffer.append("," + c + byte6 + byte7);
        }
        return stringbuffer.toString();
    }

    private String formatTAddress(com.netaphor.snmp.OctetString octetstring)
    {
        byte abyte0[] = octetstring.getValue();
        if(abyte0.length < 4)
        {
            return null;
        }
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < 4; i++)
        {
            if(i != 0)
            {
                stringbuffer.append(".");
            }
            stringbuffer.append(abyte0[i]);
        }

        if(abyte0.length == 5)
        {
            stringbuffer.append(":" + abyte0[4]);
        }
        return stringbuffer.toString();
    }

    public boolean isErrorString(String s)
    {
        return errorStrings.get(s) != null;
    }

    private static byte[] getByteArrayFromHexString(String s)
    {
        if(s == null || s.length() <= 0)
        {
            return null;
        }
        s = s.replaceAll(":| |0x|0X", "");
        int i = s.length();
        int j = i / 2 + i % 2;
        byte abyte0[] = new byte[j];
        int k = 0;
        if(i % 2 != 0)
        {
            s = (new StringBuffer(s)).insert(0, '0').toString();
        }
        for(int l = 0; l < i; l += 2)
        {
            abyte0[k++] = java.lang.Integer.valueOf(s.substring(l, l + 2), 16).byteValue();
        }

        return abyte0;
    }

    static 
    {
        $assertionsDisabled = !(COM.dragonflow.Utils.Snmp.SNMPSession.class).desiredAssertionStatus();
        errorStrings = new HashMap();
        errorStrings.put("noSuchObject", new Integer(0));
        errorStrings.put("noSuchName", new Integer(2));
        errorStrings.put("tooBig", new Integer(1));
        errorStrings.put("badValue", new Integer(3));
        errorStrings.put("readOnly", new Integer(4));
        errorStrings.put("genErr", new Integer(5));
        errorStrings.put("authorizationError", new Integer(16));
        errorStrings.put("commitFailed", new Integer(14));
        errorStrings.put("inconsistentName", new Integer(18));
        errorStrings.put("inconsistentValue", new Integer(12));
        errorStrings.put("noAccess", new Integer(6));
        errorStrings.put("noCreation", new Integer(11));
        errorStrings.put("notWritable", new Integer(17));
        errorStrings.put("resourceUnavailable", new Integer(13));
        errorStrings.put("undoFailed", new Integer(15));
        errorStrings.put("wrongEncoding", new Integer(9));
        errorStrings.put("wrongLength", new Integer(8));
        errorStrings.put("wrongType", new Integer(7));
        errorStrings.put("wrongValue", new Integer(10));
    }
}
