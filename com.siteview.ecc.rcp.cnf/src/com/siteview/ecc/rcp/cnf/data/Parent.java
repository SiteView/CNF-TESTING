package com.siteview.ecc.rcp.cnf.data;

/**
 * @author Dragonflow
 * @version $Id$
 */
public class Parent extends Child
{
    private Child[] children = new Child[0];
    private Object rootElement;

    /**
     * Constructor 
     */
    public Parent(String name)
    {
        super(name);
    }

    public void setChildren(Child[] children)
    {
        if (children != null)
        {
            setChildrensParent(null, this.children);
        }
        this.children = children;
        setChildrensParent(this, this.children);
    }

    /**
     * Sets children's parent
     * @param parent parent to be set
     * @param children children to set the parent
     */
    private static void setChildrensParent(Parent parent, Child[] children)
    {
        for (int i = 0; i < children.length; i++)
        {
            children[i].setParent(parent);
        }
    }

    // getter and setter
    public Child[] getChildren()
    {
        return children;
    }

    public void setRoot(Object parentElement)
    {
        this.rootElement = parentElement;
    }

    public Object getRoot()
    {
        return rootElement;
    }

}
