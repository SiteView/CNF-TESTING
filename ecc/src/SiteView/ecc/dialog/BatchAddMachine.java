package SiteView.ecc.dialog;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
/**
 * 新建设备时批量添加监测器
 *  @author Administrator
 */
public class BatchAddMachine extends Dialog {
	public List<String[]> s;
	public List<String> monitors=new ArrayList<String>();
	public String group;
	public String hostname;
	
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
		composite.setLayout(new FillLayout(SWT.VIRTUAL));
		
		SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
		
		Label lblNewLabel = new Label(sashForm, SWT.NONE);
		lblNewLabel.setText("\u76D1\u6D4BPing\u6307\u5B9A\u670D\u52A1\u5668\u72B6\u51B5");
		
		Button btnCheckButton_1 = new Button(sashForm, SWT.CHECK);
		btnCheckButton_1.setSelection(true);
		btnCheckButton_1.setText("Ping");
		monitors.add("Ecc.ping");
		btnCheckButton_1.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(monitors.contains("Ecc.ping")){
					List<String> list=new ArrayList<String>();
					list.add("Ecc.ping");
					monitors.removeAll(list);
				}else{
				monitors.add("Ecc.ping");}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		if(!s.get(0)[0].contains("successful")){
			Label lblNewLabel_3 = new Label(sashForm, SWT.NONE);
			lblNewLabel_3.setText("\u76D1\u6D4B\u6307\u5B9A\u8FDB\u7A0B\u8FD0\u884C\u72B6\u51B5");
			
			Label lblNewLabel_4 = new Label(sashForm, SWT.NONE);
			lblNewLabel_4.setText("   load data fail");
			i+=2;
		}
		Label lblNewLabel_1 = new Label(sashForm, SWT.NONE);
		lblNewLabel_1.setText("\u76D1\u6D4BCPU\u7684\u4F7F\u7528\u7387");
		
		Button btnCheckButton_2 = new Button(sashForm, SWT.CHECK);
		btnCheckButton_2.setSelection(true);
		btnCheckButton_2.setText("CPU");
		monitors.add("Ecc.CPUUtilization");
		btnCheckButton_2.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(monitors.contains("Ecc.CPUUtilization")){
					List<String> list=new ArrayList<String>();
					list.add("Ecc.CPUUtilization");
					monitors.removeAll(list);
				}else{
				monitors.add("Ecc.CPUUtilization");}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		
		Button btnCheckButton_4 = new Button(sashForm, SWT.CHECK);
		btnCheckButton_4.setSelection(true);
		btnCheckButton_4.setText("\u76D1\u6D4B\u5404\u78C1\u76D8\u7684\u5269\u4F59\u7A7A\u95F4\u5BB9\u91CF");
		
		if(!s.get(0)[0].contains("successful")){
			Label lblNewLabel_5 = new Label(sashForm, SWT.NONE);
			lblNewLabel_5.setText("   load data fail");
			i+=1;
		}else{
			String [] ss=s.get(1);
			for(int disksize=0;disksize<ss.length;disksize++){
				Composite c=new Composite(sashForm, SWT.NONE);
				c.setLayout(new FillLayout());
				SashForm sash=new SashForm(c, SWT.NONE);
				Label c_0=new Label(sash, SWT.NONE);
				
				 final Button btnCheckButton_5 = new Button(sash, SWT.CHECK);
				 btnCheckButton_5.setSelection(true);
				 btnCheckButton_5.setText(ss[disksize]);
				 i++;
				 sash.setWeights(new int[]{2,40});
				 monitors.add("Ecc.DiskSpace--"+btnCheckButton_5.getText());
				 btnCheckButton_5.addSelectionListener(new SelectionListener() {
						public void widgetSelected(SelectionEvent e) {
							if(monitors.contains("Ecc.DiskSpace--"+btnCheckButton_5.getText())){
								List<String> list=new ArrayList<String>();
								list.add("Ecc.DiskSpace--"+btnCheckButton_5.getText());
								monitors.removeAll(list);
							}else{
								monitors.add("Ecc.DiskSpace--"+btnCheckButton_5.getText());}
						}
						public void widgetDefaultSelected(SelectionEvent e) {
						}
					});
			}
		}
		Label lblNewLabel_2 = new Label(sashForm, SWT.NONE);
		lblNewLabel_2.setText("\u76D1\u6D4B\u7CFB\u7EDF\u5185\u5B58\u7684\u4F7F\u7528\u7387\u53CA\u5269\u4F59\u53EF\u7528\u5185\u5B58\u7684\u5927\u5C0F");
		
		Button btnCheckButton_3 = new Button(sashForm, SWT.CHECK);
		btnCheckButton_3.setSelection(true);
		btnCheckButton_3.setText("Memory");
		monitors.add("Ecc.Memory");
		btnCheckButton_3.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(monitors.contains("Ecc.Memory")){
					List<String> list=new ArrayList<String>();
					list.add("Ecc.Memory");
					monitors.removeAll(list);
				}else{
					monitors.add("Ecc.Memory");}
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		if(s.get(0)[0].contains("successful")){
			Button btnCheckButton_6 = new Button(sashForm, SWT.CHECK);
			btnCheckButton_6.setText("监测指定 Service 运行状况");
			i++;
			
			ScrolledComposite group_1 = new ScrolledComposite(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			group_1.setLayout(new FillLayout());
			group_1.setExpandHorizontal(true);
			group_1.setExpandVertical(true);
			group_1.setMinWidth(100);
			group_1.setMinHeight(4000);
			Composite com=new Composite(group_1, SWT.NONE);
			com.setLayout(new FillLayout());
			group_1.setContent(com);// 
			SashForm sa=new SashForm(com, SWT.VERTICAL);
			sa.setLayout(new FillLayout());
			int sa_con=0;
			i++;
			String[] sss=s.get(2);
			for(int m=0;m<sss.length;m++){
				if(sss[m]!=null){
					Composite c=new Composite(sa, SWT.NONE);
					c.setLayout(new FillLayout());
					SashForm sash=new SashForm(c, SWT.NONE);
					Label c_0=new Label(sash, SWT.NONE);
					
					final Button btnCheckButton_7 = new Button(sash, SWT.CHECK);
					btnCheckButton_7.setText(sss[m]);
					sa_con++;
					sash.setWeights(new int[]{2,40});
					btnCheckButton_7.addSelectionListener(new SelectionListener() {
						public void widgetSelected(SelectionEvent e) {
							if(monitors.contains("Ecc.Server--"+btnCheckButton_7.getText())){
								List<String> list=new ArrayList<String>();
								list.add("Ecc.Server--"+btnCheckButton_7.getText());
								monitors.removeAll(list);
							}else{
							monitors.add("Ecc.Server--"+btnCheckButton_7.getText());}
						}
						public void widgetDefaultSelected(SelectionEvent e) {}
					});
				}
			}
			int [] sa_c=new int[sa_con];
			for(int j=0;j<sa_con;j++){
				sa_c[j]=1;
			}
			sa.setWeights(sa_c);
		}
		int [] n=new int[i];
		
		for(int j=0;j<i-1;j++){
			n[j]=1;
		}
		if(s.get(0)[0].contains("successful")){
			n[i-1]=8;
		}else{
			n[i-1]=1;
		}
		sashForm.setWeights(n);
		return composite;
	}
	
	protected void buttonPressed(int buttonId) {
		if(buttonId==IDialogConstants.OK_ID){
			
			for(int i=0;i<monitors.size();i++){
				String s=monitors.get(i);
				if(s.equals("Ecc.ping")){
					
				}
			}
		}
		super.buttonPressed(buttonId);
	}
}
