package SiteView.ecc.Modle;

import java.util.ArrayList;
import java.util.List;

import Siteview.Api.BusinessObject;

public class GroupModle {
	private List<GroupModle> groups=new ArrayList<GroupModle>();;
	private List<MachineModle> machines=new ArrayList<MachineModle>();
	private BusinessObject bo;
	private boolean editGroup;//编辑组
	private boolean addSubGroup;//增加子组
	private boolean addMachine;//增加设备
	private boolean addMonitor;//增加监测器
	private boolean deleteGroup;//删除组
	private boolean deleteMchine;//删除设备
	private boolean deleteMonitor;//删除监测器
	
	
	public boolean isEditGroup() {
		return editGroup;
	}
	public void setEditGroup(boolean editGroup) {
		this.editGroup = editGroup;
	}
	public boolean isAddSubGroup() {
		return addSubGroup;
	}
	public void setAddSubGroup(boolean addSubGroup) {
		this.addSubGroup = addSubGroup;
	}
	public boolean isAddMachine() {
		return addMachine;
	}
	public void setAddMachine(boolean addMachine) {
		this.addMachine = addMachine;
	}
	public boolean isAddMonitor() {
		return addMonitor;
	}
	public void setAddMonitor(boolean addMonitor) {
		this.addMonitor = addMonitor;
	}
	public boolean isDeleteGroup() {
		return deleteGroup;
	}
	public void setDeleteGroup(boolean deleteGroup) {
		this.deleteGroup = deleteGroup;
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
	public GroupModle(BusinessObject bo) {
		super();
		this.bo = bo;
	}
	public GroupModle(List<GroupModle> groups, List<MachineModle> machines,
			BusinessObject bo) {
		super();
		this.groups = groups;
		this.machines = machines;
		this.bo = bo;
	}
	public List<GroupModle> getGroups() {
		return groups;
	}
	public GroupModle(BusinessObject bo, boolean editGroup,
			boolean addSubGroup, boolean addMachine, boolean addMonitor,
			boolean deleteGroup, boolean deleteMchine, boolean deleteMonitor) {
		super();
		this.bo = bo;
		this.editGroup = editGroup;
		this.addSubGroup = addSubGroup;
		this.addMachine = addMachine;
		this.addMonitor = addMonitor;
		this.deleteGroup = deleteGroup;
		this.deleteMchine = deleteMchine;
		this.deleteMonitor = deleteMonitor;
	}
	public void setGroups(List<GroupModle> groups) {
		this.groups = groups;
	}
	public List<MachineModle> getMachines() {
		return machines;
	}
	public void setMachines(List<MachineModle> machines) {
		this.machines = machines;
	}
	public BusinessObject getBo() {
		return bo;
	}
	public void setBo(BusinessObject bo) {
		this.bo = bo;
	}
}
