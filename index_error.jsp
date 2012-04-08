<?xml version="1.0"?>
<%@ page contentType="text/xml; charset=GBK" %>
<%@ page isErrorPage="true" %>
<error msg="<%= exception.getMessage()%>">
<![CDATA[
<%
 java.io.CharArrayWriter cw = new java.io.CharArrayWriter();
 java.io.PrintWriter pw = new java.io.PrintWriter(cw,true);
 exception.printStackTrace(pw);
 out.println(cw.toString());
 %>
]]>
</error>
