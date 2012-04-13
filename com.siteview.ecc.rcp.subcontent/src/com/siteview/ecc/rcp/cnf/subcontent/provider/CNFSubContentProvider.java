package com.siteview.ecc.rcp.cnf.subcontent.provider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.siteview.ecc.rcp.cnf.data.Child;
import com.siteview.ecc.rcp.cnf.data.Parent;
import com.siteview.ecc.rcp.cnf.subcontent.data.Pet;

/**
 * @author Dragonflow
 * @version $Id$
 */
public class CNFSubContentProvider implements ITreeContentProvider
{

    private static final Object[] EMPTY_ARRAY = new Object[0];

    public Object[] getChildren(Object parentElement)
    {
        if (parentElement instanceof Child 
                && !(parentElement instanceof Parent))
        {
            Child child = ((Child) parentElement);
            char lastDigit = child.getName().charAt(child.getName().length() - 1);
            if (Character.isDigit(lastDigit))
            {
                int petCount = Integer.parseInt(String.valueOf(lastDigit));
                Pet[] pets = new Pet[petCount];
                for (int i = 0; i < petCount; i++)
                {
                    pets[i] = new Pet(child.getName() + "'s pet " + (i + 1), child);
                }
                return pets;
            }
            return EMPTY_ARRAY;
        } else
        {
            return EMPTY_ARRAY;
        }
    }

    public Object getParent(Object element)
    {
        if (element instanceof Pet)
        {
            return ((Pet) element).getOwner();
        }
        return null;
    }

    public boolean hasChildren(Object element)
    {
        return getChildren(element).length != 0;
    }

    public Object[] getElements(Object inputElement)
    {
        return getChildren(inputElement);
    }

    public void dispose()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    { /* ... */
    }
}
