<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Purchase Record Payment History</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp"%>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js"
	type="text/javascript"></script>
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
	<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/payment.js" type="text/javascript"></script>
</head>
<body class="body-cls">
	<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
	<!--- breadcrumb start -->
	<div class="breadcrumbwrap">
		<div class="container">
			<div class="row">
				<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink"
							link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>" />
						<c:choose>
								<c:when
									test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when>
								<c:otherwise>Business</c:otherwise>
							</c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink"
							link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose>
									<c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when>
									<c:otherwise>${client.businessname}</c:otherwise>
								</c:choose></a></li>
						<li class="breadcrumb-item active">Purchases Payment History</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
				<div class=" "></div>
			<div class="tab-pane" id="gtab1" role="tabpanel">
					<div class="normaltable meterialform" id="monthlynormaltable">
		<div class="filter">
			<div class="noramltable-row"><div class="noramltable-row-hdr">Filter</div><div class="noramltable-row-desc"><div class="sfilter"><span id="divFiltersGSTR2"></span>
					<span class="btn-remove-tag" onClick="clearFilters('GSTR2')">Clear All<span data-role="remove"></span></span>
				</div></div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search Filter</div><div class="noramltable-row-desc">
				<select id="multiselectGSTR21" class="multiselect-ui form-control" multiple="multiple"><option value="2017-2018">2017-2018</option><option value="2018-2019">2018-2019</option><option value="2019-2020">2019-2020</option></select>
				<select id="multiselectGSTR22" class="multiselect-ui form-control" multiple="multiple"></select>
				<select id="multiselectGSTR23" class="multiselect-ui form-control" multiple="multiple"></select>
				<select id="multiselectGSTR24" class="multiselect-ui form-control" multiple="multiple"><option value="cash">Cash</option><option value="bank">Bank</option><option value="tds">TDS</option><option value="writeoff">Write-Off</option></select>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Payments<div class="normaltable-col-txt" id="idCountGSTR2"></div></div>
				<div class="normaltable-col hdr">Total Amount Received<div class="normaltable-col-txt" id="idTotalValGSTR2"></div></div>
			</div>
		</div>
	</div>
			</div>
		<div class=" "><h4 class="f-18-b pull-left"><h4>Purchase Payments of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></h4></h4></div>
		<div class="customtable db-ca-view ">
				<table id='paymentstable1' class="row-border dataTable meterialform"
					cellspacing="0" width="100%">
					<thead>
						<tr><th class="text-center" width=5%>S.no</th><th class="text-center" width=12%>Date</th><th class="text-center">Payment Id</th><th class="text-center">Invoice Number</th><th class="text-center">Customer</th><th class="text-center">GSTIN</th><th class="text-center">Amount Received</th><th class="text-center">Payment Mode</th><th class="text-center">Reference No</th><th class="text-center" style="display:none;">Financial Year</th></tr>
					</thead>
					<tbody id='paymentsalestable'></tbody>
				</table>
			</div>
		</div>
	</div>
	<script>var paymenturlSuffix='/${id}/${fullname}/${usertype}/${clientid}';var Paymenturlprefix='${month}/${year}';var contextPath='<c:out value="${contextPath}"/>';</script>
</body>
</html>