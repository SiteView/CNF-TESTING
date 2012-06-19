package SiteView.ecc.tools;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
/**
 * ����ѡ��ؼ�
 * @author Administrator
 *
 */
public class TimeSelectionControl {
	public static void showControl(Composite parent){
		Label start=new Label(parent,SWT.None);
		start.setText("��ʼʱ��:");
		start.setBounds(10,12,58,18);
		final DateTime startDate = new DateTime(parent, SWT.DATE);
		final DateTime startTime  =   new  DateTime(parent, SWT.TIME  |  SWT.SHORT);
        startDate.setBounds(68, 10, 80, 20);
        startTime.setBounds(148,10,50,20);
		Label end=new Label(parent,SWT.None);
		end.setText("����ʱ��:");
		end.setBounds(210,12,58,18);
		final DateTime endDate = new DateTime(parent, SWT.DATE|SWT.TIME); 
		final DateTime endTime=   new  DateTime(parent, SWT.TIME  |  SWT.SHORT);
        endDate.setBounds(268, 10, 80, 20);
        endTime.setBounds(348, 10, 50, 20);
	}
}
