<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Admin User Report</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet" 	href="${contextPath}/static/mastergst/css/dashboard/dashboards.css" 	media="all" />
<link rel="stylesheet"	href="${contextPath}/static/mastergst/css/reports/reports.css" 	media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<!-- datepicker start -->
<script src="${contextPath}/static/mastergst/js/common/datetimepicker-inv.js" type="text/javascript"></script> 
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<!-- datepicker end -->
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/admin/admin.css" media="all" />
<script src="${contextPath}/static/mastergst/js/admin/usersDetail.js" type="text/javascript"></script>
<!-- filters css & js start -->
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css"	media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
<!-- filters css & js end -->


<%-- <script src="${contextPath}/static/mastergst/js/admin-js/activeUsers.js" type="text/javascript"></script> --%>
<style>#demousertable_filter input{width:150px;}
#demousertable thead tr:nth-child(1){color: white;background-color: #5769bb;}
#demousertable thead tr:nth-child(2){background-color: white;}
</style>
</head>
<jsp:include page="/WEB-INF/views/admin/userDetails.jsp" />
<body class="body-cls">
	<%@include file="/WEB-INF/views/includes/admin_header.jsp"%>
	<div class="bodywrap" style="min-height: 480px; padding-top: 10px">
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
	<div class="container">
		<div class="row">
			<div class="col-sm-12">&nbsp;</div>
			<div class="col-sm-12">&nbsp;</div>
			<div class="col-sm-12" style="margin-top: 10px;">
				<a id="backpage_lnk" href="${contextPath}/userreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>" class="btn btn-blue-dark report mr-0 pull-right" role="button">BACK</a>
			</div>
		</div>
	</div>
	<!-- container -->
	<div class="container">
		<div class="col-sm-12">&nbsp;</div>
			
			<div class="customtable db-ca-view ">
				<table id='demousertable' class="row-border dataTable"
					cellspacing="0" width="100%">
					<thead>
						<tr><th class="text-center">User Type</th>
						<th class="text-center">UserId</th>
						<th class="text-center">User Name</th>
						<th class="text-center">Phone Number</th>
						<th class="text-center">Signup Date</th>
						<th class="text-center">Sub.Start Date</th>
						<th class="text-center">Sub.End Date</th>
						<th class="text-center">Status</th>
						<th class="text-center">Payment Status</th>
						<!-- <th class="text-center">Payment Type</th> -->
						<th class="text-center">Api Type</th></tr>
					</thead>
					<tbody id='AdminUserstable'></tbody>
				</table>
			</div>	
		</div>
	<!-- container close -->
	<script type="text/javascript">
	var table;
	
		$(document).ready(function() {
			$('#demousertable thead tr').clone(true).appendTo( '#demousertable thead' );
		  
		   $('#demousertable thead tr:eq(1) th').each( function (i) {
		        var title = $(this).text();
		        
		        $(this).html( '<input class="form-control" type="text" style="width:60px;height:15px;padding: 4px 7px;font-size: 12px;" placeholder="Search '+title+'" />' );
		 
		        $( 'input', this ).on( 'keyup change', function () {
		            if ( table.column(i).search() !== this.value ) {
		                table
		                    .column(i)
		                    .search( this.value )
		                    .draw();
		            }
		        } );
		       
		    } );
		   var table = $('#demousertable').DataTable( {
			   "ajax": {
					url: '${contextPath}/demoUsersReportData/?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>',
			        type: 'GET'
			     },
		        orderCellsTop: true,
		        fixedHeader: true,
		        "paging": true,
			     "searching":true,
			     'pageLength':10,
			     "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
				"responsive": true,
			     "processing":true,
		         dom:'<"toolbar">lBfrtip',
		         "language": {
						"search": "_INPUT_",
						"searchPlaceholder": "Search...",
						"paginate": {
						   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
							"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
					   }
					 },
					 columns : [                
			                {data : 'type', 'defaultContent':''},
			        	    {data : 'userId', 'defaultContent':''},
			                {data : 'fullname', 'defaultContent':''},
			                {data : 'mobilenumber', 'defaultContent':''},
			                {data : 'userSignDate', 'defaultContent':''},
			                {data : 'subscriptionStartDate', 'defaultContent':''},
			                {data : 'subscriptionExpiryDate', 'defaultContent':''},
			                {data : 'needToFollowUp', 'defaultContent':''},
			                {data : 'patmentStatus', 'defaultContent':''},
			               /*  {data : 'patmentType', 'defaultContent':''}, */
			                {data : 'apiType', 'defaultContent':''}
					]
		    });
		   $('#demousertable tbody').on('click', 'tr', function () {
				var dat = table.row($(this)).data();
				getUsersDetails(dat.userId);
			}); 
		   $(".tabtable div.toolbar").html('<h4>Expired Users &nbsp;&nbsp;</h4>');
		} );
	
	</script>
</body>
</html>