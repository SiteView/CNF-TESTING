package SiteView.ecc.Modle;

import Siteview.Api.BusinessObject;

public class MachineModle {
	private BusinessObject bo;
	private boolean editMachine;//±à¼­Éè±¸
	private boolean addMonitor;//Ôö¼Ó¼à²âÆ÷
	private boolean deleteMchine;//É¾³ýÉè±¸
	private boolean deleteMonitor;//É¾³ý¼à²âÆ÷
	
	public MachineModle(BusinessObject bo){
		this.bo=bo;
	}
	public BusinessObject getBo() {
		return bo;
	}
	public void setBo(BusinessObject bo) {
		this.bo = bo;
	}
	public boolean isEditMachine() {
		return editMachine;
	}
	public void setEditMachine(boolean editMachine) {
		this.editMachine = editMachine;
	}
	public boolean isAddMonitor() {
		return addMonitor;
	}
	public void setAddMonitor(boolean addMonitor) {
		this.addMonitor = addMonitor;
	}
	public boolean isDeleteMchine() {
		return deleteMchine;
	}
	public void setDeleteMchine(boolean deleteMchine) {
		this.deleteMchine = deleteMchine;
	}
	public boolean isDeleteMonitor() {
		return deleteMonitor;
	}
	public void setDeleteMonitor(boolean deleteMonitor) {
		this.deleteMonitor = deleteMonitor;
	}
	public MachineModle(BusinessObject bo, boolean editMachine,
			boolean addMonitor, boolean deleteMchine, boolean deleteMonitor) {
		super();
		this.bo = bo;
		this.editMachine = editMachine;
		this.addMonitor = addMonitor;
		this.deleteMchine = deleteMchine;
		this.deleteMonitor = deleteMonitor;
	}
	
}
