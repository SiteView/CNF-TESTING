package com.siteview.ecc.rcp.cnf.internal;

import java.lang.reflect.InvocationTargetException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.siteview.ecc.rcp.cnf.data.MonitorServer;
import com.siteview.ecc.rcp.cnf.data.MonitorServerManager;
import com.siteview.ecc.rcp.cnf.ui.SecureLoginDialog;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication
{

	public static final String PLUGIN_ID = "com.siteview.ecc";
    public Object start(IApplicationContext context) throws RemoteException, NotBoundException
    {
     
    	context.applicationRunning();//only run one
//			MonitorServerManager.getInstance().getServers().get("");
		MonitorServer server = new MonitorServer("admin", "", "admin");
//			MonitorServerManager.getInstance().registerServer(server);
		if (!login(server))				
			return IApplication.EXIT_OK;
//			MonitorServerManager.getInstance().getCurServer().connectAndLogin();
//			server.connectAndLogin();
    	Display display = PlatformUI.createDisplay();
        try
        {
            int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
            

            if (returnCode == PlatformUI.RETURN_RESTART)
            {
                return IApplication.EXIT_RESTART;
            }
            return IApplication.EXIT_OK;
        } finally
        {
            display.dispose();
        }
    }
    
	private boolean login(final MonitorServer server)
	{
		boolean firstTry = true;
		SecureLoginDialog loginDialog = new SecureLoginDialog(null);
		while (!(MonitorServerManager.getInstance().getLoginSucess()))
		{
			IPreferencesService service = Platform.getPreferencesService();
			boolean auto_login = service.getBoolean(Application.PLUGIN_ID,
					LoginPreferencePage.AUTO_LOGIN, false, null);
			MonitorServer details = loginDialog.getConnectionDetails();
			if (!auto_login || details == null || !firstTry) 
			{
				if (loginDialog.open() != Window.OK)
					return false;
				details = loginDialog.getConnectionDetails();
			}
			firstTry = false;
			
//			session.setConnectionDetails(details);	
//			MonitorServerManager.getInstance().registerServer(server);
			
			MonitorServerManager.getInstance().setCurServer(details);
			try {
				MonitorServerManager.getInstance().registerServer(details);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connectWithProgress(details);
			
//			HashMap savedDetails = loginDialog.getSavedDetails();
//			for (Iterator it = savedDetails.keySet().iterator(); it.hasNext();) {
//				String name = (String) it.next();
//				MonitorServer multServer = (MonitorServer) savedDetails.get(name);
//				
//				MonitorServerManager.getInstance().setCurServer(multServer);
//				try {
//					MonitorServerManager.getInstance().registerServer(multServer);
////					multServer.connectAndLogin();
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (NotBoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
////				connectWithProgress(multServer);
//			}
		}		
		return true;
	}

	private void connectWithProgress(final MonitorServer server) {
		ProgressMonitorDialog progress = new ProgressMonitorDialog(null);
		progress.setCancelable(true);
		try {
			progress.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
				throws InvocationTargetException {
					try {
						server.connectAndLogin(monitor);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}	
	
    public void stop()
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null)
            return;
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable() {
            public void run()
            {
                if (!display.isDisposed())
                    workbench.close();
            }
        });
    }
}
