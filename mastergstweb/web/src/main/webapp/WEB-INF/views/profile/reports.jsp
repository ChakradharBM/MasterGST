<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Reports</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<style>
.table td {padding: .5rem;padding-left: 2rem;vertical-align: middle!important;border-top: none!important;border-bottom: 1px solid #eee;padding-right:0px!important;}
.rtable td {border:none!important;}
	a.btn.btn-blue-dark.report.mr-0.pull-right{padding: 5px 24px !important;}
	p {font-size:15px}
</style>
</head>
<body class="body-cls">
<c:choose>
	<c:when test='${not empty client && not empty client.id}'>
		<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
	</c:when>
	<c:otherwise>
		<%@include file="/WEB-INF/views/includes/newclintheader.jsp" %>
	</c:otherwise>
	</c:choose>
<div class="breadcrumbwrap"><div class="container"><div class="row">
        <div class="col-md-12 col-sm-12">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><c:choose><c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:when><c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:otherwise></c:choose></li>
      <li class="breadcrumb-item active">Global Reports</li>
    </ol>
	<div class="retresp"></div>
	</div></div></div> </div>
  <div class="db-ca-wrap" >
    <div class="container" style="background: white;">
	<div class=" ">&nbsp;</div>
	<div class="card-deck">
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">GST Filing Status Reports</h5>
      <p class="card-text">All Clients Filing Status Report. <a href="${contextPath}/cp_ClientsReportsData/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark report mr-0 mb-3 pull-right" role="button">View</a></p>
    </div>
  </div>
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
	<c:choose>
		<c:when test="${usertype eq userCenter}">
			<h5 class="card-title mt-4 text-center">Suvidha Kendra's Reports</h5>
			<p class="card-text">All Suvidha Kendra Usage Report. <a href="${contextPath}/cp_allcenters/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark report mr-0 mb-3 pull-right" role="button">View</a></p>
		</c:when>
		<c:otherwise>
			<h5 class="card-title mt-4 text-center">Accounting Reports</h5>
      		<p class="card-text">Trial Balance Report <a href="#" class="btn btn-blue-dark report mr-0 pull-right disabled" role="button">View</a></p>
     		<p class="card-text">Balance Sheet Report <a href="#" class="btn btn-blue-dark report mr-0 pull-right disabled" role="button">View</a></p>
	  		<p class="card-text">P & L Report <a href="#" class="btn btn-blue-dark report mr-0 pull-right disabled" role="button">View</a></p>
    		<p class="card-text">TDS Report <a href="#" class="btn btn-blue-dark report mr-0 pull-right disabled" role="button">View</a></p>
    		<p class="card-text mb-4">Aging Report <a href="#" class="btn btn-blue-dark report mr-0 pull-right disabled" role="button">View</a></p>
		</c:otherwise>
	</c:choose>
    </div>
  </div>
</div>
<div class=" ">&nbsp;</div>
	<div class="card-deck">
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">GST Group Wise Reports</h5>
       <p class="card-text">GST Group Wise Sales Reports  <a href="${contextPath}/cp_ClientsReportsGroupData/Sales/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
       <p class="card-text">GST Group Wise Purchases Reports  <a href="${contextPath}/cp_ClientsReportsGroupData/GSTR2/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
       <p class="card-text">GST Group Wise GSTR1 Reports  <a href="${contextPath}/cp_ClientsReportsGroupData/GSTR1/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
       <p class="card-text">GST Group Wise GSTR2A Reports  <a href="${contextPath}/cp_ClientsReportsGroupData/GSTR2A/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
       <p class="card-text">GST Group Wise GSTR2B Reports  <a href="${contextPath}/cp_ClientsReportsGroupData/GSTR2B/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text">GST Group Wise Payments Received  <a href="${contextPath}/PaymentReportsGroupData/GSTR1/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
      <p class="card-text">GST Group Wise Payments Made <a href="${contextPath}/PaymentReportsGroupData/GSTR2/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
   	<p class="card-text">GST Group Wise E-invoice Reports  <a href="${contextPath}/cp_ClientsEinvoiceReportsGroupData/GSTR1/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
		<p class="card-text">GST Group Wise GSTR3B Reports  <a href="${contextPath}/cp_ClientsReportsGroupData/GSTR3B/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark report mr-0 mb-3 pull-right" role="button">View</a></p>
    </div>
  </div>
  <div class="card col-md-6 col-sm-12 col-lg-6">
     <div class="card-body">
     <c:choose>
		<c:when test="${usertype eq userCenter}">
			<h5 class="card-title mt-4 text-center">Accounting Reports</h5>
      		<p class="card-text">Trial Balance Report <a href="#" class="btn btn-blue-dark report mr-0 pull-right disabled" role="button">View</a></p>
     		<p class="card-text">Balance Sheet Report <a href="#" class="btn btn-blue-dark report mr-0 pull-right disabled" role="button">View</a></p>
	  		<p class="card-text">P & L Report <a href="#" class="btn btn-blue-dark report mr-0 pull-right disabled" role="button">View</a></p>
    		<p class="card-text">TDS Report <a href="#" class="btn btn-blue-dark report mr-0 pull-right disabled" role="button">View</a></p>
    		<p class="card-text mb-4">Aging Report <a href="#" class="btn btn-blue-dark report mr-0 pull-right disabled" role="button">View</a></p>
		</c:when>
		<c:otherwise>
			<p class="card-text mt-4 mb-4">MasterGST System is upgrading with more intelligence day by day, watch for more reports in this area and mail us at <a href="mailto:Sales@mastergst.com">Sales@mastergst.com</a> if you need any specific reports we will enable it for you.</p>
		</c:otherwise>
	</c:choose>
	
    </div>
  </div>
</div>
<div class=" ">&nbsp;</div>
	</div>
    </div>
	<!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
	<script type="text/javascript">
	$(document).ready(function(){
		$('.nonAspReports').addClass('active');
	});
	</script>