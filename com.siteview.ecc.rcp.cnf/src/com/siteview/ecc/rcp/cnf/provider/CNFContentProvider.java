package com.siteview.ecc.rcp.cnf.provider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.siteview.ecc.rcp.cnf.data.Child;
import com.siteview.ecc.rcp.cnf.data.Parent;
import com.siteview.ecc.rcp.cnf.data.Root;

/**
 * Content provider for the parent child non-resource based CNF viewer
 * @author Dragonflow
 * @version $Id$
 */
public class CNFContentProvider implements ITreeContentProvider
{

    private static final Object[] EMPTY_ARRAY = new Object[0];
    private Parent[] parents;

    public Object[] getChildren(Object parentElement)
    {
        if (parentElement instanceof Root)
        {
            if (parents == null)
            {
                initializeParents(parentElement);
            }
            return parents;
        } else if (parentElement instanceof Parent)
        {
            return ((Parent) parentElement).getChildren();
        } else if (parentElement instanceof Child)
        {
            return EMPTY_ARRAY;
        } else
        {
            return EMPTY_ARRAY;
        }
    }

    public Object getParent(Object element)
    {
        if (element instanceof Child)
        {
            return ((Child) element).getParent();
        } else if (element instanceof Parent)
        {
            return ((Parent) element).getRoot();
        }
        return null;
    }

    public boolean hasChildren(Object element)
    {
        return (element instanceof Root || element instanceof Parent);
    }

    public Object[] getElements(Object inputElement)
    {
        return getChildren(inputElement);
    }

    public void dispose()
    {
        this.parents = null;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    { /* ... */
    }

    /**
     * Init code for empty model
     */
    private void initializeParents(Object parentElement)
    {
        this.parents = new Parent[3];
        for (int i = 0; i < this.parents.length; i++)
        {
            this.parents[i] = new Parent("Parent " + i);
            this.parents[i].setRoot(parentElement);
            Child[] children = new Child[3];
            for (int j = 0; j < children.length; j++)
            {
                children[j] = new Child("Child " + i + j);
            }
            this.parents[i].setChildren(children);
        }
    }

}
