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
// WebSphereConnectionProperties, WebSphereServiceImpl

public final class WebSphereServiceImpl_Skel implements java.rmi.server.Skeleton {

    private static final java.rmi.server.Operation operations[] = { new Operation("COM.dragonflow.Utils.WebSphere.WebSphereServer getServer(COM.dragonflow.Utils.WebSphere.WebSphereConnectionProperties)"), new Operation("long getToken()") };

    private static final long interfaceHash = 0x570486a708144ae9L;

    public WebSphereServiceImpl_Skel() {
    }

    public void dispatch(java.rmi.Remote remote, java.rmi.server.RemoteCall remotecall, int i, long l) throws java.lang.Exception {
        if (i < 0) {
            if (l == 0x957f931896ad1f02L) {
                i = 0;
            } else if (l == 0xdb8bfedf0878e4a5L) {
                i = 1;
            } else {
                throw new UnmarshalException("invalid method hash");
            }
        } else if (l != 0x570486a708144ae9L) {
            throw new SkeletonMismatchException("interface hash mismatch");
        }
        COM.dragonflow.Utils.WebSphere.WebSphereServiceImpl websphereserviceimpl = (COM.dragonflow.Utils.WebSphere.WebSphereServiceImpl) remote;
        switch (i) {
        case 0: // '\0'
            COM.dragonflow.Utils.WebSphere.WebSphereConnectionProperties websphereconnectionproperties;
            try {
                java.io.ObjectInput objectinput = remotecall.getInputStream();
                websphereconnectionproperties = (COM.dragonflow.Utils.WebSphere.WebSphereConnectionProperties) objectinput.readObject();
            } catch (java.io.IOException ioexception2) {
                throw new UnmarshalException("error unmarshalling arguments", ioexception2);
            } catch (java.lang.ClassNotFoundException classnotfoundexception) {
                throw new UnmarshalException("error unmarshalling arguments", classnotfoundexception);
            } finally {
                remotecall.releaseInputStream();
            }
            COM.dragonflow.Utils.WebSphere.WebSphereServer websphereserver = websphereserviceimpl.getServer(websphereconnectionproperties);
            try {
                java.io.ObjectOutput objectoutput = remotecall.getResultStream(true);
                objectoutput.writeObject(websphereserver);
            } catch (java.io.IOException ioexception) {
                throw new MarshalException("error marshalling return", ioexception);
            }
            break;

        case 1: // '\001'
            remotecall.releaseInputStream();
            long l1 = websphereserviceimpl.getToken();
            try {
                java.io.ObjectOutput objectoutput1 = remotecall.getResultStream(true);
                objectoutput1.writeLong(l1);
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
