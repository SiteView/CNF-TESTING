package SiteView.ecc.Modle;

import java.util.List;


public class SiteViewEcc {
	private String name="SiteViewEcc9.2";
	private boolean addGroup;
	public SiteViewEcc(){}
	public SiteViewEcc(boolean addGroup, List<GroupModle> list) {
		super();
		this.addGroup = addGroup;
		this.list = list;
	}
	public boolean isAddGroup() {
		return addGroup;
	}
	public void setAddGroup(boolean addGroup) {
		this.addGroup = addGroup;
	}
	private List list;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	//构造方法
	public SiteViewEcc(List list) {
		super();
		this.list = list;
	}
	
}
