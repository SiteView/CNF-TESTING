/*
 * 
 * Created on 2005-2-16 17:01:20
 *
 * SNMPTrapListenerThread.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>SNMPTrapListenerThread</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.net.InetAddress;
import java.util.Observable;

import COM.dragonflow.Log.LogManager;

import com.netaphor.snmp.Address;
import com.netaphor.snmp.GenericAddress;
import com.netaphor.snmp.PDU;
import com.netaphor.snmp.Session;
import com.netaphor.snmp.Target;
import com.netaphor.snmp.TrapEvent;
import com.netaphor.snmp.TrapListener;
import com.netaphor.snmp.TrapPDU;
import com.netaphor.snmp.VariableBinding;

// Referenced classes of package COM.dragonflow.SiteView:
// Platform, Monitor, SNMPTrapBase

class SNMPTrapListenerThread extends Observable implements TrapListener {

    int trapListenerSleepTime;

    private int trapListenerPollTime;

    SNMPTrapListenerThread() {
        trapListenerSleepTime = 5000;
        trapListenerPollTime = 500;
    }

    private void notifyTrapObservers(TrapEvent trapevent) {
        PDU pdu = trapevent.getPDU();
        if (pdu != null) {
            setChanged();
            try {
                notifyObservers(pdu);
            } catch (Throwable throwable) {
                LogManager.log("Error",
                        "While notifying external SNMP clients, received exception "
                                + throwable.getMessage());
            }
        }
    }

    public void onTrap(TrapEvent trapevent) {
        int i;
        label0: {
            PDU pdu = trapevent.getPDU();
            i = trapListenerSleepTime;
            notifyTrapObservers(trapevent);
            if (pdu == null) {
                break label0;
            }
            i = trapListenerPollTime;
            if (!(pdu instanceof TrapPDU)) {
                PDU _tmp = pdu;
                if (pdu.getType() != -92) {
                    PDU _tmp1 = pdu;
                    if (pdu.getType() != -89) {
                        LogManager.log("Error",
                                "received unknown snmp packet type: "
                                        + pdu.getType());
                        break label0;
                    }
                }
            }
            String s = "";
            PDU _tmp2 = pdu;
            if (pdu.getType() == -92) {
                TrapPDU trappdu = (TrapPDU) pdu;
                s = "from=" + trapevent.getSource().toString() + "\toid="
                        + trappdu.getEnterprise().toString() + "\ttrap="
                        + getTrapName(trappdu.getGenericTrapType().getValue())
                        + "\tspecific="
                        + trappdu.getSpecificTrapType().toString()
                        + "\ttraptime=" + trappdu.getTimestamp()
                        + "\tcommunity=" + trapevent.getCommunity()
                        + "\tagent=" + trappdu.getAgentAddress().toString()
                        + "\tversion= v1";
            } else {
                PDU _tmp3 = pdu;
                if (pdu.getType() == -89) {
                    String s1 = " n/a";
                    Target target;
                    Address address;
                    if ((target = pdu.getRespondingTarget()) != null
                            && (address = target.getAddress()) != null) {
                        s1 = address.toString();
                        int l;
                        if ((l = s1.lastIndexOf(':')) > 0) {
                            s1 = s1.substring(0, l);
                        }
                    }
                    s = "from=" + trapevent.getSource().toString() + "\toid="
                            + " n/a" + "\ttrap= n/a" + "\tspecific= n/a"
                            + "\ttraptime=" + "n/a" + "\tcommunity="
                            + trapevent.getCommunity() + "\tagent=" + s1
                            + "\tversion= v2c";
                }
            }
            int j = 1;
            VariableBinding avariablebinding[] = pdu.getVariableBindingList();
            for (int k = 0; k < avariablebinding.length; k++) {
                String s2 = avariablebinding[k].getVariable().toString();
                int i1 = s2.indexOf("STRING: ");
                if (i1 != -1) {
                    s2 = s2.substring(i1 + 8);
                }
                s2 = s2.replace('\r', '_');
                s2 = s2.replace('\n', '_');
                s2 = s2.replace('\t', '_');
                s = s + "\tvar" + j + "=" + s2;
                j++;
            }

            LogManager.log("SNMPTrap", s);
        }
        Platform.sleep(i);
    }

    public void startListenerThread(Monitor monitor) throws Exception {
        Session session = new Session();
        long l = monitor.getSettingAsLong("_snmpTrapListener");
        trapListenerSleepTime = monitor.getSettingAsLong("_snmpTrapSleepTime",
                5000);
        InetAddress inetaddress = null;
        try {
            inetaddress = InetAddress.getLocalHost();
        } catch (Exception exception) {
            LogManager.log("Error",
                    "SNMP Trap Listener is unable to get local host");
            exception.printStackTrace();
            return;
        }
        GenericAddress genericaddress = null;
        if (l > 0L) {
            genericaddress = new GenericAddress(inetaddress + ":" + l);
        } else {
            genericaddress = new GenericAddress(inetaddress.getHostAddress());
        }
        session.enableTrapReception(genericaddress, this);
        try {
            Thread.sleep(trapListenerSleepTime);
        } catch (InterruptedException interruptedexception) {
        }
        SNMPTrapBase.listenerStatus = "Receiving SNMP traps at address: "
                + inetaddress + ", port: " + l + ", poll: ";
        LogManager.log("RunMonitor", SNMPTrapBase.listenerStatus);
    }

    String getTrapName(int i) {
        if (i == 0) {
            return "cold start";
        }
        if (i == 1) {
            return "warm start";
        }
        if (i == 2) {
            return "link down";
        }
        if (i == 3) {
            return "link up";
        }
        if (i == 6) {
            return "enterprise specific";
        } else {
            return "" + i;
        }
    }
}
