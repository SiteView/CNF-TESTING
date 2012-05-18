package com.siteview.ecc.workbench.editors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

//import com.plugindev.addressbook.Activator;
//import com.plugindev.addressbook.util.ImageCache;
//import com.plugindev.addressbook.util.ImageKeys;
//import com.plugindev.addressbook.util.Messages;

public class SourcePage extends FormPage {
	private String peopleName;
	public Text text = null;
	private Text[] texts;
	private Map<Text, String> textMap;
	public SourcePage(FormEditor editor){
		super(editor, "source",Messages.PAGE_NAME_SOURCE);
		this.peopleName = editor.getEditorInput().getName();
		this.textMap = new HashMap<Text, String>();
		
	}
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		form.setText(Messages.PAGE_NAME_SOURCE);
//		form.setBackgroundImage(ImageCache.getInstance().getImage(ImageKeys.getImageDescriptor(ImageKeys.IMG_FORM_BG)));
		TableWrapLayout layout = new TableWrapLayout();
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		form.getBody().setLayout(layout);
		createFormTextSection(form, toolkit);
		loadSource1();
	}
	
	private void createFormTextSection(final ScrolledForm form, FormToolkit toolkit) {
		Section section =
			toolkit.createSection(
				form.getBody(),
				Section.TWISTIE | Section.DESCRIPTION);
		section.setActiveToggleColor(
			toolkit.getHyperlinkGroup().getActiveForeground());
		section.setToggleColor(
			toolkit.getColors().getColor(FormColors.SEPARATOR));
		toolkit.createCompositeSeparator(section);

		text = toolkit.createText(section, "源代码", SWT.WRAP|SWT.FLAT|SWT.READ_ONLY);

		section.setClient(text);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(false);
			}
		});

		section.setText("显示源代码");
		section.setDescription("显示源代码文件中的内容");
		TableWrapData td = new TableWrapData();
		td.align = TableWrapData.FILL;
		td.grabHorizontal = true;
		section.setLayoutData(td);
		td = new TableWrapData();
		td.align = TableWrapData.CENTER;
		text.setLayoutData(td);
		text.setFont(new Font(text.getDisplay(), new FontData("Tahoma", 9, SWT.NORMAL)));
		text.getBorderWidth();
		
		loadSource1();
		
		Section section2 =
				toolkit.createSection(
					form.getBody(),
					Section.TWISTIE | Section.DESCRIPTION);
		section2.setActiveToggleColor(
				toolkit.getHyperlinkGroup().getActiveForeground());
		section2.setToggleColor(
		toolkit.getColors().getColor(FormColors.SEPARATOR));
		toolkit.createCompositeSeparator(section2);

		section2.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(false);
			}
		});

		section2.setText("显示监测器");
		section2.setDescription("显示监测器编辑界面");
			
		Composite client = toolkit.createComposite(section2);
		GridLayout glayout = new GridLayout();
		glayout.marginWidth = glayout.marginHeight = 0;
		glayout.numColumns = 2;
		client.setLayout(glayout);	
		
		FormEditorInput input = (FormEditorInput)this.getEditor().getEditorInput();
		for(Object group : input.getGroups())
		{
			createTextItem(toolkit,client, (Map)group);
		}
		
		for(Object monitor : input.getMonitors())
		{
			createTextItem(toolkit,client, (Map)monitor);
		}
		
		section2.setClient(client);
	}
	/*
	 * 在13.6小节加入，从文件中读取源代码
	 */
//	public void loadSource() {
//		if(text == null)
//			return;
//		File file = Activator.getDefault().getStateLocation().
//		append(peopleName +".addr").toFile();
//		if(file.exists() == false){
//			text.setText("");
//			return;
//		}
//		StringBuffer buffer = new StringBuffer();
//		try {
//			BufferedReader reader;
//			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//			String temp = reader.readLine();
//
//			while(temp != null)
//			{
//				if(temp.contains("<list") || temp.contains("</list>"))
//					buffer.append("\t");
//				else if(temp.contains("<textItem"))
//					buffer.append("\t\t");
//				else if(temp.contains("<choiceItem")|| temp.contains("</choiceItem>"))
//					buffer.append("\t\t");
//				else if(temp.contains("<choiceString"))
//					buffer.append("\t\t\t");
//				buffer.append(temp);
//				buffer.append("\n");
//				temp = reader.readLine();
//			}
//		} catch (FileNotFoundException e) {
//			// TODO 自动生成 catch 块
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO 自动生成 catch 块
//			e.printStackTrace();
//		}finally{
//			text.setText(buffer.toString());
//		}
//	}
	
	public void loadSource1() 
	{		
		FormEditorInput input = (FormEditorInput)this.getEditor().getEditorInput();
		String dd = input.getGroups().toArray().toString() + input.getMonitors().toArray().toString();
//		dd += "\\r\\n";
		for(Object group : input.getGroups())
		{
			dd += ((Map)group).toString();
//			dd += "\\r\\n";
		}
		for(Object monitor : input.getMonitors())
		{
			dd += ((Map)monitor).toString();
//			dd += "\\r\\n";
		}
		text.setText(dd);		
//		IContainer container = file.getParent(); +

//		IFile monitorFile = null;
	}
	
	private void createTextItem(FormToolkit toolkit,Composite parent, Map tempMap)
	{
		int i = 0;
		texts = new Text[tempMap.size()];
		Iterator it = tempMap.entrySet().iterator(); 
		while (it.hasNext())
		{ 
			Map.Entry entry = (Map.Entry) it.next(); 
			Object key = entry.getKey();    
			Object value = entry.getValue(); 
			toolkit.createLabel(parent, key.toString());
			texts[i] = toolkit.createText(parent, value.toString());
			
			GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
			gd.widthHint = 10;
			texts[i].setLayoutData(gd);
			textMap.put(texts[i], key.toString());
			texts[i].addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e){
					//在13.7节加入
//					if(updated == true)
//						return;
					//结束在13.7节的加入
					Text text = (Text)e.getSource();
//					if(input != null)
//						input.setStringValue(textMap.get(text), text.getText());
				}
			});
//			createSpacer(toolkit, parent, 2);
			
			i++;
		} 		
	}
}
