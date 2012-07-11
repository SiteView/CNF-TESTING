package SiteView.ecc.dialog;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import COM.dragonflow.SiteViewException.SiteViewException;
import SiteView.ecc.data.MonitorServer;
import SiteView.ecc.views.EccTreeControl;
import Siteview.SiteviewSecurityException;

public class EditGroup extends Dialog {

	private String title="编辑组";
	/* 
	 * 定义控件
	 */
	private Button good;
	private Button error;
	private Button waring;
	private Button disable;
	private Button noData;
	private Button dependsOnUtil;
	private Combo  frequencyUtil;
	/*
	 * 数据接口
	 */
	private EccTreeControl eccTreeControl;
	private GroupTreeDialog groupTreeDialog;
	private MonitorServer monitorServer;
	private Text groupName;
	private Text description;
	private Text dependsCondition;
	private Text frequency;
	private Text dependsOn;
	
	public static Map<String,Object> dependsOnitem=null;
	/*
	 * 组数据
	 */
	
	public EditGroup(Shell Shell) {
		super(Shell);
		monitorServer=new MonitorServer();
		eccTreeControl=new EccTreeControl();
	}
	/*
	 * 初始化配置
	 */
	protected void configureShell(Shell newShell) {
		newShell.setSize(500,500);
		newShell.setLocation(250,150);
		newShell.setText(title);
		super.configureShell(newShell);
	}
	
	/*
	 * 创建面板元素
	 */
	protected Control createDialogArea(Composite parent) {
	//	GroupTreeDialog.descriptionitem=null;
		Composite composite=(Composite)super.createDialogArea(parent);
		composite.setLayout(null);
			Label labelName = new Label(composite, SWT.NONE);
			labelName .setBounds(10, 10, 80, 20);
			labelName .setText("组名称:");
			
			groupName= new Text(composite, SWT.BORDER);
			groupName.setBounds(90, 10, 400, 20);
			Label lableDescription = new Label(composite, SWT.NONE);
			lableDescription.setText("组描述:");
			lableDescription.setBounds(10, 70, 80, 40);
			
			description = new Text(composite, SWT.BORDER);
			description.setBounds(90, 70, 400, 40);
			
			Label lablDependsCondition = new Label(composite, SWT.NONE);
			lablDependsCondition .setText("依赖条件:");
			lablDependsCondition .setBounds(10, 120, 80, 20);
			
			dependsOn = new Text(composite, SWT.BORDER);
			dependsOn.setBounds(90, 120, 290, 20);
			dependsOnUtil=new Button(composite, SWT.NONE);
			dependsOnUtil.setText("...");
			dependsOnUtil.setBounds(390,120,40,20);
			dependsOnUtil.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					GroupTreeDialog gt=new GroupTreeDialog(null);
					gt.open();
					if(dependsOnitem!=null){
						dependsOn.setText(dependsOnitem.get("_name").toString());
					}
				}
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			
			Label labelDependsOn = new Label(composite, SWT.NONE);
			labelDependsOn.setText("组依赖:");
			labelDependsOn.setBounds(10, 160, 80, 20);
			good=new Button(composite, SWT.RADIO|SWT.LEFT);
			good.setText("正常");
			good.setBounds(90, 160, 50, 20);
			
			error=new Button(composite, SWT.RADIO|SWT.LEFT);
			error.setText("错误");
			error.setBounds(150, 160, 50, 20);
			
			waring=new Button(composite, SWT.RADIO|SWT.LEFT);
			waring.setText("警告");
			waring.setBounds(210, 160,50, 20);
			
			noData=new Button(composite, SWT.RADIO|SWT.LEFT);
			noData.setText("无数据");
			noData.setBounds(270, 160, 60, 20);
			
			disable=new Button(composite, SWT.RADIO|SWT.LEFT);
			disable.setText("禁止");
			disable.setBounds(340, 160, 50, 20);
			
			Label labelFrequency = new Label(composite, SWT.NONE);
			labelFrequency.setText("刷新频率:");
			labelFrequency.setBounds(10, 190, 80,20);
			
			frequency = new Text(composite, SWT.BORDER);
			frequency.setBounds(90, 190, 100, 20);
			
			frequencyUtil = new Combo(composite, SWT.READ_ONLY);
			frequencyUtil.add("分");
			frequencyUtil.add("秒");
			frequencyUtil.add("时");
			frequencyUtil.add("天");
			frequencyUtil.select(0);
			frequencyUtil.setBounds(200,190, 100, 20);
			if(EccTreeControl.s!=null){
				Map groupItem=(Map) EccTreeControl.item.getData();
				edit(groupItem);
			}
//		groupName.setText(m_api.get_SystemFunctions().get_CurrentLoginId());
		return composite;
	}
	
	public void edit(Map groupItem){
		groupName.setText(groupItem.get("_name").toString());
		description.setText(groupItem.get("_description").toString());
		dependsOn.setText(groupItem.get("_dependsOn").toString());
		if(groupItem.get("_dependsCondition").toString().equals("good")){
			good.setSelection(true);
		}else if(groupItem.get("_dependsCondition").toString().equals("error")){
			error.setSelection(true);
		}else if(groupItem.get("_dependsCondition").toString().equals("waring")){
			waring.setSelection(true);
		}else if(groupItem.get("_dependsCondition").toString().equals("disable")){
			disable.setSelection(true);
		}else{
			noData.setSelection(true);
		}	
		frequency.setText(groupItem.get("_frequency").toString());
		
	}
	
	protected void initializeBounds() {
		super.getButton(IDialogConstants.OK_ID).setText("保存");
		super.getButton(IDialogConstants.CANCEL_ID).setText("取消");
	}
	
	protected void buttonPressed(int buttonId) {
			try{
				Map groupItem=(Map) EccTreeControl.item.getData();
				if(EccTreeControl.s==null){
					groupItem.put("_parentid", groupItem.get("_id"));
					groupItem.remove("_id");
				}
//				groupItem.put("_name", groupName.getText()==null?groupName.getText():"");
//				groupItem.put("_description", description.getText()==null?groupName.getText():"");
//				groupItem.put("_dependsCondition", dependsCondition.getText()==null?groupName.getText():"");
//				groupItem.put("frequency", frequency.getText()==null?groupName.getText():"");
//				groupItem.put("_dependsOn", dependsOn.getText()==null?groupName.getText():"");
				
                monitorServer.savaGroup(groupItem);
               String  message="添加组成功";
                 MessageDialog.openInformation(getShell(), title, message);
			}
            catch (SiteviewSecurityException exception){
            	String message="添加不成功";
            	 MessageDialog.openInformation(getShell(), title, message);
            	 return;
            }
		super.buttonPressed(buttonId);
	}
}
