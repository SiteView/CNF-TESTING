package com.siteview.ecc.rcp.cnf.subcontent.provider;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

import com.siteview.ecc.rcp.cnf.subcontent.data.Pet;

/**
 * @author Dragonflow
 * @version $Id$
 */
public class CNFSubLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider
{

    public String getDescription(Object anElement)
    {
         return "Description of the " + getText(anElement);
    }

    public String getText(Object element)
    {
        if (element instanceof Pet)
        {
            return ((Pet)element).getName();
        }  
        return null;
    }

    public Image getImage(Object element)
    {
        if (element instanceof Pet) 
        {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
        } 
        return null;
    }
    
}
