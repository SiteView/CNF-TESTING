package com.siteview.ecc.workbench.editors;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
public class FormEditorInput implements IEditorInput {

	private String name;
	
	private List monitors;
	private List groups;	
	CompilationUnit unit; 
	
	public CompilationUnit getUnit() {
		return unit;
	}
	public void setUnit(CompilationUnit unit) {
		this.unit = unit;
	}
	public FormEditorInput(String name) {
		this.name = name;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		return true;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_OBJ_ELEMENT);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		return name;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return getName();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}
	public boolean equals(Object obj){
		if(obj instanceof FormEditorInput)
			return this.name.equals(((FormEditorInput)obj).getName());
		return false;
	}
	/**
	 * @return the monitors
	 */
	public List getMonitors() {
		return monitors;
	}
	/**
	 * @param monitors the monitors to set
	 */
	public void setMonitors(List monitors) {
		this.monitors = monitors;
	}
	/**
	 * @return the groups
	 */
	public List getGroups() {
		return groups;
	}
	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List groups) {
		this.groups = groups;
	}
}