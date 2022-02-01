<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Error Log</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet"
	href="${contextPath}/static/mastergst/css/dashboard/dashboards.css"
	media="all" />
</head>
<body>
	<%@include file="/WEB-INF/views/includes/admin_header.jsp"%>

<div class="bodywrap">
<div class="container">
  <div class="db-inner">
	<h4 class="hdrtitle" style="display: inline-block;color: #374583;font-size: 20px;text-align:center;margin-top:15px">Error Log</h4> 
	
  	<div id="logContent">${logContent}</div>
    </div>
</div>
</div>
<!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
</body>
</html>