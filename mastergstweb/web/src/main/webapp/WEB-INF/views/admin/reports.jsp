<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Reports</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<style>
	.table td {
		padding: .5rem;
		padding-left: 2rem;
		vertical-align: middle!important;
		border-top: none!important;
		border-bottom: 1px solid #eee;
		padding-right:0px!important;
	}
	.rtable td {
		border:none!important;
	}
	a.btn.btn-blue-dark.report.mr-0.pull-right{padding: 5px 24px !important;}
	p {font-size:15px}
</style>
<script type="text/javascript">
$(document).ready(function(){
	$('#reports_lnk').addClass('active');
});

</script>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/admin_header.jsp" %>
    <div class="bodybreadcrumb">
	<!--- company info bodybreadcrumb start -->
    	<div class="container">
			<div class="row">
				<div class="col-sm-12">
					<div class="bdcrumb-tabs" style="margin-top: 11px;">Reports</div>
				</div>
			</div>
		</div>
        <!--- company info bodybreadcrumb end -->
<div class="container">
<div class="row">
        
	</div>
	</div>
  </div>
  <!--- breadcrumb end -->
  <div class="db-ca-wrap" >
    <div class="container" style="background: white;">
	<div class=" ">&nbsp;</div>
	<div class="card-deck">
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Subscriptions</h5>
      <p class="card-text permissionPAID_EXPIRED_USERS">Paid Expired Users (Sales Status)
      	<a href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=paidexpiredyusers" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a>
      </p>
      <p class="card-text permissionALL_EXPIRED_USERS">All Expired Users
      	<a href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=expiredyusers" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a>
      </p>
	  <p class="card-text permissionRENEWAL">Renewal in Next 45 Days. 
	  	<a id="monthlyapiusage_lnk" href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=beforeexpiryusers" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a>
	  </p>
	  <p class="card-text permissionPARTNER_PAYMENTS">Partner Payments. 
	  	<a id="monthlyapiusage_lnk" href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=partnerpayments" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a>
	  </p>
	  <p class="card-text permissionINVOICES_LIMIT_EXCEEDS_USERS mb-4">Invoices Limit Exceeds Users. 
	  	<a id="monthlyapiusage_lnk" href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=getapisexceeds" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a>
	  </p>
    </div>
  </div>
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Invoices Usage Dashboard</h5>
      <p class="card-text permissionMONTHLY_INV_USAGE">Monthly Invoices Usage. 
      	<a id="monthlyapiusage_lnk" href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=monthlyapiusage" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a>
      </p>
       <p class="card-text permissionCUSTOM_MONTHLY_API">Customer wise Monthly API Usage Reports. 
      	<a id="monthlyapiusage_lnk" href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=monthlywiseusagereport" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a>
      </p>
       <p class="card-text permissionAPI_VERSION_DOCUMENT">API Version Document
      	<a id="monthlyapiusage_lnk" href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=apiversion" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a>
      </p>
        <p class="card-text">Reconciliation Status
      	<a id="monthlyapiusage_lnk" href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=reconcilestatus" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a>
      </p>
    </div>
  </div>
</div>
<div class=" ">&nbsp;</div>
	<div class="card-deck">
  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Payments</h5>
	  <p class="card-text permissionACTIVE_USERS">Active Users.<a href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=activeUsers" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionDEMO_USERS">Demo Users.<a href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=demoUsers" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
      <p class="card-text permissionSUBSCRIPTION_SUMMARY">Subscription Summary Report.<a href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=subscriptionsummaryreport" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
  	  <p class="card-text permissionUSER_SUMMARY">User Summary Report.<a href="${contextPath}/adminreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&type=userscountsummaryreport" class="btn btn-blue-dark report mr-0 mb-3 pull-right" role="button">View</a></p>
  </div>
  </div>
 <!--  <div class="card col-md-6 col-sm-12 col-lg-6">
    <div class="card-body">
      <h5 class="card-title mt-4 text-center">Summary Report</h5>
      <p class="card-text permissionMONTHLY_TAX_ITCSUM">Monthly Tax and ITC summary Report.<a href="#" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text permissionGST_FILING_STATUS">GST Filing Status Report.<a href="#" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  <p class="card-text mb-4 permissionGST_FILING_STATUS_SUM">GST Filing summary Report.<a href="#" class="btn btn-blue-dark report mr-0 pull-right" role="button">View</a></p>
	  
    </div>
  </div> -->
</div>
<div class=" ">&nbsp;</div></div>
    </div>
	<!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->