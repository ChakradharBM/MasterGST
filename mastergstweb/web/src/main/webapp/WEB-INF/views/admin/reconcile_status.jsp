<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | API Version Document</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard/dashboard.css" media="all" />
<link rel="stylesheet"	href="${contextPath}/static/mastergst/css/reports/reports.css" 	media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/admin/reconcileStatus_report.js" type="text/javascript"></script>	

<!-- datepicker end -->
<style type="text/css">
button.btn.btn-xs{padding: 3px 13px;background-color: #314999;border:none;}
.bodybreadcrumb{margin-top:-8px!important}
tr td:last-child {display: inline-flex;margin: 0!important;width: 89%;}
td input{border: 1px solid lightgray!important;border-radius: 6px!important;}
.gstr-info-tabs .nav-tabs { margin-bottom:10px;margin-left: -10px; }
.gstr-info-tabs .nav-tabs .nav-item.show .nav-link, .gstr-info-tabs .nav-tabs .nav-link.active, .gstr-info-tabs .nav-tabs .nav-link:hover, .gstr-info-tabs .nav-tabs .nav-link.active:hover { background:transparent; border-bottom:3px solid #8ee3fe; border-left:0; border-right:0; border-top:0; color:#374583; font-weight:bold;font-size:13px }
.gstr-info-tabs .nav-tabs .nav-link { border-bottom:3px solid transparent; border-left:0; border-right:0; border-top:0; color:#707172; font-size:13px; text-transform:uppercase }
.gstr-info-tabs .nav-tabs .nav-link:hover { border-bottom:3px solid #8ee3fe; border-left:0; border-right:0; border-top:0; font-size:13px}
.gstr-info-tabs .nav-tabs { border:0 }
.gstr-info-tabs .nav-tabs .nav-item { position:relative; }
.gstr-info-tabs .nav-tabs .nav-link.active::after, .gstr-info-tabs .nav-tabs .nav-link:hover::after {
content:'';
border-left: 10px solid transparent;
border-right: 10px solid transparent;
border-top: 10px solid #8ee3fe;
position:absolute;
left:40%;
bottom:-10px;
}
.gstr-info-tabs .nav-tabs .nav-link.active::before, .gstr-info-tabs .nav-tabs .nav-link:hover::before {
content: '';
border-left: 10px solid transparent;
border-right: 10px solid transparent;
border-top: 10px solid #f6f9fb;
position: absolute;
left: 40%;
bottom: -6px;
z-index: 1;
}
</style>
</head>
<jsp:include page="/WEB-INF/views/admin/userDetails.jsp" />
<body>
<%@include file="/WEB-INF/views/includes/admin_header.jsp"%>
<div class="bodywrap" style="min-height: 480px; padding-top: 10px">
	<!--- company info bodybreadcrumb start -->
	<div class="bodybreadcrumb">
		<div class="container" style="padding-top: 6px;">Reconcile Status Information</div>
	</div>
	<!--- company info bodybreadcrumb end -->
	<div class="tab-content">
   		 <div id="gstapiv" class="tab-pane fade in active show">
   		 	<div class="container" style="padding-top: 30px;">
   		 		<div class="row">
					<div class="col-sm-12">&nbsp;</div>
					<div class="col-sm-12" style="margin-top: 10px;">
						<a id="backpage_lnk" href="${contextPath}/userreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>" class="btn btn-blue-dark report mr-0 pull-right" role="button">BACK</a>
					</div>
					<div class="col-md-12 gstr-info-tabs pr-0">
   		 				<div class="customtable tabtable">
							<div class="customtable p-0 mt-3">
								<table id="reconcileStatusDbTable" class="display dataTable meterialform p-0" style="width:100%!important;">
									<thead>
										<tr>
											<th class="text-center">User Name</th>
											<th class="text-center">Email</th>
											<th class="text-center">User Type</th>
											<th class="text-center">Reconcile Created Date</th>
											<th class="text-center">2A Total Invoices</th>
											<th class="text-center">2A Processed Invoices</th>
											<th class="text-center">PR Total Invoices</th>
											<th class="text-center">PR Processed Invoices</th>
										</tr>
									</thead>
									<tbody></tbody>
								</table>
							</div>
						</div>
	   		 		</div>
				</div>
			</div>
   		</div>
    </div>	
</div>
<script type="text/javascript">
$(function(){
	loadReconcileInfo('${id}');
});
</script>
</body>
</html>