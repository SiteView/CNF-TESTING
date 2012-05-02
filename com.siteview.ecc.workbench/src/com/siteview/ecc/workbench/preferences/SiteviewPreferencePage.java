package com.siteview.ecc.workbench.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
//import com.swtdesigner.ResourceManager;
import com.siteview.ecc.rcp.cnf.internal.CNFActivator;

public class SiteviewPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Create the preference page
	 */
	public SiteviewPreferencePage() {
		super();
	}

	/**
	 * Create contents of the preference page
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout());

		final Label imageLabel = new Label(container, SWT.NONE);
//		imageLabel.setImage(ResourceManager.getPluginImage(CNFActivator.getDefault(), "images/logo.JPG"));
//		imageLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label todoPluginWrittenLabel = new Label(container, SWT.NONE);
		todoPluginWrittenLabel.setText("SiteviewPreference Plugin");

		final Label contactMeaLink = new Label(container, SWT.NONE);
		contactMeaLink.setText("Contact : http://www.siteview.com");
		//
		return container;
	}

	/**
	 * Initialize the preference page
	 */
	public void init(IWorkbench workbench) {
		// Initialize the preference page
	}

}
