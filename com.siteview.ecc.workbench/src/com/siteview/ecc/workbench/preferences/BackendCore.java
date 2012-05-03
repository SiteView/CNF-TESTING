package  com.siteview.ecc.workbench.preferences;

import com.siteview.ecc.workbench.preferences.runtimeinfo.RuntimeInfoManager;

//import org.erlide.backend.internal.BackendFactory;
//import org.erlide.backend.internal.BackendManager;
//import org.erlide.backend.runtimeinfo.RuntimeInfo;
//import org.erlide.backend.runtimeinfo.RuntimeInfoManager;

public class BackendCore {

    private static RuntimeInfoManager runtimeInfoManager;
//    private static IBackendManager backendManager;

    public static final RuntimeInfoManager getRuntimeInfoManager() {
        if (runtimeInfoManager == null) {
            runtimeInfoManager = new RuntimeInfoManager();
        }
        return runtimeInfoManager;
    }

//    public static final IBackendManager getBackendManager() {
//        if (backendManager == null) {
//            final RuntimeInfo erlideRuntime = getRuntimeInfoManager()
//                    .getErlideRuntime();
//            final BackendFactory backendFactory = new BackendFactory(
//                    getRuntimeInfoManager());
//            backendManager = new BackendManager(erlideRuntime, backendFactory);
//        }
//        return backendManager;
//    }
}
