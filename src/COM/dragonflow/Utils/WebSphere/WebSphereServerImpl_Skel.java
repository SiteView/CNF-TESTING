/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * PDHRawCounterCache.java
 *
 * History:
 *
 */
package COM.dragonflow.Utils.WebSphere;

import java.rmi.MarshalException;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;
import java.rmi.server.SkeletonMismatchException;

// Referenced classes of package COM.dragonflow.Utils.WebSphere:
// WebSphereServerImpl

public final class WebSphereServerImpl_Skel implements java.rmi.server.Skeleton {

    private static final java.rmi.server.Operation operations[] = { new Operation("String getBrowseData()"), new Operation("COM.dragonflow.Utils.WebSphere.WebSphereCounter getCounters(COM.dragonflow.Utils.WebSphere.WebSphereCounter[])[]"),
            new Operation("String getServerName()") };

    private static final long interfaceHash = 0xf36e3583336c79d2L;

    public WebSphereServerImpl_Skel() {
    }

    public void dispatch(java.rmi.Remote remote, java.rmi.server.RemoteCall remotecall, int i, long l) throws java.lang.Exception {
        if (i < 0) {
            if (l == 0x8f52993aa062f7fbL) {
                i = 0;
            } else if (l == 0x61a7d5ece71019b9L) {
                i = 1;
            } else if (l == 0x850816858666c3c4L) {
                i = 2;
            } else {
                throw new UnmarshalException("invalid method hash");
            }
        } else if (l != 0xf36e3583336c79d2L) {
            throw new SkeletonMismatchException("interface hash mismatch");
        }
        COM.dragonflow.Utils.WebSphere.WebSphereServerImpl websphereserverimpl = (COM.dragonflow.Utils.WebSphere.WebSphereServerImpl) remote;
        switch (i) {
        case 0: // '\0'
            remotecall.releaseInputStream();
            String s = websphereserverimpl.getBrowseData();
            try {
                java.io.ObjectOutput objectoutput = remotecall.getResultStream(true);
                objectoutput.writeObject(s);
            } catch (java.io.IOException ioexception) {
                throw new MarshalException("error marshalling return", ioexception);
            }
            break;

        case 1: // '\001'
            COM.dragonflow.Utils.WebSphere.WebSphereCounter awebspherecounter[];
            try {
                java.io.ObjectInput objectinput = remotecall.getInputStream();
                awebspherecounter = (COM.dragonflow.Utils.WebSphere.WebSphereCounter[]) objectinput.readObject();
            } catch (java.io.IOException ioexception3) {
                throw new UnmarshalException("error unmarshalling arguments", ioexception3);
            } catch (java.lang.ClassNotFoundException classnotfoundexception) {
                throw new UnmarshalException("error unmarshalling arguments", classnotfoundexception);
            } finally {
                remotecall.releaseInputStream();
            }
            COM.dragonflow.Utils.WebSphere.WebSphereCounter awebspherecounter1[] = websphereserverimpl.getCounters(awebspherecounter);
            try {
                java.io.ObjectOutput objectoutput2 = remotecall.getResultStream(true);
                objectoutput2.writeObject(awebspherecounter1);
            } catch (java.io.IOException ioexception2) {
                throw new MarshalException("error marshalling return", ioexception2);
            }
            break;

        case 2: // '\002'
            remotecall.releaseInputStream();
            String s1 = websphereserverimpl.getServerName();
            try {
                java.io.ObjectOutput objectoutput1 = remotecall.getResultStream(true);
                objectoutput1.writeObject(s1);
            } catch (java.io.IOException ioexception1) {
                throw new MarshalException("error marshalling return", ioexception1);
            }
            break;

        default:
            throw new UnmarshalException("invalid method number");
        }
    }

    public java.rmi.server.Operation[] getOperations() {
        return (java.rmi.server.Operation[]) operations.clone();
    }

}
