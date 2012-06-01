package com.siteview.codegen;

import java.util.*;

public class MonitorCodeGenerator
{
  protected static String nl;
  public static synchronized MonitorCodeGenerator create(String lineSeparator)
  {
    nl = lineSeparator;
    MonitorCodeGenerator result = new MonitorCodeGenerator();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "package ";
  protected final String TEXT_3 = ";" + NL + "" + NL + "public class ";
  protected final String TEXT_4 = NL + "{" + NL + "\t";
  protected final String TEXT_5 = NL + "\tpublic \tString ";
  protected final String TEXT_6 = " = \"";
  protected final String TEXT_7 = "\";" + NL + "\t";
  protected final String TEXT_8 = NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
  MonitiorGenArgInfo argInfo = (MonitiorGenArgInfo)argument;
  Set<String> enumItems = argInfo.getItems();
  Map<String, Object> props = argInfo.getProps();
  String className = argInfo.getClassName();
  String packageName = argInfo.getPackageName();

    stringBuffer.append(TEXT_2);
    stringBuffer.append(packageName);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_4);
    for(String key:props.keySet()){
    stringBuffer.append(TEXT_5);
    stringBuffer.append(key);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(props.get(key).toString());
    stringBuffer.append(TEXT_7);
    }
    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}
