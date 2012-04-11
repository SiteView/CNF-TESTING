/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.Utils:
// JMXInterface
public class WebSphereJMXInterface extends COM.dragonflow.Utils.JMXInterface {

    public java.lang.Class AdminClientClass;

    public java.lang.Class StatsClass;

    public java.lang.Class PMIModuleConfigClass;

    public java.lang.Class PMIDataInfoClass;

    public java.lang.Class StatsImplClass;

    public java.lang.Class PMIClientClass;

    public java.lang.Class StatisticClass;

    public java.lang.Class BoundaryStatisticImplClass;

    public java.lang.Class BoundedRangeStatisticImplClass;

    public java.lang.Class CountStatisticImplClass;

    public java.lang.Class DoubleStatisticImplClass;

    public java.lang.Class RangeStatisticImplClass;

    public java.lang.Class TimeStatisticImplClass;

    public java.lang.reflect.Method getStatisticNamesMethod;

    public java.lang.reflect.Method getShortNameMethod;

    public java.lang.reflect.Method getMBeanTypeMethod;

    public java.lang.reflect.Method listAllDataMethod;

    public java.lang.reflect.Method getDICategoryMethod;

    public java.lang.reflect.Method getDICommentMethod;

    public java.lang.reflect.Method getDIDescriptionMethod;

    public java.lang.reflect.Method getDINameMethod;

    public java.lang.reflect.Method setConfigMethod;

    public java.lang.reflect.Method getStatisticMethod;

    public java.lang.reflect.Method dataMembersMethod;

    public java.lang.reflect.Method getStatisticsMethod;

    public java.lang.reflect.Method getSubStatsMethod;

    public java.lang.reflect.Method getStatsNameMethod;

    public java.lang.reflect.Method getStatsMethod;

    public java.lang.reflect.Method getStatisticNameMethod;

    public java.lang.reflect.Method getLowerBoundMethod;

    public java.lang.reflect.Method getUpperBoundMethod;

    public java.lang.reflect.Method getCurrentMethod;

    public java.lang.reflect.Method getCountMethod;

    public java.lang.reflect.Method getDoubleMethod;

    public java.lang.reflect.Method getRSCurrentMethod;

    public java.lang.reflect.Method getTotalMethod;

    public java.lang.reflect.Method getNLSValueMethod;

    public WebSphereJMXInterface(java.lang.Class class1) {
        java.lang.ClassLoader classloader = java.lang.Thread.currentThread().getContextClassLoader();
        try {
            AdminClientClass = class1;
            queryNamesMethod = class1.getMethod("queryNames", new java.lang.Class[] { ObjectNameClass, QueryExpClass });
            getAttributeMethod = class1.getMethod("getAttribute", new java.lang.Class[] { ObjectNameClass, String.class });
            getMBeanInfoMethod = class1.getMethod("getMBeanInfo", new java.lang.Class[] { ObjectNameClass });
            java.lang.Class class2 = String.class;
            java.lang.Class class3 = java.lang.Object[].class;
            java.lang.Class class4 = String[].class;
            invokeMethod = class1.getMethod("invoke", new java.lang.Class[] { ObjectNameClass, class2, class3, class4 });
            StatsClass = classloader.loadClass("com.ibm.websphere.management.statistics.Stats");
            PMIModuleConfigClass = classloader.loadClass("com.ibm.websphere.pmi.PmiModuleConfig");
            PMIDataInfoClass = classloader.loadClass("com.ibm.websphere.pmi.PmiDataInfo");
            StatsImplClass = classloader.loadClass("com.ibm.websphere.pmi.stat.StatsImpl");
            getStatisticNamesMethod = StatsClass.getMethod("getStatisticNames", null);
            getShortNameMethod = PMIModuleConfigClass.getMethod("getShortName", null);
            getMBeanTypeMethod = PMIModuleConfigClass.getMethod("getMbeanType", null);
            listAllDataMethod = PMIModuleConfigClass.getMethod("listAllData", null);
            getDICategoryMethod = PMIDataInfoClass.getMethod("getCategory", null);
            getDICommentMethod = PMIDataInfoClass.getMethod("getComment", null);
            getDIDescriptionMethod = PMIDataInfoClass.getMethod("getDescription", null);
            getDINameMethod = PMIDataInfoClass.getMethod("getName", null);
            setConfigMethod = StatsImplClass.getMethod("setConfig", new java.lang.Class[] { PMIModuleConfigClass });
            getStatisticMethod = StatsImplClass.getMethod("getStatistic", new java.lang.Class[] { String.class });
            dataMembersMethod = StatsImplClass.getMethod("dataMembers", null);
            getStatisticsMethod = StatsImplClass.getMethod("getStatistics", null);
            getSubStatsMethod = StatsImplClass.getMethod("getSubStats", null);
            getStatsNameMethod = StatsImplClass.getMethod("getName", null);
            getStatsMethod = StatsImplClass.getMethod("getStats", new java.lang.Class[] { String.class });
            StatisticClass = classloader.loadClass("com.ibm.websphere.management.statistics.Statistic");
            BoundaryStatisticImplClass = classloader.loadClass("com.ibm.websphere.pmi.stat.BoundaryStatisticImpl");
            BoundedRangeStatisticImplClass = classloader.loadClass("com.ibm.websphere.pmi.stat.BoundedRangeStatisticImpl");
            CountStatisticImplClass = classloader.loadClass("com.ibm.websphere.pmi.stat.CountStatisticImpl");
            DoubleStatisticImplClass = classloader.loadClass("com.ibm.websphere.pmi.stat.DoubleStatisticImpl");
            RangeStatisticImplClass = classloader.loadClass("com.ibm.websphere.pmi.stat.RangeStatisticImpl");
            TimeStatisticImplClass = classloader.loadClass("com.ibm.websphere.pmi.stat.TimeStatisticImpl");
            getStatisticNameMethod = StatisticClass.getMethod("getName", null);
            getLowerBoundMethod = BoundaryStatisticImplClass.getMethod("getLowerBound", null);
            getUpperBoundMethod = BoundaryStatisticImplClass.getMethod("getUpperBound", null);
            getCurrentMethod = BoundedRangeStatisticImplClass.getMethod("getCurrent", null);
            getCountMethod = CountStatisticImplClass.getMethod("getCount", null);
            getDoubleMethod = DoubleStatisticImplClass.getMethod("getDouble", null);
            getRSCurrentMethod = RangeStatisticImplClass.getMethod("getCurrent", null);
            getTotalMethod = TimeStatisticImplClass.getMethod("getTotal", null);
            PMIClientClass = classloader.loadClass("com.ibm.websphere.pmi.client.PmiClient");
            getNLSValueMethod = PMIClientClass.getMethod("getNLSValue", new java.lang.Class[] { String.class });
        } catch (java.lang.Exception exception) {
            java.lang.System.err.println("Failed to load javax.management class or method: " + exception.toString());
        }
    }
}
