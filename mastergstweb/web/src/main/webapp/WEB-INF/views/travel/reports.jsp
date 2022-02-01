<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Reports</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<style>
	.table td {padding: .5rem;	padding-left: 2rem;	vertical-align: middle!important; border-top: none!important; border-bottom: 1px solid #eee;padding-right:0px!important;}
	.rtable td {border:none!important;}
	a.btn.btn-blue-dark.report.mr-0.pull-right{padding: 5px 24px !important;}
	p {font-size:15px}
</style>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp" %> 
<div class="breadcrumbwrap">
<div class="container">
<div class="row">
        <div class="col-md-12 col-sm-12">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
	<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
      <li class="breadcrumb-item active">Travel</li>
    </ol>
	<div class="retresp"></div>
	</div>
	</div>
	</div>
  </div>
  <!--- breadcrumb end -->
  <div class="db-ca-wrap" >
    <div class="container" style="background: white;">
    <div class="returns_dropdown">
	<div class=" ">&nbsp;</div>
	<div class="card-deck">
  <div class="card col-md-12 col-sm-12 col-lg-12">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Travel</h5>
      <p class="card-text">Average Balance Report. <a href="${contextPath}/travelreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=avgbalance" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text">Bus Detail Report. <a href="${contextPath}/travelreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=busdetail" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text">Variable Expense Report. <a href="${contextPath}/travelreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=variableexpence" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  
	  <p class="card-text">Vehicle Wise Mileage Report. <a href="${contextPath}/travelreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=vehicalwisemilage" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text">GST Payment Report. <a href="${contextPath}/travelreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=gstpayment" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text mb-4">P&L Report. <a href="${contextPath}/travelreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=pandl" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
    </div>
  </div>
</div>
<div class=" ">&nbsp;</div>
</div>

	</div>
    </div>
	<!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->