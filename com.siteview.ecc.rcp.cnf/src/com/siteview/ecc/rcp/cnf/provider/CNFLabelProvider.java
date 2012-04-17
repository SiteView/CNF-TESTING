package com.siteview.ecc.rcp.cnf.provider;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

import com.siteview.ecc.rcp.cnf.data.Child;
import com.siteview.ecc.rcp.cnf.data.Parent;

/**
 * Label provider for the parent child non-resource based CNF viewer
 * @author Dragonflow
 * @version $Id$
 */
public class CNFLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider
{
    public String getText(Object element)
    {
        if (element instanceof HashMap)
        {
            return (String) ((HashMap)element).get("Name");
        } else if (element instanceof Child) 
        {
            return ((Child)element).getName();
        } 
        return null;
    }

    public String getDescription(Object element)
    {
        String text = getText(element);
        return "This is a description of " + text;
    }
    
    public Image getImage(Object element)
    {
        if (element instanceof Parent) 
        {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
        } else if (element instanceof Child) 
        {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
        }
        return null;
    }
}
