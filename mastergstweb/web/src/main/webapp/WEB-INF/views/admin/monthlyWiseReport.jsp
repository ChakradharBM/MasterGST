<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Users</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet" 	href="${contextPath}/static/mastergst/css/dashboard/dashboards.css" 	media="all" />
<link rel="stylesheet"	href="${contextPath}/static/mastergst/css/reports/reports.css" 	media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/admin/admin.css" media="all" />
<script src="${contextPath}/static/mastergst/js/common/datetimepicker-inv.js" type="text/javascript"></script> 
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<script src="${contextPath}/static/mastergst/js/admin/usersDetail.js" type="text/javascript"></script>
<style type="text/css">
.datepicker-orient-left{top: 15%!important;}
.dataTables_length{font-weight: bold;}
.bodybreadcrumb{margin-top:-8px!important}
#monthlyUsageProcess {position:absolute;  z-index: 9; font-size: 23px; left: 50%;  color: #374583; width:30em; height:20px;margin-left: -15em; top:240px}
</style>
</head>
<jsp:include page="/WEB-INF/views/admin/userDetails.jsp" />
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
							<li class="nav-item">Reports</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!--- company info bodybreadcrumb end -->
	<div class="container" style="padding-top: 30px;">
		<div class="row">
			<div class="col-sm-12">&nbsp;</div>
			<div class="col-sm-12" style="margin-top: 10px;">
				<span style="display: inline-flex;">
					<b style="margin-top: 7px; margin-right: 10px;">Return Period : </b>
					<input type="text" class="form-control" id="apiusagedatepicker" style="width: 113px;"/><i class="fa fa-sort-desc" style="position: absolute; top: 4px;left: 19%;"></i>  
					<a id="report_generate" style="margin-left: 10px;" class="btn btn-blue-dark" role="button" onclick="generateReport()">GENERATE</a>
				</span>
				<a id="backpage_lnk" href="${contextPath}/userreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>" class="btn btn-blue-dark report mr-0 pull-right" role="button">BACK</a>
			</div>
			<div class="col-sm-12">&nbsp;</div>
			<div id="monthlyUsageProcess" class="text-center"></div>
			<div class="customtable tabtable col-sm-12">
				<table id="meteringDataTable" class="display dataTable meterialform col-sm-12 p-0"  cellspacing="0" width="100%">
					<thead>
						<tr>
							<th>Customer Name</th>
							<th>Email</th>
							<th>GST API</th>
							<th>EWAY BILL API</th>
							<th>E-INVOICE API</th>
							<th>GST SANDBOX</th>
							<th>EWAY BILL SANDBOX API</th>
							<th>E-INVOICE SANDBOX API</th>
						</tr>
					</thead>
					<tbody id="tabledata">
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
		
<!-- scripts -->		
<script type="text/javascript">
var meteringTab;
$(document).ready(function(){
		
	var date=new Date();
	var month = date.getMonth()+1;
	var year = date.getFullYear();
		meteringTab=$('#meteringDataTable').DataTable({
		     "paging": true,
		     "order": [[0,'desc']],
		     "searching":true,
		     'pageLength':10,
		     "processing":true,
	         dom:'<"toolbar">lBfrtip',
			 "language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		 });
		$(".tabtable div.toolbar").html('<h4>Monthly Wise API Usage &nbsp;&nbsp;</h4>');
	monthlywiseusagereports(month,year);	
});
function generateReport(){
	var apidatepicker=$('#apiusagedatepicker').val();	
	var res = apidatepicker.split("/");
	var month=res[0];
	var year=res[1];
	monthlywiseusagereports(month,year);
}

function monthlywiseusagereports(month,year){
	meteringTab.clear();
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url: "${contextPath}/monthlywiseusagereports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&currentmonth="+month+"&currentyear="+year,
		beforeSend: function () {
			$('#monthlyUsageProcess').text('Please wait, Loading Data...');
		},
		success : function(summary) {
			Object.keys(summary.data).forEach(function(user) {
				var rowData = [summary.data[user].fullname,summary.data[user].email,summary.data[user].gstAPIAllowedInvoices,summary.data[user].ewaybillAPIAllowedInvoices,summary.data[user].einvAPIAllowedInvoices,summary.data[user].gstSanboxAllowedCountInvoices,summary.data[user].ewaybillSanboxAllowedInvoices,summary.data[user].einvSanboxAllowedInvoices];
				meteringTab.row.add(rowData);
			});
			$('#meteringDataTable tbody').on('click', 'tr', function () {
				var dat = meteringTab.row($(this)).data();
				getAllUsersDetails(dat[1]);
			}); 
			meteringTab.draw();
			$('#monthlyUsageProcess').text(' ');
		},
		error:function(dat){
		}
	});
}
</script>
<script type="text/javascript">
$('#reports_lnk').addClass('active');
var currDate=new Date();
$(document).ready(function(){
	var month = currDate.getMonth()+1;
	var year = currDate.getFullYear();
	var dateValue = ((''+month).length<2 ? '0' : '') + month + '/' + year;
	var date = $('#apiusagedatepicker').datepicker({
		viewMode: 1,
		minViewMode: 1,
		format: 'mm/yyyy'
	}).on('changeDate', function(ev) {
		$('.datepicker').toggle();
	});
	$('#apiusagedatepicker').val(dateValue).trigger('change');
});
</script>
</body>
</html>