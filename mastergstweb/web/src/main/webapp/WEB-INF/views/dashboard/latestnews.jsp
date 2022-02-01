<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Latest News</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script type="text/javascript" src="${contextPath}/static/mastergst/js/echarts/echarts.js"></script>
<script type="text/javascript" src="${contextPath}/static/mastergst/js/echarts/echartsTheme.js"></script>
<script type="text/javascript" src="${contextPath}/static/mastergst/js/client/dashboard.js"></script>

<script type="text/javascript">

</script>

</head>
<body>
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>

<div class="breadcrumbwrap dash_bread">
<div class="container">
	<div class="row">
        <div class="col-md-12 col-sm-12">
            
				<ol class="breadcrumb">
					<li class="breadcrumb-item active" style="font-size: 14px;font-weight: 700;">Latest News</li>
				</ol>
					
					<div class="retresp"></div>
			</div>
		</div>
	</div>
</div>

		 <div class="db-ca-wrap">
		
			 <div class="container">
			 <c:if test='${not empty allnews}'>
			 <c:forEach items="${allnews}" var="allnews">
			 <div class="mb-2 p-2" style="background-color: white;">
			 <div class=""><h4 class="m-0"><strong>${allnews.title}</strong></h4></div><div class="mb-2 mr-3" style="font-size: 13px;color: slategray;">${allnews.createdDate}</div><div class="mr-3 p-2">${allnews.description}</div>
			 </div>
			 </c:forEach>
			 </c:if>
			 <c:if test='${empty allnews}'>
			 <div style="height: 450px;background-color: white;text-align: center;"> <h3 style="padding-top: 15%;">Latest News Not Available</h3></div>
			 </c:if>
			 </div>
		</div>

<!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script type="text/javascript">
$(function() {
	$('#nav-dashboard').addClass('active');
	$('#news-btn').addClass('active');
	
});

</script>
</body></html>