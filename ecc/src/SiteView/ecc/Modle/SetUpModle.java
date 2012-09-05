package SiteView.ecc.Modle;

import java.util.ArrayList;
import java.util.List;

import system.Windows.Forms.Layout.ArrangedElementCollection;

public class SetUpModle {
	public String name="…Ë÷√";
	public List list=new ArrayList();
	public SetUpModle(List<Object> list) {
		super();
		this.list = list;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Object> getList() {
		return list;
	}
	public void setList(List<Object> list) {
		this.list = list;
	}
	public SetUpModle(){
		list.add(new UserManageModle());
	}
}
