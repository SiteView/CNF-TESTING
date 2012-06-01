package com.siteview.codegen;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoniotrCodeGenUtils
{
	public static String getEnumSourceCode(String packageName, String fileName,
			Map<String, Object> props)
	{
		Pattern pattern = Pattern.compile("(.+).java");
		Matcher mat = pattern.matcher(fileName);
		mat.find();
		String className = mat.group(1);

		MonitorCodeGenerator gen = new MonitorCodeGenerator();
		MonitiorGenArgInfo argInfo = new MonitiorGenArgInfo();
		argInfo.setClassName(className);
//		argInfo.setItems(itemDefSet);
		argInfo.setProps(props);
		argInfo.setPackageName(packageName);
		return gen.generate(argInfo);
	}

}
