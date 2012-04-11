/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StandardPreference;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;

public class DDCDefaultPreferences extends COM.dragonflow.SiteView.Preferences {

    public static COM.dragonflow.Properties.StringProperty pConfigJdbcURL;

    public static COM.dragonflow.Properties.StringProperty pConfigJdbcDriver;

    public static COM.dragonflow.Properties.StringProperty pConfigJdbcUser;

    public static COM.dragonflow.Properties.StringProperty pConfigJdbcPassword;

    public static COM.dragonflow.Properties.StringProperty pInitializeDatabaseFromFiles;

    public static COM.dragonflow.Properties.StringProperty pFileCheckFrequency;

    public DDCDefaultPreferences() {
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, java.util.HashMap hashmap, java.util.HashMap hashmap1) {
        if (stringproperty == pFileCheckFrequency) {
            try {
                new Integer(s);
            } catch (java.lang.NumberFormatException numberformatexception) {
                hashmap1.put(stringproperty, "The entry is not a valid number.");
            }
        }
        return s;
    }

    static {
        pConfigJdbcURL = new StringProperty("_configJdbcURL");
        pConfigJdbcURL.setDisplayText("Database Connection URL", "Enter the URL to the database connection (for example, if the ODBC connection is called, the URL would be jdbc:odbc:).");
        pConfigJdbcURL.setParameterOptions(true, 1, false);
        pConfigJdbcDriver = new StringProperty("_configJdbcDriver");
        pConfigJdbcDriver.setDisplayText("Database Driver", "Enter the driver used to connect to the database.");
        pConfigJdbcDriver.setParameterOptions(true, 2, false);
        pConfigJdbcUser = new StringProperty("_configJdbcUser");
        pConfigJdbcUser.setDisplayText("Database Username", "Enter the username used to connect to the database.");
        pConfigJdbcUser.setParameterOptions(true, 3, false);
        pConfigJdbcPassword = new StringProperty("_configJdbcPassword");
        pConfigJdbcPassword.setDisplayText("Database Password", "Enter the password used to connect to the database.");
        pConfigJdbcPassword.setParameterOptions(true, 4, false);
        pConfigJdbcPassword.isPassword = true;
        pInitializeDatabaseFromFiles = new BooleanProperty("_initializeDatabaseFromFiles", "");
        pInitializeDatabaseFromFiles
                .setDisplayText(
                        "Initialize database from current configuration files",
                        "If you would like the database initialized with the current contents of the configuration files, check this box. Warning: checking this box will delete the current configurations stored in the database for this server - we advise using this feature only when initially setting up database configuration.");
        pInitializeDatabaseFromFiles.setParameterOptions(true, 5, true);
        pFileCheckFrequency = new NumericProperty("_fileCheckFrequency", "120", "seconds");
        pFileCheckFrequency.setDisplayText("Configuration Update Frequency", "Enter the configuration update frequency in seconds.");
        pFileCheckFrequency.setParameterOptions(true, 6, true);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pConfigJdbcURL, pConfigJdbcDriver, pConfigJdbcUser, pConfigJdbcPassword, pInitializeDatabaseFromFiles, pFileCheckFrequency };
        COM.dragonflow.StandardPreference.DDCDefaultPreferences.addProperties("COM.dragonflow.StandardPreference.DDCDefaultPreferences", astringproperty);
    }
}
