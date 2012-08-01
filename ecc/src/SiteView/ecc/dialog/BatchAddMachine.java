package SiteView.ecc.dialog;


import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteViewException.SiteViewException;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.SashForm;

public class BatchAddMachine extends Dialog {
	public String[] s;
	
	Button btnPing;
	Button btnCheckButton;
	Button btnMemory;
	public BatchAddMachine(Shell Shell) {
		super(Shell);
	}
	/*
	 * 初始化配置
	 */
	protected void configureShell(Shell newShell) {
		newShell.setSize(500,500);
		newShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		newShell.setLocation(250,150);
		newShell.setText("批量添加监测器");
		super.configureShell(newShell);
	}
	protected Control createDialogArea(Composite parent) {
		int i=7;
		Composite composite=(Composite)super.createDialogArea(parent);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		composite.setLayout(new FillLayout(SWT.VIRTUAL));
		
		SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
		sashForm.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		Label lblNewLabel = new Label(sashForm, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		lblNewLabel.setText("\u76D1\u6D4BPing\u6307\u5B9A\u670D\u52A1\u5668\u72B6\u51B5");
		
		Button btnCheckButton_1 = new Button(sashForm, SWT.CHECK);
		btnCheckButton_1.setSelection(true);
		btnCheckButton_1.setText("Ping");
		btnCheckButton_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		if(!s[0].contains("successful")){
			Label lblNewLabel_3 = new Label(sashForm, SWT.NONE);
			lblNewLabel_3.setText("\u76D1\u6D4B\u6307\u5B9A\u8FDB\u7A0B\u8FD0\u884C\u72B6\u51B5");
			lblNewLabel_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
			
			Label lblNewLabel_4 = new Label(sashForm, SWT.NONE);
			lblNewLabel_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
			lblNewLabel_4.setText("   load data fail");
			i+=2;
		}
		Label lblNewLabel_1 = new Label(sashForm, SWT.NONE);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		lblNewLabel_1.setText("\u76D1\u6D4BCPU\u7684\u4F7F\u7528\u7387");
		
		Button btnCheckButton_2 = new Button(sashForm, SWT.CHECK);
		btnCheckButton_2.setSelection(true);
		btnCheckButton_2.setText("CPU");
		btnCheckButton_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		Button btnCheckButton_4 = new Button(sashForm, SWT.CHECK);
		btnCheckButton_4.setSelection(true);
		btnCheckButton_4.setText("\u76D1\u6D4B\u5404\u78C1\u76D8\u7684\u5269\u4F59\u7A7A\u95F4\u5BB9\u91CF");
		btnCheckButton_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		if(!s[0].contains("successful")){
			Label lblNewLabel_5 = new Label(sashForm, SWT.NONE);
			lblNewLabel_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
			lblNewLabel_5.setText("   load data fail");
			i+=1;
		}else{
			for(int disksize=1;disksize<s.length;disksize++){
				 Button btnCheckButton_5 = new Button(sashForm, SWT.CHECK);
				 btnCheckButton_5.setSelection(true);
				 btnCheckButton_5.setText(s[disksize]);
				 btnCheckButton_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
				 i++;
			}
		}
		Label lblNewLabel_2 = new Label(sashForm, SWT.NONE);
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		lblNewLabel_2.setText("\u76D1\u6D4B\u7CFB\u7EDF\u5185\u5B58\u7684\u4F7F\u7528\u7387\u53CA\u5269\u4F59\u53EF\u7528\u5185\u5B58\u7684\u5927\u5C0F");
		
		Button btnCheckButton_3 = new Button(sashForm, SWT.CHECK);
		btnCheckButton_3.setSelection(true);
		btnCheckButton_3.setText("Memory");
		btnCheckButton_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		int [] n=new int[i];
		for(int j=0;j<i;j++){
			n[j]=1;
		}
		sashForm.setWeights(n);
		
		return composite;
	}
	
	protected void buttonPressed(int buttonId) {
		if(buttonId==IDialogConstants.OK_ID){
		}
		super.buttonPressed(buttonId);
	}
}
