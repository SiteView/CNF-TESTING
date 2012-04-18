/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * PDHRawCounterCache.java
 *
 * History:
 *
 */
package com.dragonflow.Utils.WebSphere;

import java.rmi.MarshalException;
import java.rmi.UnexpectedException;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;

// Referenced classes of package com.dragonflow.Utils.WebSphere:
// WebSphereServer, WebSphereService, WebSphereConnectionProperties

public final class WebSphereServiceImpl_Stub extends java.rmi.server.RemoteStub implements com.dragonflow.Utils.WebSphere.WebSphereService, java.rmi.Remote {

    private static final java.rmi.server.Operation operations[] = { new Operation("com.dragonflow.Utils.WebSphere.WebSphereServer getServer(com.dragonflow.Utils.WebSphere.WebSphereConnectionProperties)"), new Operation("long getToken()") };

    private static final long interfaceHash = 0x570486a708144ae9L;

    private static final long serialVersionUID = 2L;

    private static boolean useNewInvoke;

    private static java.lang.reflect.Method $method_getServer_0;

    private static java.lang.reflect.Method $method_getToken_1;

    public WebSphereServiceImpl_Stub() {
    }

    public WebSphereServiceImpl_Stub(java.rmi.server.RemoteRef remoteref) {
        super(remoteref);
    }

    public com.dragonflow.Utils.WebSphere.WebSphereServer getServer(com.dragonflow.Utils.WebSphere.WebSphereConnectionProperties websphereconnectionproperties) throws java.rmi.RemoteException {
        try {
            if (useNewInvoke) {
                java.lang.Object obj = super.ref.invoke(this, $method_getServer_0, new java.lang.Object[] { websphereconnectionproperties }, 0x957f931896ad1f02L);
                return (com.dragonflow.Utils.WebSphere.WebSphereServer) obj;
            }
            java.rmi.server.RemoteCall remotecall = super.ref.newCall(this, operations, 0, 0x570486a708144ae9L);
            try {
                java.io.ObjectOutput objectoutput = remotecall.getOutputStream();
                objectoutput.writeObject(websphereconnectionproperties);
            } catch (java.io.IOException ioexception) {
                throw new MarshalException("error marshalling arguments", ioexception);
            }
            super.ref.invoke(remotecall);
            com.dragonflow.Utils.WebSphere.WebSphereServer websphereserver;
            try {
                java.io.ObjectInput objectinput = remotecall.getInputStream();
                websphereserver = (com.dragonflow.Utils.WebSphere.WebSphereServer) objectinput.readObject();
            } catch (java.io.IOException ioexception1) {
                throw new UnmarshalException("error unmarshalling return", ioexception1);
            } catch (java.lang.ClassNotFoundException classnotfoundexception) {
                throw new UnmarshalException("error unmarshalling return", classnotfoundexception);
            } finally {
                super.ref.done(remotecall);
            }
            return websphereserver;
        } catch (java.lang.RuntimeException runtimeexception) {
            throw runtimeexception;
        } catch (java.rmi.RemoteException remoteexception) {
            throw remoteexception;
        } catch (java.lang.Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public long getToken() throws java.rmi.RemoteException {
        try {
            if (useNewInvoke) {
                java.lang.Object obj = super.ref.invoke(this, $method_getToken_1, null, 0xdb8bfedf0878e4a5L);
                return ((java.lang.Long) obj).longValue();
            }
            java.rmi.server.RemoteCall remotecall = super.ref.newCall(this, operations, 1, 0x570486a708144ae9L);
            super.ref.invoke(remotecall);
            long l;
            try {
                java.io.ObjectInput objectinput = remotecall.getInputStream();
                l = objectinput.readLong();
            } catch (java.io.IOException ioexception) {
                throw new UnmarshalException("error unmarshalling return", ioexception);
            } finally {
                super.ref.done(remotecall);
            }
            return l;
        } catch (java.lang.RuntimeException runtimeexception) {
            throw runtimeexception;
        } catch (java.rmi.RemoteException remoteexception) {
            throw remoteexception;
        } catch (java.lang.Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    static {
        try {
            (java.rmi.server.RemoteRef.class).getMethod("invoke", new java.lang.Class[] { java.rmi.Remote.class, java.lang.reflect.Method.class, java.lang.Object[].class, java.lang.Long.TYPE });
            useNewInvoke = true;
            $method_getServer_0 = (com.dragonflow.Utils.WebSphere.WebSphereService.class).getMethod("getServer", new java.lang.Class[] { com.dragonflow.Utils.WebSphere.WebSphereConnectionProperties.class });
            $method_getToken_1 = (com.dragonflow.Utils.WebSphere.WebSphereService.class).getMethod("getToken", new java.lang.Class[0]);
        } catch (java.lang.NoSuchMethodException _ex) {
            useNewInvoke = false;
        }
    }
}
