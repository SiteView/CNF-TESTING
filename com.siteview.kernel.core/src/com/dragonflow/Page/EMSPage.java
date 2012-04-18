/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Page;

//import com.dragonflow.topaz.ems.TopazEmsApi.EmsApiException;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class EMSPage extends com.dragonflow.Page.CGI
{

    private static final String ARsHandlerClass = "com.dragonflow.topaz.ems.RemedyARsSource.RemedyARsHandler";
    private static final String RemedyHandlerClass = "com.dragonflow.topaz.ems.ClarifySource.ClarifyServerHandler";
    private static final String bmcPatrolAdapter = "com.dragonflow.topaz.ems.tdm.backend.bmc.BmcCdmMixinBackendAdapter";
    private static final String TOPAZ_EMS_API_DIR;
//    private static com.dragonflow.topaz.ems.GenericProbe.util.log.LogHelper _logHelper;

    public EMSPage()
    {
//        _logHelper.debug("EmsPage constructor - create page.");
    }

    public void printBody()
        throws java.lang.Exception
    {
        String EmsType = request.getValue("_EmsType");
        java.lang.Integer iType = new Integer(EmsType);
        String xmlResult = "";
//        _logHelper.flow("EmsPage request: " + request.queryString);
        switch(iType.intValue())
        {
        case 1: // '\001'
//            try
//            {
////                _logHelper.flow("EmsPage EmsType==HelpDesk.");
//                xmlResult = HelpDeskHandleRequest();
//                break;
//            }
//            catch(com.dragonflow.topaz.ems.TopazEmsApi.EmsApiException e)
//            {
//                _logHelper.error("ERROR: request wasn't processed due to unsupported error.\nIf this error occures please consider to restart SiteView");
//                _logHelper.debug("EmsPage::printBody couldn't handle HelpDesk request.", e);
//                xmlResult = "Error:" + e.apiAction() + " - " + e.getMessage();
//            }
            break;

        default:
//            try
//            {
////                _logHelper.flow("Trying to serve BMC TBA Adapter request");
//                xmlResult = handleConfigurationRequest();
//                break;
//            }
////            catch(com.dragonflow.topaz.ems.TopazEmsApi.EmsApiException e)
//            {
//                _logHelper.error("ERROR: request wasn't processed due to unsupported Ems type=" + iType.intValue() + ".\n Currently only HelpDesk type are supported.");
//            }
//            _logHelper.debug("EmsPage::printBody unsupported EmsType -" + iType.intValue());
//            xmlResult = "Error: EmsPage handling - Unknown Ems Type. Not handling request!";
            break;
        }
        outputStream.print(xmlResult);
//        _logHelper.flow("EmsPage request succeeded and create a xml result.");
//        _logHelper.debug("EmsPage:: request XML result: \n" + xmlResult);
    }

    private String HelpDeskHandleRequest()
//        throws com.dragonflow.topaz.ems.TopazEmsApi.EmsApiException
    {
        String EmsAppName = request.getValue("_EmsName");
        java.lang.Integer iName = new Integer(EmsAppName);
//        com.dragonflow.topaz.ems.TopazEmsApi.HelpDeskApiServer hdServer;
        switch(iName.intValue())
        {
        case 1: // '\001'
//            try
//            {
//                java.lang.Class aClass = java.lang.Class.forName("com.dragonflow.topaz.ems.RemedyARsSource.RemedyARsHandler");
//                hdServer = (com.dragonflow.topaz.ems.TopazEmsApi.HelpDeskApiServer)aClass.newInstance();
//            }
//            catch(java.lang.Exception e)
//            {
//                _logHelper.error("ERROR: EmsPage Error instantiating class com.dragonflow.topaz.ems.RemedyARsSource.RemedyARsHandler");
//                _logHelper.flow("Error EmsPage::HelpDeskHandleRequest occured", e);
//                throw new EmsApiException("EmsPage", "Error instantiating class com.dragonflow.topaz.ems.RemedyARsSource.RemedyARsHandler: " + e.getMessage());
//            }
//            hdServer.SetHTTPRequest(request);
            break;

        case 2: // '\002'
//            try
//            {
//                hdServer = (com.dragonflow.topaz.ems.TopazEmsApi.HelpDeskApiServer)java.lang.Class.forName("com.dragonflow.topaz.ems.ClarifySource.ClarifyServerHandler").newInstance();
//            }
//            catch(java.lang.Exception e)
//            {
//                _logHelper.error("ERROR: EmsPage Error instantiating class com.dragonflow.topaz.ems.RemedyARsSource.RemedyARsHandler");
//                _logHelper.flow("Error EmsPage::HelpDeskHandleRequest occured", e);
//                throw new EmsApiException("EmsPage", "Error instantiating class com.dragonflow.topaz.ems.ClarifySource.ClarifyServerHandler: " + e.getMessage());
//            }
//            hdServer.SetHTTPRequest(request);
            break;

        default:
//            _logHelper.error("ERROR: occured when Handling EMS request: Unknown Ems Type. Not handling request!");
//            _logHelper.flow("Error EmsPage::HelpDeskHandleRequest occured: Unknown Ems Type. Not handling request!");
//            throw new EmsApiException("EmsPage handling", "Unknown Ems Type. Not handling request!");
        }
//        return hdServer.HandleRequest(request);
		return "";
    }

    private String handleConfigurationRequest()
//        throws com.dragonflow.topaz.ems.TopazEmsApi.EmsApiException
    {
//        _logHelper.warning("Trying to serve BMC configuration for " + request.getRemoteAddress().toString());
//        com.dragonflow.topaz.ems.TopazEmsApi.HelpDeskApiServer srv = null;
//        try
//        {
//            srv = (com.dragonflow.topaz.ems.TopazEmsApi.HelpDeskApiServer)java.lang.Class.forName("com.dragonflow.topaz.ems.tdm.backend.bmc.BmcCdmMixinBackendAdapter").newInstance();
//        }
//        catch(java.lang.Throwable e)
//        {
//            _logHelper.error("Can not handle the BMC configuration request");
//            throw new EmsApiException("EmsPage handling", "Unknown Ems Type. Not handling request! Reason: " + e.getMessage());
//        }
//        _logHelper.debug("Proceding to handle the request");
//        long startTs = java.lang.System.currentTimeMillis();
//        String answer = srv.HandleRequest(request);
//        long endTs = java.lang.System.currentTimeMillis();
//        long duration = (endTs - startTs) / 1000L;
//        _logHelper.debug("Finished handling request. handling took " + duration + " seconds");
//        return answer;
		return "";
    }

    public void printCGIHeader()
    {
        request.printHeader(outputStream);
    }

    public void printCGIFooter()
    {
        outputStream.flush();
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        com.dragonflow.Page.EMSPage page = new EMSPage();
        java.lang.System.out.println("Loading Page.main");
        if(args.length > 0)
        {
            for(int i = 0; i < args.length; i++)
            {
                java.lang.System.out.println(args[i]);
            }

            page.args = args;
        }
        page.handleRequest();
    }

    static 
    {
        TOPAZ_EMS_API_DIR = com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "ems" + java.io.File.separator + "TopazEmsApi" + java.io.File.separator;
//        try
//        {
//            com.dragonflow.topaz.ems.GenericProbe.util.log.LogPolicyFactory.getInstance("TopazEmsApi").init(TOPAZ_EMS_API_DIR + "log.config");
//            _logHelper = com.dragonflow.topaz.ems.GenericProbe.util.log.LogPolicyFactory.getInstance("TopazEmsApi").getLogger("TopazEmsApi");
//        }
//        catch(java.io.IOException e)
//        {
//            com.dragonflow.Log.LogManager.log("RunMonitor", "error in creating TopazEmsApi log ." + e.getLocalizedMessage());
//        }
//        catch(java.lang.IllegalStateException e)
//        {
//            com.dragonflow.Log.LogManager.log("RunMonitor", "TopazEmsApi log already initialized.");
//        }
    }
}
