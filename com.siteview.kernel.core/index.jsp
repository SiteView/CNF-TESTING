<?xml version="1.0"?>
<%@ page contentType="text/xml; charset=GBK" %>
<%@ page errorPage="index_error.jsp" %>
<jsp:useBean id="indexBeanId" scope="page" class="SiteViewMain.IndexBean" />
<jsp:setProperty name="indexBeanId" property="*" />
<%
indexBeanId.setRequest(request);
indexBeanId.setResponse(response);
indexBeanId.go();
%>
