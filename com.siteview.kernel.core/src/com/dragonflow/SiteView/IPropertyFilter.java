/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * IPropertyFilter.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>IPropertyFilter</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;
import java.util.Vector;

public interface IPropertyFilter {

    public abstract Enumeration getConfigurationAddProperties(Vector vector,
            boolean flag, int i);

    public abstract Enumeration getConfigurationEditProperties(Vector vector,
            boolean flag, int i);

    public abstract Enumeration getConfigurationAllProperties(Vector vector,
            boolean flag);

    public abstract Enumeration getConfigurationRequiredProperties(String s)
            throws Exception;

    public abstract Enumeration getMeasurementProperties(Vector vector,
            boolean flag);

    public abstract Enumeration getRuntimeProperties(Vector vector, boolean flag);
}
