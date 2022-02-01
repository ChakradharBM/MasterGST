<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions"%>
<%@page import="java.util.UUID"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ page import="com.mastergst.core.common.MasterGSTConstants" %>
<%
Date todaysDate = new Date();
DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
String strDate= df2.format(todaysDate);
%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
