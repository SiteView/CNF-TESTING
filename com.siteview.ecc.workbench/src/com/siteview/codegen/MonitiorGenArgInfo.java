package com.siteview.codegen;

import java.util.Map;
import java.util.Set;

public class MonitiorGenArgInfo
{
	private Map<String, Object> props;
	private Set<String> items;
	private String className;
	private String packageName;
	
	public String getPackageName()
	{
		return packageName;
	}
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	public String getClassName()
	{
		return className;
	}
	public void setClassName(String className)
	{
		this.className = className;
	}
	public Set<String> getItems()
	{
		return items;
	}
	public void setItems(Set<String> items)
	{
		this.items = items;
	}
	public Map<String, Object> getProps() {
		return props;
	}
	public void setProps(Map<String, Object> props) {
		this.props = props;
	}	
}
