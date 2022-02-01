<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <meta name="description" content="" />
    <meta name="author" content="" />
    <link rel="icon" href="static/images/master/favicon.ico" />
    <title>MasterGST | Low Stock Report</title>
    <%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
    <%@include file="/WEB-INF/views/includes/reports_script.jsp"%>
    <link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/select2.min.css" media="all" />
    <script src="${contextPath}/static/mastergst/js/jquery/select2.min.js" type="text/javascript"></script>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
    <div class="breadcrumbwrap">
	    <div class="container bread">
	     	<div class="row">
	        	<div class="col-md-12 col-sm-12">
	            	<ol class="breadcrumb">
	                	<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
						<li class="breadcrumb-item active">Low Stock Report</li>
	                </ol>
	                <div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
		<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right mb-3" role="button" style="padding: 4px 25px;">Back</a>
		     <h4>Low Stock Report Of <c:choose><c:when test='${fn:length(client.businessname) > 45}'>${fn:substring(client.businessname, 0, 45)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></h4>
		     <div class="customtable db-ca-view salestable reportsdbTableLowStock">
					<table id='reports_lowStock' class="row-border dataTable meterialform" cellspacing="0" width="100%">
						<thead>
							<tr>
								<th class="text-center">Item Name</th><th class="text-center">Item Code</th><th class="text-center">Stock Quantity</th><th class="text-center">Low Stock Level</th><th class="text-center">Stock Value</th>
							</tr>
						</thead>
						<tbody id='lowStock_body'></tbody>
					</table>
			 </div>
		
		</div>
		</div>
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<script src="${contextPath}/static/mastergst/js/inventory/stock_summary_reports.js" type="text/javascript"></script>
	<script type="text/javascript">
	loadLowStockTable('${id}', '${client.id}', '${month}', '${year}', '${usertype}', '${fullname}');
	</script>
</body>
</html>