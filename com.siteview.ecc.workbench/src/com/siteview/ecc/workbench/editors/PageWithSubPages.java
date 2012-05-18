/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.siteview.ecc.workbench.editors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.*;
//import org.eclipse.ui.forms.article.FormArticlePlugin;
import org.eclipse.ui.forms.editor.*;
import org.eclipse.ui.forms.widgets.*;
/**
 *
 */
public class PageWithSubPages extends FormPage {
	private CTabFolder tabFolder;
	private Text text;
	
	class TextSection {
		String text;
		public TextSection(String text) {this.text = text;}
	}
	/**
	 * @param id
	 * @param title
	 */
	public PageWithSubPages(FormEditor editor) {
		super(editor, "composite", Messages.getString("PageWithSubPages.label")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		form.setText(Messages.getString("PageWithSubPages.title")); //$NON-NLS-1$
//		form.setBackgroundImage(FormArticlePlugin.getDefault().getImage(
//				FormArticlePlugin.IMG_FORM_BG));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		form.getBody().setLayout(layout);
		tabFolder = new CTabFolder(form.getBody(), SWT.FLAT|SWT.TOP);
		toolkit.adapt(tabFolder, true, true);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 0;
		tabFolder.setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		tabFolder.setSelectionBackground(new Color[] {selectedColor, toolkit.getColors().getBackground()}, new int[] {50});
		//tabFolder.setCursor(FormsResources.getHandCursor());

		toolkit.paintBordersFor(tabFolder);
		createTabs(toolkit);
		createText(toolkit, form.getBody());
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateSelection();
			}
		});
		tabFolder.setSelection(0);
		updateSelection();		
	}
	private void createTabs(FormToolkit toolkit) {
		createTab(toolkit, Messages.getString("PageWithSubPages.copyright.tab"), Messages.getString("PageWithSubPages.copyright.text")); //$NON-NLS-1$ //$NON-NLS-2$
		createTab(toolkit, Messages.getString("PageWithSubPages.license.tab"), Messages.getString("PageWithSubPages.license.text")); //$NON-NLS-1$ //$NON-NLS-2$
		createTab(toolkit, Messages.getString("PageWithSubPages.desc.tab"), Messages.getString("PageWithSubPages.desc.text")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	private void createText(FormToolkit toolkit, Composite parent) {
		Composite tabContent = toolkit.createComposite(parent);
		tabContent.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		tabContent.setLayout(layout);
		layout.numColumns = 2;
		layout.marginWidth = 0;
		GridData gd;
		text = toolkit.createText(tabContent, "", SWT.MULTI|SWT.WRAP); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_BOTH);
		gd.verticalSpan = 2;
		text.setLayoutData(gd);
		Button apply = toolkit.createButton(tabContent, Messages.getString("PageWithSubPages.apply"), SWT.PUSH); //$NON-NLS-1$
		apply.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_BEGINNING));
		Button reset = toolkit.createButton(tabContent, Messages.getString("PageWithSubPages.reset"), SWT.PUSH); //$NON-NLS-1$
		reset.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_BEGINNING));
	}
	private void updateSelection() {
		CTabItem item = tabFolder.getSelection();
		TextSection section = (TextSection)item.getData();
		text.setText(section.text);
	}
	private void createTab(FormToolkit toolkit, String title, String content) {
		CTabItem item = new CTabItem(tabFolder, SWT.NULL);
		TextSection section = new TextSection(content);
		item.setText(title);
		item.setData(section);
	}
}