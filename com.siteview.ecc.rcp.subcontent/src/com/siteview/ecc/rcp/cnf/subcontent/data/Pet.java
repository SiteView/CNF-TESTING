package com.siteview.ecc.rcp.cnf.subcontent.data;

import com.siteview.ecc.rcp.cnf.data.Child;

/**
 * Represents a pet
 * @author Dragonflow
 */
public class Pet
{
    private String name;
    private Child owner;
    
    public Pet(String name, Child owner)
    {
        super();
        this.name = name;
        this.owner = owner;
    }
    
    
    public final String getName()
    {
        return name;
    }
    public final void setName(String name)
    {
        this.name = name;
    }
    public final Child getOwner()
    {
        return owner;
    }
    public final void setOwner(Child owner)
    {
        this.owner = owner;
    }
}
