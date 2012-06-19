package SiteView.ecc.reportchart;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

import SiteView.ecc.tools.TimeSelectionControl;
import Siteview.Api.BusinessObject;
import siteview.windows.forms.LayoutViewBase;

public class StatusCTIReport extends LayoutViewBase {
	
	public StatusCTIReport(Composite parent) {
		super(parent);
	}

	protected void createView(Composite parent) {
		TimeSelectionControl.showControl(parent);
	    Button selete=new Button(parent, SWT.NONE);
	    selete.setBounds(410, 10, 50, 20);
	    selete.setText("≤È—Ø");
        selete.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		}); 
		
	}

	public void SetDataFromBusOb(BusinessObject bo) {
	}
}
