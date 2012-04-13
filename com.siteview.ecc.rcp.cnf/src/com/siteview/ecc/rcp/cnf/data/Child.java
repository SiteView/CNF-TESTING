package com.siteview.ecc.rcp.cnf.data;

/**
 * Child element
 * @author Dragonflow
 * @version $Id$
 */
public class Child
{
    private String name;
    private Parent parent;

    /**
     * Constructor
     */
    public Child(String name)
    {
        super();
        this.name = name;
    }

    // getters and setter
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @param parent
     */
    public void setParent(Parent parent)
    {
        this.parent = parent;
    }

    public Parent getParent()
    {
        return parent;
    }

}
