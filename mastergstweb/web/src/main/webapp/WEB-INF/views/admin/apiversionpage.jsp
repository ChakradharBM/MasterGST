<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | API Version Document</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet" 	href="${contextPath}/static/mastergst/css/dashboard/dashboards.css" 	media="all" />
<link rel="stylesheet"	href="${contextPath}/static/mastergst/css/reports/reports.css" 	media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<style type="text/css">
button.btn.btn-xs{padding: 3px 13px;background-color: #314999;border:none;}
.bodybreadcrumb{margin-top:-8px!important}
tr td:last-child {display: inline-flex;margin: 0!important;width: 89%;}
td input{border: 1px solid lightgray!important;border-radius: 6px!important;}
</style>
</head>
<body>
<%@include file="/WEB-INF/views/includes/admin_header.jsp"%>
<div class="bodywrap" style="min-height: 480px; padding-top: 10px">
	<!--- company info bodybreadcrumb start -->
	<div class="bodybreadcrumb">
		<div class="container">
			<div class="row">
				<div class="col-sm-12">
					<div class="bdcrumb-tabs">
						<ul class="nav nav-tabs" role="tablist">
							 <li class="nav-item"> <a class="nav-link active" data-toggle="tab" id="gstapiversion" href="#gstapiv">GST API</a> </li>
							 <li class="nav-item"> <a class="nav-link" data-toggle="tab" id="ewaybillversion" href="#ewaybillv">E-Way Bill API</a> </li>
							 <li class="nav-item"> <a class="nav-link" data-toggle="tab" id="einvoiceversion" href="#einvoicev">E-invoice API</a> </li>
						</ul>
					</div>
				</div>
			</div>
		</div>
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
					<div class="col-sm-12">&nbsp;</div>
					<div class="customtable tabtable col-sm-12">
						<jsp:include page="/WEB-INF/views/admin/apisversion_tap.jsp">
							<jsp:param name="apiVersionTabName" value="GSTR"/>
							<jsp:param name="contextPath" value="${contextPath}"/>
						</jsp:include>
					</div>
				</div>
			</div>
   		</div>
    	<div id="ewaybillv" class="tab-pane fade">
   		 	<div class="container" style="padding-top: 30px;">
				<div class="row">
					<div class="col-sm-12">&nbsp;</div>
					<div class="col-sm-12" style="margin-top: 10px;">
						<a id="backpage_lnk" href="${contextPath}/userreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>" class="btn btn-blue-dark report mr-0 pull-right" role="button">BACK</a>
					</div>
					<div class="col-sm-12">&nbsp;</div>
					<div class="customtable tabtable col-sm-12">
						<jsp:include page="/WEB-INF/views/admin/apisversion_tap.jsp">
							<jsp:param name="apiVersionTabName" value="EWaybill"/>
							<jsp:param name="contextPath" value="${contextPath}"/>
						</jsp:include>
					</div>
				</div>
			</div>
   		</div>
   		<div id="einvoicev" class="tab-pane fade">
   		 	<div class="container" style="padding-top: 30px;">
				<div class="row">
					<div class="col-sm-12">&nbsp;</div>
					<div class="col-sm-12" style="margin-top: 10px;">
						<a id="backpage_lnk" href="${contextPath}/userreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>" class="btn btn-blue-dark report mr-0 pull-right" role="button">BACK</a>
					</div>
					<div class="col-sm-12">&nbsp;</div>
					<div class="customtable tabtable col-sm-12">
						<jsp:include page="/WEB-INF/views/admin/apisversion_tap.jsp">
							<jsp:param name="apiVersionTabName" value="Einvoice"/>
							<jsp:param name="contextPath" value="${contextPath}"/>
						</jsp:include>
					</div>
				</div>
			</div>
   		</div>
	</div>	
</div>
<!-- scripts -->		
<script type="text/javascript">
$("body").on("click", ".btn-cancel", function(){
    var name = $(this).parents("tr").attr('data-name');
    var email = $(this).parents("tr").attr('data-email');
    var v1 = $(this).parents("tr").attr('data-v1');
    var v2 = $(this).parents("tr").attr('data-v2');
    var v3 = $(this).parents("tr").attr('data-v3');

    $(this).parents("tr").find("td:eq(1)").text(name);
    $(this).parents("tr").find("td:eq(2)").text(email);
    $(this).parents("tr").find("td:eq(3)").text(v1);
    $(this).parents("tr").find("td:eq(4)").text(v2);
    $(this).parents("tr").find("td:eq(5)").text(v3);

    $(this).parents("tr").find(".btn-edit").show();
    $(this).parents("tr").find(".btn-update").remove();
    $(this).parents("tr").find(".btn-cancel").remove();
});
</script>
<script type="text/javascript">
	$('#reports_lnk').addClass('active');
</script>
<script src="${contextPath}/static/mastergst/js/admin/apisversion.js" type="text/javascript"></script>
</body>
</html>