/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Resource;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public class SiteViewErrorCodes {

    public static long NO_ERROR = 0L;

    public static long ERR_NOT_AVAILABLE = 10000L;

    public static long ERR_PARAM_API_MONITOR_UNASSOCIATED_ID = 10001L;

    public static long ERR_PARAM_API_MONITOR_NONEXISTANT_PROPERTY = 10002L;

    public static long ERR_PARAM_API_MONITOR_PROPERTY_NOT_FOUND = 10003L;

    public static long ERR_PARAM_API_MONITOR_GROUP_ID_MISSING = 10004L;

    public static long ERR_PARAM_API_MONITOR_UNASSOCIATED_TOPAZ_ID = 10005L;

    public static long ERR_PARAM_API_MONITOR_COUNTER_ALREADY_EXISTS = 10006L;

    public static long ERR_PARAM_API_MONITOR_COUNTER_PROPERTY_NOT_VALID = 10007L;

    public static long ERR_PARAM_API_MONITOR_UNASSOCIATED_COUNTER_ID = 10008L;

    public static long ERR_PARAM_API_MONITOR_BROWSABLE_TYPE_REQUIRED = 10009L;

    public static long ERR_PARAM_API_MONITOR_COUNTER_DOES_NOT_EXIST = 10010L;

    public static long ERR_PARAM_API_MONITOR_VERIFICATION_ERRORS = 10011L;

    public static long ERR_PARAM_API_MONITOR_ID_NOT_VALID = 10012L;

    public static long ERR_PARAM_API_MONITOR_TYPE_NOT_VALID = 10013L;

    public static long ERR_PARAM_API_MONITOR_COUNTER_EXCEPTION = 10014L;

    public static long ERR_PARAM_API_MONITOR_NO_IP = 10015L;

    public static long ERR_PARAM_API_MONITOR_NOT_LOADABLE = 10016L;

    public static long ERR_PARAM_API_MONITOR_INVALID_THRESHOLD = 10026L;

    public static long ERR_PARAM_API_MONITOR_INVALID_THRESHOLD_COND_MISSING = 10036L;

    public static long ERR_PARAM_API_MONITOR_INVALID_THRESHOLD_VAL_MISSING = 10046L;

    public static long ERR_PARAM_API_MONITOR_INVALID_COUNTER = 10056L;

    public static long ERR_PARAM_API_UNABLE_TO_READ_WSDLURL = 10066L;

    public static long ERR_PARAM_API_UNABLE_TO_GENERATE_METHODNAME = 10076L;

    public static long ERR_PARAM_API_UNABLE_TO_GENERATE_ARGNAMES = 10086L;

    public static long ERR_PARAM_API_UNABLE_TO_GENERATE_NAMESPACE = 10096L;

    public static long ERR_PARAM_API_UNABLE_TO_RETRIEVE_DISKS = 10106L;

    public static long ERR_PARAM_API_GROUP_NAME_MISSING = 11000L;

    public static long ERR_PARAM_API_GROUP_NAME_EMPTY = 11001L;

    public static long ERR_PARAM_API_GROUP_NAME_SITEVIEW = 11002L;

    public static long ERR_PARAM_API_GROUP_PROPERTIES_NOT_NULL = 11003L;

    public static long ERR_PARAM_API_GROUP_ID_EMPTY = 11004L;

    public static long ERR_PARAM_API_GROUP_MOVE_DESTINATION_SELF = 11005L;

    public static long ERR_PARAM_API_GROUP_MOVE_DESTINATION_SUBGROUP = 11006L;

    public static long ERR_PARAM_API_GROUP_COPY_DESTINATION_SELF = 11007L;

    public static long ERR_PARAM_API_GROUP_COPY_DESTINATION_SUBGROUP = 11008L;

    public static long ERR_PARAM_API_GROUP_PROPERTY_NOT_FOUND = 11009L;

    public static long ERR_PARAM_API_GROUP_TOPAZ_NOT_CONFIGURED = 11010L;

    public static long ERR_PARAM_API_GROUP_ID_NOT_VALID = 11011L;

    public static long ERR_PARAM_API_GROUP_SUBGROUP_NOT_FOUND = 11012L;

    public static long ERR_PARAM_API_PREFERENCE_NONEXISTANT_PROPERTY = 12000L;

    public static long ERR_PARAM_API_PREFERENCE_PROPERTY_EXCEPTION = 12001L;

    public static long ERR_PARAM_API_PREFERENCE_INSTANCE_NOTFOUND = 12002L;

    public static long ERR_PARAM_API_PREFERENCE_PROFILE_ID_REQUIRED = 12003L;

    public static long ERR_PARAM_API_PREFERENCE_PROFILE_NAME_REQUIRED = 12004L;

    public static long ERR_PARAM_API_PREFERENCE_VERIFICATION_ERRORS = 12005L;

    public static long ERR_PARAM_API_PREFERENCE_MONITOR_REF_SCHEDULE = 12006L;

    public static long ERR_PARAM_API_ALERT_NONEXISTANT_PROPERTY = 13000L;

    public static long ERR_PARAM_API_ALERT_INVALID_TARGET_LIST = 13001L;

    public static long ERR_PARAM_API_ALERT_INVALID_DISABLE_DATE_RANGE = 13002L;

    public static long ERR_PARAM_API_UNASSOCIATED_TOPAZ_ID = 14000L;

    public static long ERR_PARAM_API_TYPE_NOT_FOUND = 14001L;

    public static long ERR_PARAM_API_TOPAZ_SERVER_ADDRESS_MISSING = 14002L;

    public static long ERR_OP_SS_API_SIEBEL_COMMAND_MALFORMED = 14003L;

    public static long ERR_PARAM_PROFILE_NAME_IS_MISSING = 14004L;

    public static long ERR_PARAM_API_REPORT_NO_MONITORS = 15000L;

    public static long ERR_PARAM_API_REPORT_PROPERTY_NOT_FOUND = 15010L;

    public static long ERR_PARAM_API_REPORT_MISSING_GROUP = 15020L;

    public static long ERR_PARAM_API_REPORT_TAB_MISSING_MONITOR = 15030L;

    public static long ERR_PARAM_API_REPORT_TAB_MISSING_GROUP = 15040L;

    public static long ERR_PARAM_API_REPORT_INVALID_TIME = 15050L;

    public static long ERR_PARAM_API_REPORT_INVALID_DATE = 15060L;

    public static long ERR_OP_API_MONITOR_TIMED_OUT = 20000L;

    public static long ERR_OP_API_MONITOR_TIMED_OUT_WAITING = 20001L;

    public static long ERR_OP_SS_EXCEPTION = 30000L;

    public static long ERR_OP_SS_LICENCE_EXCEPTION = 30001L;

    public static long ERR_OP_SS_RUN_COMMAND = 30002L;

    public static long ERR_OP_SS_MUST_BE_CONNECTED_TO_TOPAZ = 30003L;

    public static long ERR_OP_SS_CAN_NOT_RETRIEVE_TOPAZ_SETTINGS = 30004L;

    public static long ERR_OP_SS_CAN_NOT_RETRIEVE_TOPAZ_CENTRAL_AUTH = 30005L;

    public static long ERR_OP_SS_UNDER_TOPAZ_CONTROL = 30006L;

    public static long ERR_OP_SS_CAN_NOT_FREE_PROFILES = 30007L;

    public static long ERR_OP_SS_CAN_NOT_DISABLE_TOPAZ = 30008L;

    public static long ERR_OP_SS_CAN_NOT_RESET_TOPAX_PROFILES = 30009L;

    public static long ERR_OP_SS_LICENSE_EXPIRED = 30010L;

    public static long ERR_OP_SS_ACCOUNT_DOES_NOT_EXIST = 30011L;

    public static long ERR_OP_SS_LOGIN_INCORRECT = 30012L;

    public static long ERR_OP_SS_ACCOUNT_DISABLED = 30013L;

    public static long ERR_OP_SS_SITEVIEW_NOT_FULLY_STARTED = 30014L;

    public static long ERR_OP_SS_CANT_RETRIEVE_FREE_PROFILE_ID = 30015L;

    public static long ERR_OP_SS_ALREADY_ATTACHED = 30016L;

    public static long ERR_OP_SS_NOT_MARKED_CONTROLLED = 30017L;

    public static long ERR_OP_SS_MARKED_CONTROLLED = 30018L;

    public static long ERR_OP_SS_CONTROLLED_BY_OTHER = 30019L;

    public static long ERR_OP_SS_UNSUPPORTED_VERSION = 30020L;

    public static long ERR_OP_SS_CIRCULAR_GROUPS = 30021L;

    public static long ERR_OP_SS_MONITOR_EXCEPTION = 31000L;

    public static long ERR_OP_SS_MONITOR_TOPAZ_NOT_CONFIGURED = 31001L;

    public static long ERR_OP_SS_MONITOR_PERFMON_EXCEPTION = 31002L;

    public static long ERR_OP_SS_MONITOR_LICENSE_LIMIT = 31003L;

    public static long ERR_OP_SS_MONITOR_LICENSE_TYPE = 31004L;

    public static long ERR_OP_SS_MONITOR_TOPAZ_DELETE_EXCEPTION = 31005L;

    public static long ERR_OP_SS_GROUP_EXCEPTION = 32000L;

    public static long ERR_OP_SS_GROUP_LICENSE_LIMIT = 32001L;

    public static long ERR_OP_SS_GROUP_CREATE = 32002L;

    public static long ERR_OP_SS_GROUP_COPY_SUBGROUP = 32003L;

    public static long ERR_OP_SS_GROUP_CREATE_EXCEPTION = 32004L;

    public static long ERR_OP_SS_GROUP_ACCESS_EXCEPTION = 32005L;

    public static long ERR_OP_SS_ALERT_EXCEPTION = 33000L;

    public static long ERR_OP_SS_PREFERENCE_EXCEPTION = 34000L;

    public static long ERR_OP_SS_PREFERENCE_DELETE_NOT_ALLOWED = 34001L;

    public static long ERR_OP_SS_PREFERENCE_RETRIEVE_MASTER_SETTINGS = 34002L;

    public static long ERR_OP_SS_PREFERENCE_UPDATE_EXCEPTION = 34003L;

    public static long ERR_OP_SS_PREFERENCE_RETRIEVE_TOPAZ_SETTINGS = 34004L;

    public static long ERR_OP_SS_PREFERENCE_TOPAZ_UNCONFIGURED = 34005L;

    public static long ERR_OP_SS_PREFERENCE_TOPAZ_ALREADY_CONFIGURED = 34006L;

    public static long ERR_OP_SS_REPORT_NO_DATA = 35000L;

    public static long ERR_OP_SS_REPORT_REALTIME_NO_MONITORS = 35010L;

    public static long ERR_OP_SS_REPORT_REALTIME_NO_OUTPUT = 35020L;

    public static long ERR_OP_SS_REPORT_CANNOT_WRITE = 35030L;

    public static long ERR_OP_SS_REPORT_MUTLTIPLE_ERRORS = 35040L;

    public static long ERR_OP_SS_REPORT_OUT_OF_MEMORY = 35050L;

    public static long ERR_OP_SS_NOT_AUTHORIZED = 36000L;

    public static long ERR_OP_SS_OUT_OF_MEMORY = 36010L;

    public static long ERR_OP_SS_HEALTH_CANNOT_WRITE = 37000L;

    public static long ERR_OP_SS_HEALTH_NO_TEMPLATE_FILE = 37010L;

    public static long ERR_AVAIL_SS_GENERAL = 40000L;

    public static long ERR_AVAIL_SS_NO_MACHINE_ADAPTOR = 40001L;

    public static long ERR_AVAIL_SS_MONITOR_TIMED_OUT = 40002L;

    public SiteViewErrorCodes() {
    }

}
