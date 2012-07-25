package SiteView.ecc.tab.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

import SiteView.ecc.tools.FileTools;
import Siteview.Api.BusinessObject;

import COM.dragonflow.Page.viewPage;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;

import siteview.windows.forms.LayoutViewBase;
import system.Collections.ICollection;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Table;

public class RelativelyMonitor extends LayoutViewBase{
	public static BusinessObject bo;
	private Table table;
	private Table table_1;
	public RelativelyMonitor(Composite parent) {
		super(parent);
	}

	@Override
	public void createView(Composite parent) {
		if (parent.getChildren().length > 0) {
			for (Control control : parent.getChildren()) {
				control.dispose();
			}
		}
		parent.setLayout(new FillLayout());
		
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		Label lblNewLabel_1 = new Label(sashForm, SWT.NONE);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_1.setFont(SWTResourceManager.getFont("ËÎÌו", 10, SWT.NORMAL));
		lblNewLabel_1.setText("\u8BBE\u5907");
		
		Label lblNewLabel = new Label(sashForm, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		ICollection iCollection=FileTools.getBussCollection("", "", "RemoteMachine");
		lblNewLabel.setText("New Label");
		
		Label lblNewLabel_2 = new Label(sashForm, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("ËÎÌו", 10, SWT.NORMAL));
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_2.setText("\u76F8\u540CIP\u76D1\u6D4B\u5668");
		
		table = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		Label lblNewLabel_3 = new Label(sashForm, SWT.NONE);
		lblNewLabel_3.setFont(SWTResourceManager.getFont("ËÎÌו", 10, SWT.NORMAL));
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_3.setText("\u6709\u4F9D\u8D56\u5173\u7CFB\u76D1\u6D4B\u5668");
		
		table_1 = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);
		sashForm.setWeights(new int[] {10, 20, 10, 50, 10, 50});
	}
	@Override
	public void SetDataFromBusOb(BusinessObject bo) {
		// TODO Auto-generated method stub
	}
	
}
